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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Before;
import org.junit.Test;
import twitter4j.RateLimitStatus;
import twitter4j.Status;

/**
 * @author alalwww
 */
public class FavoriteListItemsTest {

    @Mocked
    Status mock01;
    @Mocked
    Status mock02;
    @Mocked
    Status mock03;
    @Mocked
    Status mock04;
    @Mocked
    Status mock05;
    @Mocked
    Status mock06;
    @Mocked
    Status mock07;
    @Mocked
    Status mock08;
    @Mocked
    Status mock09;
    @Mocked
    Status mock10;

    twitter4j.ResponseList<Status> responseList;
    FavoriteListItems items;

    @Before
    public void setUp() {
        setupMocks();
    }

    @Test
    public void testLatestId() {
        assertThat(items.latestId())
                .isEqualTo(Optional.of(10L));
    }

    @Test
    public void testOldestId() {
        assertThat(items.oldestId())
                .isEqualTo(Optional.of(1L));
    }

    private void setupMocks() {
        new NonStrictExpectations() {
            {
                mock01.getId();
                result = 1L;
                mock02.getId();
                result = 2L;
                mock03.getId();
                result = 3L;
                mock04.getId();
                result = 4L;
                mock05.getId();
                result = 5L;
                mock06.getId();
                result = 6L;
                mock07.getId();
                result = 7L;
                mock08.getId();
                result = 8L;
                mock09.getId();
                result = 9L;
                mock10.getId();
                result = 10L;
            }
        };

        responseList = new ResponseListMock();
        responseList.add(mock01);
        responseList.add(mock02);
        responseList.add(mock03);
        responseList.add(mock04);
        responseList.add(mock05);
        responseList.add(mock06);
        responseList.add(mock07);
        responseList.add(mock08);
        responseList.add(mock09);
        responseList.add(mock10);
        Collections.shuffle(responseList);
        items = new FavoriteListItems(responseList);
    }

    static class ResponseListMock extends ArrayList<Status> implements twitter4j.ResponseList<Status> {

        @Override
        public int getAccessLevel() {
            return 0;
        }

        @Override
        public RateLimitStatus getRateLimitStatus() {
            return new RateLimitStatus() {

                @Override
                public int getRemaining() {
                    return 0;
                }

                @Override
                public int getLimit() {
                    return 0;
                }

                @Override
                public int getResetTimeInSeconds() {
                    return 0;
                }

                @Override
                public int getSecondsUntilReset() {
                    return 0;
                }
            };
        }

    }
}
