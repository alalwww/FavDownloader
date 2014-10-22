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

import static net.awairo.common.javafx.SceneChangeManager.*;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.awairo.common.javafx.FXMLSpec;
import net.awairo.common.javafx.SceneChangeManager.SceneSpec;
import net.awairo.favdler.favlist.TweetListCell;
import net.awairo.favdler.favlist.TweetListView;
import net.awairo.favdler.oauth.TwitterOAuthView;

/**
 * FXMLリソース定義.
 *
 * @author alalwww
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Views {

    /**
     * シーンFXML.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Scenes {
        public static final SceneSpec<TwitterOAuthView> OAUTH_VIEW = newSceneSpec("oauth/TwitterOAuthView.fxml");
        public static final SceneSpec<TweetListView> TWEET_LIST_VIEW = newSceneSpec("favlist/TweetListView.fxml");

    }

    public static final FXMLSpec<TweetListCell> TWEET_LIST_CELL = FXMLSpec.of("favlist/TweetListCell.fxml");
}
