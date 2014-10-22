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

import java.util.Properties;

import lombok.extern.log4j.Log4j2;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * version.
 *
 * @author alalwww
 */
@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Version {

    /**
     * バージョン情報文字列.
     */
    public static final String value;

    static {
        String s;
        try {
            Properties prop = Resources.loadProperties("version.properties");
            s = prop.getProperty("version", "unknown");
        } catch (RuntimeException ignore) {
            log.warn(ignore);
            s = "unknown";
        }
        value = s;
    }
}
