package phase2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class Parser {
    static Path inputFile = Path.of("phase1", "Output.txt");
    static Path outputFile = Path.of("phase2", "Atoms.txt");

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

        try {
            // overwrite output file to make it empty
            Files.writeString(outputFile, "");
        }
        catch(IOException e)
        {
            System.out.println("Couldn't write to output file.");
            System.exit(0);
        }

        Parse();
    }

    static void Parse()
    {
        input = getInput();
        Program();  // The initial starting non-terminal
        
        if(i >= lines.size()) {
            accept();
        }
        else {
            System.out.println("Rejected in parse");
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
        System.exit(0);  // Terminate
    }

    static String getInput()
    {
        input = lines.get(i);
        i++;
        System.out.println(input + " " + i);
        return input;
    }

    static void Program()
    {

        Statement();
        
        

        if(input.equals("end_of_line"))
        {
            
            // Crashing without this if statement
            if(i<lines.size())
            {
                input = getInput();
            }
            

            if(i < lines.size())
            {
                System.out.println("This PROGRAM() is reached.");
                if(input.equals("curly_brace_right"))
                {
                    input = getInput();
                    System.out.println("Input #1 is: " + input);
                    
                    if(i < lines.size())
                    {
                        input = getInput();
                        System.out.println("Input #2 is: " + input);
                    }
                    else
                    {
                        return;
                    }

                    
                }
                if(i<lines.size())
                {
                    
                    Program();
                }
                
                
            }
        }
        else {
            System.out.println("This REJECT is reached.");
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

                                // //DO WE NEED THIS NOW????????
                                // if(input.equals("curly_brace_right"))
                                // {
                                //     input = getInput();
                                // }
                                // else
                                // {
                                    
                                //     reject();
                                // }
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
                        
                        // if(input.equals("curly_brace_right"))
                        // {
                        //     input = getInput();
                        // }
                        // else
                        // {
                        //     reject();
                        // }
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
                        // if(input.equals("curly_brace_right"))
                        // {
                        //     input = getInput();
                        // }
                        // else
                        // {
                        //     reject();
                        // }
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
            input = getInput();
            if(input.equals("curly_brace_left"))
            {
                input = getInput();
                Program();
                // if(input.equals("curly_brace_right"))
                // {
                //     input = getInput();
                // }
                // else
                // {
                //     reject();
                // }
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
                            
                            // if(input.equals("curly_brace_right"))
                            // {
                                
                            //     input = getInput();
                            // }
                            // else
                            // {
                            //     System.out.println("HELLOO");
                            //     reject();
                            // }
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
            String var_name = input.split(" ")[1];

            input = getInput();
            if(input.equals("assign_op"))
            {
                input = getInput();
                Value();
                
                //output atom
                try {
                    Files.writeString(outputFile, "(MOV," + "val" + ",," + var_name + ")\n", StandardOpenOption.APPEND);
                }
                catch(IOException e)
                {
                    System.out.println("Couldn't write to output file.");
                    System.exit(0);
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
        // TODO: make it possible to reach Term from here :(
        if(input.startsWith("var_id") && !lines.get(i).startsWith("mult_op") && !lines.get(i).startsWith("div_op")&& !lines.get(i).startsWith("end_of_line"))
        {
            String var_name = input.split(" ")[1];

            input = getInput();
            
            if(input.equals("add_op"))
            {
                input = getInput();
                Term();

                //output atom
                try {
                    Files.writeString(outputFile, "(ADD," + var_name + "," + "val" + "," + var_name + ")\n", StandardOpenOption.APPEND);
                }
                catch(IOException e)
                {
                    System.out.println("Couldn't write to output file.");
                    return;
                }
            }
            else if(input.equals("sub_op"))
            {
                input = getInput();
                Term();

                //output atom
                try {
                    Files.writeString(outputFile, "(SUB," + var_name + "," + "val" + "," + var_name + ")\n", StandardOpenOption.APPEND);
                }
                catch(IOException e)
                {
                    System.out.println("Couldn't write to output file.");
                    return;
                }
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
            String var_name = input.split(" ")[1];

            input = getInput();
            if(input.equals("mult_op"))
            {
                input = getInput();
                Term();

                //output atom
                try {
                    Files.writeString(outputFile, "(MUL," + var_name + "," + "val" + "," + var_name + ")\n", StandardOpenOption.APPEND);
                }
                catch(IOException e)
                {
                    System.out.println("Couldn't write to output file.");
                    return;
                }
            }
            else if(input.equals("div_op"))
            {
                input = getInput();
                Term();

                //output atom
                try {
                    Files.writeString(outputFile, "(DIV," + var_name + "," + "val" + "," + var_name + ")\n", StandardOpenOption.APPEND);
                }
                catch(IOException e)
                {
                    System.out.println("Couldn't write to output file.");
                    return;
                }
            }
            else {  //otherwise, we just have a var_id
                input = getInput();
            }
        }
        else if(input.startsWith("int_literal"))
        {
            // int val = Integer.parseInt(input.split(" ")[1]);
            input = getInput();
            // return val;
        }
        else if(input.startsWith("double_literal"))
        {
            // double val = Double.parseDouble(input.split(" ")[1]);
            input = getInput();
            // return val;
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