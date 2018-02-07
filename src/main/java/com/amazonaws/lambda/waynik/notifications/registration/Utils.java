package com.amazonaws.lambda.waynik.notifications.registration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class Utils {
    public static String getStackTrace(Throwable aThrowable) {
	    Writer result = new StringWriter();
	    PrintWriter printWriter = new PrintWriter(result);
	    aThrowable.printStackTrace(printWriter);
	    return result.toString();
    }
}
