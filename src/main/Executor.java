package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

public class Executor {
	
	HashMap<String, String> vars = new HashMap<>();
	HashMap<String, String[]> massives = new HashMap<>();
	int vars_in_func_count = 0;
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
				if(currword.charAt(currword.length()-1) == ']')
				{
					String massname = "";
					int num = 0;
					for(int j=0; j<currword.length(); j++)
					{
						if(currword.charAt(j) == '[')
						{
							massname = currword.substring(1, j);
							num = Integer.parseInt(currword
									.substring(j+1, currword.length()-1));
							break;
						}
					}
					line = line.replace(currword, massives.get(massname)[num]);
				}
				else {
					String varname = currword.replace("%", "").strip();
					line = line.replace(currword, vars.get(varname));
				}
			}
		}
		return line;
	}
	
	public void jump_to(String metka, List<String> lines) throws NumberFormatException, InterruptedException, IOException
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
	
	public boolean if_jto(String first, String oper, String second)
	{
		String[] words_f = first.split("\\s");
		String[] words_s = second.split("\\s");
		first = replace_vars(first, words_f);
		second = replace_vars(second, words_s);
		boolean a = false;
		switch(oper)
		{
			case "==":
				if(first.equals(second)) a = true;
				break;
			case "!=":
				if(!first.equals(second)) a = true;
				break;
			case ">":
				if(Integer.parseInt(first) > Integer.parseInt(second)) 
					a = true;
				break;
			case "<":
				if(Integer.parseInt(first) < Integer.parseInt(second)) 
					a = true;
				break;
			case ">=":
				if(Integer.parseInt(first) >= Integer.parseInt(second)) 
					a = true;
				break;
			case "<=":
				if(Integer.parseInt(first) <= Integer.parseInt(second)) 
					a = true;
				break;
			default:
				System.out.println(ANSI_RED + "Unknow operator: '" + oper + "'!" + ANSI_RESET);
				System.exit(-1);
				break;
		}
		return a;
	}
	
	public boolean is_massive(String mass_name)
	{
		if(mass_name.charAt(mass_name.length()-1) == ']') return true;
		else return false;
	}
	
	public int massive_get_num(String mass_name)
	{
		int num = 0;
		for(int i=0; i<mass_name.length(); i++)
		{
			if(mass_name.charAt(i) == '[')
			{
				num = Integer.parseInt(mass_name
						.substring(i+1, mass_name.length()-1));
				break;
			}
		}
		return num;
	}
	
	public void execute(String func, String line, 
			String[] words, List<String> lines) throws NumberFormatException, InterruptedException, IOException
	{
		switch(func)
		{
			case "println":
				System.out.println(replace_vars(line, words));
				break;
			case "var":
				String name = replace_vars(words[0], words);
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
				if(if_jto(words[0], words[1], words[2])) jump_to(words[3], lines);
				break;
			case "while":
				boolean a = true;
				while(a)
				{
					if(if_jto(words[0], words[1], words[2])) jump_to(words[3], lines);
					else a = false;
				}
				break;
			case "call":
			    for(int i=1; i<words.length; i++)
	    		{
	    			vars.put("arg"+vars_in_func_count, replace_vars(words[i], words));
	    			vars_in_func_count++;
	    		}
			    jump_to(words[0], lines);
			    for(int i=0; i<vars_in_func_count; i++) 
			    	vars.put("arg"+vars_in_func_count, "");
			    vars_in_func_count = 0;
				break;
			case "call-import":
				for(int i=2; i<words.length; i++)
	    		{
	    			vars.put("arg"+vars_in_func_count, words[i]);
	    			vars_in_func_count++;
	    		}
				Scanner sc = new Scanner(new File(words[0]));
		    	List<String> import_file = new ArrayList<String>();
		    	while (sc.hasNextLine()){import_file.add(sc.nextLine());}
			    jump_to(words[1], import_file);
			    for(int i=0; i<vars_in_func_count; i++) 
			    	vars.put("arg"+vars_in_func_count, "");
			    vars_in_func_count = 0;
				break;
			case "mass":
				String mass_name_mass = replace_vars(words[0], words);
				int num_mass = Integer.parseInt(replace_vars(words[1], words));
				String[] mass_mass = new String[num_mass];
				massives.put(mass_name_mass, mass_mass);
				break;
			case "mass.set":
				String mass_name_set = replace_vars(words[0], words);
				int num_set = Integer.parseInt(replace_vars(words[1], words));
				String nachto = replace_vars(
						line.replace(words[0], "")
						.replace(words[1], "").strip(), words);
				String[] mass_set = massives.get(mass_name_set);
				mass_set[num_set] = nachto;
				massives.put(mass_name_set, mass_set);
				break;
			case "mass.out":
				String[] massoutm = massives.get(replace_vars(words[0], words));
				String strokamass="";
				for(int j=0; j<massoutm.length; j++)
				{
					if(j==0) strokamass=massoutm[j];
					else strokamass+="\n"+massoutm[j];
				}
				System.out.println(strokamass);
				break;
			case "sleep":
				Thread.sleep(Integer.parseInt(replace_vars(words[0],words)));
				break;
			case "string.replace":
				String varrepl = replace_vars(words[0], words);
			    String chto = replace_vars(words[1], words);
			    String nachtor = replace_vars(words[2], words);
			    if(is_massive(varrepl)) {
			    	int nums = massive_get_num(varrepl);
			    	String massname = varrepl.substring(varrepl.length()-2,varrepl.length()-1);
			    	String[] massrep = massives.get(massname);
			    	massrep[nums] = massrep[nums].replace(chto, nachtor).strip();
				    massives.put(massname, massrep);
			    }
			    else vars.put(varrepl, vars.get(varrepl).replace(chto, nachtor).strip());
				break;
			case "file.readline":
				Scanner scc = new Scanner(new File(replace_vars(words[0], words).strip()));
		    	List<String> lines_of_fileread = new ArrayList<String>();
		    	while (scc.hasNextLine()){lines_of_fileread.add(scc.nextLine());}
		    	vars.put(words[2], lines_of_fileread.get(
		    			Integer.parseInt(replace_vars(words[1], words))));
				break;
			case "file.writeline":
				try(FileWriter writer = new FileWriter(replace_vars(words[0], words), false))
		        {
		           writer.write(replace_vars(line.replace(words[0], ""), words));
		           writer.flush();
		        }
		        catch(IOException ex){System.out.println(ex.getMessage());}
				break;
			case "file.readall":
				Scanner sccc = new Scanner(new File(replace_vars(words[0], words).strip()));
				String[] massreadall = massives.get(replace_vars(words[0], words));
				int zxzc=massreadall.length;
		    	while (sccc.hasNextLine())
		    	{
		    		if(zxzc<0) break;
		    		massreadall[zxzc]=sccc.nextLine();
		    		zxzc--;
		    	}
				break;
			case "file.count_lines":
				List<String> fcllist = new ArrayList<>();
				Scanner scfcl = new Scanner(new File(replace_vars(words[0], words).strip()));
		    	while (scfcl.hasNextLine()){fcllist.add(scfcl.nextLine());}
		    	vars.put(words[1], Integer.toString(fcllist.size()));
				break;
			case "return":
				System.exit(Integer.parseInt(words[0]));
				break;
		}
	}
	
}