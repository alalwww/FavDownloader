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

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import net.awairo.common.exception.AppException;
import net.awairo.common.function.Runner;

/**
 * タスクの例外処理の共通化と、汎用メソッドの追加を行う基底クラス.
 *
 * <p>このクラスは直接使用することを想定していない。{@link MyTask}または{@link NoResultTask｝を使用してください。</p>
 *
 * @see MyTask
 * @see NoResultTask
 *
 * @author alalwww
 * @param <R> タスクの実行結果タイプ
 * @param <T> チェーン可能なメソッドの戻り値のタイプ
 */
@Log4j2
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class TaskBase<R, T extends TaskBase<R, T>> extends Task<R>
        implements WorkerStateEventHandlers.Task<R, T>, DialogFactory {

    private Optional<String> name = Optional.empty();

    @NonNull
    @Getter
    @Setter
    private Optional<Runner> scheduledRunner = Optional.empty();

    @NonNull
    @Getter
    @Setter
    private Optional<Runner> runningRunner = Optional.empty();

    @NonNull
    @Getter
    @Setter
    private Optional<Runner> cancelledRunner = Optional.empty();

    @NonNull
    @Getter
    @Setter
    private Optional<Runner> doneRunner = Optional.empty();

    @NonNull
    @Getter
    @Setter
    private Optional<Consumer<Throwable>> failedConsumer = Optional.empty();

    @NonNull
    @Getter
    @Setter
    private Optional<Consumer<R>> succeededConsumer = Optional.empty();

    @Override
    protected final R call() throws Exception {

        // ブレークポイントの設置に利用したり、例外の大元をトレースしたりするために定義

        try {
            return _execute();
        } catch (AppException e) {
            // 処理済み(ログ出力済み)のはずなので基本的にログ処理はしない
            log.trace("AppException", e);
            throw e;

        } catch (RuntimeException e) {
            log.debug("未処理の実行時例外を補足", e);
            throw new AppException(e);

        } catch (Exception e) {
            log.debug("未処理の例外を補足", e);
            throw new AppException(e);

        } catch (Error e) {
            // エラーが起きるような重大な状況はできるだけ追跡可能な情報を残したいのでここでもログ処理をいれている。OOEとかログ処理も無理かもだけど
            log.fatal("エラー", e);
            throw e;
        }
    }

    /**
     * タスクを実行し、実行結果を返します.
     *
     * @return 実行結果
     * @throws Exception 継続不可能な問題が発生した場合.
     */
    /* package */abstract R _execute() throws Exception;

    // -----------------------------

    /**
     * このタスクに名前をつけます.(デバッグログ用)
     *
     * @param taskName タスク名、または名前を削除する場合null)
     * @return このインスタンス
     */
    public final T name(String taskName) {
        name = Optional.ofNullable(taskName);
        return instance();
    }

    /**
     * このタスクの名前を取得.
     *
     * @return タスクの名前
     */
    public final String name() {
        return toString();
    }

    @Override
    public String toString() {
        return name.orElse(super.toString());
    }

    // -----------------------------

    /**
     * <p>このメソッドは、FXアプリケーションスレッド内で、対応する{@link WorkerStateEvent}のディスパッチ後にコールされます。</p>
     */
    @Override
    protected void scheduled() {
        super.scheduled();
        log.trace("{} task scheduled.", this);
        scheduledRunner.ifPresent(r -> r.run());
    }

    /**
     * <p>このメソッドは、FXアプリケーションスレッド内で、対応する{@link WorkerStateEvent}のディスパッチ後にコールされます。</p>
     */
    @Override
    protected void running() {
        super.running();
        log.trace("{} task running.", this);
        runningRunner.ifPresent(r -> r.run());
    }

    /**
     * <p>このメソッドは、FXアプリケーションスレッド内で、対応する{@link WorkerStateEvent}のディスパッチ後にコールされます。</p>
     */
    @Override
    protected void cancelled() {
        super.cancelled();
        log.trace("{} task cancelled.", this);
        cancelledRunner.ifPresent(r -> r.run());
        doneRunner.ifPresent(r -> r.run());
    }

    /**
     * <p>このメソッドは、FXアプリケーションスレッド内で、対応する{@link WorkerStateEvent}のディスパッチ後にコールされます。</p>
     */
    @Override
    protected void failed() {
        super.failed();
        log.trace("{} task failed.", this);
        failedConsumer.ifPresent(c -> c.accept(getException()));
        doneRunner.ifPresent(r -> r.run());
    }

    /**
     * <p>このメソッドは、FXアプリケーションスレッド内で、対応する{@link WorkerStateEvent}のディスパッチ後にコールされます。</p>
     */
    @Override
    protected void succeeded() {
        super.succeeded();
        log.trace("{} task succeeded.", this);
        succeededConsumer.ifPresent(c -> c.accept(getValue()));
        doneRunner.ifPresent(r -> r.run());
    }

    // -----------------------------

    @SuppressWarnings("unchecked")
    @Override
    public T instance() {
        return (T) this;
    }

    // -----------------------------

    /**
     * スレッドを指定時間スリープします.
     *
     * @param millis スレッドをスリープする時間(ミリ秒)
     * @return {@code true} はスリープ時間が経過し正常に復帰 {@code false} はスリープ中にタスクがキャンセルされた
     * @throws AppException InterruptedExceptionが発生した場合(キャンセル以外の理由でスリープが中断された場合)
     *
     * @see Thread#sleep(long)
     */
    protected final boolean sleep(long millis) {
        return sleep(millis, this::sleepCanceled);
    }

    /**
     * スレッドを指定時間スリープします.
     *
     * @param millis スレッドをスリープする時間(ミリ秒)
     * @param sleepCanceled スリープがキャンセルされた場合の処理
     * @return {@code true} はスリープ時間が経過し正常に復帰 {@code false} はスリープ中にタスクがキャンセルされた
     * @throws AppException InterruptedExceptionが発生した場合(キャンセル以外の理由でスリープが中断された場合)
     *
     * @see Thread#sleep(long)
     */
    protected final boolean sleep(long millis, LongConsumer sleepCanceled) {

        try {
            Thread.sleep(millis);
            return true;
        } catch (InterruptedException e) {
            if (isCancelled()) {
                sleepCanceled.accept(millis);
                return false;
            }

            throw new AppException(e);
        }
    }

    /**
     * {@link #sleep(long)}メソッドにてスリープ中にキャンセルされた場合の処理を行います.
     *
     * @param millis スリープ時間
     */
    protected void sleepCanceled(long millis) {
        log.trace("{} sleepCanceled.", this);
    }
}
