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

import java.util.concurrent.Callable;
import java.util.function.Consumer;

import com.google.common.base.Throwables;

/**
 * @author alalwww
 */
public interface ThrowableRunner {

    void run() throws Exception;

    default ThrowableRunner compose(ThrowableRunner before) {
        return () -> {
            before.run();
            run();
        };
    }

    default ThrowableRunner andThen(ThrowableRunner after) {
        return () -> {
            run();
            after.run();
        };
    }

    default Runner toRunner() {
        return () -> {
            try {
                run();
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
        };
    }

    static ThrowableRunner wrap(Callable<?> callable) {
        return () -> callable.call();
    }

    static ThrowableRunner wrap(Runnable runnable) {
        return () -> runnable.run();
    }

    static <T> ThrowableRunner wrap(Consumer<T> consumer, T value) {
        return () -> consumer.accept(value);
    }
}
