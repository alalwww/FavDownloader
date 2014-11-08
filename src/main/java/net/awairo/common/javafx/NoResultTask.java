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

import lombok.RequiredArgsConstructor;

import net.awairo.common.function.ThrowableSupplier;

/**
 * 結果を返す必要のない後はやるだけタスク.
 *
 * @author alalwww
 */
public abstract class NoResultTask extends TaskBase<Void, NoResultTask> {

    /**
     * タスクのアップデート、戻り値が必要ない単純なタスクを生成します.
     *
     * @param task タスクの処理内容
     * @return 新しいタスク
     */
    public static NoResultTask of(NoResultTask.Function task) {
        return new NoResultTask.Simple(task);
    }

    @Override
    final Void _execute() throws Exception {
        execute();
        return null;
    }

    /**
     * 結果を返さないタスクを実行.
     *
     * <p>このメソッドおよびこのメソッドより実行する処理はFXのメインスレッド以外で実行される。ビュー操作などはできない。</p>
     *
     * @throws Exception 継続不可能な問題が発生した場合
     */
    protected abstract void execute() throws Exception;

    /**
     * タスクの単純な処理を定義する関数.
     *
     * @author alalwww
     */
    @FunctionalInterface
    public interface Function {

        /**
         * 処理を実行します.
         *
         * @throws Exception 継続不可能な問題が発生した場合
         */
        void execute() throws Exception;

        /**
         * このタスクの実行後に指定したタスクを実行します.
         *
         * @param afterTask
         * @return 合成したタスク
         */
        default NoResultTask.Function andThen(NoResultTask.Function afterTask) {
            checkNotNull(afterTask, "afterTask");

            return () -> {
                execute();
                afterTask.execute();
            };
        }

        /**
         * 完了後に結果を返すタスクに変換します.
         *
         * @param supplier 結果のサプライヤー
         * @return 結果を返すタスク
         */
        default <R> MyTask.Function<R> result(ThrowableSupplier<? extends R> supplier) {
            checkNotNull(supplier, "supplier");

            return () -> {
                execute();
                return supplier.get();
            };
        }

        /**
         * {@link NoResultTask.Function}関数に型変換します.
         *
         * @param runnable 変換元の関数
         * @return タスクの処理関数
         */
        static NoResultTask.Function of(java.lang.Runnable runnable) {
            return () -> runnable.run();
        }

        /**
         * {@link NoResultTask.Function}関数に型変換します.
         *
         * @param callable 変換元の関数
         * @return タスクの処理関数
         */
        static <R> NoResultTask.Function of(java.util.concurrent.Callable<R> callable) {
            return () -> callable.call();
        }
    }

    @RequiredArgsConstructor
    private static final class Simple extends NoResultTask {
        private final NoResultTask.Function task;

        @Override
        protected void execute() throws Exception {
            task.execute();
        }
    }
}
