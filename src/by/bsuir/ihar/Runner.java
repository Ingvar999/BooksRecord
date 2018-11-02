package by.bsuir.ihar;


import by.bsuir.ihar.data_layer.Implementation.TextFileReaderWriter;
import by.bsuir.ihar.logic_layer.Implementation.BooksRecordKernel;
import by.bsuir.ihar.view_layer.Implementation.ConsoleView;

import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;

public class Runner {

    public static void main(String[] args)
            throws NoSuchMethodException,
            IllegalAccessException,
            IllegalArgumentException,
            InvocationTargetException,
            NoSuchAlgorithmException
    {
        BooksRecordKernel app = new BooksRecordKernel();
        app.start(new ConsoleView(app), new TextFileReaderWriter());
    }
}
