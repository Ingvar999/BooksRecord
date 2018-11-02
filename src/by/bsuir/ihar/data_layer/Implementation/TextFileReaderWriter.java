package by.bsuir.ihar.data_layer.Implementation;

import by.bsuir.ihar.data_layer.Interface.IDataTransfer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TextFileReaderWriter implements IDataTransfer {
    @Override
    public <T> List<T> getAll(String sourceName)
        throws IOException,
            ClassNotFoundException
    {
        File file = new File(sourceName);
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(sourceName);
            ObjectInputStream oin = new ObjectInputStream(fis);
            return (List<T>) oin.readObject();
        }
        return new ArrayList<T>();
    }

    @Override
    public <T> void append(String sourceName, T item)
            throws IOException,
            ClassNotFoundException
    {
        List<T> list = getAll(sourceName);
        list.add(item);
        FileOutputStream fos = new FileOutputStream(sourceName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(list);
        oos.flush();
        oos.close();
    }
}
