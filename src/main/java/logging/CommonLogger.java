package logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import elections.party.Party;

public class CommonLogger {
    private static final Logger LOGGER = Logger.getLogger(Party.class.getName());
	private static CommonLogger LOGGER_INSTANCE = new CommonLogger();
	private String LOG_FILE_NAME = "./logs.log";
	
	private CommonLogger(){
		try {
            FileHandler fileHandler = new FileHandler(LOG_FILE_NAME);
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
            LOGGER.addHandler(fileHandler);
            LOGGER.setUseParentHandlers(false);
   		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static CommonLogger getInstance(){
		return LOGGER_INSTANCE;
	}
	
	public void log(Level level,String className,String methodName, String message){
		if(level != null){
			LOGGER.logp(level, className, methodName, message);
		}
	}
}
