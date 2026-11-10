package phase3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;


public class CodeGenerator {
    static Path inputFile = Path.of("phase2", "Atoms.txt");
    static Path outputFile = Path.of("phase3", "Binary.txt");

    static List<String> lines;

    static int pc = 0b000;
    static HashMap<Integer, String> codeSegment = new HashMap<>();

    //this will store:
    //Ex:          'a'     0b000  5
    static HashMap<String, Number[]> dataSegment = new HashMap<>();

    static HashMap<String, Integer> labels = new HashMap<>();

    static int dataSegmentpc = 0b100;

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

        Generate();

    }

    static void Generate() {

        //turns 1 if add/sub/mult/div occurs
        //cleared at MOV
        int arithmetic_operation_identifier = 0;
        for (var line : lines) {
            String[] pieces = line.substring(1, line.length()).split(",");
            String operation = pieces[0];
            String left = pieces[1];
            String right = pieces[2];
            String result = pieces[3];
            String comp = pieces[4];
            String dest = pieces[5];

            if(operation.equals("MOV"))
            {
                //skip MOV after an ADD/SUB/MULT/DIV
                if(arithmetic_operation_identifier == 1)
                {
                    //clear for the future
                    arithmetic_operation_identifier = 0;
                    continue;
                }
                else
                {
                    mov();
                }
            }
            else if(operation.equals("LBL"))
            {
                lbl(dest);
            }
            else if(operation.equals("JMP"))
            {
                jmp();
            }
            else if (operation.equals("ADD"))
            {
                arithmetic_operation_identifier = 1;
                add(left, right, result);
            }
            else if(operation.equals("SUB"))
            {
                arithmetic_operation_identifier = 1;
                sub(left, right, result);
            }
            else if(operation.equals("MUL"))
            {
                arithmetic_operation_identifier = 1;
                mul(left, right, result);
            }
            else if(operation.equals("DIV"))
            {
                arithmetic_operation_identifier = 1;
                div(left, right, result);
            }
            else if(operation.equals("TST"))
            {
                tst();
            }
            
        }
        return;
    }

   
    static void add(String left, String right, String result) {
        
        //get the address 
        String leftAddress = getAddress(left);
        String rightAddress = getAddress(right);
        String resultAddress = getAddress(result);

        if(leftAddress==null || rightAddress == null || resultAddress == null)
        {
            System.out.println("Issue with the addresses being null.\n");
        }


        Number [] leftElements = dataSegment.get(left);
        Number [] rightElements = dataSegment.get(right);

        //lw rt leftAddress
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //lw ireg2 leftAddress
            String instruction = "lw" + "2" + "00" + leftAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            
            //lw frep2 leftAddress
            String instruction = "lw" + "a" + "00"+ leftAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else
        {
            System.out.println("Failed to lw rt leftAddress [ADD].\n");
        }

    
        //lw rt rightAddress
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //lw ireg3 rightAddress
            String instruction = "lw" + "3" + "00" + rightAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            
            //lw frep3 rightAddress
            String instruction = "lw" + "b" + "00"+ rightAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else
        {
            System.out.println("Failed to lw rt rightAddress [ADD].\n");
        }



        //add rt rx ry
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //add ireg6 ireg2 ireg3
            String instruction = "2"+"6"+"2"+"3"+"0000";
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            
            //add fpreg6 fpreg2 frepg3
            String instruction = "2" + "e" + "a" + "b" + "0000";
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else
        {
            System.out.println("Failed to add rt rx ry [ADD].\n");
        }

        //MIGHT NEED TO DO IN SW --> CORRESPONDS TO MOV
        //sw rt address
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //sw ireg6 resultAddress
            String instruction = "e" + "6" + "00" + resultAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            
            //sw frep6 resultAddress
            String instruction = "e" + "e" + "00" + resultAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else
        {
            System.out.println("Failed to sw rt address [ADD].\n");
        }

        
    }
    static void sub(String left, String right, String result)
    {
         //get the address 
        String leftAddress = getAddress(left);
        String rightAddress = getAddress(right);
        String resultAddress = getAddress(result);

        if(leftAddress==null || rightAddress == null || resultAddress == null)
        {
            System.out.println("Issue with the addresses being null.\n");
        }

        Number [] leftElements = dataSegment.get(left);
        Number [] rightElements = dataSegment.get(right);

        //lw rt leftAddress
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //lw ireg2 leftAddress
            String instruction = "lw" + "2" + "00" + leftAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            
            //lw frep2 leftAddress
            String instruction = "lw" + "a" + "00"+ leftAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else
        {
            System.out.println("Failed to lw rt leftAddress [SUB].\n");
        }

         //lw rt rightAddress
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //lw ireg3 rightAddress
            String instruction = "lw" + "3" + "00" + rightAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            
            //lw frep3 rightAddress
            String instruction = "lw" + "b" + "00"+ rightAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else
        {
            System.out.println("Failed to lw rt rightAddress [SUB].\n");
        }

        //sub rt rx ry
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //sub ireg6 ireg2 ireg3
            String instruction = "3"+"6"+"2"+"3"+"0000";
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            
            //sub fpreg6 fpreg2 frepg3
            String instruction = "3" + "e" + "a" + "b" + "0000";
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else
        {
            System.out.println("Failed to sub rt rx ry [SUB].\n");
        }


        //MIGHT NEED TO DO IN SW --> CORRESPONDS TO MOV
        //sw rt address
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //sw ireg6 resultAddress
            String instruction = "e" + "6" + "00" + resultAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            
            //sw frep6 resultAddress
            String instruction = "e" + "e" + "00" + resultAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else
        {
            System.out.println("Failed to sw rt address [SUB].\n");
        }


    }

    static void mul(String left, String right, String result)
    {
        //get the address 
        String leftAddress = getAddress(left);
        String rightAddress = getAddress(right);
        String resultAddress = getAddress(result);

        if(leftAddress==null || rightAddress == null || resultAddress == null)
        {
            System.out.println("Issue with the addresses being null.\n");
        }

        Number [] leftElements = dataSegment.get(left);
        Number [] rightElements = dataSegment.get(right);

        //lw rt leftAddress
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //lw ireg2 leftAddress
            String instruction = "lw" + "2" + "00" + leftAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            
            //lw frep2 leftAddress
            String instruction = "lw" + "a" + "00"+ leftAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else
        {
            System.out.println("Failed to lw rt leftAddress [MUL].\n");
        }

        //lw rt rightAddress
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //lw ireg3 rightAddress
            String instruction = "lw" + "3" + "00" + rightAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            
            //lw frep3 rightAddress
            String instruction = "lw" + "b" + "00"+ rightAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else
        {
            System.out.println("Failed to lw rt rightAddress [MUL].\n");
        }

        //mul rt rx ry
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //mul ireg6 ireg2 ireg3
            String instruction = "4"+"6"+"2"+"3"+"0000";
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            
            //mul fpreg6 fpreg2 frepg3
            String instruction = "4" + "e" + "a" + "b" + "0000";
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else
        {
            System.out.println("Failed to sub rt rx ry [MUL].\n");
        }

        //MIGHT NEED TO DO IN SW --> CORRESPONDS TO MOV
        //sw rt address --> transfer register to address
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //sw ireg6 resultAddress
            String instruction = "e" + "6" + "00" + resultAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            
            //sw frep6 resultAddress
            String instruction = "e" + "e" + "00" + resultAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else
        {
            System.out.println("Failed to sw rt address [MUL].\n");
        }



        return;
    }
    static void div(String left, String right, String result)
    {
       //get the address 
        String leftAddress = getAddress(left);
        String rightAddress = getAddress(right);
        String resultAddress = getAddress(result);

        if(leftAddress==null || rightAddress == null || resultAddress == null)
        {
            System.out.println("Issue with the addresses being null.\n");
        }


        Number [] leftElements = dataSegment.get(left);
        Number [] rightElements = dataSegment.get(right);

        //lw rt leftAddress
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //lw ireg2 leftAddress
            String instruction = "lw" + "2" + "00" + leftAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            
            //lw frep2 leftAddress
            String instruction = "lw" + "a" + "00"+ leftAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else
        {
            System.out.println("Failed to lw rt leftAddress [ADD].\n");
        }

    
        //lw rt rightAddress
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //lw ireg3 rightAddress
            String instruction = "lw" + "3" + "00" + rightAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            
            //lw frep3 rightAddress
            String instruction = "lw" + "b" + "00"+ rightAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else
        {
            System.out.println("Failed to lw rt rightAddress [ADD].\n");
        }



        //add rt rx ry
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //div ireg6 ireg2 ireg3
            String instruction = "5"+"6"+"2"+"3"+"0000";
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            
            //div fpreg6 fpreg2 frepg3
            String instruction = "5" + "e" + "a" + "b" + "0000";
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else
        {
            System.out.println("Failed to add rt rx ry [ADD].\n");
        }

        //MIGHT NEED TO DO IN SW --> CORRESPONDS TO MOV
        //sw rt address
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //sw ireg6 resultAddress
            String instruction = "e" + "6" + "00" + resultAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            
            //sw frep6 resultAddress
            String instruction = "e" + "e" + "00" + resultAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else
        {
            System.out.println("Failed to sw rt address [ADD].\n");
        }
        return;
    }

    static void mov()
    {
        //make sure to move the value to the datasegment too!!
        return;
    }

    static void lbl(String dest)
    {
        // save this instruction's address for later
        labels.put(dest, pc);

        // nop (all 0's)
        String instruction = "00000000";
        codeSegment.put(pc, instruction);
        pc = pc+4;
        return;
    }

    static void tst()
    {
        return;
    }
    static void jmp()
    {
        return;
    }

    
    static String getType(String num)
    {
        try{
            Integer.parseInt(num);
            return "Int";
        }
        catch(NumberFormatException e)
        {
        }

        try{
            Double.parseDouble(num);
            return "Double";
        } catch (NumberFormatException e) {
        }

        return "Variable";
    }

    static Number getValue(String num)
    {
        //get the type of the value
        String type = getType(num);
    
        if(type.equals("Int"))
        {
            //returns the int
            int numInt= Integer.parseInt(num);
            return numInt;
        }
        else if(type.equals("Double"))
        {
            //returns the double 
            double numDouble = Double.parseDouble(num);
            return numDouble;
        }
        else if(type.equals("Variable"))
        {
            //gets the number stored in the data segment
            //should already be stored in the data segment: value loaded into a variable in MOV
            Number [] elements = dataSegment.get(num);
            
            if(elements == null)
            {
                return -1;
            }

            Number value = elements[1];
            
            return value;
        }
        else
        {
            return -1;
        }

        
    }

    static String getAddress(String num)
    {
        if(dataSegment.containsKey(num))
        {
            Number [] elements = dataSegment.get(num);

            //returns the address
            return Integer.toHexString(elements[0].intValue());
        }
        else
        {
            //must add the element to the dataSegment
            Number value = getValue(num);
            if(value.intValue() == -1)
            {
                return null;
            }
            Number address = dataSegmentpc;
            dataSegmentpc = dataSegmentpc+4;

            Number[] elements = new Number[2];
            elements[0]= address;
            elements[1] = value;

            dataSegment.put(num, elements);

            return Integer.toHexString(address.intValue());
        }
    }

    
}
