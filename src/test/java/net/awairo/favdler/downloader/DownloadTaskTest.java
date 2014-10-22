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

import org.junit.Test;

/**
 * @author alalwww
 */
public class DownloadTaskTest {

    @Test
    public void testDownload() throws Exception {

        String url = "http://www.awairo.net/images/busy_banner.png";
        File toDir = new File("bin");
        System.out.println(toDir.getAbsolutePath());
        DownloadTask.download(new FromSpec(1000L, "sn", url, "json"), toDir);

    }

    @Test
    public void testCheckState() throws Exception {
        //        fail();
    }

    @Test
    public void testCreateOutFile() throws Exception {
        //        fail();
    }
}
