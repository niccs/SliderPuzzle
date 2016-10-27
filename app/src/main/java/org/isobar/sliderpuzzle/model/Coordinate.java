package org.isobar.sliderpuzzle.model;

/**
 * Created by HP on 21/04/2016.
 */
public class Coordinate {

    public int row;
    public int column;

    public Coordinate(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public boolean matches(Coordinate coordinate) {
        return coordinate.row == row && coordinate.column == column;
    }

    public boolean sharesAxisWith(Coordinate coordinate) {
        return (row == coordinate.row || column == coordinate.column);
    }

    public boolean isToRightOf(Coordinate coordinate) {
        return sharesAxisWith(coordinate) && (column > coordinate.column);
    }

    public boolean isToLeftOf(Coordinate coordinate) {
        return sharesAxisWith(coordinate) && (column < coordinate.column);
    }

    public boolean isAbove(Coordinate coordinate) {
        return sharesAxisWith(coordinate) && (row < coordinate.row);
    }

    public boolean isBelow(Coordinate coordinate) {
        return sharesAxisWith(coordinate) && (row > coordinate.row);
    }

    @Override
    public String toString() {
        return "Coordinate [row=" + row + ", column=" + column + "]";
    }

}
