
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JFileChooser;

public class MainGUI extends JFrame 
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
	
	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	
	//constructor to build the main GUI 
	public MainGUI () 
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
        //instance object parserGUI, uses ParserGUI constructor
        ParserGUI parserGUI = new ParserGUI();
		
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
		        parserGUI.displayParseProgress(result);
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
        //instance object indexerGUI, uses IndexerGUI constructor
        IndexerGUI indexerGUI = new IndexerGUI();
        
        //call to displaySearchResult method in "IndexerGUI"
        indexerGUI.displaySearchResult(searchWord);
	}
	
	//main method
	public static void main (String [] args) 
	{
		MainGUI main = new MainGUI ();	
	} 

} 

