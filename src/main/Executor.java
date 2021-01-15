package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Executor {
	
	HashMap<String, String> vars = new HashMap<>();
	Scanner in = new Scanner(System.in);
	
	public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
	
	public String replace_vars(String line, String[] words)
	{
		for(int i=0; i<words.length; i++)
		{
			String currword = words[i];
			if(currword.charAt(0) == '%')
			{
				String varname = currword.replace("%", "").strip();
				line = line.replace(currword, vars.get(varname));
			}
		}
		return line;
	}
	
	public void jump_to(String metka, List<String> lines)
	{
		metka = metka.strip();
		for(int i=0; i<lines.size(); i++)
		{
			if(lines.get(i).charAt(0) == ':')
			{
				String[] wordsinline = lines.get(i).split("\\s");
				String metka_name = wordsinline[0]
						.replace(":", "").strip();
				if(metka_name.equals(metka)) 
					Brain.go(lines.get(i).replace(wordsinline[0], "").strip(), lines);
			}
		}
	}
	
	public void execute(String func, String line, 
			String[] words, List<String> lines)
	{
		switch(func)
		{
			case "println":
				System.out.println(replace_vars(line, words));
				break;
			case "var":
				String name = words[0];
				vars.put(name, replace_vars(line.replace(name, "").replace(words[1], "").strip(), words));
				break;
			case "print":
				System.out.print(replace_vars(line, words));
				break;
			case "jump":
				jump_to(words[0], lines);
				break;
			case "plus":
				vars.put(words[0], Integer.toString(Integer
						.parseInt(vars.get(words[0]))+Integer.parseInt(replace_vars(words[1], words))));
				break;
			case "minus":
				vars.put(words[0], Integer.toString(Integer
						.parseInt(vars.get(words[0]))-Integer.parseInt(replace_vars(words[1], words))));
				break;
			case "multiply":
				vars.put(words[0], Integer.toString(Integer
						.parseInt(vars.get(words[0]))*Integer.parseInt(replace_vars(words[1], words))));
				break;
			case "split":
				vars.put(words[0], Integer.toString(Integer
						.parseInt(vars.get(words[0]))/Integer.parseInt(replace_vars(words[1], words))));
				break;
			case "input":
				vars.put(in.nextLine().strip(), words[0]);
				break;
			case "if":
				String first = replace_vars(words[0].strip(), words);
				String oper = words[1];
				String second = replace_vars(words[2].strip(), words);
				String metka = words[3];
				switch(oper)
				{
					case "==":
						if(first.equals(second)) jump_to(metka, lines);
						break;
					case "!=":
						if(!first.equals(second)) jump_to(metka, lines);
						break;
					case ">":
						if(Integer.parseInt(first) > Integer.parseInt(second)) 
							jump_to(metka, lines);
						break;
					case "<":
						if(Integer.parseInt(first) < Integer.parseInt(second)) 
							jump_to(metka, lines);
						break;
					case ">=":
						if(Integer.parseInt(first) >= Integer.parseInt(second)) 
							jump_to(metka, lines);
						break;
					case "<=":
						if(Integer.parseInt(first) <= Integer.parseInt(second)) 
							jump_to(metka, lines);
						break;
					default:
						System.out.println(ANSI_RED + "Unknow operator: '" + oper + "'!" + ANSI_RESET);
						System.exit(-1);
						break;
				}
				break;
			case "return":
				System.exit(Integer.parseInt(words[0]));
				break;
		}
	}
	
}
