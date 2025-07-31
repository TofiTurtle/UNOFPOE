package org.example.eiscuno.model.saveGame;

/**
 * Interface for handling serialization and deserialization of game objects.
 * <p>
 * Provides methods to convert game objects to a persistent format (serialization)
 * and reconstruct them from that format (deserialization).
 * </p>
 */
public interface ISerializableFileHandler {

    /**
     * Serializes an object and saves it to a file.
     *
     * @param filename the path of the file where the object will be stored
     * @param element the game object to be serialized
     * @throws java.io.IOException if an I/O error occurs during serialization
     * @throws IllegalArgumentException if the specified element is not serializable
     */
    void serialize(String filename, Object element);

    /**
     * Deserializes an object from a file.
     *
     * @param filename the path of the file containing the serialized object
     * @return the deserialized game object
     * @throws java.io.IOException if an I/O error occurs during deserialization
     * @throws java.io.FileNotFoundException if the specified file doesn't exist
     * @throws ClassNotFoundException if the class of the serialized object cannot be found
     */
    Object deserialize(String filename);
}