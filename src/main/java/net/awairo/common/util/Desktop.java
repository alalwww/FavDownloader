/*
 * FavDownloader
 *
 * (c) 2014 alalwww
 * https://github.com/alalwww
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package net.awairo.common.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import lombok.extern.log4j.Log4j2;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.awairo.common.exception.AppException;

/**
 * {@link java.awt.Desktop} wrapper.
 *
 * @author alalwww
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public final class Desktop {
    private static final java.awt.Desktop instance = java.awt.Desktop.getDesktop();

    /**
     * システムデフォルトブラウザでURIを開く.
     *
     * @param uri 開くURI文字列
     */
    public static void openURI(String uri) {
        try {
            openURI(new URI(uri));
        } catch (URISyntaxException e) {
            log.warn("不正なURIシンタックスです。", e);
            throw new AppException(e, "illegal_uri_format");
        }
    }

    /**
     * システムデフォルトブラウザでURIを開く.
     *
     * @param format 開くURIのフォーマット
     * @param args フォーマットに当てはめる値
     */
    public static void openURI(String format, Object... args) {
        if (args == null || args.length == 0)
            openURI(format);
        else
            openURI(String.format(format, args));
    }

    /**
     * システムデフォルトブラウザでURIを開く.
     *
     * @param uri 開くURI
     */
    public static void openURI(URI uri) {
        try {
            instance.browse(uri);
        } catch (IOException e) {
            log.warn("URI({})が開けませんでした。", uri, e);
            throw new AppException(e, "failed_to_open_uri");
        }
    }

}
