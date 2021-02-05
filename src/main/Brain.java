package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Brain {
	
	static Executor executor = new Executor();
	
	public String get_type(String word)
	{
		String type;
		switch(word.charAt(0))
		{
			case '"':
				type = "STRING";
				break;
			case '\'':
				type = "CHAR";
				break;
			case '%':
				type = "VAR";
				break;
			default:
				type = "INTEGER";
				break;
		}
		return type;
	}
	
	private static boolean isDigit(String s) {
	    try {
	        Integer.parseInt(s);
	        return true;
	    } catch (NumberFormatException e) {
	        return false;
	    }
	}
	
	public static void go(String line, List<String> lines, String file_name) throws NumberFormatException, InterruptedException, IOException
	{	
		String[] words = line.split("\\s");
		String func = words[0].toLowerCase();
		line = line.replace(words[0], "").trim();
		words = line.split("\\s");
		
		executor.execute(func, line, words, lines, file_name);
	}
	
}
