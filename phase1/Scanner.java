package phase1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.function.Function;

public class Scanner {
    public static void main(String[] args) {

        Path inputFile = Path.of("phase1", "DummyFile.txt");
        Path outputFile = Path.of("phase1", "Output.txt");

        String fileContent;

        try {
            fileContent = Files.readString(inputFile);
        }
        catch(IOException e)
        {
            System.out.println("Couldn't read input file: " + e);
            return;
        }

        try {
            // overwrite output file to make it empty
            Files.writeString(outputFile, "");
        }
        catch(IOException e)
        {
            System.out.println("Couldn't write to output file.");
            return;
        }

        StateTable tables = new StateTable();
        int [][] transitionTable = tables.getTransitionTable();
        boolean [] acceptingTable = tables.getAcceptingTable();
        List<Function<String, String>> stateActionTable = tables.getStateActionTable();

        int state = 0; 
        String input = "";
        String stateName;
        for(int i=0; i<fileContent.length(); i++)
        {
            char c = fileContent.charAt(i);

            int columnIndex = getColumn(c);

            if(columnIndex != -1 && transitionTable[state][columnIndex] != -1)
            {
                state = transitionTable[state][columnIndex];
                input += c;
            }

            else if(acceptingTable[state] == true)
            {
                // state-action handling on accepting state
                if (stateActionTable.get(state) != null) {
                    stateName = stateActionTable.get(state).apply(input);
                }
                else {
                    stateName = getStateName(state);
                }

                if (stateName.equals("var_id") || stateName.contains("literal")) {
                    stateName += " " + input;
                }

                stateName += "\n";

                //output token
                try {
                    Files.writeString(outputFile, stateName, StandardOpenOption.APPEND);
                }
                catch(IOException e)
                {
                    System.out.println("Couldn't write to output file.");
                    return;
                }

                // empty the input token substring and reset to the start state
                input = "";
                state = 0;

                // avoid skipping characters directly adjacent to accepted tokens
                i--;
            }
            else if(c == ' ' || c == '\n'  || c == '\r')
            {
                continue;
            }
            else
            {
                //reject/crash
                System.out.println("Ended on a non-accepting state.");
                return;
            }
        }

        System.out.println("Successfully wrote to output file '" + outputFile + "'");
    }

    private static int getColumn(char c)
    {
        int index = -1;

        //checks a-zA-Z_
        if((c>='a' && c<='z') || (c>='A' && c<='Z') || c == '_')
        {
            index = 1;
        }
        else if(c=='0')
        {
            index =2;
        }
        else if (c>='1' && c<='9') 
        {
            index = 3;
        }
        else if(c=='.')
        {
            index = 4;
        }
        else if(c == '+')
        {
            index = 5;
        }
        else if(c=='-')
        {
            index = 6;
        }
        else if(c == '*')
        {
            index = 7;
        }
        else if(c == '/')
        {
            index = 8;
        }
        else if(c=='=')
        {
            index = 9;
        }
        else if(c=='<')
        {
            index = 10;
        }
        else if(c == '>')
        {
            index = 11;
        }
        else if(c =='!')
        {
            index = 12;
        }
        else if(c=='{')
        {
            index = 13;
        }
        else if(c == '}')
        {
            index = 14;
        }
        else if(c == '(')
        {
            index = 15;
        }
        else if(c == ')')
        {
            index = 16;
        }
        else if(c == ';')
        {
            index = 17;
        }
        
       
        return index;
    }

    private static String getStateName(int state) {
        switch (state) {
            case 0: return "";
            case 2: return "int_literal";
            case 3: return "int_literal";
            case 5: return "double_literal";
            case 6: return "add_op";
            case 7: return "sub_op";
            case 8: return "mult_op";
            case 9: return "div_op";
            case 10: return "assign_op";
            case 11: return "equal";
            case 12: return "less_than";
            case 13: return "less_or_equal";
            case 14: return "greater_than";
            case 15: return "greater_or_equal";
            case 17: return "unequal";
            case 18: return "curly_brace_left";
            case 19: return "curly_brace_right";
            case 20: return "parenthesis_left";
            case 21: return "parenthesis_right";
            case 22: return "end_of_line";
            default: return "ERROR";
        }
    }
}