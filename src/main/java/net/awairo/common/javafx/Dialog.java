/*
 * FavDownloader
 *
 * (c) 2014 alalwww
 * https://github.com/alalwww
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package net.awairo.common.javafx;

import static com.google.common.base.Preconditions.*;

import java.net.URL;
import java.util.Optional;
import java.util.function.BiConsumer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import net.awairo.common.exception.AppException;
import net.awairo.common.javafx.SimpleDialog.DialogType;
import net.awairo.common.util.RB;

/**
 * ダイアログ.
 *
 * @author alalwww
 */
@Getter
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Dialog<C extends SceneController> {

    private boolean modal;
    private boolean resizable;
    private C controller;

    /**
     * ダイアログを表示します.
     */
    public void show() {
        if (modal) {
            controller().stage().showAndWait();
        } else {
            controller().stage().show();
        }
    }

    /**
     * ダイアログを閉じます.
     */
    public void close() {
        controller().stage().close();
    }

    /**
     * ウィンドウクローズ処理.
     */
    public static final <T extends SceneController> void closeAction(Dialog<T> dialog, ActionEvent event) {
        dialog.close();
    }

    /* package */static ApplicationBase application;

    /**
     * 見出しとメッセージ本文といくつかのボタンを持つ単純なダイアログを作る新たなビルダーを取得します.
     *
     * @return シンプルダイアログビルダー
     */
    public static SimpleDialogBuilder simple() {
        return new SimpleDialogBuilder();
    }

    /**
     * システムエラーダイアログを表示します.
     *
     * @param t エラー
     */
    public static void showSystemError(Throwable t) {
        Platform.runLater(() -> simple()
                .toErrorDialog()
                .message(t instanceof AppException ? t.getMessage() : RB.labelOf("system_error"))
                .build()
                .show());
    }

    /**
     * 新しいダイアログビルダーを取得します.
     *
     * @param fxmlPath ダイアログのFXML
     * @return ビルダー
     */
    public static <C extends SceneController> Builder<C> builder(URL fxmlPath) {
        return new Builder<C>(FXMLSpec.<C> of(fxmlPath));
    }

    /**
     * 新しいダイアログビルダーを取得します.
     *
     * @param fxmlPath ダイアログのFXML
     * @param controller コントローラーインスタンス
     * @return ビルダー
     */
    public static <C extends SceneController> Builder<C> builder(URL fxmlPath, C controller) {
        return Dialog.<C> builder(fxmlPath).controller(controller);
    }

    /**
     * ダイアログビルダー.
     *
     * @author alalwww
     * @param <C> コントローラーのタイプ
     */

    public static final class Builder<C extends SceneController> extends AbstractBuilder<C, Builder<C>> {
        public Builder(FXMLSpec<C> fxml) {
            super(fxml);
        }
    }

    /**
     * ダイアログビルダー.
     *
     * @author alalwww
     * @param <C> コントローラーのタイプ
     */
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AbstractBuilder<C extends SceneController, B extends AbstractBuilder<C, B>> {

        @NonNull
        private final FXMLSpec<C> fxml;

        private C controller;

        public B controller(C controller) {
            this.controller = controller;
            return instance();
        }

        private Modality initModality = Modality.APPLICATION_MODAL;

        public B initModality(Modality initModality) {
            this.initModality = checkNotNull(initModality, "initModality");
            return instance();
        }

        private StageStyle initStyle = StageStyle.UTILITY;

        public B initStyle(StageStyle initStyle) {
            this.initStyle = checkNotNull(initStyle, "initStyle");
            return instance();
        }

        private boolean alwaysOnTop = false;

        public B alwaysOnTop(boolean alwaysOnTop) {
            this.alwaysOnTop = alwaysOnTop;
            return instance();
        }

        private String title = "";

        public B title(String title) {
            this.title = checkNotNull(title, "title");
            return instance();
        }

        private boolean modal = false;

        public B modal(boolean modal) {
            this.modal = modal;
            return instance();
        }

        private boolean resizable = false;

        public B resizable(boolean resizable) {
            this.resizable = resizable;
            return instance();
        }

        private boolean centerOnScreen = true;

        public B centerOnScreen(boolean centerOnScreen) {
            this.centerOnScreen = centerOnScreen;
            return instance();
        }

        private double x;

        public B x(double x) {
            centerOnScreen(false);
            this.x = x;
            return instance();
        }

        private double y;

        public B y(double y) {
            centerOnScreen(false);
            this.y = y;
            return instance();
        }

        private boolean built = false;

        @SuppressWarnings("unchecked")
        private B instance() {
            return (B) this;
        }

        public Dialog<C> build() {

            checkState(!built);
            built = true;

            Stage dialog = new Stage(initStyle);

            dialog.setX(x);
            dialog.setY(y);

            if (centerOnScreen)
                dialog.centerOnScreen();

            dialog.initOwner(application.primaryStage());
            dialog.setTitle(title);

            if (modal) {
                checkState(initModality != Modality.NONE, "initModalityが不正値です");
                dialog.initModality(initModality);
            }

            dialog.setAlwaysOnTop(alwaysOnTop);

            dialog.setResizable(resizable);

            return new Dialog<C>()
                    .controller(FXMLLoaders.loadViewOn(dialog, fxml, Optional.ofNullable(controller)))
                    .modal(modal)
                    .resizable(resizable);
        }
    }

    /**
     * シンプルダイアログビルダー.
     *
     * @author alalwww
     */
    @Setter
    @Accessors(fluent = true)
    public static final class SimpleDialogBuilder extends AbstractBuilder<SimpleDialog, SimpleDialogBuilder> {

        private SimpleDialog.DialogType dialogType;
        private String headline = "";
        private String message = "";
        private BiConsumer<Dialog<SimpleDialog>, ActionEvent> yesActionHandler;
        private BiConsumer<Dialog<SimpleDialog>, ActionEvent> noActionHandler;
        private BiConsumer<Dialog<SimpleDialog>, ActionEvent> okActionHandler;
        private BiConsumer<Dialog<SimpleDialog>, ActionEvent> cancelActionHandler;

        private SimpleDialogBuilder() {
            super(SimpleDialog.FXML);
        }

        public SimpleDialogBuilder toErrorDialog() {
            modal(true);
            resizable(false);
            alwaysOnTop(true);
            title(RB.labelOf("error"));
            headline(RB.labelOf("error"));
            dialogType(DialogType.OK_BUTTON);
            okActionHandler(Dialog::closeAction);
            return this;
        }

        @Override
        public Dialog<SimpleDialog> build() {
            Dialog<SimpleDialog> dialog = super.build();
            dialog.controller()
                    .dialogType(dialogType)
                    .headlineText(headline)
                    .messageText(message);
            if (yesActionHandler != null)
                dialog.controller().yes.setOnAction(e -> yesActionHandler.accept(dialog, e));
            if (noActionHandler != null)
                dialog.controller().no.setOnAction(e -> noActionHandler.accept(dialog, e));
            if (okActionHandler != null)
                dialog.controller().ok.setOnAction(e -> okActionHandler.accept(dialog, e));
            if (cancelActionHandler != null)
                dialog.controller().cancel.setOnAction(e -> cancelActionHandler.accept(dialog, e));
            return dialog;
        }
    }
}
