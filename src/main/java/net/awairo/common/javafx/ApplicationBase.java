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

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

import net.awairo.common.util.RB;
import net.awairo.common.util.Resources;
import net.awairo.common.util.Version;

/**
 * アプリケーションベース.
 *
 * @author alalwww
 */
@Log4j2
public abstract class ApplicationBase extends Application implements SceneChanger {

    public static final int STUTUS_EXIT = 0;
    public static final int STUTUS_ERROR = 1;

    private static final AtomicInteger status = new AtomicInteger(STUTUS_EXIT);

    private Stage stage;

    //---------------------------------------------

    @Override
    public final void init() throws Exception {
        log.trace("init()");
        log.info("Java version: {}", System.getProperty("java.version"));
        log.info("JavaFX version: {}", System.getProperty("javafx.version"));
        log.info("VM: {}", System.getProperty("java.vm.name"));
        log.info("OS: {} ({})", System.getProperty("os.name"), System.getProperty("os.arch"));

        Resources.getResourceBundle(); // initialize
        Dialog.application = this;
        TaskExecutorImpl.restartExecutor();
        initApplication();
    }

    @Override
    public final void start(Stage primaryStage) {
        log.trace("start({})", primaryStage);

        stage = primaryStage;

        SceneChangeManager.initialize(stage);

        stage.setTitle(getPrimaryStageTitle());

        stage.setOnCloseRequest(event -> {
            log.trace("window close event");
            TaskExecutorImpl.shutdownExecutor();
        });

        startApplication();
    }

    @Override
    public final void stop() throws Exception {
        log.trace("stop()");
        TaskExecutorImpl.shutdownExecutor();
        stopApplication();
    }

    //---------------------------------------------

    /** アプリケーションの初期化処理. */
    protected void initApplication() {}

    /** アプリケーションの開始処理. */
    protected abstract void startApplication();

    /** アプリケーションの停止処理. */
    protected void stopApplication() {}

    /**
     * @return メインウィンドウのタイトル.
     */
    protected String getPrimaryStageTitle() {
        return RB.getString("title.primary_stage");
    }

    /**
     * @return ルートステージ
     */
    protected Stage primaryStage() {
        checkState(stage != null);
        return stage;
    }

    /**
     * 名前のついた起動引数を取得します.
     *
     * @see javafx.application.Application.Parameters#getNamed()
     *
     * @param name 引数名
     * @return 値
     */
    protected Optional<String> getNamedParameter(String name) {
        return Optional.ofNullable(getParameters().getNamed().get(name));
    }

    //---------------------------------------------

    /**
     * アプリケーションの起動.
     *
     * @param clazz 自身のクラス
     * @param args command line parameters
     */
    protected static <A extends ApplicationBase> void launchApplication(Class<A> clazz, String... args) {
        checkNotNull(clazz);

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            try {
                log.error("unexpectec exception", e);
                status.set(STUTUS_ERROR);
                Platform.exit();
            } catch (Error e2) {
                // TODO: Log4j 2.1 以降に更新したら見直す
                // https://issues.apache.org/jira/browse/LOG4J2-832 が修正されるまでコケることあるっぽい
                System.err.println("Log4J2のエラーによりログの出力に失敗");

                System.err.println("発生した例外");
                e.printStackTrace(System.err);

                System.err.println("ログ処理の例外");
                e2.printStackTrace(System.err);

                System.err.println("アプリケーションを終了します");
                status.set(STUTUS_ERROR);
                Platform.exit();
            }
        });

        try {
            log.info("start application. version: {}", Version.value);

            launch(clazz, args);

        } catch (Throwable e) {

            if (e instanceof ThreadDeath)
                return;

            status.set(STUTUS_ERROR);

            log.fatal("unexpected exception", e);

        } finally {
            try {
                log.info("stop application.");
                System.exit(status.get());
            } catch (Throwable e) {
                if (e instanceof ThreadDeath)
                    return;
                e.printStackTrace(System.err);
            }
        }
    }
}
