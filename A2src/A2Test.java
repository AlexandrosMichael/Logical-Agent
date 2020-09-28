import java.util.ArrayList;

/**
 * Class with a main method that is used to compare the performance of the different agent types. The wolrd is set up
 * in the same way as in A2main.java with some small differences to allow a comparison to be made.
 */
public class A2Test {

    private static Agent agent;
    private static Game game;


    /**
     * Method which sets up the Game and Agent instance required for the game to be played
     * @param agentType i.e. RPX, SPX or SAT
     * @param worldName e.g. S2, M7, L3
     * @return returns a String with either 'game won' or 'game lost' indicating whether the agent has won or lost
     * the game.
     */
    public static String setupWorld(String agentType, String worldName) {
        game = new Game(worldName);
        agent = new Agent(agentType, game);
        String gameResult = agent.playGame();
        return gameResult;
    }


    /**
     * Main method of the class
     * @param args can pass the number of times the 30 worlds are played by each agent. Default is 5
     */
    public static void main(String[] args) {
        int rpxWon = 0;
        int spxWon = 0;
        int satWon = 0;
        int nIterations = 5;
        if (args.length == 1) {
            try {
                nIterations = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException e) {
                System.out.println("Exception: " + e.getMessage());
            }
        }
        // Adding the agent types in a list to be iterated later on
        ArrayList<String> agentTypes = new ArrayList<>();
        agentTypes.add("RPX");
        agentTypes.add("SPX");
        agentTypes.add("SATX");

        //Adding the world names  in a list to be iterated later on
        ArrayList<String> worlds = new ArrayList<>();
        worlds.add("S");
        worlds.add("M");
        worlds.add("L");

        for (int j = 0; j < nIterations; j++) {
            for (int i = 1; i <= 10; i++) {
                for (String agentType : agentTypes) {
                    for (String world : worlds) {
                        // append the version of the world i.e. S1, S2, S3 etc....
                        world = world + Integer.toString(i);
                        System.out.println("World: " + world + " Agent: " + agentType);
                        String result = setupWorld(agentType, world);
                        switch (result) {
                            // if game is won, repending on the agent type, increment variable
                            case "game won":
                                if (agentType == "RPX") {
                                    rpxWon++;
                                } else if (agentType == "SPX") {
                                    spxWon++;
                                } else if (agentType == "SATX") {
                                    satWon++;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }

        // print out the results
        System.out.println();
        System.out.println("------------------------------------------");
        System.out.println("\tResults for " + nIterations + " attempt(s) at each level:");
        System.out.println("\tRPX won: " + rpxWon + "/" + (30*nIterations) + " - " + Math.round(((Integer.valueOf(rpxWon).floatValue()/(30*nIterations)) * 100)) + "% win rate");
        System.out.println("\tSPX won: " + spxWon + "/" + (30*nIterations) + " - " + Math.round(((Integer.valueOf(spxWon).floatValue()/(30*nIterations)) * 100))+ "% win rate");
        System.out.println("\tSATX won: " + satWon + "/" + (30*nIterations) + " - " + Math.round(((Integer.valueOf(satWon).floatValue()/(30*nIterations)) * 100)) + "% win rate");
        System.out.println("------------------------------------------ ");
    }
}
