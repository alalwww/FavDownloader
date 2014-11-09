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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;

/**
 * お気に入り一覧のアイテムリスト.
 *
 * @author alalwww
 */
@Log4j2
@Getter
@Accessors(fluent = true)
public final class FavoriteListItems {

    private final ObservableList<Tweet> list;
    /** 残数. */
    private int remaining;
    /** API上限. */
    private int limit;
    /** リセットされる時間. */
    private int resetTimeInSeconds;
    /** リセットまでの秒数. */
    private int secondsUntilReset;

    /**
     * Constructor.
     *
     * @param responseList 返却リスト
     */
    public FavoriteListItems(ResponseList<Status> responseList) {
        this(responseList, responseList.getRateLimitStatus());
    }

    /**
     * Constructor.
     *
     * @param rateLimitStatus API残数
     */
    public FavoriteListItems(RateLimitStatus rateLimitStatus) {
        this(Collections.emptyList(), rateLimitStatus);
    }

    private FavoriteListItems(List<Status> list, RateLimitStatus rateLimitStatus) {

        //ソート済みである前提で再ソートはしない
        this.list = FXCollections.observableArrayList(list.stream()
                .map(Tweet::new)
                .collect(Collectors.toList()));

        updateRateLimitStatus(rateLimitStatus);

        log.debug("お気に入り取得{}件 API残数{}/{} あと{}秒", list.size(), remaining, limit, secondsUntilReset);
    }

    private void updateRateLimitStatus(RateLimitStatus status) {
        limit = status.getLimit();
        remaining = status.getRemaining();
        resetTimeInSeconds = status.getResetTimeInSeconds();
        secondsUntilReset = status.getSecondsUntilReset();
    }

    /**
     * @param result 現在のアイテムに追加し更新するためのお気に入り一覧
     */
    public void update(ResponseList<Status> responseList) {

        list.setAll(Stream.concat(list.stream(), responseList.stream().map(Tweet::new))
                .sorted(Comparator.comparingLong(Tweet::getId).reversed())
                .collect(Collectors.toList()));

        updateRateLimitStatus(responseList.getRateLimitStatus());

        log.debug("お気に入り追加{}件 API残数{}/{} あと{}秒", responseList.size(), remaining, limit, secondsUntilReset);
    }

    /**
     * @return 最新ツイートのID
     */
    public Optional<Long> latestId() {
        return list.stream()
                .map(Tweet::getId)
                .max(Long::compare);
    }

    /**
     * @return 一番古いツイートのID
     */
    public Optional<Long> oldestId() {
        return list.stream()
                .map(Tweet::getId)
                .min(Long::compare);
    }

}
