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
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import lombok.AccessLevel;

import lombok.NoArgsConstructor;
import javafx.stage.Stage;
import lombok.experimental.ExtensionMethod;
import lombok.extern.log4j.Log4j2;

import net.awairo.common.extension.Extensions;

/**
 * シーン変更マネージャ.
 *
 * @author alalwww
 */
@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SceneChangeManager {

    private static Stage stage;

    /* package */static void initialize(Stage primaryStage) {
        log.trace("initialize()");

        checkNotNull(primaryStage);
        checkState(stage == null);

        stage = primaryStage;
    }

    /**
     * 新しいシーン情報を作ります.
     *
     * @param fxmlPath
     * @return
     */
    public static <C extends SceneController> SceneSpec<C> newSceneSpec(String fxmlPath) {
        return new SceneSpec<>(fxmlPath);
    }

    /**
     * 指定したシーンに変更します.
     *
     * @param sceneSpec 次のシーンの情報
     * @return 次のシーン情報
     */
    public static <C extends SceneController> NextSceneContext<C> changeSceneTo(SceneSpec<C> sceneSpec) {
        log.trace("changeSceneTo({})", sceneSpec);

        NextSceneContext<C> nextScene = sceneSpec.toNextScene();
        C controller = FXMLLoaders.loadViewOn(stage, nextScene);
        nextScene.controller(controller);
        controller.stage().show();
        return nextScene;
    }

    /**
     * シーン情報.
     *
     * @author alalwww
     * @param <C>
     */
    public static class SceneSpec<C extends SceneController> extends FXMLSpec<C> {
        private SceneSpec(String fxmlPath) {
            super(fxmlPath);
        }

        private SceneSpec(URL url) {
            super(url);
        }

        private NextSceneContext<C> toNextScene() {
            return new NextSceneContext<>(this);
        }
    }

    /**
     * 次のシーン情報.
     *
     * @author alalwww
     * @param <C>
     */
    @ExtensionMethod(Extensions.Options.class)
    public static final class NextSceneContext<C extends SceneController> extends SceneSpec<C> {
        private Optional<Consumer<C>> callback = Optional.empty();
        private C controller;

        private NextSceneContext(SceneSpec<C> sc) {
            super(sc.url);
        }

        /* package */void controller(C controller) {
            this.controller = controller;
            callback.ifPresent(consumer -> consumer.accept(controller));
        }

        public C controller() {
            checkState(controller != null);
            return controller;
        }

        public NextSceneContext<C> callback(Consumer<C> callback) {
            checkNotNull(callback);

            this.callback = this.callback
                    .map(before -> before.andThen(callback))
                    .orElse(callback)
                    .option();

            return this;
        }

        @Override
        public String toString() {
            return String.format("url: %s, controller: %s", url, Objects.toString(controller, "empty"));
        }
    }

}
