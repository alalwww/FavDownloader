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

import javafx.application.Platform;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.awairo.common.function.Runner;
import net.awairo.common.function.ThrowableRunner;

/**
 * FX関連汎用処理.
 *
 * @author alalwww
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FxUtils {

    /**
     * UIスレッドであることをチェックします.
     */
    public static void checkFxApplicationThread() {
        checkState(isFxApplicationThread(), "アプリケーションスレッド以外で実行する場合`Platform.runLater`メソッドを使用してください");
    }

    /**
     * アプリケーションスレッドかを判定します.
     *
     * @return true はFXアプリケーションスレッド
     * @see javafx.application.Platform#isFxApplicationThread()
     */
    public static boolean isFxApplicationThread() {
        return Platform.isFxApplicationThread();
    }

    /**
     * イベントキューにアプリケーションスレッドで実行する処理を詰みます.
     *
     * <p>
     * 処理はアプリケーションスレッド上で実行されるため、多用するとアプリケーションの動作に支障が出ますが、UIの操作が可能です。
     * </p>
     *
     * @param runner 処理
     *
     * @see javafx.application.Platform#runLater(Runnable)
     */
    public static void runLater(Runner runner) {
        Platform.runLater(runner.toRunnable());
    }

    /**
     * イベントキューにアプリケーションスレッドで実行する処理を詰みます.
     *
     * <p>処理はアプリケーションスレッド上で実行されるため、多用するとアプリケーションの動作に支障が出ますが、UIの操作が可能です。 </p>
     * <p>発生した検査例外はすべて{@link RuntimeException}でラップされ実行したスレッド上にスローされます.</p>
     *
     * @param runner 処理
     *
     * @see #runLater(Runner)
     */
    public static void silentRrunLater(ThrowableRunner runner) {
        runLater(runner.toRunner());
    }

    /**
     * 現在のスレッドがアプリケーションスレッドである場合処理を実行し、違う場合{@link Platform#runLater(Runnable)}をコールします.
     *
     * @param runnar 処理
     *
     * @see #runLater(Runner)
     */
    public static void runOnApplicationThread(Runner runnar) {

        if (isFxApplicationThread()) {
            runnar.run();
            return;
        }

        runLater(runnar);
    }

    /**
     * 現在のスレッドがアプリケーションスレッドである場合処理を実行し、違う場合{@link Platform#runLater(Runnable)}をコールします.
     *
     * <p>発生した検査例外はすべて{@link RuntimeException}でラップされ実行したスレッド上にスローされます.</p>
     *
     * @param runnar 処理
     *
     * @see #runLater(Runner)
     */
    public static void sile5ntRunOnApplicationThread(ThrowableRunner runnar) {
        runOnApplicationThread(runnar.toRunner());
    }
}
