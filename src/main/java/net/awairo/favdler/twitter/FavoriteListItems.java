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

import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
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

    private final ObservableList<Tweet> list = FXCollections.observableArrayList();
    private int limit;
    private int remaining;
    private int resetTimeInSeconds;
    private int secondsUntilReset;

    public FavoriteListItems(twitter4j.ResponseList<Status> responseList) {
        log.trace("new　instance from: {}", responseList);

        list.addAll(responseList.parallelStream().map(Tweet::new).collect(Collectors.toList()));
        limit = responseList.getRateLimitStatus().getLimit();
        remaining = responseList.getRateLimitStatus().getRemaining();
        resetTimeInSeconds = responseList.getRateLimitStatus().getResetTimeInSeconds();
        secondsUntilReset = responseList.getRateLimitStatus().getSecondsUntilReset();
    }

    /**
     * @param result 現在のアイテムに追加し更新するためのお気に入り一覧
     */
    public void update(FavoriteListItems result) {
        list.addAll(result.list);

        limit = result.limit;
        remaining = result.remaining;
        resetTimeInSeconds = result.resetTimeInSeconds;
        secondsUntilReset = result.secondsUntilReset;
    }

}
