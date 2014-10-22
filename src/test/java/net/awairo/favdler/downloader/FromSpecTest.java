/*
 * FavDownloader
 *
 * (c) 2014 alalwww
 * https://github.com/alalwww
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package net.awairo.favdler.downloader;

import static org.fest.assertions.api.Assertions.*;

import org.junit.Test;

import net.awairo.favdler.favlist.twitter.Tweet;

/**
 * @author alalwww
 */
public class FromSpecTest {

    static final Tweet[] tweets = new Tweet[] {};

    @Test
    public void testParseName() {
        assertThat(FromSpec.parseName("aaa.bbb/ccc/ddd.eee"))
                .isEqualTo("ddd.eee");
        assertThat(FromSpec.parseName("aaa.bbb.ccc.ddd.eee"))
                .isEqualTo("aaa.bbb.ccc.ddd.eee");
    }

}
