package phase1;

import java.nio.file.Files;
import java.nio.file.Path;

public class Scanner {
    public static void main(String[] args) {

        Path filePath = Path.of("");

        try {
            String fileContent = Files.readString(filePath);


            int state = 0; 
            for(int i=0; i<fileContent.length(); i++)
            {

                char c= fileContent.charAt(i);
                int columnIndex = getColumn(state, c);

                
            }
            
            
        } catch (Exception e) {
        }

        
    }

    public static int getColumn(int row, char c)
    {
        int index = -1;
        StateTable table = new StateTable();
        int [][] transitionTable = table.getTransitionTable();

        //checks a-zA-Z_, a-zA-z0-9_, 0, 1-9, 0-9
        if((c>='a' && c<='z') || (c>='A' && c<='Z') || (c>=0 && c<=9) || c=='_')
        {
            index = 2;

            //check if there is a state here
            if(transitionTable[row][index] == -1)
            {
                //check if alphabet
                if((c>='a' && c<='z') || (c>='A' && c<='Z') || c=='_' )
                {
                    index = 1;
                }

                //check if numeric: 0-9
                else if(c>=0 && c<=9)
                {
                    index = 5;

                    //extra check for numeric: can be 0 or 1-9 (or simply 0-9)
                    if(transitionTable[row][index] == -1)
                    {
                        if(c == 0)
                        {
                            index = 3;
                        }
                        else
                        {
                            index = 4;
                        }
                    }
                }
            }
        }
        else if(c=='.')
        {
            index = 6;
        }
        else if(c == '+')
        {
            index = 7;
        }
        else if(c=='-')
        {
            index = 8;
        }
        else if(c == '*')
        {
            index = 9;
        }
        else if(c == '/')
        {
            index = 10;
        }
        else if(c=='=')
        {
            index = 11;
        }
        else if(c=='<')
        {
            index = 12;
        }
        else if(c == '>')
        {
            index = 13;
        }
        else if(c =='!')
        {
            index = 14;
        }
        else if(c=='{')
        {
            index = 15;
        }
        else if(c == '}')
        {
            index = 16;
        }
        else if(c == '(')
        {
            index = 17;
        }
        else if(c == ')')
        {
            index = 18;
        }
        else if(c == ';')
        {
            index = 19;
        }

        
        return index;
        

    }
}