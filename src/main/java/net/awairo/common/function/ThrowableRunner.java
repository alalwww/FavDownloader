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

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.base.Throwables;

/**
 * 例外をスローする可能性のある{@link Runner}.
 *
 * @author alalwww
 */
public interface ThrowableRunner {

    /**
     * 処理を実行します.
     *
     * @throws Exception 続行不可能な問題が発生した場合
     */
    void run() throws Exception;

    /**
     * この関数の前に実行する処理を合成します.
     *
     * @param before 前に実行する関数
     * @return 合成関数
     */
    default ThrowableRunner compose(ThrowableRunner before) {
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
    default ThrowableRunner andThen(ThrowableRunner after) {
        checkNotNull(after, "after");

        return () -> {
            run();
            after.run();
        };
    }

    /**
     * {@link Runner} に変換します.
     *
     * @return Runner
     */
    default Runner toRunner() {
        return () -> {
            try {
                run();
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
        };
    }

    /**
     * {@link Runnable} に変換します.
     *
     * @return Runnable
     */
    default Runnable toRunnable() {
        return toRunner().toRunnable();
    }

    /**
     * {@link Callable} をラップします.
     *
     * @param callable Callable
     * @return Callableをラップした関数
     */
    static ThrowableRunner wrap(Callable<?> callable) {
        checkNotNull(callable, "callable");

        return () -> callable.call();
    }

    /**
     * {@link Runnable} をラップします.
     *
     * @param runnable Runnable
     * @return Runnableをラップした関数
     */
    static ThrowableRunner wrap(Runnable runnable) {
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
    static <T> ThrowableRunner wrap(Consumer<? super T> consumer, Supplier<? extends T> valueSupplier) {
        checkNotNull(consumer, "consumer");
        checkNotNull(valueSupplier, "valueSupplier");

        return () -> consumer.accept(valueSupplier.get());
    }
}
