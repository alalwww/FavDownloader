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

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import net.awairo.common.function.Runner;

/**
 * サービス.
 *
 * @author alalwww
 * @param <R> タスクの処理結果のタイプ
 * @param <S> チェーン可能なメソッドの戻り値のタイプ
 */
@Log4j2
public abstract class ServiceBase<R, S extends ServiceBase<R, S>> extends Service<R>
        implements WorkerStateEventHandlers.Service<R, S>, DialogFactory {

    private Optional<String> name = Optional.empty();

    @Getter
    @Setter
    private Optional<Runner> scheduledRunner = Optional.empty();
    @Getter
    @Setter
    private Optional<Runner> readyRunner = Optional.empty();
    @Getter
    @Setter
    private Optional<Runner> runningRunner = Optional.empty();
    @Getter
    @Setter
    private Optional<Runner> cancelledRunner = Optional.empty();
    @Getter
    @Setter
    private Optional<Runner> doneRunner = Optional.empty();
    @Getter
    @Setter
    private Optional<Consumer<Throwable>> failedConsumer = Optional.empty();
    @Getter
    @Setter
    private Optional<Consumer<R>> succeededConsumer = Optional.empty();

    /**
     * Constructor.
     */
    protected ServiceBase() {
        setExecutor(TaskExecutorImpl.instance());
        executorProperty().addListener(event -> {
            log.debug("変更禁止: {}", event);
            throw new InternalError("Executorは変更できません");
        });

    }

    @Override
    protected final Task<R> createTask() {
        log.trace("create task.");
        return newTask();
    }

    /**
     * 新しいタスクを生成します.
     *
     * @return タスク
     */
    protected abstract Task<R> newTask();

    // -----------------------------

    /**
     * このサービスに名前をつけます.(デバッグログ用)
     *
     * @param taskName サービス名、または名前を削除する場合null)
     * @return このインスタンス
     */
    public final S name(String taskName) {
        name = Optional.ofNullable(taskName);
        return instance();
    }

    /**
     * このサービスの名前を取得.
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
    protected void ready() {
        super.ready();
        log.trace("{} task ready.", this);
        readyRunner.ifPresent(r -> r.run());
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
    public S instance() {
        return (S) this;
    }

}
