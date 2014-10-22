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

import static com.google.common.base.Preconditions.*;

import javafx.stage.StageStyle;

import net.awairo.common.javafx.ApplicationBase;

/**
 * FavDownloader.
 *
 * @author alalwww
 */
public final class FavDownloader extends ApplicationBase {

    private static FavDownloader app;

    /* package */static FavDownloader instance() {
        return app;
    }

    public FavDownloader() {
        checkState(app == null);
        app = this;
    }

    @Override
    public void startApplication() {

        primaryStage().initStyle(StageStyle.UNIFIED);

        changeSceneTo(Views.Scenes.OAUTH_VIEW)
                .controller().stage().show();
    }

    /**
     * main.
     *
     * @param args command line parameters
     */
    public static void main(String... args) {
        launchApplication(FavDownloader.class, args);
    }

}
