package org.example.eiscuno.model.saveGame;

import java.io.*;

/**
 * Implementation of {@link IPlainTextFileHandler} for basic text file operations.
 * <p>
 * This class provides concrete implementations for reading from and writing to plain text files,
 * handling the underlying I/O operations and basic error management.
 * </p>
 */
public class PlainTextFileHandler implements IPlainTextFileHandler {

    /**
     * Writes content to a specified text file.
     * <p>
     * The method will overwrite any existing content in the target file.
     * Uses UTF-8 encoding by default.
     * </p>
     *
     * @param filePath the absolute or relative path to the target file
     * @param content the text content to be written to the file
     * @throws RuntimeException if an I/O error occurs during writing
     */
    @Override
    public void writeToFile(String filePath, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error writing to file: " + filePath, e);
        }
    }

    /**
     * Reads content from a specified text file.
     * <p>
     * The file content is read line by line and returned as an array of strings,
     * with lines separated by commas. Empty lines are ignored.
     * </p>
     *
     * @param fileName the name of the file to read from
     * @return an array of strings containing the file content split by commas
     * @throws RuntimeException if an I/O error occurs during reading
     */
    @Override
    public String[] readFromFile(String fileName) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line.trim()).append(",");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error reading from file: " + fileName, e);
        }
        return content.toString().split(",");
    }
}