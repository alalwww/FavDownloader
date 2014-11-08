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

import java.util.Optional;
import java.util.function.Function;

import com.google.common.base.Throwables;

/**
 * 例外をスロー可能な{@link java.util.function.Function}.
 *
 * @author alalwww
 *
 * @param <T> 引数のタイプ
 * @param <R> 戻り値のタイプ
 */
@FunctionalInterface
public interface ThrowableFunction<T, R> {

    /**
     * 引数に処理を適用します.
     *
     * @param value 引数
     * @return 結果
     * @throws Exception 継続不可能な問題が発生した場合
     */
    R apply(T value) throws Exception;

    /**
     * この関数の前に実行する別の関数を合成します.
     *
     * @param before 前に実行する関数
     * @return 合成した関数
     */
    default <T2> ThrowableFunction<T2, R> compose(ThrowableFunction<? super T2, ? extends T> before) {
        checkNotNull(before, "before");

        return t2 -> apply(before.apply(t2));
    }

    /**
     * この関数の前に実行する別の関数を合成します.
     *
     * @param before 前に実行する関数
     * @return 合成した関数
     */
    default <T2> ThrowableFunction<T2, R> compose(Function<? super T2, ? extends T> before) {
        checkNotNull(before, "before");

        return compose(wrap(before));
    }

    /**
     * この関数の後に実行する関数を合成します.
     *
     * @param after 後に実行する関数
     * @return 合成した関数
     */
    default <R2> ThrowableFunction<T, R2> andThen(ThrowableFunction<? super R, ? extends R2> after) {
        checkNotNull(after, "after");

        return r1 -> after.apply(apply(r1));
    }

    /**
     * この関数の後に実行する関数を合成します.
     *
     * @param after 後に実行する関数
     * @return 合成した関数
     */
    default <R2> ThrowableFunction<T, R2> andThen(Function<? super R, ? extends R2> after) {
        checkNotNull(after, "after");

        return andThen(wrap(after));
    }

    /**
     * {@link Function}に変換します.
     *
     * @return Function
     */
    default Function<T, R> toFunction() {
        return t -> {
            try {
                return apply(t);
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
        };
    }

    /**
     * {@link Function}に変換します.
     *
     * @return Function
     */
    default Function<T, Optional<R>> toOptionalFunction() {
        return t -> {
            try {
                return Optional.ofNullable(apply(t));
            } catch (Exception e) {
                return Optional.empty();
            }
        };
    }

    /**
     * {@link java.util.function.Function}をラップします.
     *
     * @param fn 元にする{@link java.util.function.Function}
     * @return ラップした{@link ThrowableFunction}
     */
    static <T, R> ThrowableFunction<T, R> wrap(Function<? super T, ? extends R> fn) {
        checkNotNull(fn, "fn");

        return t -> fn.apply(t);
    }
}
