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

    /**
     * 受け取った値がmin以下の場合minを返すマップ関数.
     *
     * @param min 下限値
     * @return マップ関数
     */
    public static <T extends Comparable<? super T>> Function<T, T> minLimit(T min) {
        checkNotNull(min, "b");

        return a -> Filters.isLesserThanOrEqual(min).test(a) ? a : min;
    }

    /**
     * 受け取った値がmax以上の場合maxを返すマップ関数.
     *
     * @param max 上限値
     * @return マップ関数
     */
    public static <T extends Comparable<? super T>> Function<T, T> maxLimit(T max) {
        checkNotNull(max, "b");

        return a -> Filters.isGreaterThanOrEqual(max).test(a) ? a : max;
    }

    /**
     * 受け取った値が制限値の範囲内でない場合は、境界値で丸めて返すマップ関数.
     *
     * @param min 下限値
     * @param max 上限値
     * @return マップ関数
     */
    public static <T extends Comparable<? super T>> Function<T, T> limit(T min, T max) {
        checkNotNull(min, "min");
        checkNotNull(max, "max");
        checkArgument(min.compareTo(max) < 0);

        return a -> maxLimit(max).andThen(minLimit(min)).apply(a);
    }

    /**
     * 値を検証し値を結果に応じた{@link Optional}でラップし返すマップ関数.
     *
     * @param validator 検証処理
     * @return マップ関数
     */
    public static <T> Function<T, Optional<T>> validate(Predicate<? super T> validator) {
        checkNotNull(validator, "validator");

        return tryParse(a -> validator.test(a) ? a : null);
    }

    /**
     * 値が下限値以上かを検証し返すマップ関数.
     *
     * @param min 下限値
     * @return マップ関数
     */
    public static <T extends Comparable<? super T>> Function<T, Optional<T>> validateMin(T min) {
        checkNotNull(min, "min");

        return validate(Filters.isGreaterThanOrEqual(min));
    }

    /**
     * 値が上限値以下かを検証し返すマップ関数.
     *
     * @param max 上限値
     * @return マップ関数
     */
    public static <T extends Comparable<? super T>> Function<T, Optional<T>> validateMax(T max) {
        checkNotNull(max, "max");

        return validate(Filters.isLesserThanOrEqual(max));
    }

    /**
     * 受け取った値が制限値の範囲内かを検証し返すマップ関数.
     *
     * @param min 下限値
     * @param max 上限値
     * @return マップ関数
     */
    public static <T extends Comparable<? super T>> Function<T, Optional<T>> validateLimit(T min, T max) {
        checkNotNull(min, "min");
        checkNotNull(max, "max");
        checkArgument(min.compareTo(max) < 0);

        return validate(Filters.isLesserThanOrEqual(max).and(Filters.isGreaterThanOrEqual(min)));
    }

    /**
     * 値をパースし結果を返すマップ関数.
     *
     * @param parser パース処理
     * @return マップ関数
     */
    public static <V, R> Function<V, Optional<R>> tryParse(Parser<? super V, ? extends R> parser) {
        checkNotNull(parser, "parser");

        return value -> {
            try {
                R ret = parser.parse(value);
                if (ret == null)
                    log.debug("parse failed.");
                return Optional.ofNullable(ret);
            } catch (ParseException | NumberFormatException e) {
                log.debug("parse failed.", e);
                return Optional.empty();
            }
        };
    }

    /**
     * パーサー.
     *
     * @author alalwww
     * @param <V> パース対象のタイプ
     * @param <R> パース結果のタイプ
     */
    @FunctionalInterface
    public interface Parser<V, R> {

        /**
         * 値をパースし返します.
         *
         * @param value パース対象値
         * @return パース結果 null を返却した場合パース失敗と同等の結果となる
         * @throws ParseException パースに失敗した場合
         * @throws NumberFormatException パースに失敗した場合
         */
        R parse(V value) throws ParseException, NumberFormatException;

        /**
         * この関数の前に実行する関数を合成します.
         *
         * @param before 前に実行する関数
         * @return 合成関数
         */
        default <T> Parser<T, R> compose(Parser<? super T, ? extends V> before) {
            checkNotNull(before, "before");

            return t -> parse(before.parse(t));
        }

        /**
         * この関数の後に実行する関数を合成します.
         *
         * @param after 後に実行する関数
         * @return 合成関数
         */
        default <T> Parser<V, T> andThen(Parser<? super R, ? extends T> after) {
            checkNotNull(after, "after");

            return t -> after.parse(parse(t));
        }

        /**
         * 値をそのまま返すだけの関数.
         *
         * @return 値をそのまま返すだけの関数
         */
        static <T> Parser<T, T> identity() {
            return t -> t;
        }

        /**
         * 常に検証が失敗する関数.
         *
         * @return 常に検証が失敗する関数
         */
        static <T> Parser<T, T> allwaysFail() {
            return t -> null;
        }

        /**
         * {@link Function}をラップします.
         *
         * @param fn ファンクション
         * @return ラップした関数
         */
        static <V, R> Parser<V, R> wrap(Function<V, R> fn) {
            checkNotNull(fn, "fn");

            return v -> fn.apply(v);
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
