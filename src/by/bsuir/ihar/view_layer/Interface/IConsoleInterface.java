package by.bsuir.ihar.view_layer.Interface;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Консольный интерфейс взаимодействия с пользователем.
 * @author Игорь Шиманский
 * @version 1.0
 */
public interface IConsoleInterface {
    /**
     * Добавление обработчика для консольной команды
     * @param command  - новая команда
     * @param handler - метод-обработчик
     */
    void attachHandler(String command, Method handler);
    /**
     * Удаление обработчика для консольной команды
     * @param command  - новая команда
     */
    void detachHandler(String command);
    /**
     * Начало ввода и обработки комманд
     */
    void start()
            throws NoSuchMethodException,
            IllegalAccessException,
            IllegalArgumentException,
            InvocationTargetException;
}
