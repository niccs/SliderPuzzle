package org.isobar.sliderpuzzle.processor;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
 * Created by HP on 21/04/2016.
 */
public class ImageProcessor {

    Bitmap srcImage, scaledImage;
    int gridScale, tileDimen;
    HashMap<Integer, Bitmap> imgSlicesMap;
    List<Integer> keysList;
    private static final String TAG = ImageProcessor.class.getName();

    /**
     * This hashmap contains image slices stored with a unique key associated, will be used to check if puzzle is solved
     */
    public HashMap<Integer, Bitmap> getImageSlicesMap() {
        return imgSlicesMap;
    }

    public ImageProcessor(Bitmap mainImage, int gridScale, int tileDimen) {
        super();
        this.srcImage = mainImage;
        this.gridScale = gridScale;
        this.tileDimen = tileDimen;
        scaleAndSliceOriginalImage();
    }

    protected void scaleAndSliceOriginalImage() {
        int x, y;
        Bitmap bitmap;
        int fullWidth = tileDimen * gridScale;
        int fullHeight = tileDimen * gridScale;
        scaledImage = Bitmap.createScaledBitmap(srcImage, fullWidth, fullHeight, true);
        imgSlicesMap = new HashMap<>();
        for (int rowI = 0; rowI < gridScale; rowI++) {
            for (int colI = 0; colI < gridScale; colI++) {
                x = Math.round(colI * fullWidth / gridScale);
                y = Math.round(rowI * fullHeight / gridScale);
                bitmap = Bitmap.createBitmap(scaledImage, x, y, tileDimen, tileDimen);
                if(colI==gridScale-1 && rowI==gridScale-1) {
                    Log.d(TAG,"just ignore last image slice");
                    break;
                }
                imgSlicesMap.put(rowI * gridScale + colI, bitmap);

            }
        }
        keysList = new ArrayList<>(imgSlicesMap.keySet());
        Log.d(TAG,"keyList size is "+keysList.size());
        Collections.shuffle(keysList);
    }

    public int serveRandomImageIndex(int i) {
        return keysList.get(i);
    }

}