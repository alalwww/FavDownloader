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
import java.util.Arrays;
import java.util.ResourceBundle;

import com.google.common.primitives.Doubles;

import javafx.scene.control.ProgressBar;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * シンプルなダイアログのコントローラー.
 *
 * @author alalwww
 */
public final class SimpleDialog implements SceneController {

    public static final FXMLSpec<SimpleDialog> FXML = FXMLSpec.of("common/SimpleDialog.fxml");

    @FXML
    VBox root;

    @FXML
    Label headline;

    @FXML
    Label message;

    @FXML
    ProgressBar progress;

    @FXML
    HBox yesno;
    @FXML
    Button yes;
    @FXML
    Button no;

    @FXML
    HBox okcancel;
    @FXML
    Button ok;
    @FXML
    Button cancel;

    // ---------------------------------------------

    private DialogType dialogType = DialogType.OK_BUTTON;

    public SimpleDialog dialogType(DialogType state) {
        this.dialogType = checkNotNull(state);
        state.applyTo(this);
        return this;
    }

    public DialogType dialogType() {
        return dialogType;
    }

    public SimpleDialog headlineText(String headline) {
        this.headline.setText(headline);
        return this;
    }

    public SimpleDialog messageText(String message) {
        this.message.setText(message);
        return this;
    }

    public Label headline() {
        return headline;
    }

    public Label message() {
        return message;
    }

    public ProgressBar progress() {
        return progress;
    }

    public Button yes() {
        return yes;
    }

    public Button no() {
        return no;
    }

    public Button ok() {
        return ok;
    }

    public Button cancel() {
        return cancel;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        bindDisabled(yesno, yes, no);
        bindDisabled(okcancel, ok, cancel);

        initializeButtonBindingFor(yes);
        initializeButtonBindingFor(no);
        initializeButtonBindingFor(ok);
        initializeButtonBindingFor(cancel);

        progress.progressProperty().addListener((obs, o, n) -> {
            if (n.doubleValue() >= 1.0)
                stage().close();
        });
    }

    @Override
    public void postInitialize() {}

    private static void bindDisabled(HBox box, Button... targets) {
        box.disabledProperty().addListener((obs, o, n) -> {
            Arrays.stream(targets).forEach(b -> b.setDisable(n));
        });
    }

    private static void initializeButtonBindingFor(Button b) {
        b.disabledProperty().addListener((obs, o, n) -> {
            b.setVisible(n);
            if (!n) {
                b.setDefaultButton(false);
                b.setCancelButton(false);
            }
        });
    }

    /**
     * ボタン表示状態.
     */
    public enum DialogType {
        EMPTY {
            @Override
            void applyTo(SimpleDialog controller) {
                controller.root.getChildren().remove(controller.progress);
                controller.root.getChildren().remove(controller.yesno);
                controller.root.getChildren().remove(controller.okcancel);
            }
        },
        PROGRESS {
            @Override
            void applyTo(SimpleDialog controller) {
                controller.root.getChildren().remove(controller.yesno);
                controller.root.getChildren().remove(controller.okcancel);
                controller.progress.progressProperty().addListener((obs, o, n) -> {
                    if (Doubles.compare(n.doubleValue(), 1.0d) >= 0)
                        controller.stage().close();
                });
            }
        },
        YES_BUTTON {
            @Override
            void applyTo(SimpleDialog controller) {
                controller.root.getChildren().remove(controller.progress);
                controller.root.getChildren().remove(controller.okcancel);
                controller.yesno.getChildren().remove(controller.no);
                controller.yesno.setDisable(false);
                controller.yes.setDefaultButton(true);
                controller.yes.setCancelButton(true);
            }
        },
        NO_BUTTON {
            @Override
            void applyTo(SimpleDialog controller) {
                controller.root.getChildren().remove(controller.progress);
                controller.root.getChildren().remove(controller.okcancel);
                controller.yesno.getChildren().remove(controller.yes);
                controller.yesno.setDisable(false);
                controller.no.setDefaultButton(true);
                controller.no.setCancelButton(true);
            }
        },
        YES_NO_BUTTON {
            @Override
            void applyTo(SimpleDialog controller) {
                controller.root.getChildren().remove(controller.progress);
                controller.root.getChildren().remove(controller.okcancel);
                controller.yesno.setDisable(false);
                controller.yes.setDefaultButton(true);
                controller.no.setCancelButton(true);
            }
        },
        OK_BUTTON {
            @Override
            void applyTo(SimpleDialog controller) {
                controller.root.getChildren().remove(controller.progress);
                controller.root.getChildren().remove(controller.yesno);
                controller.okcancel.getChildren().remove(controller.cancel);
                controller.okcancel.setDisable(false);
                controller.ok.setDefaultButton(true);
                controller.ok.setCancelButton(true);
            }
        },
        CACNEL_BUTTON {
            @Override
            void applyTo(SimpleDialog controller) {
                controller.root.getChildren().remove(controller.progress);
                controller.root.getChildren().remove(controller.yesno);
                controller.okcancel.getChildren().remove(controller.ok);
                controller.okcancel.setDisable(false);
                controller.cancel.setDefaultButton(true);
                controller.cancel.setCancelButton(true);
            }
        },
        OK_CANCEL_BUTTON {
            @Override
            void applyTo(SimpleDialog controller) {
                controller.root.getChildren().remove(controller.progress);
                controller.root.getChildren().remove(controller.yesno);
                controller.okcancel.setDisable(false);
                controller.ok.setDefaultButton(true);
                controller.cancel.setCancelButton(true);
            }
        },
        ;

        /* package */abstract void applyTo(SimpleDialog controller);
    }
}
