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

import java.util.function.Predicate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * フィルター関数.
 *
 * @author alalwww
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Filters {

    /**
     * 渡された値がotherより大きい場合にtrueを返す関数.
     *
     * @param other 比較対象
     * @return Predicate
     */
    public static <T extends Comparable<? super T>> Predicate<T> isGreaterThan(T other) {
        return a -> a.compareTo(other) > 0;
    }

    /**
     * 渡された値がotherより大きい場合にtrueを返す関数.
     *
     * @param other 比較対象 (null値を比較できるかはTのComparableの実装による)
     * @return Predicate
     */
    public static <T extends Comparable<? super T>> Predicate<T> gt(T other) {
        return isGreaterThan(other);
    }

    /**
     * 渡された値がotherより大きい場合にtrueを返す関数.
     *
     * @param other 比較対象 (null値を比較できるかはTのComparableの実装による)
     * @return Predicate
     */
    public static <T extends Comparable<? super T>> Predicate<T> isGreaterThanOrEqual(T other) {
        return a -> a.compareTo(other) >= 0;
    }

    /**
     * 渡された値がotherより大きい場合にtrueを返す関数.
     *
     * @param other 比較対象 (null値を比較できるかはTのComparableの実装による)
     * @return Predicate
     */
    public static <T extends Comparable<? super T>> Predicate<T> ge(T other) {
        return isGreaterThanOrEqual(other);
    }

    /**
     * 渡された値がotherより大きい場合にtrueを返す関数.
     *
     * @param other 比較対象 (null値を比較できるかはTのComparableの実装による)
     * @return Predicate
     */
    public static <T extends Comparable<? super T>> Predicate<T> isLesserThan(T other) {
        return a -> a.compareTo(other) < 0;
    }

    /**
     * 渡された値がotherより大きい場合にtrueを返す関数.
     *
     * @param other 比較対象 (null値を比較できるかはTのComparableの実装による)
     * @return Predicate
     */
    public static <T extends Comparable<? super T>> Predicate<T> lt(T other) {
        return isLesserThan(other);
    }

    /**
     * 渡された値がotherより小さいか等しい場合にtrueを返す関数.
     *
     * @param other 比較対象 (null値を比較できるかはTのComparableの実装による)
     * @return Predicate
     */
    public static <T extends Comparable<? super T>> Predicate<T> isLesserThanOrEqual(T other) {
        return a -> a.compareTo(other) <= 0;
    }

    /**
     * 渡された値がotherより小さいか等しい場合にtrueを返す関数.
     *
     * @param other 比較対象 (null値を比較できるかはTのComparableの実装による)
     * @return Predicate
     */
    public static <T extends Comparable<? super T>> Predicate<T> le(T other) {
        return isLesserThanOrEqual(other);
    }
}
