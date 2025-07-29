package org.example.eiscuno.model.saveGame;

public interface IPlainTextFileHandler {
    void writeToFile(String filePath, String content);
    String[] readFromFile(String fileName);

}
