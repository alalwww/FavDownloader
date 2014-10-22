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

import java.util.function.Supplier;

/**
 * 例外をスロー可能な{@link java.util.function.Supplier}.
 *
 * @author alalwww
 *
 * @param <R> 戻り値のタイプ
 */
public interface ThrowableSupplier<R> {

    /**
     * 値を取得します.
     *
     * @return 結果
     * @throws Exception 継続不可能な問題が発生した場合
     */
    R get() throws Exception;

    /**
     * この関数の結果を受け別の値を返す関数を合成します.
     *
     * @param mapper 合成するマッパー関数
     * @return 合成した関数
     */
    default <R2> ThrowableSupplier<R2> map(ThrowableFunction<? super R, ? extends R2> mapper) {
        return () -> mapper.apply(get());
    }

    /**
     * {@link java.util.function.Supplier}をラップします.
     *
     * @param supplier ラップする{@link java.util.function.Supplier}
     * @return ラップした{@link ThrowableSupplier}
     */
    static <R> ThrowableSupplier<R> wrap(Supplier<? extends R> supplier) {
        return () -> supplier.get();
    }
}
