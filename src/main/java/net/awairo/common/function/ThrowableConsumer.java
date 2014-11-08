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

import com.google.common.base.Throwables;

/**
 * 例外をスロー可能な{@link java.util.function.Consumer}.
 *
 * @author alalwww
 *
 * @param <T> 引数のタイプ
 */
@FunctionalInterface
public interface ThrowableConsumer<T> {

    /**
     * 引数を受け入れます.
     *
     * @param value 引数
     * @throws Exception 継続不可能な問題が発生した場合
     */
    void accept(T value) throws Exception;

    /**
     * この関数の後に実行する関数を合成します.
     *
     * @param after 後に実行する関数
     * @return 合成した関数
     */
    default ThrowableConsumer<T> andThen(ThrowableConsumer<? super T> after) {
        checkNotNull(after, "after");

        return t -> {
            accept(t);
            after.accept(t);
        };
    }

    /**
     * この関数の後に実行する関数を合成します.
     *
     * @param after 後に実行する関数
     * @return 合成した関数
     */
    default ThrowableConsumer<T> andThen(Consumer<? super T> after) {
        checkNotNull(after, "after");

        return andThen((ThrowableConsumer<? super T>) t -> after.accept(t));
    }

    /**
     * {@link Consumer} に変換します.
     *
     * @return Consumer
     */
    default Consumer<T> toConsumer() {
        return t -> {
            try {
                accept(t);
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
        };
    }

}
