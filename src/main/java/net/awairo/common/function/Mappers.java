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

import java.text.ParseException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.function.LongUnaryOperator;
import java.util.function.Predicate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * マップ関数.
 *
 * @author alalwww
 */
@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Mappers {

    public static <T extends Comparable<? super T>> Function<T, T> max(T b) {
        checkNotNull(b, "b");

        return a -> a.compareTo(b) < 0 ? a : b;
    }

    public static <T extends Comparable<? super T>> Function<T, T> max(Collection<? extends T> bs) {
        checkNotNull(bs, "bs is null");
        checkArgument(!bs.isEmpty(), "bs is empty");

        return a -> {
            return bs.stream()
                    .min(Comparator.<T> naturalOrder()) // 型を明示しないとjavacがこける(Eclipseのコンパイラだとエラーにならない)
                    .map(max(a))
                    .orElse(a);
        };
    }

    public static <T extends Comparable<? super T>> Function<T, T> min(T b) {
        checkNotNull(b, "b");

        return a -> a.compareTo(checkNotNull(b)) > 0 ? a : b;
    }

    public static <T extends Comparable<? super T>> Function<T, T> min(Collection<? extends T> bs) {
        checkNotNull(bs, "bs is null");
        checkArgument(!bs.isEmpty(), "bs is empty");

        return a -> {
            return bs.stream()
                    .min(Comparator.<T> naturalOrder()) // 型を明示しないとjavacがこける(Eclipseのコンパイラだとエラーにならない)
                    .map(min(a))
                    .orElse(a);
        };
    }

    public static <T extends Comparable<? super T>> Function<T, T> maxLimit(T max) {
        checkNotNull(max, "max");

        return a -> min(max).apply(a);
    }

    public static <T extends Comparable<? super T>> Function<T, T> minLimit(T min) {
        checkNotNull(min, "min");

        return a -> max(min).apply(a);
    }

    public static <T> Function<T, T> silentValidate(Predicate<? super T> validator, T defaultValue) {
        return a -> validator.test(a) ? a : defaultValue;
    }

    public static <T extends Comparable<? super T>> Function<T, T> tryMaxLimit(T max, T defaultValue) {
        return a -> a.compareTo(max) <= 0 ? a : defaultValue;
    }

    public static <T extends Comparable<? super T>> Function<T, T> tryMinLimit(T min, T defaultValue) {
        return a -> a.compareTo(min) >= 0 ? a : defaultValue;
    }

    public static <T extends Comparable<? super T>> Function<T, T> limit(T min, T max) {
        checkArgument(min.compareTo(max) < 0);
        return a -> maxLimit(max).andThen(minLimit(min)).apply(a);
    }

    public static <V, R> Function<V, Optional<R>> tryParse(Parser<? super V, ? extends R> parser) {
        return value -> {
            try {
                return Optional.ofNullable(parser.parse(value));
            } catch (ParseException | NumberFormatException e) {
                log.debug("parse failed.", e);
                return Optional.empty();
            }
        };
    }

    @FunctionalInterface
    public interface Parser<V, R> {
        R parse(V value) throws ParseException, NumberFormatException;

        default <T> Parser<T, R> compose(Parser<? super T, ? extends V> before) {
            checkNotNull(before, "before");
            return t -> parse(before.parse(t));
        }

        default <T> Parser<V, T> andThen(Parser<? super R, ? extends T> after) {
            checkNotNull(after, "after");
            return t -> after.parse(parse(t));
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Primitives {

        public static IntUnaryOperator max(int b) {
            return a -> Math.max(a, b);
        }

        public static LongUnaryOperator max(long b) {
            return a -> Math.max(a, b);
        }

        public static DoubleUnaryOperator max(double b) {
            return a -> Math.max(a, b);
        }

        public static IntUnaryOperator min(int b) {
            return a -> Math.min(a, b);
        }

        public static LongUnaryOperator min(long b) {
            return a -> Math.min(a, b);
        }

        public static DoubleUnaryOperator min(double b) {
            return a -> Math.min(a, b);
        }

        public static IntUnaryOperator maxLimit(int max) {
            return a -> min(max).applyAsInt(a);
        }

        public static LongUnaryOperator maxLimit(long max) {
            return a -> min(max).applyAsLong(a);
        }

        public static DoubleUnaryOperator maxLimit(double max) {
            return a -> min(max).applyAsDouble(a);
        }

        public static IntUnaryOperator minLimit(int min) {
            return a -> max(min).applyAsInt(a);
        }

        public static LongUnaryOperator minLimit(long min) {
            return a -> max(min).applyAsLong(a);
        }

        public static DoubleUnaryOperator minLimit(double min) {
            return a -> max(min).applyAsDouble(a);
        }

        public static IntUnaryOperator limit(int min, int max) {
            checkArgument(min < max);
            return a -> minLimit(min).andThen(maxLimit(max)).applyAsInt(a);
        }

        public static LongUnaryOperator limit(long min, long max) {
            checkArgument(min < max);
            return a -> minLimit(min).andThen(maxLimit(max)).applyAsLong(a);
        }

        public static DoubleUnaryOperator limit(double min, double max) {
            checkArgument(min < max);
            return a -> minLimit(min).andThen(maxLimit(max)).applyAsDouble(a);
        }
    }
}
