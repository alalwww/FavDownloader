/*
 * FavDownloader
 *
 * (c) 2014 alalwww
 * https://github.com/alalwww
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package net.awairo.favdler.twitter;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

import net.awairo.common.exception.AppException;

/**
 * Twitterへのアクセッサー.
 *
 * @author alalwww
 */
@EqualsAndHashCode
@RequiredArgsConstructor
public final class TwitterAccessor {

    private final Twitter twitter;

    /** @return Twitter. */
    public Twitter twitter() {
        return twitter;
    }

    /** @return アクセストークン. */
    public AccessToken accessToken() {
        try {
            return twitter.getOAuthAccessToken();
        } catch (TwitterException e) {
            throw new AppException(e);
        }
    }

    @Override
    public String toString() {
        return accessToken().toString();
    }
}
