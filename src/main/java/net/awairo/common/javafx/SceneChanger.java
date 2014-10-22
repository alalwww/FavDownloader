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

/**
 * シーンの切り替え機能.
 *
 * @author alalwww
 */
public interface SceneChanger {

    /**
     * シーン変更.
     *
     * @param nextScene 次のシーン
     * @return シーン変更イベント
     */
    default <C extends SceneController> SceneChangeManager.NextSceneContext<C> changeSceneTo(SceneChangeManager.SceneSpec<C> nextScene) {
        return SceneChangeManager.changeSceneTo(nextScene);
    }
}
