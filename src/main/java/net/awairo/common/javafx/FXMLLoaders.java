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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.WeakHashMap;

import lombok.experimental.Delegate;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import net.awairo.common.exception.AppException;
import net.awairo.common.util.Resources;

/**
 * FXMLロード関連の処理.
 *
 * @author alalwww
 */
@Log4j2
public final class FXMLLoaders {

    private static final ResourceBundle rb = Resources.getResourceBundle();

    /** ロード済みコントローラーとそのステージのマップ. */
    /* package */static final Map<SceneController, Stage> controllersMap = new WeakHashMap<>();

    /**
     * 新たなローダーを作成.
     *
     * @param fxml FXML
     * @return ローダー
     */
    public static <C extends Controller> Loader<C> newLoader(FXMLSpec<C> fxml) {
        return newLoader(fxml, Optional.empty());
    }

    /**
     * 新たなローダーを作成.
     *
     * <p>ただしコントローラーが設定されたFXMLに対し新たなコントローラーを設定しようとすると例外が発生する。</p>
     *
     * @param fxml FXML
     * @param controller コントローラーとして設定するインスタンス
     * @return ローダー
     */
    public static <C extends Controller> Loader<C> newLoader(FXMLSpec<C> fxml, C controller) {
        return newLoader(fxml, Optional.of(controller));
    }

    private static <C extends Controller> Loader<C> newLoader(FXMLSpec<C> fxml, Optional<C> controller) {
        final Loader<C> loader = new Loader<>(new FXMLLoader(fxml.url, rb));
        controller.ifPresent(loader::setController);
        return loader;
    }

    /**
     * 指定したステージにFXMLをロードしそのコントローラーを取得.
     *
     * @param stage ステージ
     * @param fxml FXML
     * @return FXMLに紐づくコントローラー
     */
    public static <C extends SceneController> C loadViewOn(Stage stage, FXMLSpec<C> fxml) {
        return loadViewOn(stage, fxml, Optional.empty());
    }

    /**
     * 指定したステージにFXMLをロードしそのコントローラーを取得.
     *
     * @param stage ステージ
     * @param fxmlPath FXML
     * @param controller 設定するコントローラー
     * @return 設定したコントローラー
     */
    public static <C extends SceneController> C loadViewOn(Stage stage, FXMLSpec<C> fxml, C controller) {
        return loadViewOn(stage, fxml, Optional.of(controller));
    }

    /* package */static <C extends SceneController> C loadViewOn(Stage stage, FXMLSpec<C> fxml, Optional<C> controller) {

        final Loader<C> loader = newLoader(fxml, controller);

        try (InputStream is = fxml.url.openStream()) {
            Parent parent = loader.load(is);
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.sizeToScene();
            C c = loader.controller();
            controllersMap.put(c, stage);
            c.postInitialize();
            return c;
        } catch (IOException e) {
            log.error("fxmlのロードに失敗しました", e);
            throw new AppException(e);
        }
    }

    /**
     * FXMLLoaderラッパー.
     */
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Loader<C extends Controller> {

        public C controller() {
            return loader.getController();
        }

        private void setController(C controller) {
            loader.setController(controller);
        }

        /**
         * ロードします.
         *
         * @return ロードしたFXMLのルートノード
         */
        public <T> T load() {
            try {
                return loader.load();
            } catch (IOException e) {
                log.error("FXMLのロードに失敗しました", e);
                throw new AppException(e);
            }
        }

        @Delegate(excludes = Excludes.class)
        private final FXMLLoader loader;

        /**
         * 無視するインターフェイス
         */
        private interface Excludes {
            <T> T getController();

            void setController(Object controller);

            public <T> T load() throws IOException;
        }

    }
}
