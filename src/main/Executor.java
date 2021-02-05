package main;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.Window;
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
    
    WindowWork ww = new WindowWork();
	
	public String replace_vars(String line)
	{
		String[] words = line.strip().split("\\s");
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
	
	public static void jump_to(String metka, List<String> lines, 
			String file_name) throws NumberFormatException, InterruptedException, IOException
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
					Brain.go(lines.get(i).replace(wordsinline[0], "").strip(), lines, file_name);
			}
		}
	}
	
	public boolean if_jto(String first, String oper, String second)
	{
		first = replace_vars(first);
		second = replace_vars(second);
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
	
	public void set_mass(String massname, String[] mass)
	{
		massives.remove(massives.get(massname));
		massives.put(massname, mass);
	}
	
	public void execute(String func, String line, 
			String[] words, List<String> lines, 
			String file_name) throws NumberFormatException, InterruptedException, IOException
	{
	    File file_name_file = new File(file_name);
		switch(func)
		{
			case "println":
				System.out.println(replace_vars(line));
				break;
			case "var":
				String name = replace_vars(words[0]);
				vars.put(name, replace_vars(line.replace(name, "").replace(words[1], "").strip()));
				break;
			case "print":
				System.out.print(replace_vars(line));
				break;
			case "plus":
				vars.put(words[0], Integer.toString(Integer
						.parseInt(vars.get(words[0]))+Integer.parseInt(replace_vars(words[1]))));
				break;
			case "minus":
				vars.put(words[0], Integer.toString(Integer
						.parseInt(vars.get(words[0]))-Integer.parseInt(replace_vars(words[1]))));
				break;
			case "multiply":
				vars.put(words[0], Integer.toString(Integer
						.parseInt(vars.get(words[0]))*Integer.parseInt(replace_vars(words[1]))));
				break;
			case "split":
				vars.put(words[0], Integer.toString(Integer
						.parseInt(vars.get(words[0]))/Integer.parseInt(replace_vars(words[1]))));
				break;
			case "input":
				vars.put(in.nextLine().strip(), words[0]);
				break;
			case "if":
				if(if_jto(words[0], words[1], words[2])) jump_to(words[3], lines, file_name);
				break;
			case "while":
				boolean a = true;
				while(a)
				{
					if(if_jto(words[0], words[1], words[2])) jump_to(words[3], lines, file_name);
					else a = false;
				}
				break;
			case "call":
				try {
					for(int i=1; i<words.length; i++)
		    		{
		    			vars.put("arg"+vars_in_func_count, replace_vars(words[i]));
		    			vars_in_func_count++;
		    		}
				} catch(Exception e){e.printStackTrace();}
			    jump_to(words[0], lines, file_name);
			    for(int i=0; i<vars_in_func_count; i++) 
			    	vars.put("arg"+vars_in_func_count, "");
			    vars_in_func_count = 0;
				break;
			case "call-imp":
			case "call-import":
				try {
					for(int i=2; i<words.length; i++)
		    		{
		    			vars.put("arg"+vars_in_func_count, replace_vars(words[i]));
		    			vars_in_func_count++;
		    		}
				} catch(Exception e){e.printStackTrace();}
				Scanner sc = new Scanner(new File(file_name_file.getParent()+"/"+words[0]+".epl"));
		    	List<String> import_file = new ArrayList<String>();
		    	while (sc.hasNextLine()){import_file.add(sc.nextLine());}
			    jump_to(words[1], import_file, file_name_file.getParent()+"/"+words[0]+".epl");
			    for(int i=0; i<vars_in_func_count; i++) 
			    	vars.put("arg"+vars_in_func_count, "");
			    vars_in_func_count = 0;
				break;
			case "mass":
				String mass_name_mass = replace_vars(words[0]);
				int num_mass = Integer.parseInt(replace_vars(words[1]));
				String[] mass_mass = new String[num_mass];
				massives.put(mass_name_mass, mass_mass);
				break;
			case "mass.set":
				String mass_name_set = replace_vars(words[0]);
				int num_set = Integer.parseInt(replace_vars(words[1]));
				String nachto = replace_vars(
						line.replace(words[0], "")
						.replace(words[1], "").strip());
				String[] mass_set = massives.get(mass_name_set);
				mass_set[num_set] = nachto;
				massives.put(mass_name_set, mass_set);
				break;
			case "mass.out":
				String[] massoutm = massives.get(replace_vars(words[0]));
				String strokamass="";
				for(int j=0; j<massoutm.length; j++)
				{
					if(j==0) strokamass=massoutm[j];
					else strokamass+="\n"+massoutm[j];
				}
				System.out.println(strokamass);
				break;
			case "sleep":
				Thread.sleep(Integer.parseInt(replace_vars(words[0])));
				break;
			case "win.init":
				String nwi = replace_vars(line.replace(words[0], "").replace(words[1], "").trim());
				int wwi = Integer.parseInt(replace_vars(words[0]));
				int hwi = Integer.parseInt(replace_vars(words[1]));
				ww.winit(nwi, wwi, hwi);
				break;
			case "win.label.new":
				ww.add_label(
						replace_vars(words[0]), 
						replace_vars(line.replace(words[0], "").replace(words[1], "").replace(words[2], "").strip()),
						Integer.parseInt(replace_vars(words[1])),
						Integer.parseInt(replace_vars(words[2])));
				break;
			case "win.label.set.text":
				ww.set_text_label(replace_vars(words[0]), replace_vars(line.replace(words[0], "")));
				break;
			case "win.visible":
				ww.wvisible(Boolean.parseBoolean(replace_vars(words[0])));
				break;
			case "win.bg.set.color":
				ww.wbackground_color(
						Integer.parseInt(replace_vars(words[0])), 
						Integer.parseInt(replace_vars(words[1])), 
						Integer.parseInt(replace_vars(words[2])));
				break;
			case "win.bg.set.image":
				ww.wbackground_image(file_name_file.getParent() + "/" + replace_vars(words[0]));
				break;
			case "win.label.set.pos":
				ww.set_pos_label(replace_vars(words[0]), Integer.parseInt(replace_vars(words[1]))
						, Integer.parseInt(replace_vars(words[2])));
				break;
			case "win.label.get.text":
				vars.put(replace_vars(words[1]), ww.get_text_label(replace_vars(words[0]).strip()));
				break;
			case "win.button.new":
				ww.add_button(
						replace_vars(words[0]), 
						replace_vars(line.replace(words[0], "").replace(words[1], "").replace(words[2], "").strip()),
						Integer.parseInt(replace_vars(words[1])),
						Integer.parseInt(replace_vars(words[2])), lines, file_name);
				break;
			case "win.button.set.text":
				ww.set_text_button(replace_vars(words[0]), replace_vars(line.replace(words[0], "")));
				break;
			case "win.button.set.pos":
				ww.set_pos_button(replace_vars(words[0]), Integer.parseInt(replace_vars(words[1]))
						, Integer.parseInt(replace_vars(words[2])));
				break;
			case "win.button.set.size":
				ww.set_size_button(replace_vars(words[0]), Integer.parseInt(replace_vars(words[1]))
						, Integer.parseInt(replace_vars(words[2])));
				break;
			case "win.go":
				ww.go();
				break;
			case "str.replace":
				String varrepl = replace_vars(words[0]);
			    String chto = replace_vars(words[1]);
			    String nachtor = replace_vars(words[2]);
			    if(is_massive(varrepl)) {
			    	int nums = massive_get_num(varrepl);
			    	String massname = varrepl.substring(varrepl.length()-2,varrepl.length()-1);
			    	String[] massrep = massives.get(massname);
			    	massrep[nums] = massrep[nums].replace(chto, nachtor).strip();
				    set_mass(massname, massrep);
			    }
			    else vars.put(varrepl, vars.get(varrepl).replace(chto, nachtor).strip());
				break;
			case "return":
				System.exit(Integer.parseInt(replace_vars(words[0])));
				break;
		}
	}
	
}