import java.util.ArrayList;

/**
 * Class representing the game instance. Holds the actual world view, separating the agent playing the game, making
 * the distinction between the agent's view and the actual world view distinct.
 *
 */
public class Game {

    private World world;
    private Board board;
    // list holding all of the Cell objects
    private ArrayList<Cell> allCells;
    // list holding the cells which have not been uncovered yet
    private ArrayList<Cell> coveredCells;
    // holds whether game is over
    private boolean gameOver;
    // holds whether game has been won
    private boolean gameWon;

    /**
     * Class constructor
     * @param world name of the world e.g. S1, M3, L5 etc.
     */
    public Game(String world) {
        this.world = World.valueOf(world);
        // this will hold the actual world view
        this.board = new Board(this.world.map);
        this.gameOver = false;
        this.gameWon = false;
        this.allCells = new ArrayList<>();
        this.coveredCells = new ArrayList<>();
        populateCells();
    }

    /**
     * Method which populates the allCells and coveredCells array. In the beginning all the cells are covered.
     */
    private void populateCells() {
        for (int i = 0; i < board.board.length ; i++) {
            for (int j = 0; j < board.board.length ; j++) {
                Cell cell = new Cell(i, j, board.board[j][i]);
                allCells.add(cell);
                coveredCells.add(cell);
            }
        }
    }

    /**
     * Method which returns information about the cell probed. Provides ability to 'tell agent about perceptions'
     * @param x coordinate
     * @param y coordinate
     * @return Cell object uncovered containing details about coordinates and hint
     */
    public Cell uncoverCell(int x, int y) {
        for (int i = 0; i < allCells.size(); i++) {
            if (allCells.get(i).x == x && allCells.get(i).y == y) {
                Cell cell = allCells.get(i);
                // remove cell from list holding the covered cells
                coveredCells.remove(cell);
                // if the cell uncovered is a tornado, game is over
                if (cell.getHint() == 't') {
                    gameOver = true;
                }
                // if checkGameWon returns true
                else if (checkGameWon()) {
                    gameOver = true;
                    gameWon = true;
                }
                return cell;
            }
        }
        // cell has already been uncovered, return null
        return null;
    }

    /**
     * Method which checks whether all the remaining covered cells are tornadoes.
     * Provides ability to 'determine if game is over'
     * @return true if all the remaining covered cells are tornadoes
     */
    public boolean checkGameWon() {
        if (coveredCells.size() == 0) {
            return true;
        }
        else {
            for (int i = 0; i < coveredCells.size(); i++) {
                Cell cell = coveredCells.get(i);
                // if at least one covered cell is not a tornado it means game has not been won
                if (board.board[cell.y][cell.x] != 't') {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Simple getter
     * @return board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Simplge getter
     * @return boolean value of whether the game is over
     */
    public boolean isGameOver() {
        return gameOver;
    }


    /**
     * Simple getter
     * @return boolean value of whether the game has been won
     */
    public boolean isGameWon() {
        return gameWon;
    }


}
