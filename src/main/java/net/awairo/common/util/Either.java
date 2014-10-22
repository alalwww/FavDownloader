/*
 * FavDownloader
 *
 * (c) 2014 alalwww
 * https://github.com/alalwww
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package net.awairo.common.util;

import static com.google.common.base.Preconditions.*;

import java.util.function.Consumer;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * 2つの値のいずれかを持つオブジェクト.
 *
 * @author alalwww
 * @param <L> 左のタイプ(不正値)
 * @param <R> 右のタイプ(正常値)
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = { "left", "right" })
public final class Either<L, R> {

    /**
     * 成功値を持つインスタンスを生成.
     *
     * @param value 成功値
     * @return 新しいEither
     *
     * @see #ofRight(Object)
     */
    public static <L, R> Either<L, R> success(R value) {
        return ofRight(value);
    }

    /**
     * 失敗値を持つインスタンスを生成.
     *
     * @param value 成功値
     * @return 新しいEither
     *
     * @see #ofLeft(Object)
     */
    public static <L, R> Either<L, R> fail(L value) {
        return ofLeft(value);
    }

    /**
     * 左の値を持つインスタンスを生成.
     *
     * @param value 成功値
     * @return 新しいEither
     */
    public static <L, R> Either<L, R> ofLeft(L value) {
        return new Either<>(checkNotNull(value), null);
    }

    /**
     * 右の値を持つインスタンスを生成.
     *
     * @param value 成功値
     * @return 新しいEither
     */
    public static <L, R> Either<L, R> ofRight(R value) {
        return new Either<>(null, checkNotNull(value));
    }

    // --------------------------------------------------------

    private final L left;
    private final R right;

    /**
     * @return true は成功
     */
    public boolean succeeded() {
        return hasRight();
    }

    /**
     * @return true は失敗
     */
    public boolean failed() {
        return hasLeft();
    }

    /**
     * @return true は左の値を持つ
     */
    public boolean hasLeft() {
        return left != null;
    }

    /**
     * @return true は右の値を持つ
     */
    public boolean hasRight() {
        return right != null;
    }

    /**
     * 左の値を取得.
     *
     * @return 左の値
     * @throws UnsupportedOperationException 左の値を持たない場合
     */
    public L getLeft() throws UnsupportedOperationException {
        if (hasLeft())
            return left;
        throw new UnsupportedOperationException("left is not presented.");
    }

    /**
     * 右の値を取得.
     *
     * @return 右の値
     * @throws UnsupportedOperationException 右の値を持たない場合
     */
    public R getRight() {
        if (hasRight())
            return right;
        throw new UnsupportedOperationException("right is not presented.");
    }

    /**
     * 成功している場合に引数の関数を実行する.
     *
     * @param consumer 関数
     * @return このインスタンス
     */
    public Either<L, R> ifSucceeded(Consumer<? super R> consumer) {
        if (succeeded())
            consumer.accept(right);
        return this;
    }

    /**
     * 失敗している場合に引数の関数を実行する.
     *
     * @param consumer 関数
     * @return このインスタンス
     */
    public Either<L, R> ifFailed(Consumer<? super L> consumer) {
        if (failed())
            consumer.accept(left);
        return this;
    }

    @Override
    public String toString() {
        return String.format("Either{%s=%s}",
                hasLeft()
                        ? new Object[] { "left(fail)", left }
                        : new Object[] { "right(success)", right });
    }
}
