package com.jidesoft.utils;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * LoggerUtils contains two methods to allow logger to print certain level of message to console.
 * This is mainly used for debugging purpose.
 */
public class LoggerUtils {
    public static Handler enableLogger(String loggerName, Level level) {
        Logger log = Logger.getLogger(loggerName);
        log.setLevel(level);
        Handler handler = new Handler() {
            public void publish(LogRecord record) {
                System.err.println(record.getMessage());
            }

            public void flush() {
            }

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
