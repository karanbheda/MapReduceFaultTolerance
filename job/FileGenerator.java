import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class FileGenerator {
    public static void main(String[] args) {
        String fileName = "integers.txt";
        long fileSize = 4L * 1024L * 1024L * 1024L; // 4GB - change first integer to create x Gb file
        long currentSize = 0L;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            Random rand = new Random();
            while (currentSize < fileSize) {
                int randomNumber = rand.nextInt(101);
                String line = String.format("%d%n", randomNumber);
                writer.write(line);
                currentSize += line.getBytes().length;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}