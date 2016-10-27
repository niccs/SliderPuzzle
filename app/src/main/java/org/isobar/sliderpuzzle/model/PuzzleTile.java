package org.isobar.sliderpuzzle.model;

import android.content.Context;
import android.widget.ImageView;

public class PuzzleTile extends ImageView {

	private Coordinate coordinate;
	protected boolean empty;
	private int imageSliceHandle;

	public Coordinate getCoordinate() {
		return coordinate;
	}
	public void setCoordinate(Coordinate c) {
		this.coordinate=c;
	}
	public int getImageSliceHandle() {
		return imageSliceHandle;
	}
	public void setImageSliceHandle(int imageSliceHandle) {
		this.imageSliceHandle= imageSliceHandle;
	}
	public PuzzleTile(Context context, Coordinate coordinate) {
		super(context);
		this.coordinate = coordinate;
	}
	
	@Override
	public String toString() {
		return String.format("<GameTile at row: %d, col: %d, x: %f, y: %f", coordinate.row, coordinate.column, getX(), getY());
	}

	public boolean isEmpty() {
		return empty;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
		if (empty) {
			setImageBitmap(null);
		}
	}

	public boolean isInRowOrColumnOf(PuzzleTile otherTile) {
		return (coordinate.sharesAxisWith(otherTile.coordinate));
	}

	public boolean isToRightOf(PuzzleTile tile) {
		return coordinate.isToRightOf(tile.coordinate);
	}

	public boolean isToLeftOf(PuzzleTile tile) {
		return coordinate.isToLeftOf(tile.coordinate);
	}

	public boolean isAbove(PuzzleTile tile) {
		return coordinate.isAbove(tile.coordinate);
	}

	public boolean isBelow(PuzzleTile tile) {
		return coordinate.isBelow(tile.coordinate);
	}

}
