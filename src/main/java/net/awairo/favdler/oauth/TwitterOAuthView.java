/*
 * FavDownloader
 *
 * (c) 2014 alalwww
 * https://github.com/alalwww
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package net.awairo.favdler.oauth;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.extern.log4j.Log4j2;

import net.awairo.common.exception.AppException;
import net.awairo.common.javafx.Dialog;
import net.awairo.common.javafx.SceneController;
import net.awairo.favdler.core.Views;

/**
 * Twitter認証画面.
 *
 * @author alalwww
 */
@Log4j2
public final class TwitterOAuthView implements SceneController {

    /** 認証開始ボタン. */
    @FXML
    Button oauthButton;

    /** キャンセルボタン. */
    @FXML
    Button cancelButton;

    // ---------------------------------------------

    private final TwitterOAuth oauth = new TwitterOAuth()
            .onSucceeded(this::changeSceneToFavList)
            .onFailed(this::handleError);

    // ---------------------------------------------

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.trace("initialize: loc:{}, rb:{}", location, resources);

        updateButtonState(false);

    }

    @Override
    public void postInitialize() {
        stage().setResizable(false);
    }

    // ---------------------------------------------

    private void updateButtonState(boolean startOAuth) {

        oauthButton.setDisable(startOAuth);
        oauthButton.setVisible(!startOAuth);

        cancelButton.setDisable(!startOAuth);
        cancelButton.setVisible(startOAuth);
    }

    private void changeSceneToFavList(TwitterAccessor accessor) {
        log.trace("success: accessor={}", accessor);

        changeSceneTo(Views.Scenes.TWEET_LIST_VIEW)
                .controller()
                .setTwitterAccessor(accessor);

    }

    private void handleError(Throwable error) {
        log.trace("fail({})", error);

        if (error instanceof AppException) {

            if (!((AppException) error).retryable()) {
                log.debug("処理済み例外", error);
                throw (AppException) error;
            }

            // 再実行可能な状態に戻し、エラーを表示

            updateButtonState(false);
            scene().getRoot().setDisable(true);

            Dialog.simple()
                    .toErrorDialog()
                    .message(error.getMessage())
                    .build()
                    .show();

            scene().getRoot().setDisable(false);
            return;
        }

        throw new AppException(error);
    }

    // event handlers
    // ---------------------------------------------

    public void oauthButton_onAction(ActionEvent event) {
        log.trace("oauthButton_onAction({})", event);

        updateButtonState(true);

        oauth.start();
    }

    public void cancelButton_onAction(ActionEvent event) {
        log.trace("cancelButton_onAction({})", event);

        updateButtonState(false);
        oauth.cancel();
    }
}
