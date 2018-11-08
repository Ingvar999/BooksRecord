package by.bsuir.ihar.data_layer.Interface;

import java.io.IOException;
import java.util.List;

/**
 * Интерфейс обмена данными с хранилищем.
 * @author Игорь Шиманский
 * @version 1.0
 */
public interface IDataTransfer {
    /**
     * Получение всех объектов содержащихся в ресурсе
     * @param sourceName  - имя ресурса
     * @return - список хранящихся объектов
     */
    <T> List<T> getAll(String sourceName)
            throws IOException,
            ClassNotFoundException;
    /**
     * Добавление нового объекта в ресурс
     * @param sourceName  - имя ресурса
     * @param item - добавляемый объект
     */
    <T> void append(String sourceName, T item)
            throws IOException,
            ClassNotFoundException;
    /**
     * Запись списка объектов в ресурс
     * @param sourceName  - имя ресурса
     * @param items - записываемые объекты
     */
    <T> void write(String sourceName, List<T> items)
            throws IOException,
            ClassNotFoundException;
}
