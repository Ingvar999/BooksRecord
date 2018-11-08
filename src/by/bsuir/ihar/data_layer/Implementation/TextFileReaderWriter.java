package by.bsuir.ihar.data_layer.Implementation;

import by.bsuir.ihar.data_layer.Interface.IDataTransfer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс реализующий интерфейс обмена данными с хранилищем.
 * @author Игорь Шиманский
 * @version 1.0
 * @see by.bsuir.ihar.data_layer.Interface.IDataTransfer
 */
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
        write(sourceName, list);
    }

    @Override
    public <T> void write(String sourceName, List<T> items)
            throws IOException
    {
        FileOutputStream fos = new FileOutputStream(sourceName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(items);
        oos.flush();
        oos.close();
    }
}
