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

import static org.fest.assertions.api.Assertions.*;
import static org.junit.Assert.*;

import java.util.Optional;
import java.util.function.Supplier;

import lombok.experimental.ExtensionMethod;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

/**
 * @author alalwww
 */
@RunWith(Enclosed.class)
public class ExtensionsTest {

    @ExtensionMethod(Extensions.Objects.class)
    public static class ObjectsTest {

        @Test
        public void testOrGet() {
            String nullableString = null;
            assertThat(nullableString.or("test")).isEqualTo("test");

            Integer nullableInt = null;
            assertThat(nullableInt.or(100)).isEqualTo(100);
        }

        @Test
        public void testOrElseGet() {
            String nonNullList = "";
            Supplier<String> getter = () -> {
                fail();
                return "";
            };
            nonNullList.orElseGet(getter);
        }
    }

    @ExtensionMethod(Extensions.Options.class)
    public static class OptionsTest {
        @Test
        public void testOption() {
            String nullValue = null;
            assertThat(nullValue.option()).isEqualTo(Optional.empty());
        }

        @Test
        public void testOrNull() {
            String nullValue = null;
            assertThat(nullValue.option().orNull()).isNull();
        }

    }

    @ExtensionMethod(Extensions.Strings.class)
    public static class StringsTest {
        @Test
        public void testToNumber() {

            String value = "100";
            assertThat(value.toInt()).isEqualTo(100);
            assertThat(value.toLong()).isEqualTo(100L);

            value = "111.11";
            assertThat(value.toFloat()).isEqualTo(111.11F);
            assertThat(value.toDouble()).isEqualTo(111.11D);
        }

        @Test
        public void testTryParse() {
            String value = "100";
            assertThat(value.tryParseInt()).isEqualTo(Optional.of(100));
            assertThat(value.tryParseLong()).isEqualTo(Optional.of(100L));
            assertThat(value.tryParseFloat()).isEqualTo(Optional.of(100F));
            assertThat(value.tryParseDouble()).isEqualTo(Optional.of(100D));

            value = "111.11";
            assertThat(value.tryParseInt()).isEqualTo(Optional.empty());
            assertThat(value.tryParseLong()).isEqualTo(Optional.empty());
            assertThat(value.tryParseFloat()).isEqualTo(Optional.of(111.11F));
            assertThat(value.tryParseDouble()).isEqualTo(Optional.of(111.11D));

            value = "111.11a";
            assertThat(value.tryParseInt()).isEqualTo(Optional.empty());
            assertThat(value.tryParseLong()).isEqualTo(Optional.empty());
            assertThat(value.tryParseFloat()).isEqualTo(Optional.empty());
            assertThat(value.tryParseDouble()).isEqualTo(Optional.empty());
        }
    }

    @ExtensionMethod(Extensions.Comparables.class)
    public static class ComparablesTest {

        @Test
        public void testLimitInt() {
            int value = 10;
            int min = 15;
            int max = 25;
            assertThat(value.limit(min, max)).isEqualTo(min);
            value += 10;
            assertThat(value.limit(min, max)).isEqualTo(value);
            value += 10;
            assertThat(value.limit(min, max)).isEqualTo(max);
        }

        @Test
        public void testLimitLong() {
            long value = 10L + Integer.MAX_VALUE;
            long min = 15L + Integer.MAX_VALUE;
            long max = 25L + Integer.MAX_VALUE;
            assertThat(value.limit(min, max)).isEqualTo(min);
            value += 10L;
            assertThat(value.limit(min, max)).isEqualTo(value);
            value += 10L;
            assertThat(value.limit(min, max)).isEqualTo(max);
        }

        @Test
        public void testLimitFloat() {
            float value = 10.5F;
            float min = 15.5F;
            float max = 25.5F;
            assertThat(value.limit(min, max)).isEqualTo(min);
            value += 10.5F;
            assertThat(value.limit(min, max)).isEqualTo(value);
            value += 10.5F;
            assertThat(value.limit(min, max)).isEqualTo(max);
        }

        @Test
        public void testLimitDouble() {
            double value = 10.5D + Integer.MAX_VALUE;
            double min = 15.5D + Integer.MAX_VALUE;
            double max = 25.5D + Integer.MAX_VALUE;
            assertThat(value.limit(min, max)).isEqualTo(min);
            value += 10.5D;
            assertThat(value.limit(min, max)).isEqualTo(value);
            value += 10.5D;
            assertThat(value.limit(min, max)).isEqualTo(max);
        }

        @Test
        public void testMaxInt() {
            int value = 10;
            int max = 11;
            assertThat(value.maxLimit(max)).isEqualTo(value);
            value += 1;
            assertThat(value.maxLimit(max)).isEqualTo(value);
            value += 1;
            assertThat(value.maxLimit(max)).isEqualTo(max);
        }
    }
}
