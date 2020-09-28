/*
 * CS5011 A2 Starter code
 * This class holds the board to be played
 * An example on how to print the board is given
 *
 * author Alice Toniolo
 *
 * From another class you can access, set and print a world map as
 * World world = World.valueOf("TEST0");
 * Board board = new Board(world.map);
 * board.printBoard();
 *
 */

public class Board {

    char[][] board;

    public Board(char[][] map) {
        board = map;
    }

    // method to print the board
    public void printBoard() {
        System.out.println();
        // first line
        for (int l = 0; l < board.length + 5; l++) {
            System.out.print(" ");// shift to start
        }
        for (int j = 0; j < board[0].length; j++) {
            System.out.print(j);// x indexes
            if (j < 10) {
                System.out.print(" ");
            }
        }
        System.out.println();
        // second line
        for (int l = 0; l < board.length + 3; l++) {
            System.out.print(" ");
        }
        for (int j = 0; j < board[0].length; j++) {
            System.out.print(" -");// separator
        }
        System.out.println();
        // the board
        for (int i = 0; i < board.length; i++) {
            for (int l = i; l < board.length - 1; l++) {
                System.out.print(" ");// fill with left-hand spaces
            }
            if (i < 10) {
                System.out.print(" ");
            }

            System.out.print(i + "/ ");// index+separator
            for (int j = 0; j < board[0].length; j++) {
                System.out.print(board[i][j] + " ");// value in the board
            }
            System.out.println();
        }
        System.out.println();
    }
}
