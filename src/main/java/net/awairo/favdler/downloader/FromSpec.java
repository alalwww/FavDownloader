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

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.experimental.Accessors;

import net.awairo.common.exception.AppException;
import net.awairo.favdler.favlist.twitter.Tweet;

/**
 * ダウンロード元.
 *
 * @author alalwww
 */
@Accessors(fluent = true)
public final class FromSpec {

    /**
     * 指定したツイートからダウンロード元の一覧を生成します.
     *
     * @param tweets ツイート
     * @return ダウンロード元一覧
     */
    public static ImmutableList<FromSpec> of(Tweet... tweets) {
        checkNotNull(tweets, "tweets requires non null");
        checkArgument(tweets.length > 0);

        return copyOf(Arrays.asList(tweets));
    }

    /**
     * 指定したツイートからダウンロード元の一覧を生成します.
     *
     * @param tweets ツイート一覧
     * @return ダウンロード元一覧
     */
    public static ImmutableList<FromSpec> copyOf(List<Tweet> tweets) {
        checkNotNull(tweets, "tweets");
        checkArgument(!tweets.isEmpty());

        return ImmutableList.copyOf(toFromSpecList(tweets));
    }

    private static List<FromSpec> toFromSpecList(List<Tweet> tweets) {
        return tweets.stream()
                .filter(Tweet::hasMedia)
                .flatMap(FromSpec::toFromSpecs)
                .collect(Collectors.toList());
    }

    private static Stream<FromSpec> toFromSpecs(Tweet tweet) {
        if (!tweet.hasMedia())
            return Stream.empty();

        long id = tweet.getId();
        String sn = tweet.getUser().getScreenName();
        String json = tweet.toJson();

        List<FromSpec> ret = Lists.newArrayList();
        tweet.extendedMediaEntitiesStream().forEach(m -> {
            String url = m.getMediaURL();
            ret.add(new FromSpec(id, sn, url, json));
        });

        return ret.stream();
    }

    private static final Pattern PATTERN_OF_FILENAME_FROM_HEADER = Pattern.compile(".*filename=\"([^\"]+).*\"");

    @Getter
    private final long statusId;
    @Getter
    private final String screenName;
    @Getter
    private final String mediaUrlSpec;
    @Getter
    private final String tweetJson;

    private final URL url;
    private final String nameFromUrl;

    private URLConnection connection;

    /* package */FromSpec(long statusId, String screenName, String mediaUrlSpec, String tweetJson) {
        this.statusId = statusId;
        this.screenName = checkNotNull(screenName, "screenName");
        this.mediaUrlSpec = checkNotNull(mediaUrlSpec, "mediaUrlString");
        this.tweetJson = checkNotNull(tweetJson, "tweetJson");

        try {
            this.url = new URL(mediaUrlSpec);
        } catch (MalformedURLException e) {
            throw new AppException(e, ""); // TODO: メッセージ
        }

        nameFromUrl = parseName(mediaUrlSpec);
    }

    public URLConnection openConnection() {

        try {
            connection = url.openConnection();
        } catch (IOException e) {
            throw new AppException(e);
        }

        return connection;
    }

    public String name() {
        return Optional.ofNullable(connection)
                .map(c -> c.getHeaderField("Content-Disposition"))
                .map(s -> PATTERN_OF_FILENAME_FROM_HEADER.matcher(s).replaceAll("$1"))
                .orElse(nameFromUrl);
    }

    @VisibleForTesting
    static String parseName(String urlSpec) {
        return urlSpec.substring(urlSpec.lastIndexOf('/') + 1, urlSpec.length());
    }

    @Override
    public String toString() {
        return url.toString();
    }
}
