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
import java.util.ResourceBundle;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * シーンとしてロードされたFXMLのコントローラー.
 *
 * @author alalwww
 */
public interface SceneController extends Controller {

    /**
     * 初期化処理の後に行う追加の初期化処理です。
     *
     * <p>stageなどにアクセスできるようになったタイミングで実行されます。</p>
     */
    void postInitialize();

    /**
     * このコントローラーに対応するシーンの追加されているステージを取得します.
     *
     * <p>このメソッドは{@link #initialize(URL, ResourceBundle)}がコールされたタイミングではまだ参照できません。{@link #postInitialize()}
     * がコールされたタイミングより参照できるようになります。</p>
     *
     * @return このコントローラーに紐づくシーンを設定したステージ
     */
    default Stage stage() {
        Stage stage = FXMLLoaders.controllersMap.get(this);
        checkState(stage != null);
        return stage;
    }

    /**
     * このコントローラーに対応するシーンを取得します.
     *
     * <p>このメソッドは{@link #initialize(URL, ResourceBundle)}がコールされたタイミングではまだ参照できません。{@link #postInitialize()}
     * がコールされたタイミングより参照できるようになります。</p>
     *
     * @return このコントローラーに紐付いたシーン
     */
    default Scene scene() {
        Scene scene = stage().getScene();
        checkState(scene != null);
        return scene;
    }

}
