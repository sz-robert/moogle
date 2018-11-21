
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class ParserGUI 
{
	//variables used by the GUI interface 
	JFrame frame;
	JPanel panel;
	JLabel label1;
	JLabel label2;
	JScrollPane scrollPane;
	JProgressBar progressBar;
	JTextField text;

	//default constructor  
	public ParserGUI() 
	{
		
	}
	
	//runParse() method to parse the information 
	public void runParse(String directory) 
	{
	
	}
		
	//displayParseProgress() method to display the progress of parsing the database
	public void displayParseProgress(String directory) 
	{
		
		//initializing the GUI interface to display the parsing process 
		frame = new JFrame("Parsing Progress");
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
		
		//display a progress bar during parsing to show the progress of parsing
		progressBar.setValue(100);
		label1.setText("Parsing the information in database: ");
		text.setText("Please wait while parsing the database....");
		
		JOptionPane.showMessageDialog(null, "Database is ready for search");

		//disposing the parsing progress GUI
		frame.dispose();
	}
	
}
