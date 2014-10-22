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

import lombok.AccessLevel;

import lombok.NoArgsConstructor;
import javafx.application.Platform;

/**
 * @author alalwww
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FxUtils {

    /**
     * UIスレッドであることをチェックします.
     */
    public static void checkFxApplicationThread() {
        checkState(Platform.isFxApplicationThread(), "アプリケーションスレッド以外で実行する場合`Platform.runLater`メソッドを使用してください");
    }
}
