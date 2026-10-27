package phase2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    static Path inputFile = Path.of("phase1", "Output.txt");
    static Path outputFile = Path.of("phase2", "Atoms.txt");

    static List<String> lines;
    static int i = 0;

    static String input;

    static List<String> atoms = new ArrayList<>();

    static int temp_reg_num = 0;

    static int loop_num = 0;
    static int dest_num = 0;
    static int exit_num = 0;

    static int nested = 0;

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

        // output atoms
        try {
            Files.write(outputFile, atoms, StandardOpenOption.APPEND);
        }
        catch (IOException e) {
            System.out.println("Couldn't write to output file.");
            System.exit(0);
        }
        
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

        if(input.equals("curly_brace_right"))
        {
            return;
        }

        
        Statement();
        
        if(input.equals("curly_brace_right"))
        {
            return;
        }
        

        if(input.equals("end_of_line"))
        {
            System.out.println("The input is " + input);
            // Crashing without this if statement
            if(i<lines.size())
            {
                input = getInput();
                System.out.println("The input is " + input);

            }
            System.out.println("Nested is reached, and the value is " + nested);
            
            

            if(i<lines.size())
            {
                if(input.equals("curly_brace_right"))
                {
                    System.out.println("Return inside Program to og caller.");
                    return;
                }
                else
                {
                    Program();
                }
                
            }
        }
        else 
        {
            System.out.println("This REJECT is reached in PROGRAM!!");
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
                    String loop_label = "loop" + loop_num;
                    loop_num++;

                    atoms.add(atoms.size() - nested, "(LBL,,,,," + loop_label + ")");

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
                                
                                atoms.add(atoms.size() - nested, "(JMP,,,,," + loop_label + ")");

                                if (nested > 0) {
                                    nested--;
                                }

                                System.out.println("WE GOT HERE YAY! FOR KWD IN STATEMENT");
                                // //DO WE NEED THIS NOW????????
                                if(input.equals("curly_brace_right"))
                                {
                                    if (nested > 2) {
                                        nested -= 3;
                                    }
                                    else {
                                        nested = 0;
                                    }
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
                String loop_label = "loop" + loop_num;
                loop_num++;

                atoms.add(atoms.size() - nested, "(LBL,,,,," + loop_label + ")");

                input = getInput();
                Comparison();
                if(input.equals("parenthesis_right"))
                {
                    input = getInput();
                    if(input.equals("curly_brace_left"))
                    {
                        input = getInput();
                        Program();
                        
                        atoms.add(atoms.size() - nested, "(JMP,,,,," + loop_label + ")");
                        
                        if (nested > 0) {
                            nested--;
                        }
                        System.out.println("WE GOT HERE YAY! WHILE KWD IN STATEMENT");
                                // //DO WE NEED THIS NOW????????
                        if(input.equals("curly_brace_right"))
                        {
                            if (nested > 2) {
                                nested -= 3;
                            }
                            else {
                                nested = 0;
                            }
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
                        System.out.println("WE GOT HERE YAY! IF_KWD IN STATEMENT");
                        
                        // //DO WE NEED THIS NOW????????
                        if(input.equals("curly_brace_right"))
                        {
                            if (nested > 2) {
                                nested -= 3;
                            }
                            else {
                                nested = 0;
                            }
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
            input = getInput();
            nested++;
            if(input.equals("curly_brace_left"))
            {
                input = getInput();
                Program();
                if (nested > 0) {
                    nested--;
                }
                else {
                    nested = 0;
                }

                System.out.println("WE GOT HERE YAY! ELSE_KWD IN STATEMENT");
                // //DO WE NEED THIS NOW????????
                if(input.equals("curly_brace_right"))
                {
                    if (nested > 2) {
                        nested -= 3;
                    }
                    else {
                        nested = 0;
                    }
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
                String var_name = input.split(" ")[1];

                input = getInput();
                if(input.equals("assign_op"))
                {
                    input = getInput();
                    String val = Value();
                    
                    atoms.add(atoms.size() - nested, "(MOV," + val + ",," + var_name + ")");
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
                String var_name = input.split(" ")[1];

                input = getInput();
                if(input.equals("assign_op"))
                {
                    input = getInput();
                    String val = Value();
                    
                    atoms.add(atoms.size() - nested, "(MOV," + val + ",," + var_name + ")");
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
                String val = Value();
                
                atoms.add(atoms.size() - nested, "(MOV," + val + ",," + var_name + ")");
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

    static String Value()
    {
        String val;
        if(input.startsWith("int_literal"))
        {
            val = input.split(" ")[1];
            input = getInput();
            return val;
        }
        else if(input.startsWith("double_literal"))
        {
            val = input.split(" ")[1];
            input = getInput();
            return val;
        }
        else
        {
            val = Expression();
            return val;  // temp_reg
        }
    }

    static String Expression()
    {
        String val;

        // TODO: make it possible to reach Term from here :(
        if(input.startsWith("var_id") && !lines.get(i).startsWith("mult_op") && !lines.get(i).startsWith("div_op")&& !lines.get(i).startsWith("end_of_line"))
        {
            String var_name = input.split(" ")[1];

            input = getInput();
            
            if(input.equals("add_op"))
            {
                input = getInput();
                val = Term();

                String temp_reg = "$t" + temp_reg_num;
                temp_reg_num++;

                atoms.add(atoms.size() - nested, "(ADD," + var_name + "," + val + "," + temp_reg + ")");
                return temp_reg;
            }
            else if(input.equals("sub_op"))
            {
                input = getInput();
                val = Term();
                
                String temp_reg = "$t" + temp_reg_num;
                temp_reg_num++;

                atoms.add(atoms.size() - nested, "(SUB," + var_name + "," + val + "," + temp_reg + ")");
                return temp_reg;
            }
            else
            {
                reject();
            }
        }

        else
        {
            val = Term();
            return val;
        }

        return "ERROR";
    }

    static String Term()
    {
        String val;

        if(input.startsWith("var_id"))
        {
            String var_name = input.split(" ")[1];

            input = getInput();
            if(input.equals("mult_op"))
            {
                input = getInput();
                val = Term();
                
                String temp_reg = "$t" + temp_reg_num;
                temp_reg_num++;

                atoms.add(atoms.size() - nested, "(MUL," + var_name + "," + val + "," + temp_reg + ")");
                return temp_reg;
            }
            else if(input.equals("div_op"))
            {
                input = getInput();
                val = Term();
                
                String temp_reg = "$t" + temp_reg_num;
                temp_reg_num++;

                atoms.add(atoms.size() - nested, "(DIV," + var_name + "," + val + "," + temp_reg + ")");
                return temp_reg;
            }
            else {  //otherwise, we just have a var_id
                input = getInput();
                return var_name;
            }
        }
        else if(input.startsWith("int_literal"))
        {
            val = input.split(" ")[1];
            input = getInput();
            return val;
        }
        else if(input.startsWith("double_literal"))
        {
            val = input.split(" ")[1];
            input = getInput();
            return val;
        }
        else
        {
            reject();
        }

        return "ERROR";
    }

    static void Comparison()
    {
        String val;
        if(input.startsWith("var_id"))
        {
            String var_name = input.split(" ")[1];
            input = getInput();
            String comp_op = ComparisonOp();
            if(input.startsWith("int_literal"))
            {
                val = input.split(" ")[1];
                input = getInput();

                String dest = "comp" + dest_num;
                dest_num++;

                String exit = "exit" + exit_num;
                exit_num++;

                atoms.add(atoms.size() - nested, "(TST," + var_name + "," + val + ",," + comp_op + "," + dest + ")");
                atoms.add(atoms.size() - nested, "(JMP,,,,," + exit + ")");
                atoms.add(atoms.size() - nested, "(LBL,,,,," + dest + ")");
                atoms.add(atoms.size() - nested, "(LBL,,,,," + exit + ")");

                nested += 3;
            }
            else if(input.startsWith("double_literal"))
            {
                val = input.split(" ")[1];
                input = getInput();

                String dest = "comp" + dest_num;
                dest_num++;

                String exit = "exit" + exit_num;
                exit_num++;

                atoms.add(atoms.size() - nested, "(TST," + var_name + "," + val + ",," + comp_op + "," + dest + ")");
                atoms.add(atoms.size() - nested, "(JMP,,,,," + exit + ")");
                atoms.add(atoms.size() - nested, "(LBL,,,,," + dest + ")");
                atoms.add(atoms.size() - nested, "(LBL,,,,," + exit + ")");

                nested += 3;
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

    static String ComparisonOp()
    {
        if(input.equals("less_than"))
        {
            input = getInput();
            return "5";
        }
        else if(input.equals("less_or_equal"))
        {
            input = getInput();
            return "3";
        }
        else if( input.equals("greater_than"))
        {
            input = getInput();
            return "4";
        }
        else if(input.equals("greater_or_equal"))
        {
            input = getInput();
            return "2";
        }
        else if(input.equals("equal"))
        {
            input = getInput();
            return "6";
        }
        else if(input.equals("unequal"))
        {
            input = getInput();
            return "1";
        }
        else 
        {
            reject();
        }

        return "ERROR";
    }
}