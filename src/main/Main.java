package main;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.swing.*;

public class Main {
	
	public static void go(String file_name) throws NumberFormatException, InterruptedException, IOException
	{
		Brain brain = new Brain();
    	
    	Scanner sc = new Scanner(new File(file_name));
    	List<String> lines = new ArrayList<String>();
    	while (sc.hasNextLine())
    	{
    		lines.add(sc.nextLine());
    	}
    	
    	for(int i=0; i<lines.size(); i++)
    	{
    		brain.go(lines.get(i), lines, file_name);
    	}
	}
	
	public static boolean executeBashCommand(String command) {
	    boolean success = false;
	    Runtime r = Runtime.getRuntime();
	    String[] commands = {"bash", "-c", command};
	    try {
	        Process p = r.exec(commands);

	        p.waitFor();
	        BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        String line = "";

	        while ((line = b.readLine()) != null) {
	            System.out.println(line);
	        }

	        b.close();
	        success = true;
	        return success;
	    } catch (Exception e)
	    {
	        return false;
	    }
	    
	}
	
    public static void main(String[] args) throws IOException, NumberFormatException, InterruptedException, IOException {
    	String file_name;
    	try {
    		file_name = args[0];
    	} catch(Exception e)
    	{
    		Scanner in = new Scanner(System.in);
        	file_name = in.nextLine();
    	}
    	try {
    		String compile = args[1].strip().toLowerCase();
    		switch(compile)
    		{
    			case "compile_linux":
    				File filea = new File(file_name);
        			executeBashCommand("makeself.sh " + filea.getParent() + " " + filea.getParent() + "/main.run \"SFX installer for program\" java -jar mini-epl.jar main.epl");
        			System.exit(0);
        			break;
        		default:
        			go(file_name);
        			break;
    		}
    	} catch(Exception e)
    	{
    		Brain brain = new Brain();
        	
        	Scanner sc = new Scanner(new File(file_name));
        	List<String> lines = new ArrayList<String>();
        	while (sc.hasNextLine())
        	{
        		lines.add(sc.nextLine());
        	}
        	
        	for(int i=0; i<lines.size(); i++)
        	{
        		brain.go(lines.get(i), lines, file_name);
        	}
    	}
    	
    }
}
