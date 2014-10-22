/*
 * FavDownloader
 *
 * (c) 2014 alalwww
 * https://github.com/alalwww
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package net.awairo.common.extension;

import static com.google.common.base.Preconditions.*;

import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * LombokのExtensionMethod用の拡張メソッド群.
 *
 * @author alalwww
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Extensions {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Objects {
        public static <T> T or(T $this, T nonNullValue) {
            return orElseGet($this, () -> nonNullValue);
        }

        public static <T> T orElseGet(T $this, Supplier<T> valueSupplier) {
            return $this != null ? $this : checkNotNull(valueSupplier.get());
        }

        public static boolean eq(Object $this, Object object) {
            return java.util.Objects.equals($this, object);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Options {
        public static <T> Optional<T> option(T $this) {
            return Optional.ofNullable($this);
        }

        public static <T> T orNull(Optional<T> $this) {
            return $this.orElse(null);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Strings {
        public static Integer toInt(String $this) {
            return Integer.valueOf($this);
        }

        public static Long toLong(String $this) {
            return Long.valueOf($this);
        }

        public static Float toFloat(String $this) {
            return Float.valueOf($this);
        }

        public static Double toDouble(String $this) {
            return Double.valueOf($this);
        }

        public static Optional<Integer> tryParseInt(String $this) {
            return $this != null ? Options.option(Ints.tryParse($this)) : Optional.empty();
        }

        public static Optional<Long> tryParseLong(String $this) {
            return $this != null ? Options.option(Longs.tryParse($this)) : Optional.empty();
        }

        public static Optional<Float> tryParseFloat(String $this) {
            return $this != null ? Options.option(Floats.tryParse($this)) : Optional.empty();
        }

        public static Optional<Double> tryParseDouble(String $this) {
            return $this != null ? Options.option(Doubles.tryParse($this)) : Optional.empty();
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Comparables {

        public static <T extends Comparable<? super T>> T max(T $this, T b) {
            return $this.compareTo(b) > 0 ? $this : b;
        }

        public static <T extends Comparable<? super T>> T min(T $this, T b) {
            return $this.compareTo(b) < 0 ? $this : b;
        }

        private static final String MSG = "min greater than max min=%s, max=%s";

        public static int limit(int $this, int min, int max) {
            checkArgument(min < max, MSG, min, max);
            return $this < min ? min : $this > max ? max : $this;
        }

        public static long limit(long $this, long min, long max) {
            checkArgument(min < max, MSG, min, max);
            return $this < min ? min : $this > max ? max : $this;
        }

        public static float limit(float $this, float min, float max) {
            checkArgument(min < max, MSG, min, max);
            return $this < min ? min : $this > max ? max : $this;
        }

        public static double limit(double $this, double min, double max) {
            checkArgument(min < max, MSG, min, max);
            return $this < min ? min : $this > max ? max : $this;
        }

        public static <T extends Comparable<? super T>> T limit(T $this, T min, T max) {
            checkArgument(min.compareTo(max) < 0, MSG, min, max);
            return $this.compareTo(min) < 0 ? min : $this.compareTo(max) > 0 ? max : $this;
        }

        public static int maxLimit(int $this, int limit) {
            return Math.min($this, limit);
        }

        public static long maxLimit(long $this, long limit) {
            return Math.min($this, limit);
        }

        public static float maxLimit(float $this, float limit) {
            return Math.min($this, limit);
        }

        public static double maxLimit(double $this, double limit) {
            return Math.min($this, limit);
        }

        public static <T extends Comparable<? super T>> T maxLimit(T $this, T limit) {
            return min($this, limit);
        }

        public static int minLimit(int $this, int limit) {
            return Math.max($this, limit);
        }

        public static long minLimit(long $this, long limit) {
            return Math.max($this, limit);
        }

        public static float minLimit(float $this, float limit) {
            return Math.max($this, limit);
        }

        public static double minLimit(double $this, double limit) {
            return Math.max($this, limit);
        }

        public static <T extends Comparable<? super T>> T minLimit(T $this, T limit) {
            return max($this, limit);
        }

        //        @SuppressWarnings("unchecked")
        //        public static <N extends Comparable<? super N>> N max(N a, N... values) {
        //            N b = max(a, Collections.max(Arrays.asList(values)));
        //            return a.compareTo(b) > 0 ? a : b;
        //        }
        //
        //        @SuppressWarnings("unchecked")
        //        public static <N extends Comparable<? super N>> N min(N a, N... values) {
        //            N b = min(a, Collections.min(Arrays.asList(values)));
        //            return a.compareTo(b) < 0 ? a : b;
        //        }
    }
}
