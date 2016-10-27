package org.isobar.sliderpuzzle.common;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Vibrator;

import org.isobar.sliderpuzzle.model.Coordinate;

import static org.isobar.sliderpuzzle.common.ApplicationSettings.getTileDimen;

/**
 * Created by HP on 21/04/2016.
 */
public class AppUtil {


    public static Rect rectForCoordinate(Context context,Coordinate coordinate, RectF gameBoardRect) {
        int gameBoardY = (int) Math.floor(gameBoardRect.top);
        int gameBoardX = (int) Math.floor(gameBoardRect.left);
        int top = (coordinate.row * getTileDimen(context)) + gameBoardY;
        int left = (coordinate.column * getTileDimen(context)) + gameBoardX;
        return new Rect(left, top, left + getTileDimen(context), top + getTileDimen(context));
    }



    public static  void vibrate(Context context,long d) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            v.vibrate(d);
        }
    }
}
