package library;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Log {

    private static final Logger LOGGER = Logger.getLogger(Log.class.getName());

    static {
        LOGGER.setLevel(Level.INFO);
    }

    public static Logger get() {
        return LOGGER;
    }
}