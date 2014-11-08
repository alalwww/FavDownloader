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

/**
 * タスク.
 *
 * @author alalwww
 * @param <R> タスクの実行結果タイプ
 */
public abstract class MyTask<R> extends TaskBase<R, MyTask<R>> {

    /**
     * タスクのアップデートや戻り値が必要ない単純なタスクを生成します.
     *
     * @param task タスクの処理内容
     * @param <R> タスクの実行結果タイプ
     * @return 新しいタスク
     */
    public static <R> MyTask<R> of(MyTask.Function<R> task) {
        return new MyTask.Simple<R>(task);
    }

    @Override
    final R _execute() throws Exception {
        return execute();
    }

    /**
     * タスクを実行.
     *
     * <p>このメソッドおよびこのメソッドより実行する処理はFXのメインスレッド以外で実行される。ビュー操作などはできない。</p>
     *
     * @throws Exception 継続不可能な問題が発生した場合
     */
    protected abstract R execute() throws Exception;

    /**
     * タスクの単純な処理を定義する関数.
     *
     * @author alalwww
     */
    @FunctionalInterface
    public interface Function<R> {

        /**
         * 処理を実行します.
         *
         * @throws Exception 継続不可能な問題が発生した場合
         */
        R execute() throws Exception;

        /**
         * このタスクの実行後に指定したタスクを実行します.
         *
         * @param afterTask
         * @return 合成したタスク
         */
        default MyTask.Function<R> andThen(MyTask.Function<R> afterTask) {
            checkNotNull(afterTask, "afterTask");

            return () -> {
                execute();
                return afterTask.execute();
            };
        }

        /**
         * 結果を返さないタスクに変換します.
         *
         * @return 結果を返さないタスク
         */
        default NoResultTask.Function noResult() {
            return () -> execute();
        }

        /**
         * {@link MyTask.Function}関数に型変換します.
         *
         * @param supplier 変換元の関数
         * @return タスクの処理関数
         */
        static <R> MyTask.Function<R> of(java.util.function.Supplier<? extends R> supplier) {
            return () -> supplier.get();
        }

        /**
         * {@link MyTask.Function}関数に型変換します.
         *
         * @param runnable 変換元の関数
         * @return タスクの処理関数
         */
        static MyTask.Function<Void> of(java.lang.Runnable runnable) {
            return () -> {
                runnable.run();
                return null;
            };
        }

        /**
         * {@link MyTask.Function}関数に型変換します.
         *
         * @param callable 変換元の関数
         * @return タスクの処理関数
         */
        static <R> MyTask.Function<R> of(java.util.concurrent.Callable<? extends R> callable) {
            return () -> callable.call();
        }
    }

    @RequiredArgsConstructor
    private static final class Simple<R> extends MyTask<R> {
        private final MyTask.Function<R> task;

        @Override
        protected R execute() throws Exception {
            return task.execute();
        }
    }

}
