package main;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.program.Program;

public class WindowWork {
	
	Display display = new Display();
    Shell f = new Shell(display);
	HashMap<String, Label> labels = new HashMap<>();
	HashMap<String, Button> buttons = new HashMap<>();
	
	public WindowWork() {
		f.setVisible(false);
	}
	
    public void winit(String name, int w, int h)
    {
    	f.setLayout(new GridLayout());
    	f.setText(name);
    	f.setSize(w, h);
    	f.setVisible(true);
    }    
    public void wvisible(boolean visible){f.setVisible(visible);}
    public void wbackground_color(int r, int g, int b){f.setBackground(new Color(display, r,g,b));}
    public void wbackground_image(String image){f.setBackgroundImage(new Image(display, image));}
    
    // Label
    public void add_label(String name, String text, int x, int y)
    {
    	Label label = new Label(f, SWT.NONE);
    	label.setText(text);
    	label.setLocation(new Point(x, y));
    	label.pack();
    	labels.put(name, label);
    }
    
    public void set_text_label(String name, String text)
    {
    	Label label = labels.get(name);
    	label.setText(text);
    	label.pack();
    	labels.put(name, label);
    }
    
    public void set_pos_label(String name, int x, int y)
    {
    	Label label = labels.get(name);
    	label.setLocation(x, y);
    	label.pack();
    	labels.put(name, label);
    }
    
    public String get_text_label(String name){return labels.get(name).getText();}
    
    // Buttons
    
    public void add_button(String name, String text, int x, int y, List<String> lines, String fn)
    {   	
    	Button button = new Button(f, SWT.PUSH);
    	button.setText(text);
    	button.setLocation(new Point(x, y));
    	button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Executor.jump_to(name, lines, fn);
				} catch (NumberFormatException | InterruptedException | IOException e1) {
					e1.printStackTrace();
				}
			}
		});
    	button.pack();
    	buttons.put(name, button);
    }
    
    public void set_text_button(String name, String text)
    {
    	Button button = buttons.get(name);
    	button.setText(text);
    	button.pack();
    	buttons.put(name, button);
    }
    
    public void set_pos_button(String name, int x, int y)
    {
    	Button button = buttons.get(name);
    	button.setLocation(x, y);
    	button.pack();
    	buttons.put(name, button);
    }
    
    public void set_size_button(String name, int w, int h)
    {
    	GridData gd = new GridData();
    	Button button = buttons.get(name);
    	gd.heightHint=h;
    	gd.widthHint=w;
    	button.setLayoutData(gd);
    	button.pack();
    	f.layout();
    	buttons.put(name, button);
    }
    
    // go
    
    public void go()
    {
    	f.open();
    	while (!f.isDisposed()) {
	          if (!display.readAndDispatch()) display.sleep(); 
	    } 
	    display.dispose();	
    }

}
