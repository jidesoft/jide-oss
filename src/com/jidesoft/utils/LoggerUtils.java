package com.jidesoft.utils;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * LoggerUtils contains two methods to allow logger to print certain level of message to console. This is mainly used
 * for debugging purpose.
 */
public class LoggerUtils {
    public static Handler enableLogger(String loggerName, Level level) {
        Logger log = Logger.getLogger(loggerName);
        log.setLevel(level);
        Handler handler = new Handler() {
            @SuppressWarnings({"UseOfSystemOutOrSystemErr"})
            @Override
            public void publish(LogRecord record) {
                System.err.print(record.getMessage());
                Object[] params = record.getParameters();
                if (params != null) {
                    if (params.length > 0)
                        System.err.print("= ");
                    for (int i = 0; i < params.length; i++) {
                        System.err.print(params[i]);
                        if (i < params.length - 1)
                            System.err.print(", ");
                    }
                }
                System.err.println();
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        };
        log.addHandler(handler);
        return handler;
    }

    public static void disableLogger(String loggerName, Handler handler) {
        Logger log = Logger.getLogger(loggerName);
        log.setLevel(null);
        log.removeHandler(handler);
    }
}
