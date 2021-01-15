package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
	
    public static void main(String[] args) throws IOException {
    	String file_name;
    	Scanner in = new Scanner(System.in);
    	file_name = in.nextLine();
    	Brain brain = new Brain();
    	
    	Scanner sc = new Scanner(new File(file_name));
    	List<String> lines = new ArrayList<String>();
    	while (sc.hasNextLine())
    	{
    		lines.add(sc.nextLine());
    	}
    	
    	for(int i=0; i<lines.size(); i++)
    	{
    		brain.go(lines.get(i), lines);
    	}
    	
    }
}
