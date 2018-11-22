//package searchEngineProject;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
//import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JFileChooser;

public class GUI extends JFrame 
{
	static final long serialVersionUID = 123L;
	
	//variables used by the GUI interface 
	JPanel browsePanel = new JPanel ();
	JPanel resultsPanel = new JPanel ();
	JPanel browseBooksPanel = new JPanel ();
	JPanel browseStoragePanel = new JPanel ();
	JPanel browseHashPanel = new JPanel ();
	JPanel directorySubmitPanel = new JPanel ();
	JPanel searchPanel = new JPanel ();
	JPanel searchPanel1 = new JPanel ();
	JPanel searchPanel2 = new JPanel ();
	JTextField searchField = new JTextField (20);
	JTextField searchField2 = new JTextField (20);
	JTextField browseBooksField = new JTextField (20);
	JTextField browseStorageField = new JTextField (20);
	JTextField browseHashField = new JTextField (20);
	JButton browseBooksButton = new JButton ("Browse");
	JButton browseStorageButton = new JButton ("Browse");
	JButton browseHashButton = new JButton ("Browse");
	JButton searchButton = new JButton ("Search");
	JButton submitButton = new JButton ("Process Books");
	JLabel browseLabel = new JLabel ("Zipped books: ");
	JLabel browseStorageLabel = new JLabel ("Unzip Books To: ");
	JLabel browseHashLabel = new JLabel ("Parsed Books Log: ");
	JLabel searchLabel = new JLabel ("Search Terms: ");
	JLabel searchLabel2 = new JLabel ("Exclude: ");
	//JLabel blankLabel = new JLabel ("                    ");//blank label to create gap 
	JScrollPane scrollPane;
	
	JFrame frame;
	JPanel panel;
	JTable table;
	
	JComboBox <String> searchOptions = new JComboBox <String> ();

	//creating an instance object to initialize the table
	private Object[][] searchResultTable = new Object[50][1];
	
	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	
	//constructor to build the main GUI 
	public GUI () 
	{
		setTitle ("Search Engine");
		setSize(500, 290);
		setLocation( // Center window on screen.
				(screen.width - 500)/2, 
				(screen.height - 500)/2 );
		setLayout(new BorderLayout());
		browsePanel.setLayout(new GridLayout(4,1));
		searchPanel.setLayout(new GridLayout(3,1));
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
	
		browseBooksPanel.add (browseLabel);
		browseBooksPanel.add (browseBooksField);
		browseBooksPanel.add (browseBooksButton);
		browseStoragePanel.add(browseStorageLabel);
		browseStoragePanel.add(browseStorageField);
		browseStoragePanel.add(browseStorageButton);
		browseHashPanel.add(browseHashLabel);
		browseHashPanel.add(browseHashField);
		browseHashPanel.add(browseHashButton);
		directorySubmitPanel.add (submitButton);

		
		browsePanel.add(browseBooksPanel);
		browsePanel.add(browseStoragePanel);
		browsePanel.add(browseHashPanel);
		browsePanel.add(directorySubmitPanel, BorderLayout.CENTER);
		//browsePanel.add(searchPanel1, BorderLayout.SOUTH);
		//browsePanel.add(searchPanel2);
		
		
		
		searchPanel1.add(searchLabel);
		searchPanel1.add (searchField);
		searchOptions.addItem ("AND");
		searchOptions.addItem ("OR");
		//searchOptions.addItem ("NOT");
		searchPanel1.add (searchOptions);
		
		
		searchPanel2.add(searchLabel2);
		searchPanel2.add (searchField2);
		searchPanel2.add (searchButton);
		
		
		searchPanel.add(searchPanel1);
		searchPanel.add(searchPanel2, BorderLayout.CENTER);
		
		add (browsePanel, BorderLayout.PAGE_START);
		add (searchPanel, BorderLayout.CENTER);
		
		validate ();
		//pack();
		setVisible (true);
		
		browseBooksButton.addActionListener ( new ActionListener () 
		{
			public void actionPerformed (ActionEvent e) 
			{
				browseDirectoryLocation (browseBooksField);
			} // end method
		} );// end inner class
		
		browseStorageButton.addActionListener ( new ActionListener () 
		{
			public void actionPerformed (ActionEvent e) 
			{
				browseDirectoryLocation (browseStorageField);
			} // end method
		} );// end inner class
		
		browseHashButton.addActionListener ( new ActionListener () 
		{
			public void actionPerformed (ActionEvent e) 
			{
				browseFileLocation (browseHashField);
			} // end method
		} );// end inner class
	
		submitButton.addActionListener ( new ActionListener () 
		{
			public void actionPerformed (ActionEvent e) 
			{
				submitDirectory (browseBooksField.getText(), browseStorageField.getText(), browseHashField.getText());
			} // end method
		} );// end inner class
		
		searchButton.addActionListener ( new ActionListener () 
		{
			public void actionPerformed (ActionEvent e) {
				
				search ((String) searchField.getText(), (String) searchOptions.getSelectedItem(), (String) searchField2.getText());
					
			} // end method
		} );// end inner class
	
	} // end constructor
	
	
	//browseLocation method 
	public void browseDirectoryLocation (JTextField setTextField) 
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
	    		    	
	    	String result = chooser.getSelectedFile().toString();
	    		    	
	    	Object[] options = {"Submit", "Cancel"};
	    	
			Component frame = null;
			
			int answer = JOptionPane.showOptionDialog(frame, 
					"Are you sure you want to select the following directory?\n\n" + result +
					"\n\nSubmit to Continue/Cancel to Exit" + "\n ",
					"Confirm Destination",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					
					null,     //do not use a custom Icon
					options,  //the titles of buttons
					options[0]);
						
		    if (answer == JOptionPane.YES_OPTION) 
		    {
		    	setTextField.setText(chooser.getSelectedFile().toString());
		    } 
		    
		    else if (answer == JOptionPane.NO_OPTION) 
		    {
		    	JOptionPane.showMessageDialog(null, "Browse again");
		    	setTextField.setText(null);
		    }
	    }
	    else 
	    {
	    	setTextField.setText("No directory has been selected");
	    }  
	} 
	
	public void browseFileLocation (JTextField setTextField) 
	{
	    JFrame directoryFrame = new JFrame(); 
	    
	    final JFileChooser chooser= new JFileChooser(); 
	
	    directoryFrame.setSize(500, 100); 
	
	    chooser.setCurrentDirectory(new java.io.File("."));
	    chooser.setDialogTitle("Directory");
	    
	    // comment out this line to enable choosing files instead of directories
	    // chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
	
	    chooser.setAcceptAllFileFilterUsed(false);
	
	    if (chooser.showOpenDialog(directoryFrame) == JFileChooser.APPROVE_OPTION) 
	    { 
	    	
	    	String result = chooser.getSelectedFile().toString();
	    		    	
	    	Object[] options = {"Submit", "Cancel"};
	    	
			Component frame = null;
			
			int answer = JOptionPane.showOptionDialog(frame, 
					"Are you sure you want to select the following directory?\n\n" + result +
					"\n\nSubmit to Continue/Cancel to Exit" + "\n ",
					"Confirm Destination",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					
					null,     //do not use a custom Icon
					options,  //the titles of buttons
					options[0]);
			
		    if (answer == JOptionPane.YES_OPTION) 
		    {
		    	setTextField.setText(chooser.getSelectedFile().toString());
		    } 
		    
		    else if (answer == JOptionPane.NO_OPTION) 
		    {
		    	JOptionPane.showMessageDialog(null, "Browse again");
		    	setTextField.setText(null);
		    }
	    	
	    }
	    else 
	    {
	    	setTextField.setText("No directory has been selected");
	    }   
	} 
	
	//submitDirectory method
	public void submitDirectory (String gutBooksDirectory, String bookStorageDirectory, String hashesFileDirectory) 
	{
		parser.parserMain(gutBooksDirectory, bookStorageDirectory, hashesFileDirectory);
	} 

	//search method 
	public void search (String searchWord1, String logicalOperator, String searchWord2) 
	{
		
		Retriever retriever = new Retriever();
		/*
		//message dialog
		Component frame0 = null;		
		JOptionPane.showMessageDialog(frame0,
		    "Please wait while searching....",
		    "Message",
		    JOptionPane.PLAIN_MESSAGE);
		*/
		String[] results = retriever.findSearchTerms(searchField.getText(), logicalOperator, searchField2.getText());
		
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
			searchResultTable[i][0] = results[i];
		} 
 
		//table title
		String[] tableTitle = {
				"Search result for: " + searchField.getText()};

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
		frame.setSize(500, 300);
        frame.setLocation( // Center window on screen.
                (screen.width - 500)/2, 
                (screen.height - 500)/2 );
  
		//frame.setSize (screen.width, screen.height-40);
		frame.setVisible(true);  
		
		//message dialog
		JOptionPane.showMessageDialog(frame,
		    "Maximize to see result in full screen\nMinimize to enter another search",
		    "Message",
		    JOptionPane.PLAIN_MESSAGE);
	}
	
	//main method
	public static void main (String [] args) 
	{
		GUI main = new GUI ();	
	} 

}
