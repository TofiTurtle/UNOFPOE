package org.example.eiscuno.model.saveGame;

/**
 * Interface for handling plain text file operations.
 * <p>
 * Defines the contract for reading from and writing to plain text files,
 * which is used for game data persistence.
 * </p>
 */
public interface IPlainTextFileHandler {

    /**
     * Writes content to a specified file.
     *
     * @param filePath the path of the file to write to
     * @param content the text content to be written to the file
     * @throws java.io.IOException if an I/O error occurs during writing
     */
    void writeToFile(String filePath, String content);

    /**
     * Reads content from a specified file.
     *
     * @param fileName the name of the file to read from
     * @return an array of strings representing the file content,
     *         with each element typically being a line from the file
     * @throws java.io.IOException if an I/O error occurs during reading
     * @throws java.io.FileNotFoundException if the specified file doesn't exist
     */
    String[] readFromFile(String fileName);
}