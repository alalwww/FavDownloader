/*
 * FavDownloader
 *
 * (c) 2014 alalwww
 * https://github.com/alalwww
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package net.awairo.common.javafx;

import static com.google.common.base.Preconditions.*;

import java.util.Optional;
import java.util.function.Consumer;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.awairo.common.function.Runner;

/**
 * ワーカーステートイベントのハンドラー定義.
 *
 * @author alalwww
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WorkerStateEventHandlers {

    /**
     * タスク用.
     *
     * @param <R> 戻り値のタイプ
     * @param <T> チェーン可能メソッドが返すタスクインスタンスのタイプ
     */
    public interface Task<R, T extends Task<R, T>>
            extends Scheduled<T>, Running<T>, Cancelled<T>, Failed<T>, Succeeded<R, T>, Done<T> {}

    /**
     * サービス用.
     *
     * @param <R> 戻り値のタイプ
     * @param <S> チェーン可能メソッドが返すサービスインスタンスのタイプ
     */
    public interface Service<R, S extends Service<R, S>>
            extends Task<R, S>, Ready<S> {}

    /**
     * Scheduledイベントハンドラーの定義.
     *
     * @param <I> インスタンスのタイプ
     */
    public interface Scheduled<I extends Scheduled<I>> extends Chainable<I> {
        /**
         * ランナーを取得します.
         *
         * @return ランナー
         */
        Optional<Runner> getScheduledRunner();

        /**
         * ランナーを設定します.
         * <p>これはイベントインスタンスを使用しないイベントハンドラーです。</p>
         *
         * @param runner ランナー
         */
        void setScheduledRunner(Optional<Runner> runner);

        /**
         * タスクの実行が計画された際の処理を追加します.
         *
         * @param cancelRunner タスク実行計画時の処理
         * @return このインスタンス
         */
        default I ifScheduled(Runner scheduledRunner) {

            setScheduledRunner(Optional.of(getScheduledRunner()
                    .map(r -> r.andThen(scheduledRunner))
                    .orElse(scheduledRunner)));

            return instance();
        }

        /**
         * {@link #ifScheduled(Runner)}で追加した全ての実行が計画された後の処理を削除します.
         *
         * @return このインスタンス
         */
        default I removeScheduledRunner() {
            setScheduledRunner(Optional.empty());
            return instance();
        }

        /**
         * イベントハンドラーを取得します.
         *
         * @return イベントハンドラー
         */
        EventHandler<WorkerStateEvent> getOnScheduled();

        /**
         * イベントハンドラーを設定します.
         *
         * @param value イベントハンドラー
         */
        void setOnScheduled(EventHandler<WorkerStateEvent> value);

        /**
         * 既存のイベントハンドラーがあれば破棄し、新たなイベントハンドラーを設定します.
         *
         * @param handler イベントハンドラー
         * @return このインスタンス
         */
        default I onScheduled(EventHandler<WorkerStateEvent> handler) {
            checkNotNull(handler, "handler");
            setOnScheduled(handler);
            return instance();
        }

        /**
         * 現在設定されているイベントハンドラーに新たなハンドラーを追加します.
         *
         * @param handler 追加するイベントハンドラー
         * @return このインスタンス
         */
        default I addOnScheduled(EventHandler<WorkerStateEvent> handler) {
            checkNotNull(handler, "handler");
            setOnScheduled(mergeIfPresent(getOnScheduled(), handler));
            return instance();
        }

        /**
         * イベントハンドラーを削除します.
         *
         * @return このインスタンス
         */
        default I removeOnScheduled() {
            setOnScheduled(null);
            return instance();
        }
    }

    /**
     * Readyイベントハンドラーの定義.
     *
     * @param <I> インスタンスのタイプ
     */
    public interface Ready<I extends Ready<I>> extends Chainable<I> {
        /**
         * ランナーを取得します.
         *
         * @return ランナー
         */
        Optional<Runner> getReadyRunner();

        /**
         * ランナーを設定します.
         * <p>これはイベントインスタンスを使用しないイベントハンドラーです。</p>
         *
         * @param runner ランナー
         */
        void setReadyRunner(Optional<Runner> runner);

        /**
         * タスク実行開始時の処理を追加します.
         *
         * @param failedConsumer 失敗を受け入れる処理
         * @return このインスタンス
         */
        default I ifReady(Runner readyRunner) {

            setReadyRunner(Optional.of(getReadyRunner()
                    .map(r -> r.andThen(readyRunner))
                    .orElse(readyRunner)));

            return instance();
        }

        /**
         * {@link #ifReady(Runner)}で追加した全ての失敗後の処理を削除します.
         *
         * @return このインスタンス
         */
        default I removeReadyRunner() {
            setReadyRunner(Optional.empty());
            return instance();
        }

        /**
         * イベントハンドラーを取得します.
         *
         * @return イベントハンドラー
         */
        EventHandler<WorkerStateEvent> getOnReady();

        /**
         * イベントハンドラーを設定します.
         *
         * @param value イベントハンドラー
         */
        void setOnReady(EventHandler<WorkerStateEvent> value);

        /**
         * 既存のイベントハンドラーがあれば破棄し、新たなイベントハンドラーを設定します.
         *
         * @param handler イベントハンドラー
         * @return このインスタンス
         */
        default I onReady(EventHandler<WorkerStateEvent> handler) {
            checkNotNull(handler, "handler");
            setOnReady(handler);
            return instance();
        }

        /**
         * 現在設定されているイベントハンドラーに新たなハンドラーを追加します.
         *
         * @param handler 追加するイベントハンドラー
         * @return このインスタンス
         */
        default I addOnReady(EventHandler<WorkerStateEvent> handler) {
            checkNotNull(handler, "handler");
            setOnReady(mergeIfPresent(getOnReady(), handler));
            return instance();
        }

        /**
         * イベントハンドラーを削除します.
         *
         * @return このインスタンス
         */
        default I removeOnReady() {
            setOnReady(null);
            return instance();
        }
    }

    /**
     * Runningイベントハンドラーの定義.
     *
     * @param <I> インスタンスのタイプ
     */
    public interface Running<I extends Running<I>> extends Chainable<I> {
        /**
         * ランナーを取得します.
         *
         * @return ランナー
         */
        Optional<Runner> getRunningRunner();

        /**
         * ランナーを設定します.
         * <p>これはイベントインスタンスを使用しないイベントハンドラーです。</p>
         *
         * @param runner ランナー
         */
        void setRunningRunner(Optional<Runner> runner);

        /**
         * タスク実行中の処理を追加します.
         *
         * @param cancelRunner 開始時の処理
         * @return このインスタンス
         */
        default I ifRunning(Runner runningRunner) {

            setRunningRunner(Optional.of(getRunningRunner()
                    .map(r -> r.andThen(runningRunner))
                    .orElse(runningRunner)));

            return instance();
        }

        /**
         * {@link #ifRunning(Runner)}で追加した全ての実行中の処理を削除します.
         *
         * @return このインスタンス
         */
        default I removeRunningRunner() {
            setRunningRunner(Optional.empty());
            return instance();
        }

        /**
         * イベントハンドラーを取得します.
         *
         * @return イベントハンドラー
         */
        EventHandler<WorkerStateEvent> getOnRunning();

        /**
         * イベントハンドラーを設定します.
         *
         * @param value イベントハンドラー
         */
        void setOnRunning(EventHandler<WorkerStateEvent> value);

        /**
         * 既存のイベントハンドラーがあれば破棄し、新たなイベントハンドラーを設定します.
         *
         * @param handler イベントハンドラー
         * @return このインスタンス
         */
        default I onRunning(EventHandler<WorkerStateEvent> handler) {
            checkNotNull(handler, "handler");
            setOnRunning(handler);
            return instance();
        }

        /**
         * 現在設定されているイベントハンドラーに新たなハンドラーを追加します.
         *
         * @param handler 追加するイベントハンドラー
         * @return このインスタンス
         */
        default I addOnRunning(EventHandler<WorkerStateEvent> handler) {
            checkNotNull(handler, "handler");
            setOnRunning(mergeIfPresent(getOnRunning(), handler));
            return instance();
        }

        /**
         * イベントハンドラーを削除します.
         *
         * @return このインスタンス
         */
        default I removeOnRunning() {
            setOnRunning(null);
            return instance();
        }

    }

    /**
     * Cancelledイベントハンドラーの定義.
     *
     * @param <I> インスタンスのタイプ
     */
    public interface Cancelled<I extends Cancelled<I>> extends Chainable<I> {
        /**
         * ランナーを取得します.
         *
         * @return ランナー
         */
        Optional<Runner> getCancelledRunner();

        /**
         * ランナーを設定します.
         * <p>これはイベントインスタンスを使用しないイベントハンドラーです。</p>
         *
         * @param runner ランナー
         */
        void setCancelledRunner(Optional<Runner> runner);

        /**
         * キャンセルされた際の処理を追加します.
         *
         * @param cancelRunner キャンセル時の処理
         * @return このインスタンス
         */
        default I ifCancelled(Runner cancelRunner) {

            setCancelledRunner(Optional.of(getCancelledRunner()
                    .map(r -> r.andThen(cancelRunner))
                    .orElse(cancelRunner)));

            return instance();
        }

        /**
         * {@link #ifCanceled(Runner)}で追加した全てのキャンセル後の処理を削除します.
         *
         * @return このインスタンス
         */
        default I removeCanceledRunner() {
            setCancelledRunner(Optional.empty());
            return instance();
        }

        /**
         * イベントハンドラーを取得します.
         *
         * @return イベントハンドラー
         */
        EventHandler<WorkerStateEvent> getOnCancelled();

        /**
         * イベントハンドラーを設定します.
         *
         * @param value イベントハンドラー
         */
        void setOnCancelled(EventHandler<WorkerStateEvent> value);

        /**
         * 既存のイベントハンドラーがあれば破棄し、新たなイベントハンドラーを設定します.
         *
         * @param handler イベントハンドラー
         * @return このインスタンス
         */
        default I onCancelled(EventHandler<WorkerStateEvent> handler) {
            checkNotNull(handler, "handler");
            setOnCancelled(handler);
            return instance();
        }

        /**
         * 現在設定されているイベントハンドラーに新たなハンドラーを追加します.
         *
         * @param handler 追加するイベントハンドラー
         * @return このインスタンス
         */
        default I addOnCancelled(EventHandler<WorkerStateEvent> handler) {
            checkNotNull(handler, "handler");
            setOnCancelled(mergeIfPresent(getOnCancelled(), handler));
            return instance();
        }

        /**
         * イベントハンドラーを削除します.
         *
         * @return このインスタンス
         */
        default I removeOnCancelled() {
            setOnCancelled(null);
            return instance();
        }
    }

    /**
     * Failedイベントハンドラーの定義.
     *
     * @param <I> インスタンスのタイプ
     */
    public interface Failed<I extends Failed<I>> extends Chainable<I> {

        /**
         * 失敗時の処理を追加します.
         *
         * @param failedRunner ランナー
         * @return このインスタンス
         */
        default I ifFailed(Runner failedRunner) {
            return ifFailed(r -> failedRunner.run());
        }

        /**
         * 例外コンシューマーを取得します.
         *
         * @return 例外コンシューマー
         */
        Optional<Consumer<Throwable>> getFailedConsumer();

        /**
         * 例外コンシューマーを設定します.
         * <p>これはイベントインスタンスを使用しないイベントハンドラーです。</p>
         *
         * @param runner ランナー
         */
        void setFailedConsumer(Optional<Consumer<Throwable>> failedConsumer);

        /**
         * タスク失敗時の処理を追加します.
         *
         * @param failedConsumer 失敗を受け入れる処理
         * @return このインスタンス
         */
        default I ifFailed(Consumer<Throwable> failedConsumer) {

            setFailedConsumer(Optional.of(getFailedConsumer()
                    .map(r -> r.andThen(failedConsumer))
                    .orElse(failedConsumer)));

            return instance();
        }

        /**
         * {@link #ifFailed(Consumer)}で追加した全ての失敗後の処理を削除します.
         *
         * @return このインスタンス
         */
        default I removeFailedConsumer() {
            setFailedConsumer(Optional.empty());
            return instance();
        }

        /**
         * イベントハンドラーを取得します.
         *
         * @return イベントハンドラー
         */
        EventHandler<WorkerStateEvent> getOnFailed();

        /**
         * イベントハンドラーを設定します.
         *
         * @param value イベントハンドラー
         */
        void setOnFailed(EventHandler<WorkerStateEvent> value);

        /**
         * 既存のイベントハンドラーがあれば破棄し、新たなイベントハンドラーを設定します.
         *
         * @param handler イベントハンドラー
         * @return このインスタンス
         */
        default I onFailed(EventHandler<WorkerStateEvent> handler) {
            checkNotNull(handler, "handler");
            setOnFailed(handler);
            return instance();
        }

        /**
         * 現在設定されているイベントハンドラーに新たなハンドラーを追加します.
         *
         * @param handler 追加するイベントハンドラー
         * @return このインスタンス
         */
        default I addOnFailed(EventHandler<WorkerStateEvent> handler) {
            checkNotNull(handler, "handler");
            setOnFailed(mergeIfPresent(getOnFailed(), handler));
            return instance();
        }

        /**
         * イベントハンドラーを削除します.
         *
         * @return このインスタンス
         */
        default I removeOnFailed() {
            setOnFailed(null);
            return instance();
        }
    }

    /**
     * Succeededイベントハンドラーの定義.
     *
     * @param <I> インスタンスのタイプ
     */
    public interface Succeeded<R, I extends Succeeded<R, I>> extends Chainable<I> {

        /**
         * 成功時の処理を追加します.
         *
         * @param succeededRunner ランナー
         * @return このインスタンス
         */
        default I ifSucceeded(Runner succeededRunner) {
            return ifSucceeded(r -> succeededRunner.run());
        }

        /**
         * 結果コンシューマーを取得します.
         *
         * @return 結果コンシューマー
         */
        Optional<Consumer<R>> getSucceededConsumer();

        /**
         * 結果コンシューマーを設定します.
         * <p>これはイベントインスタンスを使用しないイベントハンドラーです。</p>
         *
         * @param runner ランナー
         */
        void setSucceededConsumer(Optional<Consumer<R>> succeededConsumer);

        /**
         * タスク成功時の処理を追加します.
         *
         * @param failedConsumer 結果を受け入れる処理
         * @return このインスタンス
         */
        @SuppressWarnings("unchecked")
        default I ifSucceeded(Consumer<? super R> succeededConsumer) {

            setSucceededConsumer(Optional.of(getSucceededConsumer()
                    .map(r -> r.andThen(succeededConsumer))
                    .orElse((Consumer<R>) succeededConsumer)));

            return instance();
        }

        /**
         * {@link #ifSucceeded(Consumer)}で追加した全ての成功後の処理を削除します.
         *
         * @return このインスタンス
         */
        default I removeSucceededConsumer() {
            setSucceededConsumer(Optional.empty());
            return instance();
        }

        /**
         * イベントハンドラーを取得します.
         *
         * @return イベントハンドラー
         */
        EventHandler<WorkerStateEvent> getOnSucceeded();

        /**
         * イベントハンドラーを設定します.
         *
         * @param value イベントハンドラー
         */
        void setOnSucceeded(EventHandler<WorkerStateEvent> value);

        /**
         * 既存のイベントハンドラーがあれば破棄し、新たなイベントハンドラーを設定します.
         *
         * @param handler イベントハンドラー
         * @return このインスタンス
         */
        default I onSucceeded(EventHandler<WorkerStateEvent> handler) {
            checkNotNull(handler, "handler");
            setOnSucceeded(handler);
            return instance();
        }

        /**
         * 現在設定されているイベントハンドラーに新たなハンドラーを追加します.
         *
         * @param handler 追加するイベントハンドラー
         * @return このインスタンス
         */
        default I addOnSucceeded(EventHandler<WorkerStateEvent> handler) {
            checkNotNull(handler, "handler");
            setOnSucceeded(mergeIfPresent(getOnSucceeded(), handler));
            return instance();
        }

        /**
         * イベントハンドラーを削除します.
         *
         * @return このインスタンス
         */
        default I removeOnSucceeded() {
            setOnSucceeded(null);
            return instance();
        }
    }

    /**
     * ワーカーイベントにない処理の終了を通知するための定義.
     *
     * <p>このインターフェイスの定義するメソッドで設定されたランナーは、{@link Cancelled}、{@link Failed}、 {@link Succeeded}
     * に紐づくイベントが発生した際に、合わせて実行されます。</p>
     *
     * @author alalwww
     * @param <I> インスタンスのタイプ
     */
    public interface Done<I extends Done<I>> extends Chainable<I> {
        /**
         * ランナーを取得します.
         *
         * @return ランナー
         */
        Optional<Runner> getDoneRunner();

        /**
         * ランナーを設定します.
         * <p>これはイベントインスタンスを使用しないイベントハンドラーです。</p>
         *
         * @param runner ランナー
         */
        void setDoneRunner(Optional<Runner> runner);

        /**
         * 処理終了時の処理を追加します.
         *
         * @param doneRunner ランナー
         * @return このインスタンス
         */
        default I ifDone(Runner doneRunner) {

            setDoneRunner(Optional.of(getDoneRunner()
                    .map(r -> r.andThen(doneRunner))
                    .orElse(doneRunner)));

            return instance();
        }

        /**
         * {@link #ifDone(Runner)}で追加した全ての処理終了時の処理を削除します.
         *
         * @return このインスタンス
         */
        default I removeDoneRunner() {
            setDoneRunner(Optional.empty());
            return instance();
        }

    }

    /**
     * チェーン可能メソッドを持つインスタンスの返すべきインスタンスを定義.
     *
     * @param <I> インスタンスのタイプ
     */
    public interface Chainable<I extends Chainable<I>> {
        /**
         * インスタンスを取得します.
         *
         * @return このインターフェイスを実装したクラスのチェーン可能メソッドが返すインスタンス
         */
        I instance();
    }

    /**
     * 2つのイベントハンドラをマージします.
     *
     * @param before 先に実行するハンドラーもしくはnull
     * @param after 後に実行するハンドラーもしくはnull
     * @return 合成したハンドラー
     */
    static EventHandler<WorkerStateEvent> mergeIfPresent(
            EventHandler<WorkerStateEvent> before, EventHandler<WorkerStateEvent> after) {

        if (after == null)
            return before;

        if (before == null)
            return after;

        return event -> {
            before.handle(event);
            after.handle(event);
        };
    }

}
