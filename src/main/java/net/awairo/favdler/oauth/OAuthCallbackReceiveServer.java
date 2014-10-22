/*
 * FavDownloader
 *
 * (c) 2014 alalwww
 * https://github.com/alalwww
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package net.awairo.favdler.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import net.awairo.common.exception.AppException;
import net.awairo.common.javafx.MyTask;
import net.awairo.common.javafx.ServiceBase;

/**
 * Twitter認証からのコールバック先となるローカルHTTPサーバー.
 *
 * @author alalwww
 */
@Log4j2
@RequiredArgsConstructor
/* package */final class OAuthCallbackReceiveServer extends ServiceBase<String, OAuthCallbackReceiveServer> {

    private static final Pattern KEY_VALUE_SUPLITTER = Pattern.compile("=");

    private ServerSocket socket;

    int getLocalPort() {
        return socket.getLocalPort();
    }

    @Override
    public void start() {
        try {
            socket = new ServerSocket();
            socket.bind(null, 1);
        } catch (IOException e) {
            throw new AppException(e);
        }

        super.start();
    }

    private final AtomicBoolean running = new AtomicBoolean();

    @Override
    protected Task<String> newTask() {
        return MyTask.of(this::getOauthVerifier)
                .ifDone(() -> running.set(false));
    }

    @Override
    public boolean cancel() {
        log.debug("キャンセル{}", this);

        boolean b = super.cancel();

        if (socket != null) {
            running.set(false);
            try {
                socket.close();
            } catch (IOException e) {
                log.error("ソケットクローズに失敗", e);
                throw new AppException(e);
            } finally {
                socket = null;
            }
        }

        return b;
    }

    private String getOauthVerifier() {

        running.set(true);

        boolean responseSucceeded = false;

        try (Socket client = socket.accept()) {
            try {

                String oauthVerifier = getOAuthVerifierFrom(parseRequest(client.getInputStream()));
                log.debug("oauthVerifier:{}", oauthVerifier);

                PrintStream out = new PrintStream(client.getOutputStream());
                out.println("HTTP/1.0 202 Accepted");
                out.println("Content_Type：text/plain");
                out.println("");
                out.println("twitter oauth successed!");
                out.flush();

                responseSucceeded = true;
                return oauthVerifier;
            } catch (IOException e) {
                // IOEはおそらく復旧できないしレスポンス返すのもたぶん無理なのでスルー
                throw e;
            } catch (Exception e) {
                log.trace("例外が発生したので失敗を返却", e);

                PrintStream out = new PrintStream(client.getOutputStream());
                out.println("HTTP/1.0 401 Unauthorized");
                out.println("Content_Type：text/plain");
                out.println("");
                out.println("twitter oauth failed!");
                out.flush();

                responseSucceeded = true;
                if (e instanceof AppException)
                    throw e;
                throw new AppException(e);
            }

        } catch (IOException e) {
            if (!running.get()) {
                log.trace("コールバック受信サーバーで問題発生 running={}, レスポンス返却成功={}", running, responseSucceeded, e);
                return null; // 終了してるので何返してもたぶんOK
            }

            log.error("コールバック受信サーバーで問題発生 running={}, レスポンス返却成功={}", running, responseSucceeded, e);
            throw new AppException(e);
        }
    }

    private static List<String> parseRequest(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        List<String> request = Lists.newArrayList();

        String s = reader.readLine();
        while (reader.ready() && s != null) {
            request.add(s);
            s = reader.readLine();
        }

        log.debug("request:{}", request);
        return request;
    }

    private static String getOAuthVerifierFrom(List<String> request) {

        for (String line : request) {

            if (!line.startsWith("GET /auth"))
                continue;

            line = line.substring(line.indexOf('?') + 1, line.indexOf(" HTTP"));
            for (String keyValue : line.replace("&amp;", "&").split("&")) {
                String[] kvArray = KEY_VALUE_SUPLITTER.split(keyValue);

                if (Objects.equal(kvArray[0], "oauth_verifier"))
                    return kvArray[1];

                if (Objects.equal(kvArray[0], "denied")) {
                    log.warn("Twitter認証連携がキャンセルされました。");
                    throw new AppException("cancel_twitter_oauth");
                }
            }
        }

        log.error("Twitterからのコールバックが想定されていない形式です。: {}", request);
        throw new AppException("failed_to_twitter_oauth");
    }

}
