package phase2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/* 
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.function.Function;
*/

public class Parser {
    static Path inputFile = Path.of("phase1", "Output.txt");

    static List<String> lines;
    static int i = 0;

    static String input;

    public static void main(String[] args) {
        try {
            lines = Files.readAllLines(inputFile);
        }
        catch(IOException e)
        {
            System.out.println("Couldn't read input file: " + e);
            return;
        }

        Parse();
    }

    // TODO: convert pseudocode to real code!!!
    static void Parse()
    {
        input = getInput();
        Program();  // The initial starting non-terminal
        
        if(i >= lines.size()) {
            accept();
        }
        else {
            reject();
        }
    }

    static void accept()
    {
        System.out.println("Accept");
    }

    static void reject()
    {
        System.out.println("Rejected: " + input);
        System.exit(0);  // terminate
    }

    static String getInput()
    {
        String input = lines.get(i);
        i++;
        System.out.println(input + " " + i);
        return input;
    }

    static void Program()
    {
        Statement();

        if(input.equals("end_of_line"))
        {
            input = getInput();
            if(i < lines.size())
            {
                Program();
            }
        }
        else {
            reject();
        }
    }

    static void Statement()
    {
        if(input.equals("for_kwd"))
        {
            input = getInput();
            
            if(input.equals("parenthesis_left"))
            {
                input = getInput();
                Assignment();

                if(input.equals("end_of_line"))
                {
                    input = getInput();
                    Comparison();
                        
                    if(input.equals("end_of_line"))
                    {
                        input = getInput();
                        Assignment();
                            
                        if(input.equals("parenthesis_right"))
                        {
                            input = getInput();
                            if(input.equals("curly_brace_left"))
                            {
                                input = getInput();
                                Program();
                                    
                                if(input.equals("curly_brace_right"))
                                {
                                    input = getInput();
                                }
                                else
                                {
                                    reject();
                                }
                            }
                            else
                            {
                                reject();
                            }
                        }
                        else
                        {
                            reject();
                        }
                    }
                    else
                    {
                        reject();
                    }
                }
                else
                {
                    reject();
                }
            }  
            else
            {
                reject();
            }
        }
        else if(input.equals("while_kwd"))
        {
            input = getInput();
            if(input.equals("parenthesis_left"))
            {
                input = getInput();
                Comparison();
                if(input.equals("parenthesis_right"))
                {
                    input = getInput();
                    if(input.equals("curly_brace_left"))
                    {
                        input = getInput();
                        Program();
                        
                        if(input.equals("curly_brace_right"))
                        {
                            input = getInput();
                        }
                        else
                        {
                            reject();
                        }
                    }
                    else
                    {
                        reject();
                    }
                }
                else
                {
                    reject();
                }
            }
            else
            {
                reject();
            }
        }
        else if(input.equals("if_kwd"))
        {
            input = getInput();
            if(input.equals("parenthesis_left"))
            {	
                input = getInput();
                Comparison();
                if(input.equals("parenthesis_right"))
                {
                    input = getInput();
                    if(input.equals("curly_brace_left"))
                    {
                        input = getInput();
                        Program();
                        if(input.equals("curly_brace_right"))
                        {
                            input = getInput();
                        }
                        else
                        {
                            reject();
                        }
                    }
                    else
                    {
                        reject();
                    }
                }
                else
                {
                    reject();
                }
            }
            else
            {
                reject();
            }
        }
        else if(input.equals("else_kwd"))
        {
            if(input.equals("curly_brace_left"))
            {
                input = getInput();
                Program();
                if(input.equals("curly_brace_right"))
                {
                    input = getInput();
                }
                else
                {
                    reject();
                }
            }
            else
            {
                reject();
            }
        }
        else
        {
            Assignment();
        }
    }

    static void Assignment()
    {
        if(input.equals("int_type"))
        {
            input = getInput();
            if(input.startsWith("var_id"))
            {
                input = getInput();
                if(input.equals("assign_op"))
                {
                    input = getInput();
                    Value();
                }
                else
                {
                    reject();
                }
            }
            else
            {
                reject();
            }
        }
        else if(input.equals("double_type"))
        {
            input = getInput();
            if(input.startsWith("var_id"))
            {
                input = getInput();
                if(input.equals("assign_op"))
                {
                    input = getInput();
                    Value();
                }
                else
                {
                    reject();
                }
            }
            else
            {
                reject();
            }
        }
        else if(input.startsWith("var_id"))
        {
            input = getInput();
            if(input.equals("assign_op"))
            {
                input = getInput();
                Value();
            }
            else
            {
                reject();
            }
        }
        else
        {
            reject();
        }

    }

    static void Value()
    {
        if(input.startsWith("int_literal"))
        {
            input = getInput();
        }
        else if(input.startsWith("double_literal"))
        {
            input = getInput();
        }
        else
        {
            Expression();
        }
    }

    static void Expression()
    {
        if(input.startsWith("var_id"))
        {
            input = getInput();
            if(input.equals("add_op"))
            {
                input = getInput();
                Term();
            }
            else if(input.equals("sub_op"))
            {
                input = getInput();
                Term();
            }
            else
            {
                reject();
            }
        }

        else
        {
            Term();
        }
    }

    static void Term()
    {
        if(input.startsWith("var_id"))
        {
            input = getInput();
            if(input.equals("mult_op"))
            {
                input = getInput();
                Term();
            }
            else if(input.equals("div_op"))
            {
                input = getInput();
                Term();
            }
            //otherwise, we just have a var_id
        }
        else if(input.startsWith("int_literal"))
        {
            input = getInput();
        }
        else if(input.startsWith("double_literal"))
        {
            input = getInput();
        }
        else
        {
            reject();
        }

    }

    static void Comparison()
    {
        if(input.startsWith("var_id"))
        {
            input = getInput();
            ComparisonOp();
            if(input.startsWith("int_literal"))
            {
                input = getInput();
            }
            else if(input.startsWith("double_literal"))
            {
                input = getInput();
            }
            else
            {
                reject();
            }
        }
    }

    static void ComparisonOp()
    {
        if(input.equals("less_than"))
        {
            input = getInput();
        }
        else if(input.equals("less_or_equal"))
        {
            input = getInput();
        }
        else if( input.equals("greater_than"))
        {
            input = getInput();
        }
        else if(input.equals("greater_or_equal"))
        {
            input = getInput();
        }
        else if(input.equals("equal"))
        {
            input = getInput();
        }
        else if(input.equals("unequal"))
        {
            input = getInput();
        }
        else 
        {
            reject();
        }
    }
}