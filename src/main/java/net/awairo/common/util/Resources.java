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

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import net.awairo.common.exception.AppException;

/**
 * リソースへのアクセスを行うための共通処理.
 *
 * @author alalwww
 */
@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Resources {

    private static final ConcurrentMap<String, URL> URL_CACHE = Maps.newConcurrentMap();
    private static final String LANG_RESOURCE_NAME = "lang.resources";

    public static final String VIEW_DIR = "view/fxml/";

    private static final ResourceBundle rb;

    /**
     * @return リソースバンドル
     */
    public static ResourceBundle getResourceBundle() {
        return rb;
    }

    static {
        try {
            rb = ResourceBundle.getBundle(LANG_RESOURCE_NAME);
        } catch (RuntimeException e) {
            log.fatal("not found lang/resources.properties", e);
            throw e;
        }
    }

    /**
     * view/fxml/ 以下のリソースを取得します.
     *
     * @param viewResourceName ビューリソース名
     * @return リソース
     */
    public static URL getView(String viewResourceName) {
        return get(VIEW_DIR + viewResourceName);
    }

    /**
     * プロパティファイルを読み込みます.
     *
     * @param resourceName プロパティファイルのリソース名
     * @return プロパティ
     */
    public static Properties loadProperties(String resourceName) {
        return loadProperties(new Properties(), resourceName);
    }

    /**
     * プロパティファイルを読み込みます.
     *
     * @param prop ロード先のプロパティ
     * @param resourceName プロパティファイルのリソース名
     * @return プロパティ
     */
    public static Properties loadProperties(Properties prop, String resourceName) {
        return loadProperties(prop, get(resourceName));
    }

    /**
     * プロパティファイルを読み込みます.
     *
     * @param resourceName プロパティファイルのリソース
     * @return プロパティ
     */
    public static Properties loadProperties(URL url) {
        return loadProperties(new Properties(), url);
    }

    /**
     * プロパティファイルを読み込みます.
     *
     * @param prop ロード先のプロパティ
     * @param resourceName プロパティファイルのリソース
     * @return プロパティ
     */
    public static Properties loadProperties(Properties prop, URL url) {
        checkNotNull(prop);

        try (InputStream is = com.google.common.io.Resources.asByteSource(url).openBufferedStream()) {
            prop.load(is);
        } catch (IOException e) {
            log.error("プロパティファイル({})の読み込みに失敗しました", url, e);
            throw new AppException(e);
        }

        return prop;
    }

    /**
     * リソースを取得します.
     *
     * @param resourceName リソース名
     * @return リソースURL
     */
    public static URL get(String resourceName) {

        return URL_CACHE.computeIfAbsent(resourceName, s -> {
            try {
                return com.google.common.io.Resources.getResource(resourceName);
            } catch (RuntimeException e) {
                log.error("リソースの取得に失敗しました", e);
                throw e;
            }
        });
    }
}
