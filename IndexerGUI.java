
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class IndexerGUI 
{
	
	//variables used by the GUI interface 
	JFrame frame;
	JPanel panel;
	JTable table;
	JLabel label1;
	JLabel label2;
	JScrollPane scrollPane;
	JProgressBar progressBar;
	JTextField text;
	
	//creating an instance object to initialize the table
	private Object[][] searchResultTable = new Object[10][1];
	
	//default constructor  
	public IndexerGUI() 
	{
	
	}
	
	//runSearch() method to search the database for a word
	public void runSearch(String word) 
	{
		
	
	}
	
	
	//displaySearchResult() method to display the search results of the database in a table
	public void displaySearchResult(String word) 
	{
		
		//initializing the GUI interface to display the search process 
		frame = new JFrame("Searching Progress");
		panel = new JPanel(new GridLayout(2, 0, 20, 20));
		progressBar = new JProgressBar();
		label1 = new JLabel ();
		label2 = new JLabel ();
		text = new JTextField (28);
		progressBar.setStringPainted(true);
		text.setEditable(false);
		
		panel.add(label1);
		panel.add(progressBar);
		panel.add(text);
		panel.setOpaque(true);
		label2.setSize(1, 3);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		frame.pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation( // Center window on screen.
                (screen.width - 500)/2, 
                (screen.height - 500)/2 );
		frame.setVisible(true);  
		
		
		//the number of rows in the array
		String[] searchOutPutArray = new String[10]; 
		
		//loop (searchOutPutArray.length-1) times to create elements of the table 
		for (int i = 0; i <= searchOutPutArray.length-1; i++) 
		{
			
			searchOutPutArray [i] = "Call me Ishmael. (Moby Dick, Melville, 1851). ";
			
			searchResultTable[i][0] = searchOutPutArray[i];
			searchResultTable[searchOutPutArray.length-1][0] = searchOutPutArray[searchOutPutArray.length-1];
			
			//display a progress bar during search to show the progress of search
			progressBar.setValue(100);
			label1.setText("Searching for the requested word: ");
			text.setText("Please wait while searching the database....");

		} 

		//disposing the search progress GUI
		//frame.dispose();
		
		//string variable 
		//create the titles of the table
		String[] tableTitle = {
				"The first 10 search result for the requested word: 'Ishmael'"};

		// initializing the GUI interface to display the search result in a table
		table = new JTable(searchResultTable, tableTitle);
		table.setFillsViewportHeight(true);
		scrollPane = new JScrollPane(table);
		
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
}