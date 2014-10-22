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

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;

import net.awairo.common.javafx.Dialog.SimpleDialogBuilder;
import net.awairo.common.javafx.SimpleDialog.DialogType;

/**
 * @author alalwww
 */
public interface DialogFactory {

    String getTitle();

    String getMessage();

    ReadOnlyStringProperty messageProperty();

    ReadOnlyDoubleProperty progressProperty();

    /**
     * シンプルなダイアログを生成.
     *
     * @return メッセージとタイトルを設定したダイアログ
     */
    default SimpleDialogBuilder simpleDialog() {
        FxUtils.checkFxApplicationThread();

        return Dialog.simple()
                .title(getTitle())
                .message(getMessage());
    }

    /**
     * プログレスバーダイアログを生成.
     *
     * @return プログレスバーダイアログ
     */
    default Dialog<SimpleDialog> progressBarDialog() {
        FxUtils.checkFxApplicationThread();

        Dialog<SimpleDialog> dialog = simpleDialog()
                .dialogType(DialogType.PROGRESS)
                .build();
        dialog.controller().message().textProperty().bind(messageProperty());
        dialog.controller().progress().progressProperty().bind(progressProperty());
        return dialog;
    }

}
