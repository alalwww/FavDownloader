/*
 * FavDownloader
 *
 * (c) 2014 alalwww
 * https://github.com/alalwww
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package net.awairo.favdler.favlist;

import static com.google.common.base.Preconditions.*;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import javafx.concurrent.Task;
import lombok.val;
import lombok.extern.log4j.Log4j2;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import net.awairo.common.javafx.MyTask;
import net.awairo.common.javafx.ServiceBase;
import net.awairo.common.util.RB;
import net.awairo.favdler.twitter.FavoriteListItems;
import net.awairo.favdler.twitter.TwitterAccessor;

/**
 * お気に入りリスト取得サービス.
 *
 * @author alalwww
 */
@Log4j2
final class FavoriteListService extends ServiceBase<FavoriteListItems, FavoriteListService> {

    // TODO: なんかもうちょっとマシな場所に定義する
    private static final int RATE_LIMIT_EXCEEDED = 88;

    private final AtomicReference<FindType> state = new AtomicReference<>(FindType.RESET);
    private final Paging paging = new Paging().count(200);

    enum FindType {
        /** 現在の取得ツイートを破棄して再取得. */
        RESET,
        /** 取得済みツイートより新しい物を取得. */
        GET_LATESTS,
        /** より古いツイートの取得を継続する. */
        CONTINUE
    }

    enum TaskState {
        /** Twitterからお気に入りを取得中. */
        GET_FAVOLITES("empty", "load_favorite_list"),
        /** お気に入り一覧データを更新中. */
        UPDATE_VALUE("empty", "update_favorite_list"),
        /** 完了. */
        FINISH("empty", "empty");

        static final int SIZE = values().length;
        final String titleKey;
        final String messageKey;

        private TaskState(String titleKey, String messageKey) {
            this.titleKey = checkNotNull(titleKey);
            this.messageKey = checkNotNull(messageKey);
        }
    }

    private Twitter twitter;

    private String screenName;
    private Optional<Long> sinceId = Optional.empty();
    private Optional<Long> maxId = Optional.empty();

    /**
     * Constructor.
     */
    FavoriteListService() {
        super(true);
    }

    void initialize(TwitterAccessor accessor) {
        checkNotNull(accessor, "accessor");
        checkState(twitter == null);

        twitter = accessor.twitter();
        screenName(accessor.accessToken().getScreenName());
    }

    FavoriteListService screenName(String screenName) {
        checkNotNull(screenName, "screenName");
        checkArgument(!screenName.isEmpty(), "screenName is empty.");

        if (!Objects.equals(this.screenName, screenName))
            state.set(FindType.RESET);

        this.screenName = screenName;
        return this;
    }

    /**
     * 取得済みツイートより新しいツイートを最大で200件取得.
     * 取得済みがなければ最新から最大200件取得.
     */
    void refresh() {
        super.restart();
    }

    /**
     * 取得済みツイートより古いツイートを最大で200件取得.
     */
    void continueLoad() {
        state.set(FindType.CONTINUE);

        super.restart();
    }

    @Deprecated
    @Override
    public void start() {
        super.start();
    }

    @Deprecated
    @Override
    public void restart() {
        super.restart();
    }

    @Deprecated
    @Override
    public void reset() {
        super.reset();
    }

    @Override
    protected Task<FavoriteListItems> newTask() {

        return new MyTask<FavoriteListItems>() {

            @Override
            protected FavoriteListItems execute() throws Exception {

                // TODO: リファクタリング
                // TODO: メッセージのリソース化

                changeState(TaskState.GET_FAVOLITES);

                val lastValue = setupAndGetCurrentList();

                ResponseList<Status> res;
                try {
                    res = twitter.favorites().getFavorites(screenName, paging());
                } catch (TwitterException e) {
                    val rateLimitStatus = e.getRateLimitStatus();

                    if (e.getErrorCode() != RATE_LIMIT_EXCEEDED || rateLimitStatus == null)
                        throw e;

                    log.warn("レートリミットを超過したため取得できませんでした。 {}", rateLimitStatus);

                    changeState(TaskState.UPDATE_VALUE);

                    val ret = new FavoriteListItems(e.getRateLimitStatus());

                    changeState(TaskState.FINISH);
                    return ret;
                }

                changeState(TaskState.UPDATE_VALUE);

                if (!lastValue.isPresent()) {
                    val ret = new FavoriteListItems(res);
                    changeState(TaskState.FINISH);
                    return ret;
                }

                lastValue.ifPresent(v -> v.update(res));
                changeState(TaskState.FINISH);
                return lastValue.get();
            }

            private void changeState(TaskState state) {
                updateProgress(state.ordinal(), TaskState.SIZE);
                updateTitle(RB.labelOf(state.titleKey));
                updateMessage(RB.messageOf(state.messageKey));
            }
        };
    }

    private Optional<FavoriteListItems> setupAndGetCurrentList() {

        Optional<FavoriteListItems> lastValue;
        switch (state.getAndSet(FavoriteListService.FindType.GET_LATESTS)) {

            case CONTINUE:
                lastValue = getLastValue();
                sinceId = Optional.empty();
                lastValue.ifPresent(items -> maxId = items.oldestId());
                return lastValue;

            case GET_LATESTS:
                lastValue = getLastValue();
                lastValue.ifPresent(items -> sinceId = items.latestId().map(id -> id + 1L));
                maxId = Optional.empty();
                return lastValue;

            case RESET:
                lastValue = Optional.empty();
                sinceId = Optional.empty();
                maxId = Optional.empty();
                return lastValue;

            default:
                throw new InternalError("unreachable");
        }
    }

    private Paging paging() {
        sinceId.map(v -> v < 0 ? null : v).ifPresent(paging::setSinceId);
        maxId.map(v -> v < 0 ? null : v).ifPresent(paging::setMaxId);
        return paging;
    }

}
