/*
 * FavDownloader
 *
 * (c) 2014 alalwww
 * https://github.com/alalwww
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package net.awairo.favdler.oauth;

import java.util.Optional;
import java.util.function.Consumer;

import lombok.extern.log4j.Log4j2;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

import net.awairo.common.exception.AppException;
import net.awairo.common.javafx.FxUtils;
import net.awairo.common.javafx.MyTask;
import net.awairo.common.javafx.TaskExecutor;
import net.awairo.common.util.Desktop;
import net.awairo.favdler.twitter.TwitterAccessor;

/**
 * Twitter認証.
 *
 * @author alalwww
 */
@Log4j2
/* package */final class TwitterOAuth {

    private static final String CALLBACK = "http://localhost:%s/auth";
    private final TaskExecutor executor = new TaskExecutor() {};

    private final OAuthCallbackReceiveServer server;

    private Optional<MyTask<RequestToken>> rTokenGetterTask = Optional.empty();
    private Optional<MyTask<TwitterAccessor>> aTokenGetterTask = Optional.empty();

    private Optional<Twitter> twitter = Optional.empty();
    private Optional<RequestToken> rToken = Optional.empty();

    private Optional<Consumer<TwitterAccessor>> succeededHandler = Optional.empty();
    private Optional<Consumer<Throwable>> failedHandler = Optional.empty();

    //------------------------------------------

    /**
     * Constructor.
     */
    public TwitterOAuth() {
        FxUtils.checkFxApplicationThread();

        server = new OAuthCallbackReceiveServer()
                .ifRunning(this::createRequestTokenGetterTask)
                .ifRunning(() -> rTokenGetterTask.ifPresent(executor::execute))
                .ifFailed(this::cancel)
                .ifFailed(this::onFailed)
                .ifSucceeded(this::createAccessTokenGetterTask)
                .ifSucceeded(() -> aTokenGetterTask.ifPresent(executor::execute))
                .ifDone(this::stopServer);
    }

    //------------------------------------------

    /**
     * 正常終了時のコールバックを設定.
     *
     * @param handler コールバック
     * @return このインスタンス
     */
    public TwitterOAuth onSucceeded(Consumer<TwitterAccessor> handler) {
        FxUtils.checkFxApplicationThread();

        succeededHandler = Optional.ofNullable(handler);
        return this;
    }

    /**
     * 以上終了時のコールバックを設定.
     *
     * @param handler コールバック
     * @return このインスタンス
     */
    public TwitterOAuth onFailed(Consumer<Throwable> handler) {
        FxUtils.checkFxApplicationThread();

        failedHandler = Optional.ofNullable(handler);
        return this;
    }

    //------------------------------------------

    /**
     * 実行中の処理を中断します.
     */
    public void cancel() {
        FxUtils.checkFxApplicationThread();

        rTokenGetterTask.ifPresent(task -> task.cancel());
        aTokenGetterTask.ifPresent(task -> task.cancel());

        rTokenGetterTask = Optional.empty();
        aTokenGetterTask = Optional.empty();

        stopServer();
        clearTwitterResources();
    }

    /**
     * 認証を開始します.
     */
    public void start() {
        FxUtils.checkFxApplicationThread();

        twitter = Optional.ofNullable(new TwitterFactory().getInstance());
        server.restart();
    }

    //------------------------------------------

    private void createRequestTokenGetterTask() {
        if (!twitter.isPresent()) {
            log.debug("中断 twitter={}", twitter);
            return;
        }

        rTokenGetterTask = Optional.ofNullable(
                MyTask.of(getRequestToken(twitter.get()))
                        .ifFailed(this::cancel)
                        .ifFailed(this::onFailed)
                        .ifSucceeded(this::setRequestToken)
                        .ifSucceeded(this::openTwitterOAuth)
                        .ifDone(() -> rTokenGetterTask = Optional.empty())
                );
    }

    private void createAccessTokenGetterTask(String oauthVerifier) {
        if (!twitter.isPresent() || !rToken.isPresent()) {
            log.debug("中断 twitter={}, rToken={}", twitter, rToken);
            return;
        }

        Twitter t = twitter.get();
        RequestToken rt = rToken.get();

        aTokenGetterTask = Optional.ofNullable(
                MyTask.of(getTwitterAccessor(t, rt, oauthVerifier))
                        .ifFailed(this::cancel)
                        .ifFailed(this::onFailed)
                        .ifSucceeded(this::onSucceeded)
                        .ifDone(() -> aTokenGetterTask = Optional.empty())
                        .ifDone(this::clearTwitterResources)
                );
    }

    private MyTask.Function<RequestToken> getRequestToken(Twitter t) {
        return () -> {
            try {
                return t.getOAuthRequestToken(String.format(CALLBACK, server.getLocalPort()));
            } catch (TwitterException e) {
                throw new AppException(e);
            }
        };
    }

    private void setRequestToken(RequestToken rToken) {
        this.rToken = Optional.ofNullable(rToken);
    }

    private void openTwitterOAuth(RequestToken rToken) {
        Desktop.openURI(rToken.getAuthorizationURL());
    }

    private MyTask.Function<TwitterAccessor> getTwitterAccessor(Twitter t, RequestToken token, String oauthVerifier) {
        return () -> {
            t.getOAuthAccessToken(token, oauthVerifier);
            return new TwitterAccessor(t);
        };
    }

    private void stopServer() {
        server.cancel();
        server.reset();
    }

    private void clearTwitterResources() {
        rToken = Optional.empty();
        twitter = Optional.empty();
    }

    private void onFailed(Throwable t) {
        failedHandler.ifPresent(h -> h.accept(t));
    }

    private void onSucceeded(TwitterAccessor accessor) {
        succeededHandler.ifPresent(h -> h.accept(accessor));
    }

}
