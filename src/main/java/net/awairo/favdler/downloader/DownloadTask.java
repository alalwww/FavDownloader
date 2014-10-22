/*
 * FavDownloader
 *
 * (c) 2014 alalwww
 * https://github.com/alalwww
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package net.awairo.favdler.downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.ByteStreams;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import net.awairo.common.exception.AppException;
import net.awairo.common.javafx.NoResultTask;
import net.awairo.common.util.RB;

/**
 * ダウンロード処理.
 *
 * @author alalwww
 */
@Log4j2
@RequiredArgsConstructor
public class DownloadTask extends NoResultTask {

    private final List<FromSpec> froms;
    private final File outDirectory;

    @Override
    protected void execute() throws Exception {
        checkState(outDirectory);

        long max = froms.size();

        // シーケンシャルな処理なので同期は不要だけどフィールド値にしてスコープ広げる必要もないのでカウンタのかわりとして使用
        AtomicLong workDone = new AtomicLong();

        updateTitle(RB.labelOf("download_progress_title"));
        updateProgress(workDone.get(), max);

        froms.forEach(fromSpec -> {
            updateMessage(RB.labelOf("download_message", fromSpec));
            download(fromSpec, outDirectory);
            updateProgress(workDone.incrementAndGet(), max);
        });
    }

    @VisibleForTesting
    static void checkState(File outDir) {
        if (!outDir.exists()) {
            log.warn("出力先が存在しません。({})", outDir);
            throw new AppException("downloaddir_not_existed", outDir);
        }

        if (!outDir.isDirectory()) {
            log.warn("出力先がディレクトリではありません。({})", outDir);
            throw new AppException("downloaddir_was_not_a_directory", outDir);
        }

        if (!outDir.canWrite()) {
            log.warn("書き込み権限がありません。({})", outDir);
            throw new AppException("downloaddir_can_not_write", outDir);
        }
    }

    @VisibleForTesting
    static boolean download(FromSpec fromSpec, File outDir) {
        URLConnection connection = fromSpec.openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        File snDir = new File(outDir, fromSpec.screenName());
        if (!snDir.exists()) {
            snDir.mkdir();
        }
        if (!snDir.isDirectory()) {
            log.warn("出力先がディレクトリではありません。({})", snDir);
            throw new AppException("downloaddir_was_not_a_directory", snDir);
        }

        try (InputStream from = connection.getInputStream();
                OutputStream to = new FileOutputStream(new File(snDir, fromSpec.statusId() + "_" + fromSpec.name()))) {
            ByteStreams.copy(from, to);
        } catch (IOException e) {
            log.warn("ダウンロードに失敗しました。", e);
            return false;
        }

        return true;
    }

}
