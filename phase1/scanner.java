package phase1;

import java.nio.file.Files;
import java.nio.file.Path;

public class Scanner {
    public static void main(String[] args) {

        Path filePath = Path.of("");

        try {
            String fileContent = Files.readString(filePath);

            for(int i=0; i<fileContent.length(); i++)
            {
                
            }
            
            
        } catch (Exception e) {
        }

        
    }
}
