package org.isobar.sliderpuzzle.processor;

import android.animation.Animator;
import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import org.isobar.sliderpuzzle.model.Coordinate;
import org.isobar.sliderpuzzle.model.PuzzleTile;
import org.isobar.sliderpuzzle.model.TileDataTransferObject;

import java.util.ArrayList;
import java.util.HashSet;

import static org.isobar.sliderpuzzle.common.AppConstants.ANIMATION_DURATION;
import static org.isobar.sliderpuzzle.common.AppConstants.VIBRATE_SOLVED;
import static org.isobar.sliderpuzzle.common.AppUtil.rectForCoordinate;
import static org.isobar.sliderpuzzle.common.AppUtil.vibrate;
import static org.isobar.sliderpuzzle.common.ApplicationSettings.getGridScale;
import static org.isobar.sliderpuzzle.common.ApplicationSettings.getTileDimen;

/**
 * Created by HP on 21/04/2016.
 */


public class PuzzleProcessor {
    private ArrayList<TileDataTransferObject> tileDataTransferObjects;
    private PuzzleTile blankTile;
    private ArrayList<PuzzleTile> puzzleTiles;
    private RectF gameBoardRect;
    private Context context;
    private static final String TAG = PuzzleProcessor.class.getName();

    public PuzzleProcessor(Context context, ArrayList<TileDataTransferObject> tileDataTransferObjects, ArrayList<PuzzleTile> puzzleTiles, RectF gameBoardRect) {
        this.tileDataTransferObjects = tileDataTransferObjects;
        this.puzzleTiles = puzzleTiles;
        this.gameBoardRect = gameBoardRect;
        this.context = context;
        blankTile = getTileAtCoordinate(new Coordinate(getGridScale(context) - 1, getGridScale(context) - 1));
    }


    public boolean lastDragMovedAtLeastHalfWay() {
        if (tileDataTransferObjects != null && tileDataTransferObjects.size() > 0) {
            TileDataTransferObject firstMotionDescriptor = tileDataTransferObjects.get(0);
            if (firstMotionDescriptor.getAxialDelta() > getTileDimen(context) / 2) {
                return true;
            }
        }
        return false;
    }

    public void moveDraggedTilesByMotionEventDelta(MotionEvent event, PointF lastDragPoint) {

        boolean impossibleMove = true;
        float dxTile, dyTile;
        float dxEvent = event.getRawX() - lastDragPoint.x;
        float dyEvent = event.getRawY() - lastDragPoint.y;
        PuzzleTile puzzleTile;
        for (TileDataTransferObject tileDataTransferObject : tileDataTransferObjects) {
            puzzleTile = tileDataTransferObject.getSourceTile();
            dxTile = puzzleTile.getX() + dxEvent;
            dyTile = puzzleTile.getY() + dyEvent;

            RectF candidateRect = new RectF(dxTile, dyTile, dxTile + puzzleTile.getWidth(), dyTile + puzzleTile.getHeight());
            HashSet<PuzzleTile> tilesToCheck = null;
            if (puzzleTile.getCoordinate().row == blankTile.getCoordinate().row) {
                tilesToCheck = allTilesInRow(puzzleTile.getCoordinate().row);
            } else if (puzzleTile.getCoordinate().column == blankTile.getCoordinate().column) {
                tilesToCheck = allTilesInColumn(puzzleTile.getCoordinate().column);
            }

            boolean candidateRectInGameboard = (gameBoardRect.contains(candidateRect));
            boolean collides = candidateRectForTileCollidesWithAnyTileInSet(candidateRect, puzzleTile, tilesToCheck);

            impossibleMove = impossibleMove && (!candidateRectInGameboard || collides);
        }
        if (!impossibleMove) {
            for (TileDataTransferObject tileDataTransferObject : tileDataTransferObjects) {
                puzzleTile = tileDataTransferObject.getSourceTile();
                dxTile = puzzleTile.getX() + dxEvent;
                dyTile = puzzleTile.getY() + dyEvent;
                if (!impossibleMove) {
                    if (puzzleTile.getCoordinate().row == blankTile.getCoordinate().row) {
                        puzzleTile.setX(dxTile);
                    } else if (puzzleTile.getCoordinate().column == blankTile.getCoordinate().column) {
                        puzzleTile.setY(dyTile);
                    }
                }
            }
        }
    }

    protected boolean candidateRectForTileCollidesWithAnyTileInSet(RectF candidateRect, PuzzleTile tile, HashSet<PuzzleTile> set) {
        RectF otherTileRect;
        for (PuzzleTile otherTile : set) {
            if (!otherTile.isEmpty() && otherTile != tile) {
                otherTileRect = new RectF(otherTile.getX(), otherTile.getY(), otherTile.getX() + otherTile.getWidth(), otherTile.getY() + otherTile.getHeight());
                if (RectF.intersects(otherTileRect, candidateRect)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void animateCurrentMovedTilesToEmptySpace(final Context context, PuzzleTile movedTile) {
        Log.d(TAG, "inProcessor " + tileDataTransferObjects);
        blankTile.setX(movedTile.getX());
        blankTile.setY(movedTile.getY());
        blankTile.setCoordinate(movedTile.getCoordinate());
        ObjectAnimator animator;
        for (final TileDataTransferObject tileDataTransferObject : tileDataTransferObjects) {
            animator = ObjectAnimator.ofObject(
                    tileDataTransferObject.getSourceTile(),
                    tileDataTransferObject.getAxis(),
                    new FloatEvaluator(),
                    tileDataTransferObject.from,
                    tileDataTransferObject.to);
            animator.setDuration(ANIMATION_DURATION);
            animator.addListener(new Animator.AnimatorListener() {

                public void onAnimationStart(Animator animation) {
                }

                public void onAnimationCancel(Animator animation) {
                }

                public void onAnimationRepeat(Animator animation) {
                }

                public void onAnimationEnd(Animator animation) {
                    tileDataTransferObject.getSourceTile().setCoordinate(tileDataTransferObject.getFinalCoordinate());
                    tileDataTransferObject.getSourceTile().setX(tileDataTransferObject.getFinalRect().left);
                    tileDataTransferObject.getSourceTile().setY(tileDataTransferObject.getFinalRect().top);
                    Log.d(TAG, "source and destination tile " + tileDataTransferObject.getDestinationTile() + "" + tileDataTransferObject.getDestinationTile());
                    swipeTileHandle(tileDataTransferObject.getSourceTile(), tileDataTransferObject.getDestinationTile());

                    for (int i = 0; i < puzzleTiles.size(); i++) {
                        Log.d(TAG, "sourceTile id :" + puzzleTiles.get(i).getId() + " sourceTile image handle " + puzzleTiles.get(i).getImageSliceHandle());
                        Log.d(TAG, "-----------------------------------------");
                    }
                    if (isPuzzleSolved()) {
                        Toast.makeText(context, "Well done puzzle solved ", Toast.LENGTH_SHORT).show();
                        vibrate(context, VIBRATE_SOLVED);
                    }

                }
            });
            animator.start();
        }
    }

    //check if the player finished the Game
    private boolean isPuzzleSolved() {
        for (int i = 0; i < getGridScale(context) * getGridScale(context); i++) {
            if (puzzleTiles.get(i).getId() != puzzleTiles.get(i).getImageSliceHandle()) {
                return false;
            }
        }
        return true;
    }

    private void swipeTileHandle(PuzzleTile source, PuzzleTile dest) {
        int tempHandle = source.getImageSliceHandle();
        source.setImageSliceHandle(dest.getImageSliceHandle());
        dest.setImageSliceHandle(tempHandle);
    }

    public void animateMovedTilesBackToOrigin() {
        ObjectAnimator animator;
        if (tileDataTransferObjects != null) {
            for (final TileDataTransferObject tileDataTransferObject : tileDataTransferObjects) {
                animator = ObjectAnimator.ofObject(
                        tileDataTransferObject.getSourceTile(),
                        tileDataTransferObject.getAxis(),
                        new FloatEvaluator(),
                        tileDataTransferObject.currentPosition(),
                        tileDataTransferObject.originalPosition(context, gameBoardRect));
                animator.setDuration(ANIMATION_DURATION);
                animator.addListener(new Animator.AnimatorListener() {

                    public void onAnimationStart(Animator animation) {
                    }

                    public void onAnimationCancel(Animator animation) {
                    }

                    public void onAnimationRepeat(Animator animation) {
                    }

                    public void onAnimationEnd(Animator animation) {
                    }
                });
                animator.start();
            }
        }
    }

    public ArrayList<TileDataTransferObject> getTilesBetweenEmptyTileAndTile(PuzzleTile selectedTile) {
        tileDataTransferObjects = new ArrayList<>();
        Coordinate coordinate, finalCoordinate;
        PuzzleTile foundTile;
        TileDataTransferObject tileDataTransferObject;
        Rect finalRect, currentRect;
        float axialDelta;
        if (selectedTile.isToRightOf(blankTile)) {
            for (int i = selectedTile.getCoordinate().column; i > blankTile.getCoordinate().column; i--) {
                coordinate = new Coordinate(selectedTile.getCoordinate().row, i);
                foundTile = (selectedTile.getCoordinate().matches(coordinate)) ? selectedTile : getTileAtCoordinate(coordinate);
                finalCoordinate = new Coordinate(selectedTile.getCoordinate().row, i - 1);
                PuzzleTile destTile = getTileAtCoordinate(finalCoordinate);
                currentRect = rectForCoordinate(context, foundTile.getCoordinate(), gameBoardRect);
                finalRect = rectForCoordinate(context, finalCoordinate, gameBoardRect);
                axialDelta = Math.abs(foundTile.getX() - currentRect.left);
                tileDataTransferObject = new TileDataTransferObject(
                        foundTile,
                        "x",
                        foundTile.getX(),
                        finalRect.left
                );
                tileDataTransferObject.setFinalCoordinate(finalCoordinate);
                ;
                tileDataTransferObject.setFinalRect(finalRect);
                tileDataTransferObject.setAxialDelta(axialDelta);
                tileDataTransferObject.setDestinationTile(destTile);
                tileDataTransferObjects.add(tileDataTransferObject);
            }
        } else if (selectedTile.isToLeftOf(blankTile)) {
            for (int i = selectedTile.getCoordinate().column; i < blankTile.getCoordinate().column; i++) {
                coordinate = new Coordinate(selectedTile.getCoordinate().row, i);
                foundTile = (selectedTile.getCoordinate().matches(coordinate)) ? selectedTile : getTileAtCoordinate(coordinate);
                finalCoordinate = new Coordinate(selectedTile.getCoordinate().row, i + 1);
                PuzzleTile destTile = getTileAtCoordinate(finalCoordinate);
                currentRect = rectForCoordinate(context, foundTile.getCoordinate(), gameBoardRect);
                finalRect = rectForCoordinate(context, finalCoordinate, gameBoardRect);
                axialDelta = Math.abs(foundTile.getX() - currentRect.left);
                tileDataTransferObject = new TileDataTransferObject(
                        foundTile,
                        "x",
                        foundTile.getX(),
                        finalRect.left
                );
                tileDataTransferObject.setFinalCoordinate(finalCoordinate);
                tileDataTransferObject.setFinalRect(finalRect);
                tileDataTransferObject.setAxialDelta(axialDelta);
                tileDataTransferObject.setDestinationTile(destTile);
                tileDataTransferObjects.add(tileDataTransferObject);
            }
        } else if (selectedTile.isAbove(blankTile)) {
            for (int i = selectedTile.getCoordinate().row; i < blankTile.getCoordinate().row; i++) {
                coordinate = new Coordinate(i, selectedTile.getCoordinate().column);
                foundTile = (selectedTile.getCoordinate().matches(coordinate)) ? selectedTile : getTileAtCoordinate(coordinate);
                finalCoordinate = new Coordinate(i + 1, selectedTile.getCoordinate().column);
                PuzzleTile destTile = getTileAtCoordinate(finalCoordinate);
                currentRect = rectForCoordinate(context, foundTile.getCoordinate(), gameBoardRect);
                finalRect = rectForCoordinate(context, finalCoordinate, gameBoardRect);
                axialDelta = Math.abs(foundTile.getY() - currentRect.top);
                tileDataTransferObject = new TileDataTransferObject(
                        foundTile,
                        "y",
                        foundTile.getY(),
                        finalRect.top
                );
                tileDataTransferObject.setFinalCoordinate(finalCoordinate);
                tileDataTransferObject.setFinalRect(finalRect);
                tileDataTransferObject.setAxialDelta(axialDelta);
                tileDataTransferObject.setDestinationTile(destTile);
                tileDataTransferObjects.add(tileDataTransferObject);
            }
        } else if (selectedTile.isBelow(blankTile)) {
            for (int i = selectedTile.getCoordinate().row; i > blankTile.getCoordinate().row; i--) {
                coordinate = new Coordinate(i, selectedTile.getCoordinate().column);
                foundTile = (selectedTile.getCoordinate().matches(coordinate)) ? selectedTile : getTileAtCoordinate(coordinate);
                finalCoordinate = new Coordinate(i - 1, selectedTile.getCoordinate().column);
                PuzzleTile destTile = getTileAtCoordinate(finalCoordinate);
                currentRect = rectForCoordinate(context, foundTile.getCoordinate(), gameBoardRect);
                finalRect = rectForCoordinate(context, finalCoordinate, gameBoardRect);
                axialDelta = Math.abs(foundTile.getY() - currentRect.top);
                tileDataTransferObject = new TileDataTransferObject(
                        foundTile,
                        "y",
                        foundTile.getY(),
                        finalRect.top
                );
                tileDataTransferObject.setFinalCoordinate(finalCoordinate);
                tileDataTransferObject.setFinalRect(finalRect);
                tileDataTransferObject.setAxialDelta(axialDelta);
                tileDataTransferObject.setDestinationTile(destTile);
                tileDataTransferObjects.add(tileDataTransferObject);
            }
        }
        return tileDataTransferObjects;
    }


    protected PuzzleTile getTileAtCoordinate(Coordinate coordinate) {
        for (PuzzleTile tile : puzzleTiles) {
            if (tile.getCoordinate().matches(coordinate)) {
                return tile;
            }
        }
        return null;
    }

    protected HashSet<PuzzleTile> allTilesInRow(int row) {
        HashSet<PuzzleTile> tilesInRow = new HashSet<>();
        for (PuzzleTile tile : puzzleTiles) {
            if (tile.getCoordinate().row == row) {
                tilesInRow.add(tile);
            }
        }
        return tilesInRow;
    }

    protected HashSet<PuzzleTile> allTilesInColumn(int column) {
        HashSet<PuzzleTile> tilesInColumn = new HashSet<>();
        for (PuzzleTile tile : puzzleTiles) {
            if (tile.getCoordinate().column == column) {
                tilesInColumn.add(tile);
            }
        }
        return tilesInColumn;
    }


}
