package phase1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public class Scanner {
    public static void main(String[] args) {

        Path filePath = Path.of("DummyFile.txt");

        try {
            String fileContent = Files.readString(filePath);

            StateTable tables = new StateTable();
            int [][] transitionTable = tables.getTransitionTable();
            boolean [] acceptingTable = tables.getAcceptingTable();
            List<Function<String, String>> stateActionTable = tables.getStateActionTable();

            int state = 0; 
            String input = "";
            for(int i=0; i<fileContent.length(); i++)
            {

                char c= fileContent.charAt(i);
                int columnIndex = getColumn(state, c);
                
                
                

                if(transitionTable[state][columnIndex] != -1)
                {
                    state = transitionTable[state][columnIndex];
                    input += c;
                }
                else if(acceptingTable[state] == true)
                {
                        //output token
                    try{
                        Files.writeString(Path.of("Output.txt"), input);
                        System.out.println("Successfully written.");

                        input += c;

                            
                    }
                    catch(IOException e)
                    {
                        System.out.println("Couldn't write to file.");
                    }
                       
                        
                        
                }
                else
                {
                        //reject/crash
                        input = "";
                }

               
               


                

                
                
            }
            
            
        } catch (Exception e) {
            
        }

        
    }

    public static int getColumn(int row, char c)
    {
        int index = -1;
        StateTable table = new StateTable();
        int [][] transitionTable = table.getTransitionTable();

        //checks a-zA-Z_, a-zA-z0-9
        if((c>='a' && c<='z') || (c>='A' && c<='Z'))
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
}