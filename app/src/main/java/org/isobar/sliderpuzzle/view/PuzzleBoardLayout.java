package org.isobar.sliderpuzzle.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

import org.isobar.sliderpuzzle.R;
import org.isobar.sliderpuzzle.common.ApplicationSettings;
import org.isobar.sliderpuzzle.model.Coordinate;
import org.isobar.sliderpuzzle.model.PuzzleTile;
import org.isobar.sliderpuzzle.model.TileDataTransferObject;
import org.isobar.sliderpuzzle.processor.ImageProcessor;
import org.isobar.sliderpuzzle.processor.PuzzleProcessor;

import java.util.ArrayList;

import static org.isobar.sliderpuzzle.common.AppConstants.VIBRATE_DRAG;
import static org.isobar.sliderpuzzle.common.AppUtil.rectForCoordinate;
import static org.isobar.sliderpuzzle.common.AppUtil.vibrate;
import static org.isobar.sliderpuzzle.common.ApplicationSettings.setTileDimen;

public class PuzzleBoardLayout extends RelativeLayout implements OnTouchListener {

    private int tileDimen;
    private RectF gameBoardRect;
    protected PuzzleTile emptyTile, movedTile;
    private boolean boardCreated;
    private PointF lastDragPoint;
    private ImageProcessor imageProcessor;
    private PuzzleProcessor puzzleProcessor;
    private ArrayList<PuzzleTile> puzzleTiles;
    private int gridScale;


    int oldVal;
    int screenWidth;
    int screenHeight;
    boolean actionMove;
    protected ArrayList<TileDataTransferObject> tileDataTransferObjects;
    private static final String TAG = PuzzleBoardLayout.class.getName();

    public PuzzleBoardLayout(Context context, AttributeSet attrSet) {
        super(context, attrSet);
        gridScale = ApplicationSettings.getGridScale(context);
        Bitmap original = BitmapFactory.decodeResource(getResources(), R.drawable.globe);
        screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        screenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
        tileDimen = screenWidth / gridScale;
        Log.d(TAG, "screen Width :" + screenWidth);
        Log.d(TAG, "screen Height is :" + screenHeight);
        Log.d(TAG, "Tile dimension is :" + tileDimen);
        setTileDimen(context, tileDimen);
        imageProcessor = new ImageProcessor(original, gridScale, tileDimen);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (!boardCreated) {
            assignPuzzleBoardSize();
            createTiles();
            placeTilesInLayout();
            boardCreated = true;
            puzzleProcessor = new PuzzleProcessor(getContext(), tileDataTransferObjects, puzzleTiles, gameBoardRect);
        }
//		setBackgroundColor(Color.BLACK);
    }

    protected void placeTilesInLayout() {
        for (int tileIndex = 0; tileIndex < gridScale * gridScale; tileIndex++) {
            placeTile(puzzleTiles.get(tileIndex), tileIndex);
        }
    }

    protected void placeTile(PuzzleTile tile, int tileIndex) {
        Rect tileRect = rectForCoordinate(getContext(), tile.getCoordinate(), gameBoardRect);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(tileDimen, tileDimen);
        params.topMargin = tileRect.top;
        params.leftMargin = tileRect.left;
        tile.setId(tileIndex);
        addView(tile, params);

        int lastElementIndex= gridScale * gridScale - 1;
        if (tileIndex == lastElementIndex) {
            tile.setImageBitmap(null);
            tile.setImageSliceHandle(lastElementIndex);
            return;
        }

        int randIndexImage = imageProcessor.serveRandomImageIndex(tileIndex);
//		int randIndexImage=tileIndex;
        tile.setImageSliceHandle(randIndexImage);
        tile.setImageBitmap(imageProcessor.getImageSlicesMap().get(randIndexImage));

    }

    protected void createTiles() {
        puzzleTiles = new ArrayList<>();
        for (int rowI = 0; rowI < gridScale; rowI++) {
            for (int colI = 0; colI < gridScale; colI++) {
                PuzzleTile tile = createTileAtCoordinate(new Coordinate(rowI, colI));
                if (rowI == gridScale - 1 && colI == gridScale - 1) {
                    emptyTile = tile;
                    tile.setEmpty(true);
                }
            }
        }
    }

    protected PuzzleTile createTileAtCoordinate(Coordinate coordinate) {
        PuzzleTile tile = new PuzzleTile(getContext(), coordinate);
        puzzleTiles.add(tile);
        tile.setOnTouchListener(this);
        return tile;
    }

    public boolean onTouch(View v, MotionEvent event) {
        try {
            Log.d(TAG, "onTouch event action is " + event.getAction());
            PuzzleTile touchedTile = (PuzzleTile) v;
            if (touchedTile.isEmpty() || !touchedTile.isInRowOrColumnOf(emptyTile)) {
                return false;
            } else {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    movedTile = touchedTile;
                    vibrate(getContext(), VIBRATE_DRAG);
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    oldVal = x + y;
                    lastDragPoint = null;
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    int newval = (int) (event.getX() + event.getY());
                    Log.d(TAG, "check displacement on Move " + (newval - oldVal));
                    if (lastDragPoint != null && Math.abs(newval - oldVal) > 20) {
                        actionMove = true;
                        Log.d(TAG, "Action Move called");
                        tileDataTransferObjects = puzzleProcessor.getTilesBetweenEmptyTileAndTile(movedTile);
                        puzzleProcessor.moveDraggedTilesByMotionEventDelta(event, lastDragPoint);
                    }
                    lastDragPoint = new PointF(event.getRawX(), event.getRawY());

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // reload the motion descriptors in case of position change.
                    tileDataTransferObjects = puzzleProcessor.getTilesBetweenEmptyTileAndTile(movedTile);
                    Log.d(TAG, "inside action up " + tileDataTransferObjects.size());
                    // if last move was a dragging move and the move was over half way to the empty tile
//					if (lastDragPoint != null && lastDragMovedAtLeastHalfWay()) {
                    if (actionMove) {
                        if (puzzleProcessor.lastDragMovedAtLeastHalfWay()) {
                            Log.d(TAG, "it  is a grag event");
                            puzzleProcessor.animateCurrentMovedTilesToEmptySpace(getContext(), movedTile);
                        } else {
                            // Animate puzzleTiles back to origin
                            Log.d(TAG, "It was not enough drag  so move back ");
                            puzzleProcessor.animateMovedTilesBackToOrigin();
                        }
                        actionMove = false;
                    }
                    // otherwise, if it wasn't a drag, do the move
                    else {
                        Log.d(TAG, "it was not a drag so a  key down action to be done");
                        puzzleProcessor.animateCurrentMovedTilesToEmptySpace(getContext(), movedTile);

                    }
                    tileDataTransferObjects = null;
                    lastDragPoint = null;
                    movedTile = null;
                }

                return true;
            }
        } catch (ClassCastException e) {
            return false;
        }
    }


    private void assignPuzzleBoardSize() {
        int viewWidth = getWidth();
        int viewHeight = getHeight();


        int puzzleBoardWidth = screenWidth;
        int puzzleBoardHeight = screenWidth;
        int puzzleBoardTop = viewHeight / 2 - puzzleBoardHeight / 2;
        int puzzleBoardLeft = viewWidth / 2 - puzzleBoardWidth / 2;


        gameBoardRect = new RectF(puzzleBoardLeft, puzzleBoardTop, puzzleBoardLeft + puzzleBoardWidth, puzzleBoardTop + puzzleBoardHeight);
    }


}
