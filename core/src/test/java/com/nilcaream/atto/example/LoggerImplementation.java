package com.nilcaream.atto.example;

import com.nilcaream.atto.Logger;

import javax.inject.Singleton;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class LoggerImplementation implements Logger {

    private static StringBuilder logs = new StringBuilder();
    private static AtomicInteger count = new AtomicInteger(0);

    {
        count.incrementAndGet();
    }

    @Override
    public void accept(Level level, String s) {
        if (logs.length() > 2048) {
            throw new IllegalStateException("logger is full");
        } else {
            String message = level + " " + s;
            System.out.println(message);
            logs.append(message).append("\n");
        }
    }

    public static String getLogs() {
        return logs.toString();
    }

    public static int getCount() {
        return count.get();
    }
}
