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

import java.util.function.Consumer;

/**
 * 引数を取らず値も返さない処理.
 *
 * @author alalwww
 */
@FunctionalInterface
public interface Runner {

    void run();

    default Runner compose(Runner before) {
        return () -> {
            before.run();
            run();
        };
    }

    default Runner andThen(Runner after) {
        return () -> {
            run();
            after.run();
        };
    }

    default ThrowableRunner toThrowableRunner() {
        return () -> run();
    }

    static Runner wrap(Runnable runnable) {
        return () -> runnable.run();
    }

    static <T> Runner wrap(Consumer<T> consumer, T value) {
        return () -> consumer.accept(value);
    }
}
