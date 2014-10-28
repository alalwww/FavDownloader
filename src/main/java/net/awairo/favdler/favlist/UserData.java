/*
 * FavDownloader
 *
 * (c) 2014 alalwww
 * https://github.com/alalwww
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package net.awairo.favdler.favlist;

import java.io.File;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * シーンコントローラーとセルのコントローラーで共有するユーザーデータ.
 *
 * @author alalwww
 */
@Data
@Accessors(fluent = true)
final class UserData {

    private File userHome;
}
