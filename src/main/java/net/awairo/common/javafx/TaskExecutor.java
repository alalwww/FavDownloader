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

import javafx.concurrent.Task;

/**
 * タスクを実行機能を追加します.
 *
 * @author alalwww
 */
public interface TaskExecutor {

    /**
     * タスクを実行.
     *
     * @param task タスク
     */
    default void execute(Task<?> task) {
        TaskExecutorImpl.instance().execute(task);
    }

    /**
     * タスクを実行.
     *
     * @param task タスク
     */
    default void execute(TaskBase<?, ?> task) {
        TaskExecutorImpl.instance().execute(task);
    }

}
