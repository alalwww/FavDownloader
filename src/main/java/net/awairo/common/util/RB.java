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

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import lombok.AccessLevel;

import lombok.NoArgsConstructor;

/**
 * リソースバンドルから値を取得.
 *
 * @author alalwww
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RB {

    private static final ResourceBundle rb = Resources.getResourceBundle();

    public static String getString(String key) {
        return rb.getString(key);
    }

    public static String[] getStringArray(String key) {
        return rb.getStringArray(key);
    }

    public static List<String> getStrings(String key) {
        return Arrays.asList(getStringArray(key));
    }

    public static String messageOf(String key) {
        return messageOf(key, (Object[]) null);
    }

    public static String messageOf(String key, Object... args) {
        return Format.of(getString("message." + key), args);
    }

    public static String labelOf(String key) {
        return labelOf(key, (Object[]) null);
    }

    public static String labelOf(String key, Object... args) {
        return Format.of(getString("label." + key), args);
    }

}
