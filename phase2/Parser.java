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
}