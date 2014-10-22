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

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;

/**
 * コントローラー.
 *
 * @author alalwww
 */
public interface Controller extends Initializable, TaskExecutor, SceneChanger {

    /**
     * コントローラーのロード時に呼ばれます.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources);

}
