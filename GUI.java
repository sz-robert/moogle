
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

//import searchEngineProject.Retriever;

import javax.swing.JFileChooser;

public class GUI extends JFrame 
{
	static final long serialVersionUID = 123L;
	
	//variables used by the GUI interface 
	JPanel mainPanel = new JPanel ();
	JTextArea outputWindow = new JTextArea ();
	JTextField searchField = new JTextField (20);
	JTextField browseField = new JTextField (40);
	JScrollPane scrollPane = new JScrollPane (outputWindow);
	JButton browseButton = new JButton ("Browse");
	JButton searchButton = new JButton ("Search");
	JLabel browseLabel = new JLabel ("Browse for the location of the books:");
	JLabel searchLabel = new JLabel ("Type a word to search in the destination:");
	JLabel blankLabel = new JLabel ("                    ");//blank label to create gap 
	
	//variables to create the table
	JFrame frame;
	JPanel panel;
	JTable table;
	JLabel label1;
	JLabel label2;
	//JScrollPane scrollPane;
	JProgressBar progressBar;
	JTextField text;
	
	private Object[][] searchResultTable = new Object[10][1];
	
	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	
	//constructor to build the main GUI 
	public GUI () 
	{
		setTitle ("Search Engine");
		setSize (screen.width, screen.height-40);
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		setVisible (true);
	
		outputWindow.setEditable(false);
	
		mainPanel.add (browseLabel);
		mainPanel.add (browseButton);
		mainPanel.add (browseField);
		mainPanel.add (blankLabel);
		mainPanel.add (searchLabel);
		mainPanel.add (searchField);
		mainPanel.add (searchButton);
		
		add (mainPanel, BorderLayout.PAGE_START);
		add (scrollPane, BorderLayout.CENTER);
		
		validate ();
		
		browseButton.addActionListener ( new ActionListener () 
		{
			public void actionPerformed (ActionEvent e) 
			{
				browseLocation ();
			} // end method
		} );// end inner class
		
		searchButton.addActionListener ( new ActionListener () 
		{
			public void actionPerformed (ActionEvent e) {
				
				search ((String) searchField.getText());
					
			} // end method
		} );// end inner class
	
	} // end constructor
	
	
	//browseLocation method 
	public String browseLocation () 
	{
	    JFrame directoryFrame = new JFrame(); 
	    
	    final JFileChooser chooser= new JFileChooser(); 
	
	    directoryFrame.setSize(500, 100); 
	
	    chooser.setCurrentDirectory(new java.io.File("."));
	    chooser.setDialogTitle("Directory");
	    
	    // comment out this line to enable choosing files instead of directories
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
	
	    chooser.setAcceptAllFileFilterUsed(false);
	
	    if (chooser.showOpenDialog(directoryFrame) == JFileChooser.APPROVE_OPTION) 
	    { 
	    	browseField.setText(chooser.getSelectedFile().toString());
	    	
	    	String result = chooser.getSelectedFile().toString();
	    		    	
	    	Object[] options = {"Submit", "Cancel"};
	    	
			Component frame = null;
			
			int answer = JOptionPane.showOptionDialog(frame, "Are you sure you want to search in the following directory?\n" + result +
			"\n\nSelect 'submit' to continue, 'Cancel' to exit" + "\n ",
			"Confirm Destination",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,     //do not use a custom Icon
			options,  //the titles of buttons
			options[0]);
			
		    if (answer == JOptionPane.YES_OPTION) 
		    {
		    	//call to displayParseProgress method in "ParserGUI"
		    	JOptionPane.showMessageDialog(null, "Database is ready for search");
		    } 
		    
		    else if (answer == JOptionPane.NO_OPTION) 
		    {
		    	JOptionPane.showMessageDialog(null, "Browse again");
		    	browseField.setText(null);
		    }
	    	return result;
	    }
	    else 
	    {
	    	browseField.setText("No directory has been selected");
	    }
		return null;   
	} 

	//search method 
	public void search (String searchWord) 
	{	
		
		//message dialog
		JOptionPane.showMessageDialog(frame,
		    "No matter what you type for search, this will only show the search result for Ishmael (Moby Dick, 1851)\nThis is a test GUI",
		    "Message",
		    JOptionPane.PLAIN_MESSAGE);
		
		//table variables
		frame = new JFrame("");
		panel = new JPanel(new GridLayout(2, 0, 20, 20));
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		frame.pack();
 
		String[] searchOutPutArray = new String[10]; 
		
		//loop (searchOutPutArray.length-1) times to create elements of the table 
		for (int i = 0; i <= searchOutPutArray.length-1; i++) 
		{
			searchOutPutArray [i] = "Call me Ishmael. (Moby Dick, Melville, 1851). ";
			
			searchResultTable[i][0] = searchOutPutArray[i];
			searchResultTable[searchOutPutArray.length-1][0] = searchOutPutArray[searchOutPutArray.length-1];
		} 
 
		//table title
		String[] tableTitle = {
		"Search result for: Ishmael" };

		// initializing the GUI interface to display the search result in a table
		table = new JTable(searchResultTable, tableTitle);
		table.setFillsViewportHeight(true);
		 
		scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		table.setAutoscrolls(true);
		//table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		panel = new JPanel(new GridLayout(1,0));
		panel.add(scrollPane);    
		panel.setOpaque(true);

		frame = new JFrame("Search Result");		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		frame.pack();
		frame.setSize (screen.width, screen.height-40);
		frame.setVisible(true);  
	}
	
	//main method
	public static void main (String [] args) 
	{
		GUI main = new GUI ();	
	} 

} 

