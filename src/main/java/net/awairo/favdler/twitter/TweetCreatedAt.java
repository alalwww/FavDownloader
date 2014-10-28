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

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import net.awairo.common.util.RB;

/**
 * createdAtのラッパー.
 *
 * <p>Java8 time API　仕様</p>
 * <p>投稿後からの経過時間が短時間の場合は、相対表記に変換し値をキャッシュせず、再描画時に表示を更新できるようにし、長時間の場合は再フォーマットしないようキャッシュしている。</p>
 *
 * @author alalwww
 */
public final class TweetCreatedAt {

    /** 「現在」. */
    private static final String labelFormatRelNow = RB.labelOf("createdAt.rel.now");
    /** 相対表記「n 秒」. */
    private static final String labelFormatRelSecond = RB.labelOf("createdAt.rel.second");
    /** 相対表記「n 分」. */
    private static final String labelFormatRelMinute = RB.labelOf("createdAt.rel.minute");

    /** 絶対表記「HH:mm」. */
    private static final DateTimeFormatter labelFormatHour = DateTimeFormatter.ofPattern(RB.labelOf("createdAt.hour"));
    /** 絶対表記「M月d日」. */
    private static final DateTimeFormatter labelFormatMonth = DateTimeFormatter.ofPattern(RB.labelOf("createdAt.month"));
    /** 絶対表記「yyyy年M月d日」. */
    private static final DateTimeFormatter labelFormatYear = DateTimeFormatter.ofPattern(RB.labelOf("createdAt.year"));

    /** ツールチップ用絶対表記「yyyy年M月d日 - HH:mm:ss」. */
    private static final DateTimeFormatter tooltipFormat = DateTimeFormatter.ofPattern(RB.labelOf("createdAt.tooltip"));

    private final ZonedDateTime createdAt;
    private String labelString;
    private String tooltipString;

    /* package */TweetCreatedAt(Date createdAt) {
        this.createdAt = createdAt.toInstant().atZone(ZoneId.systemDefault());
    }

    /**
     * @return ツイートの作成日時
     */
    public ZonedDateTime get() {
        return createdAt;
    }

    /**
     * @return ラベル用文字列表現
     */
    public String toLabelString() {

        // 遅延評価
        if (labelString != null)
            return labelString;

        return formatLabel();
    }

    /**
     * @return ツールチップ用文字列表現
     */
    public String toTooltipString() {

        // 遅延評価
        if (tooltipString == null)
            tooltipString = createdAt.format(tooltipFormat);

        return tooltipString;
    }

    private String formatLabel() {

        ZonedDateTime now = ZonedDateTime.now();

        // 絶対表記、キャッシュあり

        // 年が違う yyyy年M月d日
        if (now.getYear() != createdAt.getYear()) {
            labelString = createdAt.format(labelFormatYear);
            return labelString;
        }

        // 月日が違う M月d日
        if (now.getDayOfYear() != createdAt.getDayOfYear()) {
            labelString = createdAt.format(labelFormatMonth);
            return labelString;
        }

        // 60分以上前 HH:MM
        if (now.minus(Duration.ofMinutes(60)).compareTo(createdAt) > 0) {
            labelString = createdAt.format(labelFormatHour);
            return labelString;
        }

        // 以下相対表記のためキャッシュしない

        // 60秒以上前 m 分
        if (now.minus(Duration.ofSeconds(60)).compareTo(createdAt) > 0) {
            return String.format(labelFormatRelMinute, diffSec(now, createdAt) / 60);
        }

        // 5秒以上前 s 秒
        if (now.minus(Duration.ofSeconds(5)).compareTo(createdAt) > 0) {
            return String.format(labelFormatRelSecond, diffSec(now, createdAt));
        }

        // 現在
        return labelFormatRelNow;
    }

    private static long diffSec(ZonedDateTime a, ZonedDateTime b) {
        return Math.abs(a.toEpochSecond() - b.toEpochSecond());
    }
}
