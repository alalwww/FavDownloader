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

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javafx.concurrent.Task;
import lombok.val;
import lombok.extern.log4j.Log4j2;

import net.awairo.common.javafx.FxUtils;
import net.awairo.common.javafx.NoResultTask;
import net.awairo.common.javafx.ServiceBase;
import net.awairo.common.util.RB;
import net.awairo.favdler.twitter.FavoriteListItems;

/**
 * レートリミット情報の更新サービス.
 *
 * @author alalwww
 */
@Log4j2
final class RateLimitUpdater extends ServiceBase<Void, RateLimitUpdater> {

    /** 残数. */
    private int remaining;
    /** API上限. */
    private int limit;
    /** リセットされる時間. */
    private int resetTimeInSeconds;

    public void restart(FavoriteListItems searchResult) {
        FxUtils.checkFxApplicationThread();

        log.debug("restart api limit updater");

        remaining = searchResult.remaining();
        limit = searchResult.limit();
        resetTimeInSeconds = searchResult.resetTimeInSeconds();

        super.restart();
    }

    @Deprecated
    @Override
    public void start() {
        super.start();
    }

    @Deprecated
    @Override
    public void reset() {
        super.reset();
    }

    @Deprecated
    @Override
    public void restart() {
        super.restart();
    }

    @Override
    protected Task<Void> newTask() {

        return new NoResultTask() {

            @Override
            protected void execute() throws Exception {

                do {

                    if (!update())
                        break;

                    log.trace("wait 1000ms");
                } while (sleep(1000L));
            }

            private boolean update() {

                val reset = ZonedDateTime.ofInstant(Instant.ofEpochSecond(resetTimeInSeconds), ZoneId.systemDefault());
                val now = ZonedDateTime.now();
                long secondsUntilReset = reset.toEpochSecond() - now.toEpochSecond();

                if (secondsUntilReset > 0) {
                    updateMessage(beforeResetMessage(secondsUntilReset, reset));
                    return true;
                }

                updateMessage(messageOfReseted(reset));
                log.debug("finish update");
                return false;
            }

            private String beforeResetMessage(long secondsUntilReset, ZonedDateTime reset) {
                return RB.labelOf("api_limit_format",
                        remaining,
                        limit,
                        reset.toLocalTime(),
                        secondsUntilReset);
            }

            private String messageOfReseted(ZonedDateTime reset) {
                return RB.labelOf("api_limit_format",
                        limit,
                        limit,
                        reset.toLocalTime(),
                        0);
            }

        };
    }

}
