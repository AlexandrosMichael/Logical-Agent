import java.util.ArrayList;
import java.util.Random;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Formula;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.sat4j.core.VecInt;
import org.sat4j.pb.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;


public class Agent {

    // type of Agent i.e. RPX, SPX or SAT
    private String type;
    // instance of Game class
    private Game game;
    // represents the agent's board view. This is distinct from the game's board view
    private Board board;
    // list holding all the Cell objects of the board
    private ArrayList<Cell> allCells;
    // list holding all the unexamined cells of the board
    private ArrayList<Cell> unexaminedCells;
    // list holding all the examined cells of the board. A cell marked as a danger/tornado is also considered examined
    private ArrayList<Cell> examinedCells;
    // list holding all the uncovered cells of the board
    private ArrayList<Cell> uncoveredCells;
    // list holding all the cells marked as tornado cells
    private ArrayList<Cell> tornadoCells;
    // holds the length of the board
    private int boardLength;
    // holds the number of Cells with a hint of 0 whose neighbours have not been probed yet.
    private int cellsWithFreeNeighbours;
    // used for parsing the string representation of the knowledge base into a logical formula
    private FormulaFactory f = new FormulaFactory();
    private PropositionalParser p = new PropositionalParser(f);

    /**
     * Class constructor
     * @param type agent type
     * @param game instance of Game class
     */
    public Agent(String type, Game game) {
        this.type = type;
        this.game = game;
        // the board length is the only piece of information the agent gets from the game instance
        this.boardLength = this.game.getBoard().board.length;
        this.allCells = new ArrayList<>();
        this.unexaminedCells = new ArrayList<>();
        this.tornadoCells = new ArrayList<>();
        this.board = new Board(new char[boardLength][boardLength]);
        this.examinedCells = new ArrayList<>();
        this.uncoveredCells = new ArrayList<>();
        this.cellsWithFreeNeighbours = 0;
        // populate the board
        populateBoard();
        // populate the lists
        populateCells();
        // probe the hint cells -> top left corner & middle cells
        probeHintCells();
    }

    /** -------------------------------------------- GENERAL METHODS ------------------------------------------------**/

    
    /**
     * Method which creates permutations of strings in a list. Used to generate the encoding of the knowledge base
     * as a string.
     * @param list the list containing the strings to be permuted
     * @return a list of the permutations i.e. a list containing the permutations of the list passed as a parameter
     * code adapted from: https://stackoverflow.com/questions/24460480/permutation-of-an-arraylist-of-numbers-using-recursion
     */
    public ArrayList<ArrayList<String>> listPermutations(ArrayList<String> list) {

        if (list.size() == 0) {
            ArrayList<ArrayList<String>> result = new ArrayList<>();
            result.add(new ArrayList<>());
            return result;
        }

        ArrayList<ArrayList<String>> returnMe = new ArrayList<>();

        String firstElement = list.remove(0);

        ArrayList<ArrayList<String>> recursiveReturn = listPermutations(list);
        for (ArrayList<String> li : recursiveReturn) {
            for (int index = 0; index <= li.size(); index++) {
                ArrayList<String> temp = new ArrayList<>(li);
                temp.add(index, firstElement);
                returnMe.add(temp);
            }

        }
        return returnMe;
    }
    
    /**
     * Method which initialises the board view of the agent. At t0 they are all unknown, represented by '?'
     */
    public void populateBoard() {
        for (int i = 0; i < boardLength; i++) {
            for (int j = 0; j < boardLength; j++) {
                board.board[j][i] = '?';
            }
        }
        System.out.println("Agent board view at t0");
        board.printBoard();
    }

    /**
     * Method which initialises the unexaminedCells and allCells lists
     */
    public void populateCells() {
        for (int i = 0; i < boardLength; i++) {
            for (int j = 0; j < boardLength; j++) {
                Cell cell = new Cell(j, i, '?');
                unexaminedCells.add(cell);
                allCells.add(cell);
            }
        }
    }

    /**
     * Method which probes the hint cells
     */
    public void probeHintCells() {
        System.out.println("Probing hint cells");
        System.out.println();
        Cell cell = findCell(0, 0);
        probeCell(cell);
        cell = findCell(boardLength / 2, boardLength / 2);
        probeCell(cell);
        System.out.println("Agent board view at t1");
        board.printBoard();
    }

    /**
     * Method which returns the Cell object from the unexaminedCells list with coordinates passed as parameters
     * @param x coordinate
     * @param y coordinate
     * @return Cell object at coordinates x and y
     */
    public Cell findUnexaminedCell(int x, int y) {
        for (int i = 0; i < unexaminedCells.size(); i++) {
            if (unexaminedCells.get(i).x == x && unexaminedCells.get(i).y == y) {
                return unexaminedCells.get(i);
            }
        }
        // if cell has been examined before
        return null;
    }

    /**
     * Method which returns the Cell object from the allCells list with coordinates passed as parameters
     * @param x coordinate
     * @param y coordinate
     * @return Cell object at coordinates x and y
     */
    public Cell findCell(int x, int y) {
        for (int i = 0; i < allCells.size(); i++) {
            if (allCells.get(i).x == x && allCells.get(i).y == y) {
                return allCells.get(i);
            }
        }
        // if cell does not exist
        return null;
    }

    /**
     * Method which uncovers cell passed as a parameter. It gets the information about the Cell at that position from 
     * the game instance. It updates the lists and prints out the appropriate message.
     * @param cell
     */
    public void probeCell(Cell cell) {
        Cell myCell = findCell(cell.x, cell.y);
        Cell perceivedCell = game.uncoverCell(cell.x, cell.y);
        cell.setHint(perceivedCell.getHint());
        myCell.setHint(perceivedCell.getHint());
        unexaminedCells.remove(cell);
        examinedCells.add(cell);
        uncoveredCells.add(cell);
        board.board[cell.y][cell.x] = cell.getHint();
        if (cell.getHint() == 't') {
            System.out.println("tornado " + cell.toString());
        } 
        // if the hint is 0, increment free neighbours. Tells program that there are free neighbours to be probed
        else if (cell.getHint() == '0') {
            cellsWithFreeNeighbours++;
            System.out.println("probe " + cell.toString());

        } 
        else {
            System.out.println("probe " + cell.toString());
        }
        //System.out.println();
    }

    /**
     * Method which marks the Cell objects passed as a parameter as a danger cell i.e. flag it. Used by the SPX agent.
     * @param cell to be marked as a 'danger'.
     */
    public void markCell(Cell cell) {
        Cell myCell = findCell(cell.x, cell.y);
        cell.setHint('D');
        myCell.setHint('D');
        tornadoCells.add(cell);
        examinedCells.add(cell);
        unexaminedCells.remove(cell);
        board.board[cell.y][cell.x] = cell.getHint();
        System.out.println("mark " + cell.toString());
        System.out.println();
    }

    /**
     * Method which returns whether a Cell object has been examined before
     * @param adjacentCell
     * @return
     */
    public boolean hasBeenExamined(Cell adjacentCell) {
        for (Cell cell : examinedCells) {
            if (cell.x == adjacentCell.x && cell.y == adjacentCell.y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method which returns all the neighbouring cells of a cell passed as a parameter
     * @param cell whose neighbours are to be found
     * @return an ArrayList containing the neighbours of the parameter Cell object.
     */
    public ArrayList<Cell> getAllNeighbours(Cell cell) {
        ArrayList<Cell> adjacentCells = new ArrayList<>();
        
        
        // cell over and to the left
        if (cell.x > 0 && cell.y > 0) {
            // check if already probed
            Cell adjacentCell = findCell(cell.x - 1, cell.y - 1);
            if (adjacentCell != null) {
                adjacentCells.add(adjacentCell);
            }
        }
        // cell to the left
        if (cell.x > 0) {
            Cell adjacentCell = findCell(cell.x - 1, cell.y);
            if (adjacentCell != null) {
                adjacentCells.add(adjacentCell);
            }
        }
        // cell over
        if (cell.y > 0) {
            Cell adjacentCell = findCell(cell.x, cell.y - 1);
            if (adjacentCell != null) {
                adjacentCells.add(adjacentCell);
            }
        }
        // cell under and to the right
        if (cell.x < boardLength - 1 && cell.y < boardLength - 1) {
            Cell adjacentCell = findCell(cell.x + 1, cell.y + 1);
            if (adjacentCell != null) {
                adjacentCells.add(adjacentCell);
            }
        }
        // cell to the right
        if (cell.x < boardLength - 1) {
            Cell adjacentCell = findCell(cell.x + 1, cell.y);
            if (adjacentCell != null) {
                adjacentCells.add(adjacentCell);
            }
        }
        // cell under
        if (cell.y < boardLength - 1) {
            Cell adjacentCell = findCell(cell.x, cell.y + 1);
            if (adjacentCell != null) {
                adjacentCells.add(adjacentCell);
            }
        }

        return adjacentCells;
    }

    /**
     * Method which uncovers all neighbours of a cell with hint 0 i.e. no tornadoes around it
     */
    @SuppressWarnings("Duplicates")
    public void uncoverAllClearNeighbours() {
        // list holding cells that are adjacent to a cell with hint 0. These cells can be probed
        ArrayList<Cell> adjacentCells = new ArrayList<>();
        for (Cell cell : examinedCells) {
            if (cell.getHint() == '0') {
                if (cell.x > 0 && cell.y > 0) {
                    // check if already probed
                    Cell adjacentCell = findUnexaminedCell(cell.x - 1, cell.y - 1);
                    if (adjacentCell != null) {
                        adjacentCells.add(adjacentCell);
                    }
                }
                if (cell.x > 0) {
                    Cell adjacentCell = findUnexaminedCell(cell.x - 1, cell.y);
                    if (adjacentCell != null) {
                        adjacentCells.add(adjacentCell);
                    }
                }
                if (cell.y > 0) {
                    Cell adjacentCell = findUnexaminedCell(cell.x, cell.y - 1);
                    if (adjacentCell != null) {
                        adjacentCells.add(adjacentCell);
                    }
                }
                if (cell.x < boardLength - 1 && cell.y < boardLength - 1) {
                    Cell adjacentCell = findUnexaminedCell(cell.x + 1, cell.y + 1);
                    if (adjacentCell != null) {
                        adjacentCells.add(adjacentCell);
                    }
                }
                if (cell.x < boardLength - 1) {
                    Cell adjacentCell = findUnexaminedCell(cell.x + 1, cell.y);
                    if (adjacentCell != null) {
                        adjacentCells.add(adjacentCell);
                    }
                }
                if (cell.y < boardLength - 1) {
                    Cell adjacentCell = findUnexaminedCell(cell.x, cell.y + 1);
                    if (adjacentCell != null) {
                        adjacentCells.add(adjacentCell);
                    }
                }

            }
        }
        // probe all the cells in the probe cell list. Done outside the loop to prevent ConcurrentModificationException
        for (Cell adjacentCell : adjacentCells) {
            if (!hasBeenExamined(adjacentCell)) {
                System.out.println("Uncovering free neighbour");
                probeCell(adjacentCell);
            }
        }
    }

    /**
     * Method which calls the uncoverAllClearNeighbours method.
     * Decrements the freeNeighbours variable.
     */
    @SuppressWarnings("Duplicates")
    public void clearNeighbours() {
        while (cellsWithFreeNeighbours != 0 && !game.isGameWon()) {
            uncoverAllClearNeighbours();
            cellsWithFreeNeighbours--;
        }
    }


    /** -------------------------------------------- RPX METHODS ------------------------------------------------**/


    /**
     * Method which randomly picks an unprobed cell to probe next. Used by the RPX method
     */
    public void makeRandomMove() {
        Random rand = new Random();
        Cell cell = unexaminedCells.get(rand.nextInt(unexaminedCells.size()));
        probeCell(cell);
    }

    /** -------------------------------------------- SPX METHODS ------------------------------------------------**/

    /**
     * Method which returns the number of flagged cells around the cell passed as a parameter
     * @param cell
     * @return integer value of number of flagged cells around the cell passed as a parameter
     */
    @SuppressWarnings("Duplicates")
    public int neighbouringDangers(Cell cell) {
        int nDangers = 0;
        ArrayList<Cell> adjacentCells = getAllNeighbours(cell);
        for (Cell adjacentCell : adjacentCells) {
            if (adjacentCell.getHint() == 'D') {
                nDangers++;
            }
        }
        return nDangers;
    }

    /**
     * Method which returns the number of unexamined cells around the cell passed as a parameter
     * @param cell
     * @returni nteger value of number of unexamined cells around the cell passed as a parameter
     */
    public int neighbouringUnknowns(Cell cell) {
        int nUnknowns = 0;
        ArrayList<Cell> adjacentCells = getAllNeighbours(cell);
        for (Cell adjacentCell : adjacentCells) {
            if (adjacentCell.getHint() == '?') {
                nUnknowns++;
            }
        }
        return nUnknowns;
    }

    /**
     * Method which checks whether the Cell object passed as a parameter is in an AFN situation.
     * @param cell
     * @return true if the cell is in an AFN situation
     */
    public boolean checkAFN(Cell cell) {
        ArrayList<Cell> adjacentCells = getAllNeighbours(cell);
        for (Cell adjacentCell : adjacentCells) {
            if (adjacentCell.getHint() != '?' && adjacentCell.getHint() != 'D') {
                // AFN situation is true if the number of flagged cells around cell equals hint
                if (neighbouringDangers(adjacentCell) == Character.getNumericValue(adjacentCell.getHint())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Method which checks whether the Cell object passed as a parameter is in an AMN situation
     * @param cell
     * @return true if the cells is in an AMN situation
     */
    public boolean checkAMN(Cell cell) {
        ArrayList<Cell> adjacentCells = getAllNeighbours(cell);
        for (Cell adjacentCell : adjacentCells) {
            if (adjacentCell.getHint() != '?' && adjacentCell.getHint() != 'D') {
                // AMD situation is true if the number of unexamined cells around cell equals hint minus flagged cells
                if (neighbouringUnknowns(adjacentCell) == (Character.getNumericValue(adjacentCell.getHint() - neighbouringDangers(adjacentCell)))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Method which picks a cell based on the single point strategy
     */
    public void makeSPXMove() {
        Cell myCell = null;
        String action = "R";
        // iterate all unprobed cell to find situations of AFN or AMN
        for (Cell cell : unexaminedCells) {
            if (checkAFN(cell)) {
                action = "P";
                myCell = cell;
                break;
            } else if (checkAMN(cell)) {
                action = "M";
                myCell = cell;
                break;
            }
        }
        // if no unexamined cell is in an AMN or AFN situation, make random move.
        if (action == "R") {
            System.out.println("No SPX, going random.");
            makeRandomMove();
        } else if (action == "P") {
            //System.out.println("AFN found, probing");
            probeCell(myCell);
        } else {
            //System.out.println("AMN found, marking");
            markCell(myCell);
        }
    }

    /** -------------------------------------------- SAT METHODS ------------------------------------------------**/


    /**
     * Method which takes an uncovered Cell object as a parameter and it evaluates its surroundings in order to construct
     * a logical formula.
     * @param cell
     * @return a String representing a logical formula with information about the parameter's surrounding cells.
     */
    public String createClause(Cell cell) {

        // get all the neighbours of the cell
        ArrayList<Cell> neighbours = getAllNeighbours(cell);
        // contains unknown neighbours of parameter cell
        ArrayList<Cell> unknownCells = new ArrayList<>();
        // contains marked neighbours of parameter cell
        ArrayList<Cell> markedNeighbours = new ArrayList<>();
        ArrayList<String> markedLiterals = new ArrayList<>();

        // populate the markedNeighbours and unknownCells lists
        for (Cell myCell : neighbours) {
            if (myCell.getHint() == 'D') {
                markedNeighbours.add(myCell);
            } else if (myCell.getHint() == '?') {
                unknownCells.add(myCell);
            }
        }

        // create the literals of each cell
        ArrayList<String> literals = new ArrayList<>();
        for (Cell unknownCell : unknownCells) {
            literals.add("T" + unknownCell.x + unknownCell.y);
        }
        for (Cell markedCell: markedNeighbours) {
            markedLiterals.add("T" + markedCell.x + markedCell.y);
        }

        // number of neighbouring tornado cells
        int nTornadoes = Character.getNumericValue(cell.getHint());
        // number of neighbouring cells that are unknown
        int nUnknowns = unknownCells.size();
        // number of neihbouring cells marked as dangers i.e. flagged
        int nMarked = neighbouringDangers(cell);

        // get all the permutations, to be used when adding the negation
        ArrayList<ArrayList<String>> permutedClauses = listPermutations(literals);
        for (int i = 0; i < permutedClauses.size(); i++) {
            ArrayList<String> currentClause = permutedClauses.get(i);
            // nUnknowns - nTornados - nMarked is the number of free/safe cells around cell
            // used to get all possible scenarios
            for (int j = 0; j < nUnknowns - nTornadoes - nMarked; j++) {
                String clause = currentClause.get(j);
                currentClause.remove(clause);
                clause = "~" + clause;
                currentClause.add(0, clause);
            }
        }

        // build the logical formula string
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < permutedClauses.size(); i++) {
            ArrayList<String> currentClause = permutedClauses.get(i);
            stringBuilder.append("(");
            for (int j = 0; j < currentClause.size(); j++) {
                String clause = currentClause.get(j);
                stringBuilder.append(clause);
                stringBuilder.append("&");
            }
            // delete trailing &
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append(")");
            stringBuilder.append("|");
        }

        // delete trailing |
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();

    }

    /**
     * Method which taked all uncoveredCells as a parameter, and created a logical formula with all the possibilities
     * of where tornados could possibly be.
     * @param uncoveredCells cells that have been uncovered i.e. probed
     * @return a String representation of the knowledge base.
     */
    public String convertKB(ArrayList<Cell> uncoveredCells) {

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < uncoveredCells.size(); i++) {
            Cell cell = uncoveredCells.get(i);
            if (neighbouringUnknowns(cell) > 0) {
                // for each cell, get a single clause
                String clause = createClause(cell);
                if (clause != "") {
                    stringBuilder.append("(");
                    stringBuilder.append(clause);
                    stringBuilder.append(")");
                    stringBuilder.append("&");
                }
            }
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        return stringBuilder.toString();
    }


    /**
     * Method which carries out the SAT move strategy. It calls the methods required to turn the knowledge base into
     * a logical formula string, then calls the methods that encode this into CNF DIMACS and then uses the SAT4J solver
     * to assess whether a Cell is safe to be probed.
     */
    public boolean makeSATMove() {

        ISolver solver;
        Cell myCell = null;
        String action = "R";
        try {
            // Create the KB from the probed Cells
            String kbString = convertKB(uncoveredCells);
            DIMACSGenerator dimacsGenerator = new DIMACSGenerator();
            // parse the String representing the knowledge base into a logical formula
            Formula formula = p.parse(kbString);
            // convert the formula to a CNF DIMACS encoding
            int[][] dimacsClauses = dimacsGenerator.convertToDIMACS(formula);
            // instantiate the solver
            solver = SolverFactory.newDefault();
            solver.newVar(1000);
            solver.setExpectedNumberOfClauses(50000);
            for (int j = 0; j < dimacsClauses.length; j++) {
                VecInt vecInt = new VecInt(dimacsClauses[j]);
                // add clause to solved
                solver.addClause(vecInt);
            }
            // for every unexamined cells check whether the possibility of it containing a tornado is satisfiable.
            // if not then it means that the cell can be probed safely.
            for (Cell cell : unexaminedCells) {
                String clause = "T" + Integer.toString(cell.x) + Integer.toString(cell.y);
                if (dimacsGenerator.getLiteralsHashMap().containsKey(clause)) {
                    int literal = dimacsGenerator.getLiteralsHashMap().get(clause);
                    int[] literalArray = new int[]{literal};
                    if (!solver.isSatisfiable(new VecInt(literalArray))) {
                        myCell = cell;
                        action = "P";
                        break;
                    }
                }
            }
            if (action == "P") {
                probeCell(myCell);
            }
            else if (action == "R") {
                System.out.println("SAT could not determine, going Random");
                makeRandomMove();
            }
        } catch (ParserException e) {
            System.out.println("Parser Exception: " + e.getMessage());

        } catch (ContradictionException e) {
            System.out.println("Contradiction Exception: " + e.getMessage());
        } catch (TimeoutException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return true;
    }

    /**
     * Method which plays the game. According to the agent type, it will use a method fitting that strategy
     */
    public String playGame() {
        while (!game.isGameOver()) {
            switch (type) {
                case "RPX":
                    clearNeighbours();
                    System.out.println();
                    if (!game.isGameWon()) {
                        //System.out.println("Making random move");
                        makeRandomMove();
                        board.printBoard();
                    }
                    break;
                case "SPX":
                    clearNeighbours();
                    System.out.println();
                    if (!game.isGameWon()) {
                        //System.out.println("Making SPX move");
                        makeSPXMove();
                        board.printBoard();
                    }
                    break;
                case "SATX":
                    clearNeighbours();
                    System.out.println();
                    if (!game.isGameWon()) {
                        //System.out.println("Making SAT move");
                        makeSATMove();
                        board.printBoard();
                    }
                    break;
                default:
                    break;
            }
        }
        // depending on whether the game has been won or not, it will return the appropriate string
        if (game.isGameWon()) {
            System.out.println("game won");
            System.out.println();
            return "game won";
        } else {
            System.out.println("game lost");
            System.out.println();
            return "game lost";
        }
    }
}
