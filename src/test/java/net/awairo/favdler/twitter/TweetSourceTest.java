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

import org.junit.Test;

/**
 * @author alalwww
 */
public class TweetSourceTest {

    String source;

    @Test
    public void testParseName() {
        source = "<a href=\"http://twitter.com\" rel=\"nofollow\">Twitter Web Client</a>";

        assertThat(TweetSource.parseName(source))
                .isEqualTo("Twitter Web Client");
    }

    @Test
    public void testParseUrl() {
        source = "<a href=\"http://twitter.com\" rel=\"nofollow\">Twitter Web Client</a>";

        assertThat(TweetSource.parseUrl(source))
                .isEqualTo("http://twitter.com");
    }

}
