/*
 * FavDownloader
 *
 * (c) 2014 alalwww
 * https://github.com/alalwww
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package net.awairo.common.function;

import static com.google.common.base.Preconditions.*;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 引数を取らず値も返さない処理.
 *
 * @author alalwww
 */
@FunctionalInterface
public interface Runner {

    /**
     * 処理を実行します.
     */
    void run();

    /**
     * この関数の前に実行する処理を合成します.
     *
     * @param before 前に実行する関数
     * @return 合成関数
     */
    default Runner compose(Runner before) {
        checkNotNull(before, "before");

        return () -> {
            before.run();
            run();
        };
    }

    /**
     * この関数の後に実行する処理を合成します
     *
     * @param after 後に実行する関数
     * @return 合成関数
     */
    default Runner andThen(Runner after) {
        checkNotNull(after, "after");

        return () -> {
            run();
            after.run();
        };
    }

    /**
     * {@link ThrowableRunner} に変換します.
     *
     * @return ThrowableRunner
     */
    default ThrowableRunner toThrowableRunner() {
        return () -> run();
    }

    /**
     * {@link Runnable} に変換します.
     *
     * @return Runnable
     */
    default Runnable toRunnable() {
        return () -> run();
    }

    /**
     * {@link Runnable} をラップします.
     *
     * @param runnable Runnable
     * @return Runnableをラップした関数
     */
    static Runner wrap(Runnable runnable) {
        checkNotNull(runnable, "runnable");

        return () -> runnable.run();
    }

    /**
     * {@link Consumer} をラップします.
     *
     * @param consumer コンシューマー
     * @param valueSupplier コンシューマーに渡す値の取得処理
     * @return Consumerをラップした関数
     */
    static <T> Runner wrap(Consumer<? super T> consumer, Supplier<? extends T> valueSupplier) {
        checkNotNull(consumer, "consumer");
        checkNotNull(valueSupplier, "valueSupplier");

        return () -> consumer.accept(valueSupplier.get());
    }
}
