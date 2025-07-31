package org.example.eiscuno.model.saveGame;

import java.io.*;

/**
 * Implementation of {@link ISerializableFileHandler} for object serialization.
 * <p>
 * This class provides concrete implementations for serializing objects to files
 * and deserializing them back, using Java's native serialization mechanism.
 * </p>
 */
public class SerializableFileHandler implements ISerializableFileHandler {

    /**
     * Serializes an object to the specified file.
     * <p>
     * The object is converted to a byte stream and written to the file,
     * overwriting any existing content. The object must implement {@link Serializable}.
     * </p>
     *
     * @param filename the path to the file where the object will be stored
     * @param element the object to be serialized (must implement Serializable)
     * @throws RuntimeException if an I/O error occurs during serialization
     *                         or if the object is not serializable
     */
    @Override
    public void serialize(String filename, Object element) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(element);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Serialization failed for file: " + filename, e);
        }
    }

    /**
     * Deserializes an object from the specified file.
     * <p>
     * Reads the byte stream from the file and reconstructs the original object.
     * </p>
     *
     * @param filename the path to the file containing the serialized object
     * @return the deserialized object, or null if the operation fails
     * @throws RuntimeException if an I/O error occurs during deserialization
     *                         or if the class of the serialized object cannot be found
     */
    @Override
    public Object deserialize(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Deserialization failed for file: " + filename, e);
        }
    }
}