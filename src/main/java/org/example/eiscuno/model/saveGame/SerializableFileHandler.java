package org.example.eiscuno.model.saveGame;

import java.io.*;

public class SerializableFileHandler implements ISerializableFileHandler {
    @Override
    public void serialize(String filename, Object element) {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(element);
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    @Override
    public Object deserialize(String filename) {
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return ois.readObject();
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

}
