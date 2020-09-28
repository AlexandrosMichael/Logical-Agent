/**
 * Class representing a position on the grid
 */
public class Cell {

    // x coordinate
    int x;
    // y coordinate
    int y;
    // the cell's hint e.g. 1,2, ?, D, t
    char hint;

    /**
     * Class constructor
     * @param x coordinate
     * @param y coordinate
     * @param hint the hint of the cell
     */
    public Cell(int x, int y, char hint) {
        this.x = x;
        this.y = y;
        this.hint = hint;
    }

    /**
     * Overriden to String method
     * @return a readable string giving information about the sell
     */
    @Override
    public String toString() {
        return (x + " " + y + " " + "Hint: " + hint);
    }

    /**
     * Simple getter
     * @return the hint parameter of the Cell object
     */
    public char getHint() {
        return hint;
    }

    /**
     * Simple setter
     * @param hint the value to be set as the hint attribute of the Cell object
     */
    public void setHint(char hint) {
        this.hint = hint;
    }
}
