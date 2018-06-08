package library;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

public final class Log {

    private static final Logger LOGGER = Logger.getLogger(Log.class.getName());

    static {
        StreamHandler handler = new StreamHandler(System.out, new SimpleFormatter());
        handler.setLevel(Level.ALL);
        LOGGER.addHandler(handler);
        LOGGER.setLevel(Level.ALL);
    }

    public static Logger get() {
        return LOGGER;
    }
}