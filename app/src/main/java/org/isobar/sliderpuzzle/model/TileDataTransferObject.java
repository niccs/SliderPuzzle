package org.isobar.sliderpuzzle.model;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;

import static org.isobar.sliderpuzzle.common.AppUtil.rectForCoordinate;

/**
 * This object stores all the transfer data information of a tile.
 * Once the tiles movement are validated, the data to be swiped between the two tiles is stored in this.
 * Created by HP on 21/04/2016.
 */

public class TileDataTransferObject {

    private Rect finalRect;
    private String axis;
    private PuzzleTile sourceTile;
    /*
    from :the x or y position of toBeMoved image view
    to: the x or y position of the layout rectangle
    axialDelta: the displacement data of a tile, for Action_MOVE event
     */
    public float from, to, axialDelta;

    public Coordinate getFinalCoordinate() {
        return finalCoordinate;
    }

    public void setFinalCoordinate(Coordinate finalCoordinate) {
        this.finalCoordinate = finalCoordinate;
    }

    public PuzzleTile getDestinationTile() {
        return destinationTile;
    }

    public void setDestinationTile(PuzzleTile destinationTile) {
        this.destinationTile = destinationTile;
    }

    public String getAxis() {
        return axis;
    }

    public void setAxis(String axis) {
        this.axis = axis;
    }

    public float getAxialDelta() {
        return axialDelta;
    }

    public void setAxialDelta(float axialDelta) {
        this.axialDelta = axialDelta;
    }

    public Rect getFinalRect() {
        return finalRect;
    }

    public void setFinalRect(Rect finalRect) {
        this.finalRect = finalRect;
    }

    public PuzzleTile getSourceTile() {
        return sourceTile;
    }

    public void setSourceTile(PuzzleTile sourceTile) {
        this.sourceTile = sourceTile;
    }

    private Coordinate finalCoordinate;
    private PuzzleTile destinationTile;

    public TileDataTransferObject(PuzzleTile tile, String axis, float from, float to) {
        super();
        this.sourceTile = tile;
        this.from = from;// the x or y position of toBeMoved image view
        this.to = to; // the x or y position of the layout rectangle
        this.axis = axis;
    }

    public float currentPosition() {
        if (axis.equals("x")) {
            return sourceTile.getX();
        } else if (axis.equals("y")) {
            return sourceTile.getY();
        }
        return 0;
    }

    public float originalPosition(Context ctx,RectF gameBoardRect) {
        Rect originalRect = rectForCoordinate(ctx,sourceTile.getCoordinate(),gameBoardRect);
        if (axis.equals("x")) {
            return originalRect.left;
        } else if (axis.equals("y")) {
            return originalRect.top;
        }
        return 0;
    }


    @Override
    public String toString() {
        return "GameTileMotionDescriptor [axis=" + axis + ", sourceTile="
                + sourceTile + ", from=" + from + ", to=" + to + "]";
    }
}

