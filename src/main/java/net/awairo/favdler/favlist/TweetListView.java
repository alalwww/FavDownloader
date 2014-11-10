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
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import lombok.val;
import lombok.extern.log4j.Log4j2;

import net.awairo.common.javafx.Dialog;
import net.awairo.common.javafx.NoResultTask;
import net.awairo.common.javafx.SceneController;
import net.awairo.common.javafx.SimpleDialog;
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
    private final RateLimitUpdater rateLimitUpdater = new RateLimitUpdater();
    private final FavoriteListService twitterFavorites = new FavoriteListService();

    private InitializedState initState = InitializedState.PRE_INITIALIZED;

    private enum InitializedState {
        PRE_INITIALIZED, INITIALIZED, POST_INITIALIZED, DONE
    }

    // ------------------------------------------

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        checkState(initState == InitializedState.PRE_INITIALIZED);

        list.setUserData(new UserData());
        list.setCellFactory(TweetListCell.newFactory());
        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // フォーカスのあるときはScreenNameの @ を消す
        screenName.focusedProperty().addListener(this::addOrRemoveAtmark);

        apiLimit.textProperty().bind(rateLimitUpdater.messageProperty());

        twitterFavorites.valueProperty().addListener(this::updateListViewItems);

        initializeDirChooser();

        initState = InitializedState.INITIALIZED;
    }

    @Override
    public void postInitialize() {
        checkState(initState == InitializedState.INITIALIZED);

        stage().setResizable(true);
        stage().setMinWidth(stage().getWidth());
        stage().setMaxWidth(stage().getWidth());
        initState = InitializedState.POST_INITIALIZED;
    }

    /** ツイッターへのアクセッサを設定. */
    public void setTwitterAccessor(TwitterAccessor accessor) {
        checkNotNull(accessor, "accessor");
        checkState(initState == InitializedState.POST_INITIALIZED);

        twitterFavorites.initialize(accessor);

        // 認証したユーザーアカウントを初期値にする
        val sn = accessor.accessToken().getScreenName();
        screenName.setText(addAtmark(sn));
        twitterFavorites.screenName(sn);

        // 初期表示としてお気に入り一覧のロードを始める
        refreshFavoriteList();

        initState = InitializedState.DONE;
    }

    /** 保存先ディレクトリの初期設定. */
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

    /** 最新から最大で200件のお気に入り一覧を取得する. */
    private void refreshFavoriteList() {
        twitterFavorites.screenName(removeAtmark(screenName.getText()));
        twitterFavorites.refresh();
    }

    private void updateListViewItems() {
        rateLimitUpdater.restart(twitterFavorites.getValue());
        list.getSelectionModel().clearSelection();

        if (twitterFavorites.getValue().remaining() <= 0) {
            Dialog.simple()
                    .toErrorDialog()
                    .message(RB.messageOf("rate_limit_exceeded"))
                    .build()
                    .show();
            return;
        }

        // TODO: フィルターのタスク化
        ObservableList<Tweet> items = twitterFavorites.getValue().list();

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

    private static String removeAtmark(String s) {
        return s.startsWith("@") ? s.substring(1) : s;
    }

    private static String addAtmark(String s) {
        return s.startsWith("@") ? s : "@" + s;
    }

    // ------------------------------------------
    // event handlers
    // ------------------------------------------

    private void updateListViewItems(ObservableValue<? extends FavoriteListItems> obs, FavoriteListItems oVal, FavoriteListItems nVal) {
        log.trace("favListService value changed. ({} -> {})", oVal, nVal);

        if (nVal != null)
            updateListViewItems();
    }

    private void addOrRemoveAtmark(ObservableValue<? extends Boolean> osb, Boolean oVal, Boolean nVal) {
        log.trace("focus: {}, ({} -> {})", osb, oVal, nVal);

        String name = screenName.getText();
        name = nVal ? removeAtmark(name) : addAtmark(name);

        screenName.setText(name);
    }

    /**
     * リロードボタン押下.
     */
    public void reloadFavorites_onAction(ActionEvent event) {
        refreshFavoriteList();
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

        userData().userHome(dirChooser.getInitialDirectory());
    }

    /**
     * 選択ツイート一気にDL.
     */
    public void startDownload_onAction(ActionEvent event) {
        log.trace("startDownload_onAction: {}", event);

        // 未選択はなにもしない
        if (list.getSelectionModel().getSelectedItems().isEmpty())
            return;

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
        updateListViewItems();
    }
}
