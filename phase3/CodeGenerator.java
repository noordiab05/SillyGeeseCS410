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
            String[] pieces = line.substring(1, line.length() - 1).split(",");
            String operation = pieces[0];

            String place1 = "";
            String place2 = "";
            String place3 = "";
            String place4 = "";

            if(operation.equals("MOV"))
            {
                place1 = pieces[1];
                place2 = pieces[3];
                
                //skip MOV after an ADD/SUB/MULT/DIV
                if(arithmetic_operation_identifier == 1)
                {
                    //clear for the future
                    arithmetic_operation_identifier = 0;
                    continue;
                }
                else
                {
                    mov(place1, place2);
                }
            }
            else if(operation.equals("LBL"))
            {
                place1 = pieces[5];
                lbl(place1);
            }
            else if(operation.equals("JMP"))
            {
                place1 = pieces[5];
                jmp(place1);
            }
            else if (operation.equals("ADD"))
            {
                place1 = pieces[1];
                place2 = pieces[2];
                place3 = pieces[3];
                arithmetic_operation_identifier = 1;
                add(place1, place2, place3);
            }
            else if(operation.equals("SUB"))
            {
                place1 = pieces[1];
                place2 = pieces[2];
                place3 = pieces[3];
                arithmetic_operation_identifier = 1;
                sub(place1, place2, place3);
            }
            else if(operation.equals("MUL"))
            {
                place1 = pieces[1];
                place2 = pieces[2];
                place3 = pieces[3];
                arithmetic_operation_identifier = 1;
                mul(place1, place2, place3);
            }
            else if(operation.equals("DIV"))
            {
                place1 = pieces[1];
                place2 = pieces[2];
                place3 = pieces[3];
                arithmetic_operation_identifier = 1;
                div(place1, place2, place3);
            }
            else if(operation.equals("TST"))
            {
                place1 = pieces[1];
                place2 = pieces[2];
                place3 = pieces[4];
                place4 = pieces[5];
                tst(place1, place2, place3, place4);
            }
            
        }

        // replace "jmp-redo" instructions with correct instruction
        for (var instruction_addr : codeSegment.keySet()) {
            if (codeSegment.get(instruction_addr).startsWith("jmp-redo")) {
                String dest = codeSegment.get(instruction_addr).substring(8);
                int label = labels.get(dest);
                // bal addr
                String instruction = "6" + "000" + label;
                codeSegment.put(pc, instruction);
            }

            if (codeSegment.get(instruction_addr).startsWith("tst-redo")) {
                String almost_instruction = codeSegment.get(instruction_addr).substring(8);
                String dest = almost_instruction.substring(4);
                int label = labels.get(dest);
                // branch addr
                String instruction = almost_instruction.substring(0, 4) + label;
                codeSegment.put(pc, instruction);
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
            String instruction = "d" + "2" + "00" + leftAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            
            //lw frep2 leftAddress
            String instruction = "d" + "a" + "00"+ leftAddress;
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
            String instruction = "d" + "3" + "00" + rightAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            
            //lw frep3 rightAddress
            String instruction = "d" + "b" + "00"+ rightAddress;
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
            String instruction = "d" + "2" + "00" + leftAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            
            //lw frep2 leftAddress
            String instruction = "d" + "a" + "00"+ leftAddress;
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
            String instruction = "d" + "3" + "00" + rightAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            
            //lw frep3 rightAddress
            String instruction = "d" + "b" + "00"+ rightAddress;
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
            String instruction = "d" + "2" + "00" + leftAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            
            //lw frep2 leftAddress
            String instruction = "d" + "a" + "00"+ leftAddress;
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
            String instruction = "d" + "3" + "00" + rightAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            
            //lw frep3 rightAddress
            String instruction = "d" + "b" + "00"+ rightAddress;
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
       
        return;
    }

    static void mov(String left, String result)
    {
        //I can do this (noor) :)
        //recall:
        //data segments: <String key, Numbers[2]>
        //Numbers[0] = address
        //Numbers [1] = value

        //check if result is already in the data
        //if not, add it to the data segment, and initialize it to 0 for now
        //then update it as we sw 
        if(!dataSegment.containsKey(result))
            {
                Number newResultAddress = dataSegmentpc;
                dataSegmentpc = dataSegmentpc +4;

                Number[] newResultElements = new Number[2];
                newResultElements[0]= newResultAddress;

                //initialize it to 0
                newResultElements[1] = 0;

                //place the data in the data segment
                dataSegment.put(result, newResultElements);
        }

        //move value in left to the result
        String leftType = getType(left);

        if(leftType.equals("Int"))
        {
            //IDEA:
                //store the int as a datasement
                //ex: left = 5
                //DataSegment: <5, 0b000 5>
                //then, fetch the number at that address
                //do lw then sw like usual
                //then delete that data segment (don't need it anymore)


            //add the int to the data segment temporarily 
            Number intAddress = dataSegmentpc;
            dataSegmentpc = dataSegmentpc + 4;

            Number [] intElements = new Number[2];
            intElements[0] = intAddress;
            intElements[1] = Integer.parseInt(left);
            dataSegment.put(left, intElements);

            //get result info:
            Number resultElements[] = dataSegment.get(result);
            Number resultAddress = resultElements[0];
            resultElements[1] = intElements[1];

            //update result data segment
            dataSegment.put(result, resultElements);

            //lw ireg2 leftAddress
            String instruction1 = "d" + "2" +"00" + intAddress;
            codeSegment.put(pc, instruction1);
            pc = pc+4;

            //sto ireg2 resultAddress
            String instruction2 = "e" + "2" + "00" + resultAddress;
            codeSegment.put(pc, instruction2);
            pc = pc+4;


            //remove the int from the data segment + update dataSegmentPC
            dataSegment.remove(left);
            dataSegmentpc = dataSegmentpc-4;

        }
        else if(leftType.equals("Double"))
        {
            //add the double as a temporary data segment
            Number doubleAddress = dataSegmentpc;
            dataSegmentpc = dataSegmentpc +4;

            Number [] doubleElements = new Number[2];
            doubleElements[0] = doubleAddress;
            doubleElements[1] = Double.parseDouble(left);
            dataSegment.put(left, doubleElements);

             //get result info:
            Number resultElements[] = dataSegment.get(result);
            Number resultAddress = resultElements[0];
            resultElements[1] = doubleElements[1];

            //update result data segment
            dataSegment.put(result, resultElements);

            //lw frepg2 doubleAddress
            String instruction1 = "d" + "a" +"00" + doubleAddress;
            codeSegment.put(pc, instruction1);
            pc = pc+4;

            //sw frepg2 resultAddress
            String instruction2 = "e" + "a" + "00" + resultAddress;
            codeSegment.put(pc, instruction2);
            pc = pc+4;
           

            //remove the double from the data segment + update dataSegmentPC
            dataSegment.remove(left);
            dataSegmentpc = dataSegmentpc-4;

        }
        else 
        {
            //this means left is a variable

            //get left info
            Number leftElements[] = dataSegment.get(left);
            Number leftAddress = leftElements[0];
            Number leftValue = leftElements[1];


            //check if result is in data segment
            //if not, put it in
            
            //get the result info
            Number resultElements[] = dataSegment.get(result);
            Number resultAddress = resultElements[0];
            resultElements[1] = leftValue;
            dataSegment.put(result, resultElements); //update the value
            
            
            if(leftValue instanceof Integer)
            {
                //lw ireg2 leftAddress
                String instruction1 = "d" + "2" +"00" + leftAddress;
                codeSegment.put(pc, instruction1);
                pc = pc+4;

                //sto ireg2 resultAddress
                String instruction2 = "e" + "2" + "00" + resultAddress;
                codeSegment.put(pc, instruction2);
                pc = pc+4;
            }
            else
            {
                //lw frepg2 leftAddress
                String instruction1 = "d" + "a" +"00" + leftAddress;
                codeSegment.put(pc, instruction1);
                pc = pc+4;

                //sw frepg2 resultAddress
                String instruction2 = "e" + "a" + "00" + resultAddress;
                codeSegment.put(pc, instruction2);
                pc = pc+4;

            }
            
        }
        //make sure to move the value to the datasegment too!!
        //(MOV a, b) --> lw, sw
        //check if a is a variable or number
        //if a is a variable, get its value from the data segment
        //if a is a number, then make sure to add a new data segment, with b being the key
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

    static void tst(String left, String right, String comp, String dest)
    {
        //get the address 
        String leftAddress = getAddress(left);
        String rightAddress = getAddress(right);

        if(leftAddress==null || rightAddress == null)
        {
            System.out.println("Issue with the addresses being null.\n");
        }

        Number [] leftElements = dataSegment.get(left);
        Number [] rightElements = dataSegment.get(right);

        int comp_int = Integer.parseInt(comp);
        comp_int += 7;
        String comp_hex = Integer.toHexString(comp_int);

        //lw rt leftAddress
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //lw ireg2 leftAddress
            String instruction = "d" + "2" + "00" + leftAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            
            //lw frep2 leftAddress
            String instruction = "d" + "a" + "00"+ leftAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else
        {
            System.out.println("Failed to lw rt leftAddress [TST].\n");
        }

        //lw rt rightAddress
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //lw ireg3 rightAddress
            String instruction = "d" + "3" + "00" + rightAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            
            //lw frep3 rightAddress
            String instruction = "d" + "b" + "00"+ rightAddress;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else
        {
            System.out.println("Failed to lw rt rightAddress [TST].\n");
        }

        int label = labels.getOrDefault(dest, -1);


         //add rt rx ry
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            // the addr will need to be updated later
            if (label == -1) {
                String instruction = "tst-redo" + comp_hex + "0" + "2" + "3" + dest;
                codeSegment.put(pc, instruction);
                pc = pc+4;
                return;
            }

            //add ireg6 ireg2 ireg3
            String instruction = comp_hex +"0"+"2"+"3"+label;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            // the addr will need to be updated later
            if (label == -1) {
                String instruction = "tst-redo" + comp_hex + "0" + "a" + "b" + dest;
                codeSegment.put(pc, instruction);
                pc = pc+4;
                return;
            }

            //add fpreg6 fpreg2 frepg3
            String instruction = comp_hex + "0" + "a" + "b" + label;
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else
        {
            System.out.println("Failed to branch rx ry ADDR [TST].\n");
        }

        return;
    }

    static void jmp(String dest)
    {
        int label = labels.getOrDefault(dest, -1);

        // the addr will need to be updated later
        if (label == -1) {
            String instruction = "jmp-redo" + dest;
            codeSegment.put(pc, instruction);
            pc = pc+4;
            return;
        }

        // bal addr
        String instruction = "6" + "000" + label;
        codeSegment.put(pc, instruction);
        pc = pc+4;
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
