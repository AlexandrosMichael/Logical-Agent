# Logical Agent - Tornado Sweeper

This is an implementation of a logical agent with the ability to play and solve the Tornado Sweeper game. In a similar fashion to the Minesweeper game, the goal of the game is to uncover all the cells on a hexagonal board but those containing a tornado. In the scenario in which the agent uncovers a cell containing a tornado, the game ends and the agent loses. The specification outlines several rules of Tornado Sweeper, which the program must follow and it describes different strategies that the agent can adapt in order to play the game such as the ‘Single Point Strategy for Hexagonal Worlds’ (SPX) and the ‘Satisfiability Strategy for Hexagonal Worlds’ (SATX).

## Installation and Usage

First navigate to the base directory, P2. Then in order to run the program with the already compiled source code (found in the P2/out/production/P2 folder , you may use the following command:

```bash
java -cp sat4j.jar:antlr.jar:logicng.jar:out/production/P2 A2main <RPX|SPX|SATX> <ID>
```
For example, to run the agent using the SPX strategy on world M5, use the following command:

```bash
java -cp sat4j.jar:antlr.jar:logicng.jar:out/production/P2 A2main SPX M5
```

In order to re-compile the source code, you may use the following command:

```bash
javac -cp sat4j.jar:antlr.jar:logicng.jar -d out/production/P2 A2src/*.java
```
In order to run the program comparing the performance of the agents, run the following command:

```bash
java -cp sat4j.jar:antlr.jar:logicng.jar:out/production/P2 A2Test [iterations]
```
The iterations parameter is optional and if not included, the program will carry out 5 iterations over the worlds by default. This program will play the game with all the agents, for all the worlds for a [iterations] number of times, and in the end it will print the results. 

## Report

See CS5011_P2_Report.pdf for a complete report, containing installation and usage instructions.
