package by.bsuir.ihar.data_layer.Interface;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface IDataTransfer {
    <T> List<T> getAll(String sourceName)
            throws IOException,
            ClassNotFoundException;
    <T> void append(String sourceName, T item)
            throws IOException,
            ClassNotFoundException;
}
