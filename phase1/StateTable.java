package phase1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class StateTable {

    //transition table
    private int [][] transitionTable = {
        {0,1,2,3,-1,6,7,8,9,10,12,14,16,18,19,20,21,22}, //start: 0
        {1,1,1,1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}, //1
        {2,-1,-1,-1,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}, //2
        {3,-1,3,3,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}, //3
        {4,-1,5,5,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}, //4
        {5,-1,5,5,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}, //5
        {6,-1,2,3,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}, //6
        {7,-1,2,3,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}, //7
        {8,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}, //8
        {9,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}, //9
        {10,-1,-1,-1,-1,-1,-1,-1,-1,11,-1,-1,-1,-1,-1,-1,-1,-1}, //10
        {11,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}, //11
        {12,-1,-1,-1,-1,-1,-1,-1,-1,13,-1,-1,-1,-1,-1,-1,-1,-1}, //12
        {13,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}, //13
        {14,-1,-1,-1,-1,-1,-1,-1,-1,15,-1,-1,-1,-1,-1,-1,-1,-1}, //14
        {15,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}, //15
        {16,-1,-1,-1,-1,-1,-1,-1,-1,17,-1,-1,-1,-1,-1,-1,-1,-1}, //16
        {17,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}, //17
        {18,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}, //18
        {19,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}, //19
        {20,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},//20
        {21,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}, //21
        {22,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1} //22
    };

    private boolean [] acceptingTable = {
        false, 
        true,
        true,
        true,
        false,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        false,
        true,
        true,
        true,
        true,
        true,
        true
    };

    // initialize the state-action table full of nulls
    private static List<Function<String, String>> stateActionTable = new ArrayList<>(Collections.nCopies(23, null));
    static {
        // ending on state 1 calls the valid() function
        stateActionTable.set(1, s -> valid(s));
    }

    public int [][] getTransitionTable()
    {
        return transitionTable;
    }   

    public boolean [] getAcceptingTable()
    {
        return acceptingTable;
    }

    public List<Function<String, String>> getStateActionTable()
    {
        return stateActionTable;
    }

    // determines if the token is a recognized keyword or a variable identifier
    public static String valid(String token)
    {
        if (token.equals("for")) return "for_kwd";
        if (token.equals("while")) return "while_kwd";
        if (token.equals("if")) return "if_kwd";
        if (token.equals("else")) return "else_kwd";
        if (token.equals("int")) return "int_type";
        if (token.equals("double")) return "double_type";
        return "var_id";
    }
}
