/*
 * FavDownloader
 *
 * (c) 2014 alalwww
 * https://github.com/alalwww
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package net.awairo.favdler.core;

import java.util.LinkedHashMap;
import java.util.Map;

import javafx.scene.image.Image;
import lombok.experimental.ExtensionMethod;
import lombok.extern.log4j.Log4j2;

import net.awairo.common.extension.Extensions;

/**
 * イメージキャッシュ.
 *
 * @author alalwww
 */
@Log4j2
public final class ImageCache {

    private static final Cache cache = new Cache();

    public static Image getOrNew(String url) {
        return cache.computeIfAbsent(url, Image::new);
    }

    @ExtensionMethod({ Extensions.Strings.class })
    private static final class Cache extends LinkedHashMap<String, Image> {

        private final int maxSize;

        private Cache() {
            super(initialCapacity(), loadFactor(), true);
            maxSize = maxSize();
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Image> eldest) {
            boolean remove = size() > maxSize;
            if (remove && log.isDebugEnabled())
                log.debug("remove entry: size{}, entry:{}", size(), eldest);
            return remove;
        }

        private static int initialCapacity() {
            return FavDownloader.instance()
                    .getParameters()
                    .getNamed()
                    .get("FD:cacheInitialCapacity")
                    .tryParseInt()
                    .orElse(1 << 6); // 64
        }

        private static float loadFactor() {
            return FavDownloader.instance()
                    .getParameters()
                    .getNamed()
                    .get("FD:cacheLoadFactor")
                    .tryParseFloat()
                    .orElse(0.75f);// HashMap#DEFAULT_LOAD_FACTOR
        }

        private static int maxSize() {
            return FavDownloader.instance()
                    .getParameters()
                    .getNamed()
                    .get("FD:cacheMaxSize")
                    .tryParseInt()
                    .orElse(500);
        }

    }
}
