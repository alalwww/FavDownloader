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

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * TaskExecutorImpl.
 *
 * @author alalwww
 */
@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
/* package */final class TaskExecutorImpl implements Executor {

    private static final TaskExecutorImpl INSTANCE = new TaskExecutorImpl();

    /* package */ExecutorService delegate;

    /* package */static TaskExecutorImpl instance() {
        checkState(INSTANCE.delegate != null && !INSTANCE.delegate.isShutdown(), "ExecutorServiceが開始されていません");
        return INSTANCE;
    }

    /* package */static void restartExecutor() {
        shutdownExecutor(); // 複数回呼ばれることはないと思うけど仕様確認してないので念のため

        final ThreadFactory defFactory = Executors.defaultThreadFactory();

        INSTANCE.delegate = Executors.newCachedThreadPool(r -> {
            Thread newThread = defFactory.newThread(r);
            newThread.setUncaughtExceptionHandler((t, e) -> {
                log.error("error from [{}]: {}", t, e.getMessage(), e);
            });
            return newThread;
        });
    }

    /* package */static void shutdownExecutor() {
        if (INSTANCE.delegate != null && !INSTANCE.delegate.isShutdown())
            INSTANCE.delegate.shutdown();
    }

    @Override
    public void execute(Runnable command) {
        if (!delegate.isShutdown())
            delegate.execute(command);
        else
            log.warn("Executorがシャットダウンされています");
    }

    @Override
    public String toString() {
        return "my Executor";
    }
}
