package utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {
	
	private ExceptionUtils(){}
	
	public static String getStackTrace(Exception e){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return pw.toString();
	}
}
