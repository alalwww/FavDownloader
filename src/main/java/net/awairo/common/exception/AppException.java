/*
 * FavDownloader
 *
 * (c) 2014 alalwww
 * https://github.com/alalwww
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package net.awairo.common.exception;

import static com.google.common.base.Preconditions.*;

import net.awairo.common.util.RB;

/**
 * アプリケーション例外.
 *
 * @author alalwww
 */
public final class AppException extends RuntimeException {

    private boolean retryable = true;

    /**
     * Constructor.
     *
     * @param messageKey メッセージキー
     */
    public AppException(String messageKey) {
        super(message(messageKey, (Object[]) null));
    }

    /**
     * Constructor.
     *
     * @param messageKey メッセージキー
     * @param args メッセージフォーマット用の引数
     */
    public AppException(String messageKey, Object... args) {
        super(message(messageKey, args));
    }

    /**
     * Constructor.
     *
     * <p>メッセージを指定しない例外は全てシステムエラーとして扱われます.</p>
     *
     * @param cause 原因となった例外
     */
    public AppException(Throwable cause) {
        this(checkNotNull(cause), "unexpected", (Object[]) null);
        toSystemError();
    }

    /**
     * Constructor.
     *
     * @param cause 原因となった例外
     * @param messageKey メッセージキー
     */
    public AppException(Throwable cause, String messageKey) {
        super(message(messageKey, (Object[]) null), cause);
    }

    /**
     * Constructor.
     *
     * @param cause 原因となった例外
     * @param messageKey メッセージキー
     * @param args メッセージフォーマット用の引数
     */
    public AppException(Throwable cause, String messageKey, Object... args) {
        super(message(messageKey, args), cause);
    }

    /**
     * @return trueはリトライ可能なエラー
     */
    public boolean retryable() {
        return retryable;
    }

    /**
     * システムエラーにします.
     *
     * @return システムエラー例外
     */
    public AppException toSystemError() {
        retryable = false;
        return this;
    }

    private static String message(String messageKey, Object... args) {
        return RB.messageOf(checkNotNull(messageKey), args);
    }
}
