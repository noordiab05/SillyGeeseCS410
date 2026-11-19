package phase3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.List;



public class CodeGenerator {
    static Path inputFile = Path.of("phase2", "Atoms.txt");
    static Path outputHumanLegibleFile = Path.of("phase3", "HumanLegibleBinary.txt");
    static Path outputBinaryFile = Path.of("phase3", "Binary.bin");

    static List<String> lines;

    static int pc = 0b000;
    static LinkedHashMap<Integer, String> codeSegment = new LinkedHashMap<>();
    
    //this will store:
    //Ex:          'a'     0b000  5
    static LinkedHashMap<String, Number[]> dataSegment = new LinkedHashMap<>();

    static LinkedHashMap<String, String> labels = new LinkedHashMap<>();

    //start data segment at address 400
    static final int DATA_SEGMENT_START = 0b110010000;
    static int dataSegmentpc = DATA_SEGMENT_START;

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
            // overwrite output files to make them empty
            Files.writeString(outputHumanLegibleFile, "");
            Files.writeString(outputBinaryFile, "");
        }
        catch(IOException e)
        {
            System.out.println("Couldn't write to output file.");
            System.exit(0);
        }

        Generate();

    }

    static void Generate() {

        String first_instruction = "00000004";
        codeSegment.put(0b000, first_instruction);
        pc = pc+4;

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
                mov(place1, place2);
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
                add(place1, place2, place3);
            }
            else if(operation.equals("SUB"))
            {
                place1 = pieces[1];
                place2 = pieces[2];
                place3 = pieces[3];
                sub(place1, place2, place3);
            }
            else if(operation.equals("MUL"))
            {
                place1 = pieces[1];
                place2 = pieces[2];
                place3 = pieces[3];
                mul(place1, place2, place3);
            }
            else if(operation.equals("DIV"))
            {
                place1 = pieces[1];
                place2 = pieces[2];
                place3 = pieces[3];
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

        // replace "jmp-redo" / "tst-redo" instructions with correct instruction
        for (var instruction_addr : codeSegment.keySet()) {
            if (codeSegment.get(instruction_addr).startsWith("jmp-redo")) {
                String dest = codeSegment.get(instruction_addr).substring(8);
                String label = labels.get(dest);
                System.out.println("i_addr: " + instruction_addr + " -- jmp label: " + label);

                // bal addr
                String instruction = "6" + "000" + String.format("%4s", label).replace(' ','0');
                codeSegment.put(instruction_addr, instruction);
            }

            if (codeSegment.get(instruction_addr).startsWith("tst-redo")) {
                
                String almost_instruction = codeSegment.get(instruction_addr).substring(8);
                String dest = almost_instruction.substring(4);
                String label = labels.get(dest);
                System.out.println("i_addr: " + instruction_addr + " -- tst label: " + label);
                // branch addr
                String instruction = almost_instruction.substring(0, 4) + String.format("%4s", label).replace(' ','0');
                codeSegment.put(instruction_addr, instruction);
            }
        }

        //start writing to output file
        //put the halt instruction
        String haltInstruction = "f0000000";
        codeSegment.put(pc, haltInstruction );
        pc = pc+4;

        //TreeMap<Integer, String> sortedCodeSegment = new TreeMap<>(codeSegment);
        codeSegment.forEach((address, instruction)->{
            System.out.println("The address " + address + " contains the instruction: " + instruction);
        });
        //add the code segment in the file
        codeSegment.forEach((address, instruction)->{
            try {
                int intInstruction = Integer.parseUnsignedInt(instruction, 16);
                String binaryInstruction = String.format("%32s", Integer.toBinaryString(intInstruction)).replace(' ', '0');
                String binaryAddress = String.format("%32s", Integer.toBinaryString(address)).replace(' ', '0');
                String outputToWrite = "" + binaryAddress + ": " + binaryInstruction + "\n";

                Files.writeString(outputHumanLegibleFile, outputToWrite, StandardOpenOption.APPEND);
                Files.write(outputBinaryFile, bitStringToBytes(binaryInstruction), StandardOpenOption.APPEND);
            } catch (Exception e) {
                System.out.println("Couldn't write to output files.");
                System.exit(0);
            }
        });

        //add 0s to fill the remaining space until the data segment starts
        while(pc < DATA_SEGMENT_START)
        {
            try {
                String binaryAddress = String.format("%32s", Integer.toBinaryString(pc)).replace(' ', '0');
                String binaryInstruction = "00000000000000000000000000000000";
                String outputToWrite = "" + binaryAddress + ": " + binaryInstruction + "\n";
               
                pc = pc+4;
                
                Files.writeString(outputHumanLegibleFile, outputToWrite, StandardOpenOption.APPEND);
                Files.write(outputBinaryFile, bitStringToBytes(binaryInstruction), StandardOpenOption.APPEND);
            } catch (Exception e) {
                System.out.println("Couldn't write to output files.");
                System.exit(0);
            }
        }

        //then add the datasegment

        dataSegment.forEach((variable, element_arr)->{
            try {
                int data_value;
                if(element_arr[1] instanceof Integer)
                {
                    data_value = element_arr[1].intValue();
                }
                else
                {
                     data_value = Float.floatToIntBits(element_arr[1].floatValue());
                }
                String binaryData = String.format("%32s", Integer.toBinaryString(data_value)).replace(' ', '0');
                String binaryAddress = String.format("%32s", Integer.toBinaryString(element_arr[0].intValue())).replace(' ', '0');
                                            //address               //data        //the variable it represents
                String outputToWrite = "" + binaryAddress+ ": " + binaryData + "....." + variable + "\n";

                Files.writeString(outputHumanLegibleFile, outputToWrite, StandardOpenOption.APPEND);
                Files.write(outputBinaryFile, bitStringToBytes(binaryData), StandardOpenOption.APPEND);
            } catch (Exception e) {
                System.out.println("Couldn't write to output files.");
                System.exit(0);
            }
        });
        
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
            String instruction = "d" + "2" + "00" + String.format("%4s", leftAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else
        {
            String instruction = "d" + "a" + "00"+ String.format("%4s", leftAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }

    
        //lw rt rightAddress
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //lw ireg3 rightAddress
            String instruction = "d" + "3" + "00" + String.format("%4s", rightAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else
        {
            String instruction = "d" + "b" + "00"+ String.format("%4s", rightAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;        
        }


        //add rt rx ry
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //add ireg6 ireg2 ireg3
            String instruction = "2"+"6"+"2"+"3"+"0000";
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else
        {
            //add fpreg6 fpreg2 frepg3
            String instruction = "2" + "e" + "a" + "b" + "0000";
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }

        //MIGHT NEED TO DO IN SW --> CORRESPONDS TO MOV
        //sw rt address
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //sw ireg6 resultAddress
            String instruction = "e" + "6" + "00" + String.format("%4s", resultAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else
        {
            String instruction = "e" + "e" + "00" + String.format("%4s", resultAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
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
            String instruction = "d" + "2" + "00" + String.format("%4s", leftAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else
        {
           //lw frep2 leftAddress
            String instruction = "d" + "a" + "00"+ String.format("%4s", leftAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }

         //lw rt rightAddress
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //lw ireg3 rightAddress
            String instruction = "d" + "3" + "00" + String.format("%4s", rightAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        else
        {
             //lw frep3 rightAddress
            String instruction = "d" + "b" + "00"+ String.format("%4s", rightAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }

        //sub rt rx ry
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //sub ireg6 ireg2 ireg3
            String instruction = "3"+"6"+"2"+"3"+"0000";
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        
        else
        {
            //sub fpreg6 fpreg2 frepg3
            String instruction = "3" + "e" + "a" + "b" + "0000";
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }


        //MIGHT NEED TO DO IN SW --> CORRESPONDS TO MOV
        //sw rt address
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //sw ireg6 resultAddress
            String instruction = "e" + "6" + "00" + String.format("%4s", resultAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        
        else
        {
           
            //sw frep6 resultAddress
            String instruction = "e" + "e" + "00" + String.format("%4s", resultAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
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
            String instruction = "d" + "2" + "00" + String.format("%4s", leftAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        // else if ((leftElements[1] instanceof Double) && (rightElements[1] instanceof Double)) {
            
        //     //lw frep2 leftAddress
        //     String instruction = "d" + "a" + "00"+ leftAddress;
        //     codeSegment.put(pc, instruction);
        //     pc = pc+4;
        // }
        else
        {
            
            //lw frep2 leftAddress
            String instruction = "d" + "a" + "00"+ String.format("%4s", leftAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }

        //lw rt rightAddress
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //lw ireg3 rightAddress
            String instruction = "d" + "3" + "00" + String.format("%4s", rightAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
       
        else
        {
            //lw frep3 rightAddress
            String instruction = "d" + "b" + "00"+ String.format("%4s", rightAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }

        //mul rt rx ry
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //mul ireg6 ireg2 ireg3
            String instruction = "4"+"6"+"2"+"3"+"0000";
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        
        else
        {
            //mul fpreg6 fpreg2 frepg3
            String instruction = "4" + "e" + "a" + "b" + "0000";
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }

        //MIGHT NEED TO DO IN SW --> CORRESPONDS TO MOV
        //sw rt address --> transfer register to address
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //sw ireg6 resultAddress
            String instruction = "e" + "6" + "00" + String.format("%4s", resultAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        
        else
        {
            //sw frep6 resultAddress
            String instruction = "e" + "e" + "00" + String.format("%4s", resultAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
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
            String instruction = "d" + "2" + "00" + String.format("%4s", leftAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        
        else
        {
           //lw frep2 leftAddress
            String instruction = "d" + "a" + "00"+ String.format("%4s", leftAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }

    
        //lw rt rightAddress
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //lw ireg3 rightAddress
            String instruction = "d" + "3" + "00" + String.format("%4s", rightAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        
        else
        {
            //lw frep3 rightAddress
            String instruction = "d" + "b" + "00"+ String.format("%4s", rightAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }



        //div rt rx ry
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //div ireg6 ireg2 ireg3
            String instruction = "5"+"6"+"2"+"3"+"0000";
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        
        else
        {
            //div fpreg6 fpreg2 frepg3
            String instruction = "5" + "e" + "a" + "b" + "0000";
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }

        //MIGHT NEED TO DO IN SW --> CORRESPONDS TO MOV
        //sw rt address
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //sw ireg6 resultAddress
            String instruction = "e" + "6" + "00" + String.format("%4s", resultAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        
        else
        {
            //sw frep6 resultAddress
            String instruction = "e" + "e" + "00" +String.format("%4s", resultAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
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

            Number intAddress;

            //basic initialization
            Number [] intElements = new Number[2];
            intElements[0] = 0;
            intElements[1] = 0;
            if(dataSegment.get(left) == null)
            {
                //add the int to the data segment
                intAddress = dataSegmentpc;
                dataSegmentpc = dataSegmentpc + 4;

                intElements[0] = intAddress;
                intElements[1] = Integer.parseInt(left);
                dataSegment.put(left, intElements);

            }
            else
            {
                // the int is already in the data segment
                intElements[0] = dataSegment.get(left)[0];
                intAddress = intElements[0];
                intElements[1] = dataSegment.get(left)[1];
            }

            //get result info:
            Number resultElements[] = dataSegment.get(result);
            Number resultAddress = resultElements[0];
            resultElements[1] = intElements[1];

            //update result data segment
            dataSegment.put(result, resultElements);

            String hexIntAddress = Integer.toHexString(intAddress.intValue());
            String hexResultAddress = Integer.toHexString(resultAddress.intValue());

            
            //lw ireg2 leftAddress
            String instruction1 = "d" + "2" +"00" + String.format("%4s", hexIntAddress).replace(' ','0');
            codeSegment.put(pc, instruction1);
            pc = pc+4;

            //sto ireg2 resultAddress
            String instruction2 = "e" + "2" + "00" +String.format("%4s", hexResultAddress).replace(' ','0');
            codeSegment.put(pc, instruction2);
            pc = pc+4;

            //remove the int from the data segment + update dataSegmentPC
            // dataSegment.remove(left);
            // dataSegmentpc = dataSegmentpc-4;
            
            // System.out.println("Left element is: " + left);
            

        }
        else if(leftType.equals("Double"))
        {

            Number doubleAddress;

            //basic initialization
            Number [] doubleElements = new Number[2];
            doubleElements[0] = 0;
            doubleElements[1] = 0;
            if(dataSegment.get(left) == null)
            {
                //add the double to the a data segment
                doubleAddress = dataSegmentpc;
                dataSegmentpc = dataSegmentpc + 4;

                doubleElements[0] = doubleAddress;
                doubleElements[1] = Double.parseDouble(left);
                dataSegment.put(left, doubleElements);

            }
            else
            {
                // the double is already in the data segment
                doubleElements[0] = dataSegment.get(left)[0];
                doubleAddress = doubleElements[0];
                doubleElements[1] = dataSegment.get(left)[1];
            }

            //get result info:
            Number resultElements[] = dataSegment.get(result);
            Number resultAddress = resultElements[0];
            resultElements[1] = doubleElements[1];

            //update result data segment
            dataSegment.put(result, resultElements);

            String hexDoubleAddress = Integer.toHexString(doubleAddress.intValue());
            String hexResultAddress = Integer.toHexString(resultAddress.intValue());

            //lw frepg2 doubleAddress
            String instruction1 = "d" + "a" +"00" +String.format("%4s", hexDoubleAddress).replace(' ','0');
            codeSegment.put(pc, instruction1);
            pc = pc+4;

            //sw frepg2 resultAddress
            String instruction2 = "e" + "a" + "00" + String.format("%4s", hexResultAddress).replace(' ','0');
            codeSegment.put(pc, instruction2);
            pc = pc+4;

            // //remove the double from the data segment + update dataSegmentPC
            // System.out.println("Left element is: " + left);
            // dataSegment.remove(left);
            // dataSegmentpc = dataSegmentpc-4;

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
            
            String hexLeftAddress = Integer.toHexString(leftAddress.intValue());
            String hexResultAddress = Integer.toHexString(resultAddress.intValue());
            
            if(leftValue instanceof Integer)
            {
                //lw ireg2 leftAddress
                String instruction1 = "d" + "2" +"00" + String.format("%4s", hexLeftAddress).replace(' ','0');
                codeSegment.put(pc, instruction1);
                pc = pc+4;

                //sto ireg2 resultAddress
                String instruction2 = "e" + "2" + "00" + String.format("%4s", hexResultAddress).replace(' ','0');
                codeSegment.put(pc, instruction2);
                pc = pc+4;
            }
            else
            {
                //lw frepg2 leftAddress
                String instruction1 = "d" + "a" +"00" + String.format("%4s", hexLeftAddress).replace(' ','0');
                codeSegment.put(pc, instruction1);
                pc = pc+4;

                //sw frepg2 resultAddress
                String instruction2 = "e" + "a" + "00" + String.format("%4s", hexResultAddress).replace(' ','0');
                codeSegment.put(pc, instruction2);
                pc = pc+4;

            }
            
        }
        
    }

    static void lbl(String dest)
    {
        // save this instruction's address for later
        labels.put(dest, Integer.toHexString(pc));
        System.out.println("lbl: " + dest + " -- pc: " + pc + " 0x" + (Integer.toHexString(pc)));

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

        System.out.println(leftAddress + " " + rightAddress);

        if(leftAddress==null || rightAddress == null)
        {
            System.out.println("Issue with the addresses being null.\n");
        }

        Number [] leftElements = dataSegment.get(left);
        Number [] rightElements = dataSegment.get(right);

        int comp_int = Integer.parseInt(comp);

        // opcodes for branch ops are 6 more than their equivalent atom representation
        comp_int += 6;
        String comp_hex = Integer.toHexString(comp_int);

        //lw rx leftAddress
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //lw ireg2 leftAddress
            String instruction = "d" + "2" + "00" + String.format("%4s", leftAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        
        else
        {
             //lw fpreg2 leftAddress
            String instruction = "d" + "a" + "00"+ String.format("%4s", leftAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }

        //lw ry rightAddress
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            //lw ireg3 rightAddress
            String instruction = "d" + "3" + "00" + String.format("%4s", rightAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        
        else
        {
            //lw fpreg3 rightAddress
            String instruction = "d" + "b" + "00"+ String.format("%4s", rightAddress).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }

        String label = labels.get(dest);

        // branch rx ry addr
        if((leftElements[1] instanceof Integer) && (rightElements[1] instanceof Integer))
        {
            // System.out.println(comp_hex);
            // System.out.println(label);
            // the addr will need to be updated later
            if (label == null) {
                String instruction = "tst-redo" + comp_hex + "0" + "2" + "3" + dest;
                codeSegment.put(pc, instruction);
                pc = pc+4;
                return;
            }

            // branch ireg2 ireg3 label
            String instruction = comp_hex +"0"+"2"+"3"+String.format("%4s", label).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }
        
        else
        {
            if (label == null) {
                String instruction = "tst-redo" + comp_hex + "0" + "a" + "b" + dest;
                codeSegment.put(pc, instruction);
                pc = pc+4;
                return;
            }

            // branch fpreg2 frepg3 label
            String instruction = comp_hex + "0" + "a" + "b" + String.format("%4s", label).replace(' ','0');
            codeSegment.put(pc, instruction);
            pc = pc+4;
        }

        return;
    }

    static void jmp(String dest)
    {
        String label = labels.get(dest);

        // the addr will need to be updated later
        if (label == null) {
            String instruction = "jmp-redo" + String.format("%4s", dest).replace(' ','0'); //CHECK
            codeSegment.put(pc, instruction);
            pc = pc+4;
            return;
        }

        // bal addr
        
        String instruction = "6" + "000" + String.format("%4s", label).replace(' ','0');
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
                value = 0;
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

    static byte[] bitStringToBytes(String bitString) {
        byte[] bytes = new byte[bitString.length() / 8];
        for (int i = 0; i < bytes.length; i++) {
            String byteChunk = bitString.substring(i * 8, (i + 1) * 8);
            // Parse the 8-bit chunk as an unsigned int (base 2) and cast to byte
            bytes[i] = (byte) Integer.parseInt(byteChunk, 2);
        }
        return bytes;
    }
    
}
