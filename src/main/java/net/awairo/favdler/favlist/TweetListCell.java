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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import lombok.extern.log4j.Log4j2;
import org.controlsfx.control.HyperlinkLabel;

import net.awairo.common.javafx.Controller;
import net.awairo.common.javafx.Dialog;
import net.awairo.common.javafx.FXMLLoaders;
import net.awairo.common.javafx.FXMLLoaders.Loader;
import net.awairo.common.javafx.NoResultTask;
import net.awairo.common.javafx.SimpleDialog;
import net.awairo.common.util.Desktop;
import net.awairo.favdler.core.ImageCache;
import net.awairo.favdler.core.Views;
import net.awairo.favdler.downloader.DownloadTask;
import net.awairo.favdler.downloader.FromSpec;
import net.awairo.favdler.favlist.twitter.Tweet;

/**
 * ツイート一覧のセル.
 *
 * @author alalwww
 */
@Log4j2
public final class TweetListCell extends ListCell<Tweet> implements Controller {

    @FXML
    Pane rootPane;

    @FXML
    CheckBox selected;

    @FXML
    Button download;

    @FXML
    Hyperlink name;
    @FXML
    Hyperlink screenName;

    /** アイコン. */
    @FXML
    ImageView icon;

    /** ツイート本文の表示エリア. */
    @FXML
    VBox contentBox;

    @FXML
    Hyperlink via;

    @FXML
    Hyperlink createdAt;

    @FXML
    Hyperlink replyButton;

    @FXML
    Hyperlink favoriteButton;
    @FXML
    Label favoriteCount;

    @FXML
    Hyperlink retweetButton;
    @FXML
    Label retweetCount;

    private HyperlinkLabel text = new HyperlinkLabel();
    // ------------------------------------------------------

    private Tweet tweet;

    public static Callback<ListView<Tweet>, ListCell<Tweet>> newFactory() {

        return list -> {
            Loader<TweetListCell> loader = FXMLLoaders.newLoader(Views.TWEET_LIST_CELL);
            loader.load();
            return loader.controller();
        };
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createdAt.setTooltip(new Tooltip());
        contentBox.getChildren().setAll(text);

        selected.selectedProperty().bind(selectedProperty());

        text.maxWidthProperty().bind(contentBox.widthProperty());

        text.getStyleClass().addAll("hyperlink-label");
        text.setOnAction(event -> {
            Hyperlink link = (Hyperlink) event.getSource();
            log.debug("open: {}", link.getText());
            openBrowser(link.getText());
        });
    }

    @Override
    protected void updateItem(Tweet tweet, boolean empty) {
        log.trace("updateItem empty={}", empty);

        super.updateItem(tweet, empty);

        if (empty || tweet == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        setTweet(tweet);
        rootPane.maxWidth(getListView().getWidth());
        setGraphic(rootPane);
    }

    private void setTweet(Tweet tweet) {
        checkNotNull(tweet);

        if (this.tweet != null) {
            this.tweet.checkboxSelected().unbind();
        }

        this.tweet = tweet;

        tweet.checkboxSelected().bind(selectedProperty());

        name.setText(tweet.getUser().getName());

        screenName.setText("@" + tweet.getUser().getScreenName());

        createdAt.setText(tweet.getTweetCreatedAt().toLabelString());
        createdAt.getTooltip().setText(tweet.getTweetCreatedAt().toTooltipString());

        icon.setImage(ImageCache.getOrNew(tweet.getUser().getProfileImageURL()));

        text.setText(tweet.getUrlFormattedText());
        contentBox.getChildren().setAll(text);

        if (tweet.hasMedia()) {

            HBox box = new HBox();
            box.maxWidthProperty().bind(contentBox.widthProperty());
            box.setSpacing(2.0);

            tweet.extendedMediaEntitiesStream().forEach(me -> {
                box.getChildren().add(new ImageView(ImageCache.getOrNew(me.getMediaURL())) {
                    {
                        fitWidthProperty().bind(box.widthProperty());
                        setFitHeight(100);
                        setPreserveRatio(true);
                    }
                });
            });

            contentBox.getChildren().add(box);
        }

        via.setText(tweet.getTweetSource().getName());

        favoriteCount.setText(String.valueOf(tweet.getFavoriteCount()));
        if (tweet.isFavorited()) {
            if (!favoriteButton.getStyleClass().contains("favorite-on"))
                favoriteButton.getStyleClass().add("favorite-on");
        } else {
            favoriteButton.getStyleClass().remove("favorite-on");
        }

        retweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        if (tweet.isRetweeted()) {
            if (!favoriteButton.getStyleClass().contains("retweet-on"))
                retweetButton.getStyleClass().add("retweet-on");
        } else {
            retweetButton.getStyleClass().remove("retweet-on");
        }
    }

    // --------------------------------------------
    // event handlers
    // --------------------------------------------

    public void download_onAction(ActionEvent event) {

        // TODO: ちょっとゴリ押しすぎなのでもう少しちゃんとした値渡しを考える
        File toDir = (File) getListView().getUserData();
        if (toDir == null || !toDir.exists() || !toDir.isDirectory()) {
            log.debug("toDir: {}", toDir);

            // TODO: メッセージをリソースに
            Dialog.simple().toErrorDialog().message("保存先が不正").build().show();

            return;
        }

        NoResultTask task = new DownloadTask(FromSpec.of(tweet), toDir);
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

    private static final String TWITTER = "https://twitter.com/";

    public void name_onAction(ActionEvent event) {
        openBrowser(TWITTER + "%s", tweet.getUser().getScreenName());
    }

    public void screenName_onAction(ActionEvent event) {
        name_onAction(event);
    }

    public void createdAt_onAction(ActionEvent event) {
        openBrowser(TWITTER + "%s/status/%s", tweet.getUser().getScreenName(), tweet.getId());
    }

    public void replyButton_onAction(ActionEvent event) {
        openBrowser(TWITTER + "intent/tweet?in_reply_to=%s", tweet.getId());
    }

    public void favoliteButton_onAction(ActionEvent event) {
        openBrowser(TWITTER + "intent/favorite?tweet_id=%s", tweet.getId());
    }

    public void retweetButton_onAction(ActionEvent event) {
        openBrowser(TWITTER + "intent/retweet?tweet_id=%s", tweet.getId());
    }

    public void via_onAction(ActionEvent event) {
        openBrowser(tweet.getTweetSource().getUrl());
    }

    private static void openBrowser(String format, Object... args) {
        Desktop.openURI(format, args);
    }
}
