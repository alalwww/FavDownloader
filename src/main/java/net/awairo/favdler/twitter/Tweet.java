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

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import lombok.experimental.Delegate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.RequiredArgsConstructor;
import twitter4j.MediaEntity;
import twitter4j.Status;

import net.awairo.common.exception.AppException;

/**
 * {@link twitter4j.Status}のラッパー.
 *
 * @author alalwww
 */
@RequiredArgsConstructor
public final class Tweet implements Status {
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    @Delegate(types = Status.class)
    private final Status status;

    private String json;

    public String toJson() {
        if (json == null) {
            try {
                json = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(status);
            } catch (JsonProcessingException e) {
                throw new AppException(e);
            }
        }

        return json;
    }

    private TweetSource tweetSource;
    private String urlFormattedText;

    public TweetSource getTweetSource() {
        if (tweetSource == null)
            tweetSource = new TweetSource(status.getSource());

        return tweetSource;
    }

    private TweetCreatedAt tweetCreatedAt;

    public TweetCreatedAt getTweetCreatedAt() {
        if (tweetCreatedAt == null)
            tweetCreatedAt = new TweetCreatedAt(status.getCreatedAt());

        return tweetCreatedAt;
    }

    private BooleanProperty checkboxSelected = new SimpleBooleanProperty(false);

    /**
     * @return チェックボックスの選択状態を表すプロパティ
     */
    public BooleanProperty checkboxSelected() {
        return checkboxSelected;
    }

    /**
     * @return true は拡張メディア一覧を持っている
     */
    public boolean hasMedia() {
        return getExtendedMediaEntities() != null && getExtendedMediaEntities().length > 0;
    }

    /**
     * @return メディア一覧のストリーム
     */
    public Stream<MediaEntity> extendedMediaEntitiesStream() {
        return Arrays.stream(getExtendedMediaEntities());
    }

    /**
     * @return 本文中のURLを[]で括った文字列を取得
     */
    public String getUrlFormattedText() {

        // 遅延評価
        if (urlFormattedText != null)
            return urlFormattedText;

        urlFormattedText = format(status.getText());
        return urlFormattedText;
    }

    private static Pattern URL_PATTERN = Pattern.compile("(h?ttps?://[\\w/:%#\\$&\\?\\(\\)~\\.=\\+\\-]+)");

    private static String format(String text) {
        return URL_PATTERN.matcher(text).replaceAll("[$1]");
    }
}
