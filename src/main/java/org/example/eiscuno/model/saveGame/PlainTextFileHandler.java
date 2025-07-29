package org.example.eiscuno.model.saveGame;

import java.io.*;

public class PlainTextFileHandler implements IPlainTextFileHandler {
    @Override
    public void writeToFile(String filePath, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String[] readFromFile(String fileName) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null ) {
                content.append(line.trim()).append(",");
            }

        }catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString().split(",");
    }
}