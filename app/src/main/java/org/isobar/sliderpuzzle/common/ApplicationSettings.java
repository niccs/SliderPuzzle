package org.isobar.sliderpuzzle.common;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class ApplicationSettings extends Application {

    private static final String GRID_SCALE = "GRID_SCALE";           // 4*4 default

    private static final String TILE_DIMEN = "TILE_DIMEN";
    private static SharedPreferences sharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


    private static void saveInt(Context context, String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences(context).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getGridScale(Context context) {
        return sharedPreferences(context).getInt(GRID_SCALE, 4);
    }

    public static void setGridScale(Context context, int scale) {
        saveInt(context, GRID_SCALE, scale);
    }

    public static int getTileDimen(Context context) {
        return sharedPreferences(context).getInt(TILE_DIMEN, 68);
    }

    public static void setTileDimen(Context context, int tileDimen) {
        saveInt(context, TILE_DIMEN, tileDimen);
    }
}
