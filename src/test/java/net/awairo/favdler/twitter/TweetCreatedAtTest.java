/*
 * FavDownloader
 *
 * (c) 2014 alalwww
 * https://github.com/alalwww
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package net.awairo.favdler.twitter;

import static org.fest.assertions.api.Assertions.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import mockit.NonStrictExpectations;
import org.junit.Before;
import org.junit.Test;

/**
 * @author alalwww
 */
public class TweetCreatedAtTest {

    static final String pattern = "yyyy/MM/dd HH:mm:ss";
    static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
    static final ZonedDateTime now = ZonedDateTime.of(2014, 6, 15, 12, 10, 10, 1230000, ZoneId.systemDefault());

    final SimpleDateFormat sdf = new SimpleDateFormat(pattern);

    @Before
    public void setUp() {
        new NonStrictExpectations(ZonedDateTime.class) {
            {
                ZonedDateTime.now();
                result = now;
            }
        };
    }

    @Test
    public void testGet() throws ParseException {

        String expected = "2014/01/23 01:23:45";

        assertThat(createdAtOf(expected).get().format(dtf))
                .isEqualTo(expected);
    }

    @Test
    public void testToLabelString() throws ParseException {

        assertThat(createdAtOf("2014/06/15 12:10:10").toLabelString())
                .isEqualTo("現在");
        assertThat(createdAtOf("2014/06/15 12:10:06").toLabelString())
                .isEqualTo("現在");

        assertThat(createdAtOf("2014/06/15 12:10:05").toLabelString())
                .isEqualTo("5 秒");
        assertThat(createdAtOf("2014/06/15 12:09:11").toLabelString())
                .isEqualTo("59 秒");

        assertThat(createdAtOf("2014/06/15 12:09:10").toLabelString())
                .isEqualTo("1 分");
        assertThat(createdAtOf("2014/06/15 11:10:11").toLabelString())
                .isEqualTo("59 分");
        assertThat(createdAtOf("2014/06/15 11:10:10").toLabelString())
                .isEqualTo("11:10");

        assertThat(createdAtOf("2014/06/15 00:00:00").toLabelString())
                .isEqualTo("00:00");
        assertThat(createdAtOf("2014/06/14 23:59:59").toLabelString())
                .isEqualTo("6月14日");

        assertThat(createdAtOf("2014/01/01 00:00:00").toLabelString())
                .isEqualTo("1月1日");
        assertThat(createdAtOf("2013/12/31 23:59:59").toLabelString())
                .isEqualTo("2013年12月31日");
    }

    @Test
    public void testToTooltipString() throws ParseException {
        assertThat(createdAtOf("2014/06/15 12:10:10").toTooltipString())
                .isEqualTo("2014年6月15日 - 12:10:10");
        assertThat(createdAtOf("2014/06/15 12:10:06").toTooltipString())
                .isEqualTo("2014年6月15日 - 12:10:06");

        assertThat(createdAtOf("2014/06/15 12:10:05").toTooltipString())
                .isEqualTo("2014年6月15日 - 12:10:05");
        assertThat(createdAtOf("2014/06/15 12:09:11").toTooltipString())
                .isEqualTo("2014年6月15日 - 12:09:11");

        assertThat(createdAtOf("2014/06/15 12:09:10").toTooltipString())
                .isEqualTo("2014年6月15日 - 12:09:10");
        assertThat(createdAtOf("2014/06/15 11:10:11").toTooltipString())
                .isEqualTo("2014年6月15日 - 11:10:11");
        assertThat(createdAtOf("2014/06/15 11:10:10").toTooltipString())
                .isEqualTo("2014年6月15日 - 11:10:10");

        assertThat(createdAtOf("2014/06/15 00:00:00").toTooltipString())
                .isEqualTo("2014年6月15日 - 00:00:00");
        assertThat(createdAtOf("2014/06/14 23:59:59").toTooltipString())
                .isEqualTo("2014年6月14日 - 23:59:59");

        assertThat(createdAtOf("2014/01/01 00:00:00").toTooltipString())
                .isEqualTo("2014年1月1日 - 00:00:00");
        assertThat(createdAtOf("2013/12/31 23:59:59").toTooltipString())
                .isEqualTo("2013年12月31日 - 23:59:59");
    }

    private TweetCreatedAt createdAtOf(String value) throws ParseException {
        return new TweetCreatedAt(sdf.parse(value));
    }
}
