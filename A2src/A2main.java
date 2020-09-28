/**
 * Main class of the program. It does some simple parameter checking and it then creates the game instance,
 * the right agent instance and calls the agent's playGame method
 */
public class A2main {

    // instance of Agent i.e. either RPX, SPX, SATX
    private static Agent agent;
    // instance of Game, holding the actual world view as well as the state of the game e.g game is over, game is won
    private static Game game;


    /**
     * Method which sets up the Game and Agent instance required for the game to be played
     * @param agentType i.e. RPX, SPX or SAT
     * @param worldName e.g. S2, M7, L3
     */
    public static void setupWorld(String agentType, String worldName) {
        game = new Game(worldName);
        // print the board i.e. actual world view
        System.out.println("Game board view");
        game.getBoard().printBoard();
        // instantiate the agent
        agent = new Agent(agentType, game);
        // play the game
        agent.playGame();
    }


    public static void main(String[] args) {

        // if args is equal to 2
        if (args.length == 2) {
            // parse the arguments
            String agent = args[0];
            String world = args[1];
            // set up the wolrd to start the game
            setupWorld(agent, world);

        }
        else {
            // usage message in case the number of arguments is wrong
            System.out.println("Invalid arguments. Usage: java A2main <RPX|SPX|SATX> <ID>");
        }
    }
}
