package com.jinais.gnlib.android;

import android.util.Log;

public class LogM {
	
	private static String DEBUG_LOG_SIGNATURE = "DEBUG";
	private static String ERROR_LOG_SIGNATURE = "ERROR";
	private static String PATH_LOG_SIGNATURE = "PATH";
	private static String TEST_LOG_SIGNATURE = "TEST";
	
	public static String d(Object... objects) {
		if(Values.DEBUG) {
			String message = null; 
			StringBuilder stringBuilder = new StringBuilder();
			for(Object object : objects) {
				stringBuilder.append(stringify(object));
			}
			message = stringBuilder.toString();
			return logDebug(message);
		} else {
			return null;
		}
	}
	
	public static String e(Object... objects) {
		String message = null; 
		StringBuilder stringBuilder = new StringBuilder();
		for(Object object : objects) {
			stringBuilder.append(stringify(object));
		}
		message = stringBuilder.toString();
		return logError(message);
	}
	
	public static String e(Throwable tr, Object... objects) {
		String message = null; 
		StringBuilder stringBuilder = new StringBuilder();
		for(Object object : objects) {
			stringBuilder.append(stringify(object));
		}
		message = stringBuilder.toString();
		return logErrorWithThrowable(message, tr);
	}
	
	public static String p(String path) {
		if(Values.PATHS){
			return logPath(path);
		}
		return null;
	} 
	
	public static String t(boolean condition, String message) {
		if(!Values.TEST)
			return null;
		if(condition) {
			return logTest(condition, "TRUE! " + message);
		} else {
			return logTest(condition, "FALSE! " + message);
		}
	}
	
	public static String t(String message) {
		return logTestCase("CASE: " + message);
	}

	private static String logDebug(String message) {
		Log.d(DEBUG_LOG_SIGNATURE, message);
		return DEBUG_LOG_SIGNATURE + ": " + message;
	}
	private static String logError(String message) {
		Log.e(ERROR_LOG_SIGNATURE, message);
		return ERROR_LOG_SIGNATURE + ": " + message;
	}
	private static String logErrorWithThrowable(String message, Throwable tr) {
		Log.e(ERROR_LOG_SIGNATURE, message, tr);
		return ERROR_LOG_SIGNATURE + ": " + message + "\n Throwable:  " + tr.toString();
	}
	private static String logPath(String path) {
		Log.d(PATH_LOG_SIGNATURE, path);
		return  PATH_LOG_SIGNATURE + ": " + path;
	}
	private static String logTest(boolean condition, String message) {
		if(condition) {
			Log.d(TEST_LOG_SIGNATURE, message);
		} else {
			Log.e(TEST_LOG_SIGNATURE, message);
		}
		return TEST_LOG_SIGNATURE + ": " + message;
	}
	private static String logTestCase(String message) {
		Log.d(TEST_LOG_SIGNATURE, message);
		return TEST_LOG_SIGNATURE + ": " + message; 
	}
	
	private static String stringify(Object object) {
		String message = null;
		if(object == null) {
			message = "null";
		} else {
			message = object.toString();
		} 
		return message;
	}
}
