package by.bsuir.ihar.view_layer.Implementation;

import by.bsuir.ihar.logic_layer.Implementation.BooksRecordKernel;
import by.bsuir.ihar.view_layer.Interface.IConsoleInterface;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ConsoleView implements IConsoleInterface {

    private  Map<String, Method> handlers;
    private  BooksRecordKernel kernel;

    public ConsoleView(BooksRecordKernel kernel){
        this.kernel = kernel;
        handlers = new HashMap<String, Method>();
    }

    @Override
    public void start()
            throws NoSuchMethodException,
            IllegalAccessException,
            IllegalArgumentException,
            InvocationTargetException
    {
        Scanner input = new Scanner(System.in);
        while(true){
            if (handlers.containsKey("invitation")){
                System.out.print(handlers.get("invitation").invoke(kernel, new Object[]{null}));
            }
            else
                System.out.print('>');

            String[] parts = input.nextLine().split(" ");
            if (parts.length > 0){
                if (handlers.containsKey(parts[0])){
                    System.out.print(handlers.get(parts[0]).invoke(kernel, new Object[]{parts}));
                }
            }
        }
    }

    @Override
    public void attachHandler(String command, Method handler){
        if (!handlers.containsKey(command)) {
            handlers.put(command, handler);
        }
    }

    @Override
    public void detachHandler(String command){
        if (handlers.containsKey(command)){
            handlers.remove(command);
        }
    }

}
