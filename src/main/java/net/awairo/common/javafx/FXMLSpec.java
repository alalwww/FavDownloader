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

import java.net.URL;

import net.awairo.common.util.Resources;

/**
 * FXML情報.
 *
 * @author alalwww
 * @param <C> コントローラータイプ
 */
public class FXMLSpec<C extends Controller> {

    /**
     * FXMLのパスに紐づく新たなFXML情報を生成.
     *
     * @param fxmlPath
     * @return FXML情報
     */
    public static <C extends Controller> FXMLSpec<C> of(String fxmlPath) {
        return new FXMLSpec<>(fxmlPath);
    }

    /**
     * FXMLのURLに紐づく新たなFXML情報を生成.
     *
     * @param fxmlUrl
     * @return FXML情報
     */
    public static <C extends Controller> FXMLSpec<C> of(URL fxmlUrl) {
        return new FXMLSpec<>(fxmlUrl);
    }

    /**
     * FXMLファイルのURL.
     */
    public final URL url;

    /* package */FXMLSpec(String fxmlPath) {
        this(Resources.getView(checkNotNull(fxmlPath)));
    }

    /* package */FXMLSpec(URL url) {
        this.url = checkNotNull(url);
    }

}
