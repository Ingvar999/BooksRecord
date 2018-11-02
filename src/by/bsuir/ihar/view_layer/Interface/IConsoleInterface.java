package by.bsuir.ihar.view_layer.Interface;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface IConsoleInterface {
    void attachHandler(String command, Method handler);
    void detachHandler(String command);
    void start()
            throws NoSuchMethodException,
            IllegalAccessException,
            IllegalArgumentException,
            InvocationTargetException;
}
