package phase2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/* 
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.function.Function;
*/

public class Parser {
    public static void main(String[] args) {
        Path inputFile = Path.of("phase1", "Output.txt");

        String fileContent;

        try {
            fileContent = Files.readString(inputFile);
        }
        catch(IOException e)
        {
            System.out.println("Couldn't read input file: " + e);
            return;
        }
    }

    // TODO: convert pseudocode to real code!!!
    // void Parse()
    // {
    //     <add return character to end of file>
    //     input = getInput()
    //     Program() // The initial starting non-terminal
        
    //     if(input == some character that terminates the program)
    //         accept();
    //     else
    //         reject();
        
    // }

    // void accept()
    // {
    // print("Accept");
    // }

    // void reject()
    // {
    // print("Reject");
    // exit();

    // }

    // void Program()
    // {

    // Statement()

    // if(inp = ';')
    // {
    //     inp = getInput()
    //     if(inp != 'N')
    //     {
    //         Program();
    //     }
    // }
    // }

    // Void Statement()
    // {
    // if(inp == int_type)
    // {
    //     inp = getInput()
    //     if(inp == var_id)
    //     {
    //         inp = getInput()
    //         if(inp == assign_op)
    //         {
    //             inp = getInput()
    //             Value()
    //         }
    //         else
    //         {
    //             reject()
    //         }
    //     }
    //     else 
    //     {
    //         reject()
    //     }
    // }
    // else if(inp == double_type)
    // {
    // inp = getInput()
    //     if(inp == var_id)
    //     {
    //         inp = getInput()
    //         if(inp == assign_op)
    //         {
    //             inp = getInput()
    //             Value()
    //         }
    //         else
    //         {
    //             reject()
    //         }
    //     }
    //     else 
    //     {
    //         reject()
    //     }

    // }
    // else if(inp == for_kwd)
    // {
    //     inp = getInput()
        
    //     if(inp == parenthesis_left)
    //     {
    //         inp = getInput()
    //         Assignment()
    //         if(inp == end_of_line)
    //         {
    //             inp = getInput()
    //             Comparison()
                    
    //             if(inp == end_of_line)
    //             {
    //                 inp = getInput()
    //                 Assignment()
                        
    //                 if(inp == parenthesis_right)
    //                 {
    //                     inp = getInput()
    //                     if(inp = curly_brace_left)
    //                     {
    //                         inp = getInput()
    //                         Program()
                                
    //                         if(inp = curly_brace_right)
    //                         {
    //                             inp = getInput()
    //                         }
    //                         else
    //                         {
    //                             reject()
    //                         }
    //                     }
    //                     else
    //                     {
    //                         reject()
    //                     }
    //                 }
    //                 else
    //                 {
    //                     reject()
    //                 }
    //             }
    //             else
    //             {
    //                 reject()
    //             }
    //         }
    //         else
    //         {
    //             reject()
    //         }
            
    //     else
    //     {
    //         reject()
    //     }
    // }
    // else if(inp == while_kwd)
    // {
    //     inp = getInput()
    //     if(inp == parenthesis_left)
    //     {
    //         inp = getInput()
    //         Comparison()
    //         if(inp == parenthesis_right)
    //         {
    //             inp = getInput()
    //             if(inp == curly_brace_left)
    //             {
    //                 inp = getInput()
    //                 Program()
                    
    //                 if(inp == curly_brace_right)
    //                 {
    //                     inp = getInput()
    //                 }
    //                 else
    //                 {
    //                     reject()
    //                 }
    //             }
    //             else
    //             {
    //                 reject()
    //             }
    //         }
    //         else
    //         {
    //             reject()
    //         }
    //     }
    //     else
    //     {
    //         reject()
    //     }
    // }
    // else if(inp == if_kwd)
    // {
    //     inp = getInput()
    //     if(inp == parenthesis_left)
    //     {	
    //         inp = getInput()
    //         Comparison()
    //         if(inp == parenthesis_right)
    //         {
    //             inp = getInput()
    //             if(inp == curly_brace_left)
    //             {
    //                 inp = getInput()
    //                 Program()
    //                 if(inp == curly_brace_right)
    //                 {
    //                     inp = getInput()
    //                 }
    //                 else
    //                 {
    //                     reject()
    //                 }
    //             }
    //             else
    //             {
    //                 reject()
    //             }
    //         }
    //         else
    //         {
    //             reject()
    //         }
    //     }
    //     else
    //     {
    //         reject()
    //     }
    // }
    // else if(inp == else_kwd)
    // {
    //     if(inp == curly_brace_left)
    //     {
    //         inp = getInput()
    //         Program()
    //         if(inp == curly_brace_right)
    //         {
    //             inp = getInput()
    //         }
    //         else
    //         {
    //             reject()
    //         }
    //     }
    //     else
    //     {
    //         reject()
    //     }
    // }
    // else
    // {
    //     reject()
    // }

    // }

    // Assignment()
    // {
    //     if(inp == int_type)
    //     {
    //         inp = getInput()
    //         if(inp == var_id)
    //         {
    //             inp = getInput()
    //             if(inp == assign_op)
    //             {
    //                 inp = getInput()
    //                 Value()
    //             }
    //             else
    //             {
    //                 reject()
    //             }
    //         }
    //         else
    //         {
    //             reject()
    //         }
    //     }
    //     else if(inp == double_type)
    //     {
    //         inp = getInput()
    //         if(inp == var_id)
    //         {
    //             inp = getInput()
    //             if(inp == assign_op)
    //             {
    //                 inp = getInput()
    //                 Value()
    //             }
    //             else
    //             {
    //                 reject()
    //             }
    //         }
    //         else
    //         {
    //             reject()
    //         }
    //     }
    //     else if(inp == var_id)
    //     {
    //         inp = getInput()
    //         if(inp == assign_op)
    //         {
    //             inp = getInput()
    //             Value()
    //         }
    //         else
    //         {
    //             reject()
    //         }
    //     }
    //     else
    //     {
    //         reject()
    //     }

    // }

    // Value()
    // {
    //     if(inp = int_literal)
    //     {
    //         inp = getInput()
    //     }
    //     else if(inp == double_literal)
    //     {
    //         inp = getInput()
    //     }
    //     else
    //     {
    //         Expression()
    //     }
    // }

    // Expression()
    // {
    // if(inp == var_id)
    // {
    //     if(inp == add_op)
    //     {
    //         inp = getInput()
    //         Term()
    //     }
    //     else if(inp == sub_op)
    //     {
    //         inp = getInput()
    //         Term()
    //     }
    //     else
    //     {
    //         reject()
    //     }
    // }

    // else
    // {
    // Term()
    // }
    // }

    // Term()
    // {
    //     if(inp == var_id)
    //     {
    //         inp = getInput();
    //         if(inp == mult_op)
    //         {
    //             inp = getInput()
    //             Term()
    //         }
    //         else if(inp == div_op)
    //         {
    //             inp = getInput()
    //             Term()
    //         }
    //         //otherwise, we just have a var_id
    //     }
    //     else if(inp == int_literal)
    //     {
    //         inp = getInput();
            
    //     }
    //     else if(inp == double_literal)
    //     {
    //         inp = getInput();
    //     }
    //     else
    //     {
    //         reject()
    //     }

    // }

    // Comparison()
    // {
    // if(inp = var_id)
    // {
    //     inp = getInput()
    //     ComparisonOp()
    //     if(inp == int_literal)
    //     {
    //         inp = getInput()
    //     }
    //     else if(inp == double_literal)
    //     {
    //         inp = getInput()
    //     }
    //     else
    //     {
    //         reject()
    //     }

    // }

    // }

    // ComparisonOp()
    // {
    // if(inp == <)
    // {
    //     inp = getInput()
    // }
    // else if(inp == <=)
    // {
    // inp = getInput()
    // }
    // else if( inp == >)
    // {
    // inp = getInput()
    // }
    // else if(inp == >=)
    // {
    // inp = getInput()
    // }
    // else if(inp == ==)
    // {
    // inp = getInput()
    // }
    // else if(inp == !=)
    // {
    //     inp = getInput()
    // }
    // else 
    // {
    //     reject()
    // }

    // }

}