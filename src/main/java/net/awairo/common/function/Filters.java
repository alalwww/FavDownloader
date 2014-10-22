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

    public static <T extends Comparable<? super T>> Predicate<T> gt(T other) {
        return isGreaterThan(other);
    }

    public static <T extends Comparable<? super T>> Predicate<T> ge(T other) {
        return isGreaterThanOrEqual(other);
    }

    public static <T extends Comparable<? super T>> Predicate<T> lt(T other) {
        return isLesserThan(other);
    }

    public static <T extends Comparable<? super T>> Predicate<T> le(T other) {
        return isLesserThanOrEqual(other);
    }

    public static <T extends Comparable<? super T>> Predicate<T> isGreaterThan(T other) {
        return a -> a.compareTo(other) > 0;
    }

    public static <T extends Comparable<? super T>> Predicate<T> isGreaterThanOrEqual(T other) {
        return a -> a.compareTo(other) >= 0;
    }

    public static <T extends Comparable<? super T>> Predicate<T> isLesserThan(T other) {
        return a -> a.compareTo(other) < 0;
    }

    public static <T extends Comparable<? super T>> Predicate<T> isLesserThanOrEqual(T other) {
        return a -> a.compareTo(other) <= 0;
    }

}
