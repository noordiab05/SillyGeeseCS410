package phase3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CodeGenerator {
    static Path inputFile = Path.of("phase2", "Atoms.txt");
    static Path outputFile = Path.of("phase3", "Binary.txt");

    static List<String> lines;

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

            if (operation == "ADD") {
                add();
            }
            // TODO: add more operations
        }
        return;
    }

    // TODO: finish this
    static void add() {
        return;
    }
}
