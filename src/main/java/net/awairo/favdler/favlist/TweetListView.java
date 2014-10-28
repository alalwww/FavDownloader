/*
 * FavDownloader
 *
 * (c) 2014 alalwww
 * https://github.com/alalwww
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package net.awairo.favdler.favlist;

import static com.google.common.base.Preconditions.*;

import java.io.File;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.extern.log4j.Log4j2;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;

import net.awairo.common.extension.Extensions;
import net.awairo.common.javafx.Dialog;
import net.awairo.common.javafx.MyTask;
import net.awairo.common.javafx.NoResultTask;
import net.awairo.common.javafx.SceneController;
import net.awairo.common.javafx.ServiceBase;
import net.awairo.common.javafx.SimpleDialog;
import net.awairo.common.util.RB;
import net.awairo.favdler.downloader.DownloadTask;
import net.awairo.favdler.downloader.FromSpec;
import net.awairo.favdler.twitter.FavoriteListItems;
import net.awairo.favdler.twitter.Tweet;
import net.awairo.favdler.twitter.TwitterAccessor;

/**
 * ツイート一覧画面.
 *
 * @author alalwww
 */
@Log4j2
@ExtensionMethod({ Extensions.Options.class, Extensions.Strings.class, Extensions.Comparables.class })
public final class TweetListView implements SceneController {

    /** 上と下の区切り. */
    @FXML
    SplitPane divider;

    /** スクリーンネームテキストフィールド. */
    @FXML
    TextField screenName;

    /** ディレクトリパステキストフィールド. */
    @FXML
    TextField downloadDirectoryPath;
    /** ディレクトリ選択ボタン. */
    @FXML
    Button folderSelectButton;

    /** 一覧ペイン. */
    @FXML
    AnchorPane listPane;

    /** API残数. */
    @FXML
    Label apiLimit;
    /** メディアのみチェックボックス. */
    @FXML
    CheckBox mediaOnly;

    /** お気に入りツイート一覧. */
    @FXML
    ListView<Tweet> list;

    // ------------------------------------------

    private final DirectoryChooser dirChooser = new DirectoryChooser();
    private final ApiLimitUpdater apiLimitUpdater = new ApiLimitUpdater();
    private FavoriteFinder searcher;

    // ------------------------------------------

    /** 検索結果. */
    private FavoriteListItems searchResult;

    /** ツイッターへのアクセス. */
    private TwitterAccessor accessor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.trace("initialize({}, {})", location, resources);

        list.setUserData(new UserData());
        list.setCellFactory(TweetListCell.newFactory());
        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        screenName.focusedProperty().addListener(this::addOrRemoveAtmark);

        initializeDirChooser();
    }

    @Override
    public void postInitialize() {
        stage().setResizable(true);
        stage().setMinWidth(stage().getWidth());
        stage().setMaxWidth(stage().getWidth());
    }

    /** ツイッターへのアクセッサ. */
    public void setTwitterAccessor(TwitterAccessor accessor) {
        checkState(this.accessor == null);
        this.accessor = checkNotNull(accessor);

        searcher = new FavoriteFinder(accessor.twitter());
        screenName.setText(addAtmark(accessor.accessToken().getScreenName()));

        setupService();
        searcher.start();
    }

    private void initializeDirChooser() {

        String home = System.getProperty("user.home");

        log.debug("user.home={}", home);

        if (home == null)
            log.warn("'user.home' property is null");

        File homeDir = new File(home);

        if (!homeDir.exists()) {
            log.warn("{} is not existed", homeDir);
            homeDir = new File("./");
        }
        if (!homeDir.isDirectory()) {
            log.warn("{} is not a directory", homeDir);
            homeDir = new File("./");
        }
        if (!homeDir.canWrite()) {
            log.warn("{} can not writable", homeDir);
            homeDir = new File("./");
        }

        dirChooser.setInitialDirectory(homeDir);
        downloadDirectoryPath.setText(homeDir.getAbsolutePath());

        userData().userHome(homeDir);
    }

    private void setupService() {
        searcher.screenName = removeAtmark(screenName.getText());
        searcher.count = 200; // MAX
        searcher.sinceId = Optional.empty();
        searcher.maxId = Optional.empty();
    }

    private void setToListFor(ObservableList<Tweet> items) {
        list.getSelectionModel().clearSelection();

        if (mediaOnly.isSelected()) {
            items = items.stream()
                    .filter(Tweet::hasMedia)
                    .collect(() -> FXCollections.observableArrayList(), (l, t) -> l.add(t), (l1, l2) -> l1.addAll(l2));
        }

        list.setItems(items);
    }

    private UserData userData() {
        return (UserData) list.getUserData();
    }

    // ------------------------------------------
    // event handlers
    // ------------------------------------------

    private void addOrRemoveAtmark(ObservableValue<? extends Boolean> osb, Boolean oVal, Boolean nVal) {
        log.trace("focus: {}, ({} -> {})", osb, oVal, nVal);

        String name = screenName.getText();
        name = nVal ? removeAtmark(name) : addAtmark(name);

        screenName.setText(name);
    }

    private static String removeAtmark(String s) {
        return s.startsWith("@") ? s.substring(1) : s;
    }

    private static String addAtmark(String s) {
        return s.startsWith("@") ? s : "@" + s;
    }

    /**
     * リロードボタン押下.
     */
    public void reloadFavorites_onAction(ActionEvent event) {
        log.trace("reloadFavorites_onMouseClicked: {}", event);
        searchResult = null;
        setupService();
        searcher.restart();
    }

    /**
     * ダウンロード用ディレクトリ選択ボタン押下.
     */
    public void folderSelect_onAction(ActionEvent event) {

        File before = dirChooser.getInitialDirectory();
        File selectedDir = dirChooser.showDialog(stage());

        if (selectedDir != null && selectedDir.exists() && selectedDir.isDirectory()) {
            log.debug("dir: {} to {}", before, selectedDir);

            dirChooser.setInitialDirectory(selectedDir);
            downloadDirectoryPath.setText(selectedDir.getAbsolutePath());
        }

        list.setUserData(dirChooser.getInitialDirectory());
    }

    /**
     * 選択ツイート一気にDL.
     */
    public void startDownload_onAction(ActionEvent event) {
        log.trace("startDownload_onAction: {}", event);

        File toDir = dirChooser.getInitialDirectory();

        // TODO: リストのセルと重複コード
        NoResultTask task = new DownloadTask(FromSpec.copyOf(list.getSelectionModel().getSelectedItems()), toDir);

        Dialog<SimpleDialog> progress = task.progressBarDialog();
        task.ifFailed(Dialog::showSystemError);
        // TODO: ダイアログがダサいのでどうにかしたい
        //        task.ifSucceeded(() -> Dialog.simple()
        //                .dialogType(DialogType.OK_BUTTON)
        //                .okActionHandler(Dialog::closeAction)
        //                .message("done")
        //                .build()
        //                .show()
        //                );
        task.ifDone(progress::close);
        execute(task);

        progress.show();
    }

    /**
     * メディアエンティティのあるツイートのみのチェックボックスON/OFF.
     */
    public void mediaOnly_onAction(ActionEvent event) {
        setToListFor(searchResult.list());
    }

    /**
     * 検索結果のリセット
     */
    private void resetSearchResult(FavoriteListItems result) {
        if (searchResult != null) {
            searchResult.update(result);
        } else {
            searchResult = result;
        }

        apiLimitUpdater.update();
        setToListFor(result.list());
    }

    // ------------------------------------------

    /**
     * お気に入り取得サービス.
     */
    @RequiredArgsConstructor
    final class FavoriteFinder extends ServiceBase<twitter4j.ResponseList<Status>, FavoriteFinder> {
        TweetListView instance;

        @NonNull
        final Twitter twitter;

        String screenName;

        int count = 200;
        Optional<Long> sinceId = Optional.empty();
        Optional<Long> maxId = Optional.empty();

        @Override
        protected Task<twitter4j.ResponseList<Status>> newTask() {
            return MyTask.of(() -> twitter.favorites().getFavorites(screenName, paging()))
                    .ifSucceeded(r -> resetSearchResult(new FavoriteListItems(r)));
        }

        private Paging paging() {
            Paging paging = new Paging().count(count);
            sinceId.map(v -> v < 0 ? null : v).ifPresent(paging::setSinceId);
            maxId.map(v -> v < 0 ? null : v).ifPresent(paging::setMaxId);
            return paging;
        }

    }

    /**
     * APIリセット情報の更新サービス.
     */
    final class ApiLimitUpdater extends ServiceBase<Void, ApiLimitUpdater> {

        void update() {

            if (searchResult == null)
                return;

            ZonedDateTime reset = ZonedDateTime.ofInstant(Instant.ofEpochSecond(searchResult.resetTimeInSeconds()), ZoneId.systemDefault());
            ZonedDateTime now = ZonedDateTime.now();
            long secondsUntilReset = reset.toEpochSecond() - now.toEpochSecond();

            if (secondsUntilReset > 0) {
                apiLimit.setText(beforeResetMessage(secondsUntilReset, reset));
                restart();
                return;
            }

            apiLimit.setText(messageOfReseted(reset));
        }

        String beforeResetMessage(long secondsUntilReset, ZonedDateTime reset) {
            return RB.labelOf("api_limit_format",
                    searchResult.remaining(),
                    searchResult.limit(),
                    reset.toLocalTime(),
                    secondsUntilReset);
        }

        String messageOfReseted(ZonedDateTime reset) {
            return RB.labelOf("api_limit_format",
                    searchResult.limit(),
                    searchResult.limit(),
                    reset.toLocalTime(),
                    0);
        }

        @Override
        protected Task<Void> newTask() {
            return NoResultTask.of(() -> Thread.sleep(1000))
                    .ifSucceeded(() -> update());
        }
    }
}
