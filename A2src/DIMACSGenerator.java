import org.logicng.formulas.Formula;
import org.logicng.formulas.Literal;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Set;

/**
 * Class which takes the knowledge base as a logic formula and converts it into cnf and then into the DIMACS formar
 * as required by the SAT solver to be used.
 */
public class DIMACSGenerator {
    // Hash Map which will hold the encoding of the literals used in the clauses.
    private HashMap<String, Integer> literalsHashMap;

    /**
     * Method which takes the logic formula to be translated into DIMACS format
     * @param formula the logic formula to be converted to DIMACS format
     * @return a 2d int array representing the CNF clauses in DIMACS format
     */
    public int[][] convertToDIMACS(Formula formula){
        literalsHashMap = new HashMap<>();
        ArrayList<int[]> clauses = new ArrayList<>();
        // convert the formula into CNF format
        Formula cnf = formula.cnf();
        // encode the literals into integers
        encodeLiterals(cnf.literals());
        Iterator<Formula> iterator = cnf.iterator();
        while (iterator.hasNext()) {
            clauses.add(getClause(iterator.next()));

        }
        int[][] clausesArray = new int[clauses.size()][];
        for (int i = 0; i < clauses.size(); i++) {
            int[] singleClause = clauses.get(i);
            clausesArray[i] = singleClause;
        }
        return clausesArray;
    }

    /**
     * Method which gets a single clause of the entire formula and returns its encoding in DIMACS
     * @param
     * @return
     */
    public int[] getClause(Formula clause){
        int index = 0;
        int[] literals = new int[clause.variables().size()];
        Iterator<Formula> iterator= clause.iterator();
        // if it's an atomic formula, i.e. only one variable, no need to iterate
        if (clause.isAtomicFormula()) {
            literals[index] = literalsHashMap.get(clause.toString());
        }
        // if it's not an atomic formula, iterate to get the literals
        else {
            while(iterator.hasNext()) {
                literals[index] = literalsHashMap.get(iterator.next().toString());
                index++;
            }
        }
        return literals;
    }

    /**
     * Method which calls the method generateValue on all the literals of the CNF logic formula
     * @param literals
     */
    public void encodeLiterals(SortedSet<Literal> literals) {
        Iterator<Literal> it = literals.iterator();
        while(it.hasNext()){
            generateValue(it.next().toString());
        }
    }

    /**
     * Method which encodes all of the literals into an integer value, as required by the SAT solver.
     * For each literal, it will also encode its negation. Before encoding and adding something in the Hashmap
     * it will check if the literal has already been encoded.
     * @param literal which is to be encoded and added to the Hashmap
     */
    public void generateValue(String literal){
        // if it's a negative literal
        if (literal.startsWith("~")) {
            // check if it hasn't been encoded already
            if (literalsHashMap.get(literal) == null) {
                String literalPos = literal.replace("~", "");
                // check if the positive counterpart has been encoded
                if (literalsHashMap.get(literalPos) != null) {
                    literalsHashMap.put(literal, literalsHashMap.get(literalPos) * (-1));
                }
                // if positive counterpart has not been encoded
                else {
                    literalsHashMap.put(literalPos, literalsHashMap.size()+1);
                    literalsHashMap.put(literal, literalsHashMap.get(literalPos)* (-1));
                }
            }
            // if the negative literal has been encoded
            else {
                String literalPos = literal.replace("~", "");
                // both the negative literal and positive counterpart have been encoded, no need to encode anything
                if (literalsHashMap.get(literalPos) != null) {
                    return;
                }
                else {
                    // encode the negative literal by negating the existing encoding of the positive literal
                    literalsHashMap.put(literal, literalsHashMap.get(literal) * (-1));
                }
            }
        }
        // and vice versa of the above is the literal is positive
        else {
            if (literalsHashMap.get(literal) == null){
                String literalNeg = "~" + literal;
                if (literalsHashMap.get(literal) != null) {
                    literalsHashMap.put(literal, literalsHashMap.get(literalNeg) * (-1));
                }
                else {
                    literalsHashMap.put(literal, literalsHashMap.size() + 1);
                    literalsHashMap.put(literalNeg, literalsHashMap.get(literal) * (-1));
                }
            }
            else {
                String literalNeg = "~" + literal;
                if (literalsHashMap.get(literalNeg) != null) {
                    return;
                }
                else {
                    literalsHashMap.put(literalNeg, literalsHashMap.get(literal) * (-1));
                }
            }

        }
    }


    /**
     * Simple getter
     * @return the literalsHashMap
     */
    public HashMap<String, Integer> getLiteralsHashMap() {
        return literalsHashMap;
    }
}