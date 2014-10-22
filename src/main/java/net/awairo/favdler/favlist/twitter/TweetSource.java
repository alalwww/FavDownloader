/*
 * FavDownloader
 *
 * (c) 2014 alalwww
 * https://github.com/alalwww
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package net.awairo.favdler.favlist.twitter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.annotations.VisibleForTesting;
import lombok.Getter;

/**
 * via.
 *
 * <p>ツイートソースのHTMLをパースしURLとソース(クライアントアプリ名)に分割する</p>
 *
 * @author alalwww
 */
public final class TweetSource {

    private static final Pattern PATTERN = Pattern.compile("^[^\"]+\"(http[^\"]+)[^>]+>(.+)(</a>)$");

    private final String source;
    @Getter
    private final String name;
    @Getter
    private final String url;

    public TweetSource(String source) {
        this.source = source;
        name = parseName(source);
        url = parseUrl(source);
    }

    @VisibleForTesting
    static String parseName(String source) {
        assert source != null;
        Matcher m = PATTERN.matcher(source);
        return m.matches() ? m.replaceAll("$2") : "unknown";
    }

    @VisibleForTesting
    static String parseUrl(String source) {
        assert source != null;
        Matcher m = PATTERN.matcher(source);
        return m.matches() ? m.replaceAll("$1") : "unknown";
    }

    @Override
    public String toString() {
        return String.format("TweetSource@%s:{%s}", hashCode(), source);
    }
}
