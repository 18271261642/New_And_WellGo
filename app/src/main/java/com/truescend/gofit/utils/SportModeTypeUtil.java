package com.truescend.gofit.utils;


import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.StringRes;

import com.truescend.gofit.R;

public class SportModeTypeUtil {


    private final static int[] sportModeColors = {
            0xFF4EC2C6,
            0xFF4EC2C6,
            0xFF468A8E,
            0xFF64AE3C,
            0xFFD35D2E,
            0xFF539BD2,
            0xFFDAC33F,
            0xFFA0C438,
            0xFF9C2CD2,
            0xFFC34F7B,
            0xFF9C56EB,
            0xFF38D9E3
    };

    private final static int[] sportModeLabelsRes = {
            R.string.sport_mode_walking,
            R.string.sport_mode_walking,
            R.string.sport_mode_running,
            R.string.sport_mode_mountaineering,
            R.string.sport_mode_cycling,
            R.string.sport_mode_table_tennis,
            R.string.sport_mode_basketball,
            R.string.sport_mode_football,
            R.string.sport_mode_badminton,
            R.string.sport_mode_treadmill,
            R.string.sport_mode_tennis,
            R.string.sport_mode_swimming
    };

    private final static int[] sportModeIconsRes = {
            R.mipmap.icon_sport_mode_walking,
            R.mipmap.icon_sport_mode_walking,
            R.mipmap.icon_sport_mode_running,
            R.mipmap.icon_sport_mode_mountaineering,
            R.mipmap.icon_sport_mode_cycling,
            R.mipmap.icon_sport_mode_table_tennis,
            R.mipmap.icon_sport_mode_basketball,
            R.mipmap.icon_sport_mode_football,
            R.mipmap.icon_sport_mode_badminton,
            R.mipmap.icon_sport_mode_treadmill,
            R.mipmap.icon_sport_mode_tennis,
            R.mipmap.icon_sport_mode_swimming,
    };


    public static @ColorInt
    int getColorForSportModeType(@IntRange(from = 0x01, to = 0x0B) int modeType) {
        return sportModeColors[modeType];
    }

    public static @StringRes
    int getLabelResForSportModeType(@IntRange(from = 0x01, to = 0x0B) int modeType) {
        return sportModeLabelsRes[modeType];
    }

    public static @DrawableRes
    int getIconResForSportModeType(@IntRange(from = 0x01, to = 0x0B) int modeType) {
        return sportModeIconsRes[modeType];
    }




}
