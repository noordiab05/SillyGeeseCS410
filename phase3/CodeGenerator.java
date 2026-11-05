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
    static HashMap<Integer, Integer> codeSegment = new HashMap<>();
    static HashMap<String, Integer> dataSegment = new HashMap<>();



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
                mov();
            }
            else if(operation.equals("LBL"))
            {
                lbl();
            }
            else if(operation.equals("JMP"))
            {
                jmp();
            }
            else if (operation.equals("ADD"))
            {
                add(left, right, result);
            }
            else if(operation.equals("SUB"))
            {
                sub();
            }
            else if(operation.equals("MUL"))
            {
                mul();
            }
            else if(operation.equals("DIV"))
            {
                div();
            }
            else if(operation.equals("TST"))
            {
                tst();
            }
            // TODO: add more operations
        }
        return;
    }

    // TODO: finish this
    static void add(String left, String right, String result) {
        //ireg2 = 2
        //freg2 = 10
        String leftType = getType(left);
        String rightType = getType(right);

        String rx;
        String ry;
        String rt;

        //idk
        if(leftType.equals("Int"))
        {
            int leftInt= Integer.parseInt(left);
            rx = Integer.toBinary(leftInt);
        }
        else if(leftType.equals("Double"))
        {
            float leftFloat = Float.parseFloat(left);
            int leftInt = Float.floatToIntBits(leftFloat);
            rx = Float.toBinary(leftInt);
        }
        
        

        return;
    }
    static void sub()
    {
        return;
    }
    static void mul()
    {
        return;
    }
    static void div()
    {
        return;
    }
    static void mov()
    {
        return;
    }
    static void lbl()
    {
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
            Float.parseFloat(num);
            return "Float";
        } catch (NumberFormatException e) {
        }

        return "Variable";
    }
}
