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

import java.util.Locale;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * {@link String#format(String, Object...)}のラップ処理.
 *
 * @author alalwww
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Format {

    public static String of(String messageOrFormat, Object... args) {
        return args == null || args.length == 0 ? messageOrFormat : String.format(messageOrFormat, args);
    }

    public static String of(Locale lolace, String messageOrFormat, Object... args) {
        return args == null || args.length == 0 ? messageOrFormat : String.format(lolace, messageOrFormat, args);
    }
}
