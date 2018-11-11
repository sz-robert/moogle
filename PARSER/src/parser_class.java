import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.DatatypeConverter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.util.Arrays;

public class parser_class {

	

	public class thot_new_thread implements Runnable {
	public void run(){
	System.out.println("MyClass running");
	   } 
	}
	
	public static class book_containers{
		String author = ""; 
		String title = ""; 
		String file_location = "";  
		String [] book_sentences; 
		int in_cloud = 0; //we can let the caller know whether we stored it in the cloud
	}

	public String book_storage_location = "";
	public String hash_check_file = ""; 
	public String book_directory = ""; 
	//public int storeondisk = 0; //do we want to store on disk?
	//public int storeincloud = 0; //do we want to store in cloud? 
	public int diskorcloud = 0; 
	
	List<book_containers> book_storage = new Vector<book_containers>();	//CLASS WILL GIVE
	//TO PARENT APP!
	
		public void clear_books()
		{
			book_storage.clear(); 	//CLEAR PARSED BOOKS FROM RAM! 
		}
	
		public int get_book_count()
		{
			int book_count = book_storage.size(); 
			return book_count; 
		}
	
		public String[] splitAndKeep(String input, String regex, int offset) {
	        ArrayList<String> res = new ArrayList<String>();
	        Pattern p = Pattern.compile(regex);
	        Matcher m = p.matcher(input);
	        int pos = 0;
	        while (m.find()) {
	            res.add(input.substring(pos, m.end() - offset));
	            pos = m.end() - offset;
	        }
	        if(pos < input.length()) res.add(input.substring(pos));
	        return res.toArray(new String[res.size()]);
	    }
		
		public String[] splitAndKeep(String input, String regex) {
	        return splitAndKeep(input, regex, 0);
	    }
		
		
		//REMOVE LAST CHARACTER
		public String removeLastChar(String str) {
	        return str.substring(0, str.length() - 1);
	    }
		
		public void setup_hash_log(String hash_check_file)
		{
	
			this.hash_check_file = hash_check_file;
			
		}
		
		//ALLOW USER TO CONFIGURE CLASS
		public int setup_env(String book_storage_location, String hash_check_file, String book_directory)
		{
			//DO NOT USE CLASS UNLESS THIS FUNC RETURNS 1! 
			//if it returns ZERO CLASS IS NOT CONFIGURED CORRECTLY
			
			this.book_storage_location = book_storage_location; 
			this.hash_check_file = hash_check_file; 
			this.book_directory = book_directory; 
			
			int config_good = 0; 
			
			
			if (Files.isDirectory(Paths.get(this.book_storage_location))) {
				
				  config_good += 1; 
				}
			
			if (Files.isDirectory(Paths.get(this.book_directory))) {
				  config_good += 1; 
				}
			
			if(config_good != 2)
			{
				System.out.println("ERROR: BOOK FOLDERS DO NOT EXIST");
				return 0; 
			}
			
			if(this.book_storage_location.endsWith("/"))
			{
				this.book_storage_location = removeLastChar(this.book_storage_location); 
			}
			
			if(this.book_directory.endsWith("/"))
			{
				this.book_directory = removeLastChar(this.book_directory); 
			}
			
			if(config_good == 2)
			{
				File file = new File(this.hash_check_file);
				try {
					if (file.createNewFile()) {
					    
					    System.out.println("HASH File has been created.");
					    System.out.println(this.hash_check_file);
					} else {
					
					    System.out.println("HASH File already exists.");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("ERROR: COULD NOT CREATE HASH FILE");
					return 0; 
				}
				
			return 1;
			}
			
			return 0; 
		}
		
		public String get_da_MD5(String filename) {
			
			String result = ""; //we will return this as the hash value
			try {
				FileInputStream gotcha = new FileInputStream(filename);
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.reset();
				byte[] bytes = new byte[2048];
				int numBytes;
				while ((numBytes = gotcha.read(bytes)) != -1) {
					md.update(bytes, 0, numBytes);
				}
				byte[] digest = md.digest();
			 result = new String(DatatypeConverter.printHexBinary(digest));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return result; 
	}
		
	public List<book_containers> get_parsed_books()
	{
		if(this.book_storage_location.equals(""))
		{
			System.out.println("ERROR: Please Configure the class first");
		}
		
		if(this.hash_check_file.equals(""))
		{
			System.out.println("ERROR: Please Configure the class first");
		}
		
		if(this.book_directory.equals(""))
		{
			System.out.println("ERROR: Please Configure the class first"); 
		}
		
		return this.book_storage;	//return our stack of books! 
	}
	
	public int parse_html(String file_name, String doc_type) //doc type will be either
															//htm or html
	{
		int for_return = 0; 
		
		book_containers container_for_return = new book_containers(); 
		
		specialized_ops local_instance = new specialized_ops(); 

		int success = local_instance.parse_html(file_name, this.hash_check_file);
		
		if(success == 0)
		{
			return 0; //return without adding to book continers
		}
		
		container_for_return.author = local_instance.book_author; 
		container_for_return.title = local_instance.book_title; 
		container_for_return.file_location = local_instance.book_location; 
		//container_for_return.book_sentences = (String[]) local_instance.book_lines.toArray();
		int line_size = local_instance.book_senteces.length; 
		if(line_size == 0)
		{
			System.out.println("ERROR: Unknown ERROR parsing book");
			return 0; 
		}
		
		//container_for_return.book_sentences = temp_linez; 
		container_for_return.book_sentences = local_instance.book_senteces;
		
		if(this.diskorcloud == 0)	//dont need this if storing in cloud! 
		{
		this.book_storage.add(container_for_return);
		}
		append_log("BOOK_TITLE: " + container_for_return.title);	 //so we dont parse again! 
		String book_text_for_storage = read_entire_file(file_name); //we have to read it all again sadly
									//so we can store book in the cloud
			
			if(this.diskorcloud == 1) //you have to tell the class 
				//you want to store the book in the cloud! 
				{
				specialized_ops da_insert = new specialized_ops(); 
				da_insert.mongo_cloud_insert_book(container_for_return.title, book_text_for_storage, 
						local_instance.book_author, local_instance.book_senteces, doc_type, file_name, true); 
				}
		
		return 1; 
	}
	
	public int parse_text_version(String unzipped) //USES OLD STYLE PARSING
													//DEPRECATED!
	{
		int parse_success = 0; 
		boolean delete_decide = false; //delete unzipped book if we cant parse it (DONT WASTE SPACE)
		boolean read_success = false;
		boolean book_metadata_success = false; 

				String file_hash = get_da_MD5(unzipped); 
				List<String> hashes_array = read_file(hash_check_file); 
				
				for (String dat_hash : hashes_array)
				{
					if(dat_hash.equals(file_hash))
					{
						System.out.println("Book already (ATTEMPTED) parsed. Dont parse again.");
						return parse_success; //DONT PARSE AGAIN WE ALREADY TRIED TO PARSE
						
					}
				}
						//LOG TO THE HASH CHECK FILE SO WE DONT PARSE THE SAME FILEZ! 
						append_log("BOOK_MD5: " + file_hash); 
				
				List<String> file_linez = read_file(unzipped);
				
				String book_title = ""; 
				String book_author = ""; 
				List<String> book_sentences = new Vector<String>();
				
				if(file_linez.size() > 3) //dont parse if we know we dont have
				{								//the full book. Waste of time. 
					book_title = ""; 
					book_author = "";
					int meta_count = 0; 
					String book_text = ""; 
					boolean found_text = false; 
					
					for( String individual_line : file_linez)	//simpy line by line in book
					{
						//individual_line = individual_line.trim(); 
						
						if(individual_line.contains("Title:"))
						{
							individual_line = individual_line.trim();
							//System.out.println("Book Title Found");
							System.out.println(individual_line);
							individual_line = individual_line.replace("Title:", ""); 
							individual_line = individual_line.trim(); 
							book_title = individual_line; 
							meta_count += 1; 
							int title_check = check_log(book_title, 2); //lets make sure we have already parsed this title :) 
							if(title_check == 1)
							{
								System.out.println("Error: Already parsed this book (title)");
								return parse_success; 
							}
						}
						
						if(individual_line.contains("Author:"))
						{
							individual_line = individual_line.trim();
							//System.out.println("Book Title Found");
							System.out.println(individual_line);
							individual_line = individual_line.replace("Author:", ""); 
							individual_line = individual_line.trim();
							book_author = individual_line;
							meta_count += 1; 
						}
						
						if(individual_line.contains("***") && found_text == true)
						{
							//System.out.println("Found End of Book");
							found_text = false; 
						}
						
						if(found_text)
						{
							//System.out.println("Recording Book Line");
							if(individual_line != "" && individual_line.length() != 0)
							{	
							book_text += individual_line; //lets build up the strings.
															//place book into memory. 
							}
						}	
													//test out the buffer read / searcher!
						if(individual_line.contains("START OF THIS PROJECT"))
						{
							//System.out.println("Found Beginning of Book");
							//read_entire_file(unzipped, 1);
							found_text = true; 
						}
							
				}									
				
					if(meta_count > 1)
					{	
						//lets only archive the book
						//if we got all the fields. 
						if(book_title != "" && book_author != "" && book_text != "")
						{
						book_containers book_copy = new book_containers(); 
						book_copy.author = book_author; 
						book_copy.title = book_title; 
						
						//int size_nec = book_text.length() - book_text.replace(".", "").length();
						//int size_nec = book_text.split("\\.",-1).length-1;
						//String [] book_senteces2 = new String[size_nec];  
						//System.out.println(book_text);  //DEBUG SHOW ALL THE BOOK SENTECES
						//book_senteces2 = book_text.split("\\."); 
						//for(String x : book_senteces2) //DEBUG SHOW THEM ALL SPLIT UP BY REGEX
						//{
						//	System.out.println(x);
						//}
						String [] da_book_lines = splitAndKeep(book_text, "[A-Z]*\\.\\s");
						
						String [] book_sentences2 = new String[da_book_lines.length];
						int throttlez = 0; 
						for(String i : da_book_lines)
						{
							book_sentences2[throttlez] = i; 
						}
						
						book_copy.book_sentences = book_sentences2;
						book_copy.file_location = unzipped; 
						book_storage.add(book_copy); //lets store the book
						parse_success = 1; 
						
						//LOG TO THE HASH CHECK FILE SO WE DONT PARSE THE SAME FILEZ! 
						
						append_log("BOOK_TITLE: " + book_title); 
						
						}							//so the caller can get it	
					}
					
				}
				
				File file = new File(unzipped); 
		        if(parse_success == 0)
		        {
		        		delete_decide = true; //IF WE CANT PARSE BOOK
		        								//DONT KEEP IT ON DISK WASTE OF SPACE. 
		     
		        }
				if(delete_decide) //we can decide to delete the unzipped books or not.
				{
			        if(file.delete()) 
			        { 
			            System.out.println("File deleted successfully"); 
			        } 
			        else
			        { 
			            System.out.println("Failed to delete the file"); 
			        }
				}
				
				return parse_success; 
	}	//***END FUNC
	
	public int check_log(String data, int type) //1 for hash value, 2 for book title
	{
		List<String> hashes_array = read_file(hash_check_file); 
		
		int for_return = 0; 
		
		List<String> local_check_array = new Vector<String>(); 
		
		if(type == 1)
		{
			for(String i : hashes_array)
			{
				if(i.contains("BOOK_MD5:"))	//lets get the hashes! 
				{
					String temp = i.replace("BOOK_MD5:", "");
					temp = temp.trim(); 
					local_check_array.add(temp); 
				}
			}
		}
		
		if(type == 2)
		{
			for(String i : hashes_array)
			{
				if(i.contains("BOOK_TITLE:"))	//lets get the hashes! 
				{
					String temp = i.replace("BOOK_TITLE:", "");
					temp = temp.trim(); 
					local_check_array.add(temp); 
				}
			}
		}
		
		for (String dat_hash : local_check_array)
		{
			if(dat_hash.equals(data))
			{
				System.out.println("Book already (ATTEMPTED) parsed. Dont parse again.");
				return 1; //DONT PARSE AGAIN WE ALREADY TRIED TO PARSE
			}
		}
		
		return for_return; 
		
	}
	
	public int append_log(String log_entry)
	{
		int for_return = 0; 
		
		try(FileWriter fw = new FileWriter(hash_check_file, true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			    out.println(log_entry);
			    //more code
			    out.close(); ;
			} catch (IOException e) {
			    //exception handling left as an exercise for the reader
				for_return = 0;
				System.out.println("ERROR: COULD NOT LOG BOOK"); 
				return 0; 
			}
		
		return for_return; 
	}
	
						//need to tell class whether you want the data
						//parsed store on local disk or on cloud
	
	public void start_parsing_em(int number_to_parse, int disk_or_cloud) {
		
		this.diskorcloud = disk_or_cloud; //1 for cloud 0 for disk
											//disk storage might be unstable as ive 
											//been moving all the stuff to cloud
											//per our professor
		
		int book_counter = 0; 
		if(this.book_storage_location.equals(""))
		{
			System.out.println("Please Configure the class first");
			return; 
		}
		
		if(this.hash_check_file.equals(""))
		{
			System.out.println("Please Configure the class first");
			return; 
		}
		
		if(this.book_directory.equals(""))
		{
			System.out.println("Please Configure the class first");
			return; 
		}
		
		// TODO Auto-generated method stub
		//String book_directory = "/Volumes/Untitled 1/GUTENBERG/gut_books/"; 
		
		List<String> zipped_file_list = new Vector<String>();
		stream_dir_contents(book_directory, zipped_file_list, ".zip");
		int number_of_zips = zipped_file_list.size();
		System.out.println("Number of ZIPS/BOOKS: " + number_of_zips);
		
		List<String> raw_htm_filez = new Vector<String>();	//list all the unzipped html files now!
		stream_dir_contents(book_directory, raw_htm_filez, ".htm");
		System.out.println("Number of HTM BOOKS: " + raw_htm_filez.size());
		
		List<String> raw_html_filez = new Vector<String>();	//list all the unzipped html files now!
		stream_dir_contents(book_directory, raw_html_filez, ".html");
		System.out.println("Number of HTML BOOKS: " + raw_html_filez.size());
		
		List<String> text_book_filez = new Vector<String>();	//list all the unzipped html files now!
		stream_dir_contents(book_directory, text_book_filez, ".txt");
		System.out.println("Number of TXT BOOKS: " + text_book_filez.size());
		
		int temp_success = 0; 
		
		//lets parse out all the raw text file bookz FIRST! :) 
		for(String text_file : text_book_filez)		//PARSE TEXT ONLY BOOKS
		{
			System.out.println(text_file);
			String what_u_got = read_file_parse_txt(text_file, 1);	//PARSE ARG OF 1!
			if(what_u_got == null)
			{
				System.out.println("We could not parse this TXT book");
			}
			else
			{
				System.out.println("COMPLETE SUCCESS: PARSED TXT BOOK");
			}
			//String [] da_book_lines = splitAndKeep(what_u_got, "[A-Z]*\\.\\s");
		}
		
		for(String html_file : raw_html_filez)		//PARSE HTML ONLY BOOKS
		{
			temp_success = parse_html(html_file, "html");
			if(temp_success == 1)
			{										//LETS PARSE THE HTM BOOKS FIRST
				System.out.println("SUCCESS: Parsed HTM BOOK");
				book_counter += 1; 
				if(book_counter == number_to_parse)
				{
					System.out.println("Success: Parsed Identified Number of Books");
					return; 
				}
			}
			else
			{
				System.out.println("ERROR: Coule not parse HTM book");
			}
		}
		
		for(String htm_file : raw_htm_filez)		//PARSE HTM ONLY BOOKS :) 
		{
			temp_success = parse_html(htm_file, "htm");
			if(temp_success == 1)
			{										//LETS PARSE THE HTM BOOKS FIRST
				System.out.println("SUCCESS: Parsed HTM BOOK");
				book_counter += 1; 
				if(book_counter == number_to_parse)
				{
					System.out.println("Success: Parsed Identified Number of Books");
					return; 
				}
			}
			else
			{
				System.out.println("ERROR: Coule not parse HTM book");
			}
		}

		List<String> md5_hash_list = new Vector<String>();
		boolean delete_decide = false; 
		boolean read_success = false;
		boolean book_metadata_success = false; 
		
		//int book_counter = 0; 
		for(String zip_archive : zipped_file_list)	//ITERATE THROUGH ALL THE ARCHIVES
		{
			List<String> unzipped_filez = unzip_file(zip_archive); 
			
			for ( String unzipped : unzipped_filez) //iterate through all the unzipped books
			{
				System.out.println("\n\n");
				System.out.println("Attempting to Parse Book: " + unzipped);
				
				//parse htm
				if(unzipped.contains(".htm"))
				{
						temp_success = parse_html(unzipped, "htm");
						if(temp_success == 1)
						{										//LETS PARSE THE HTM BOOKS FIRST
							System.out.println("SUCCESS: Parsed HTM BOOK");
							book_counter += 1; 
							if(book_counter == number_to_parse)
							{
								System.out.println("Success: Parsed Identified Number of Books");
								return; 
							}
						}
						
						else
						{
							System.out.println("Error: Could Not Parsed HTM Book: " + unzipped);
						}
						
				}
				
				
				//parse htm
				
				//parse html
				
				if(unzipped.contains(".html"))
				{
						temp_success = parse_html(unzipped, "html");
						if(temp_success == 1)
						{										//LETS PARSE THE HTM BOOKS FIRST
							System.out.println("SUCCESS: Parsed HTML BOOK");
							book_counter += 1; 
							if(book_counter == number_to_parse)
							{
								System.out.println("Success: Parsed Identified Number of Books");
								return; 
							}
						}
						
						else
						{
							System.out.println("Error: Could Not Parsed HTML Book: " + unzipped);
						}
				}
				
				
				//parse html
				
					
				if(unzipped.contains(".txt"))
				{
					System.out.println(unzipped);
					String what_u_got = read_file_parse_txt(unzipped, 1);	//PARSE ARG OF 1!
					if(what_u_got == null)
					{
						System.out.println("We could not parse this TXT book");
					}
					else
					{
						System.out.println("COMPLETE SUCCESS: PARSED TXT BOOK");
						book_counter += 1; 
						if(book_counter == number_to_parse)
						{
							System.out.println("Success: Parsed Identified Number of Books" + unzipped);
							return; 
						}
					}
				}
				System.out.println("Book Parse Count: " + book_counter);
			}
		}
	}
	
	public static String read_entire_file(String da_file_name)
	{
		try {
			File file = new File(da_file_name);
			int file_length = da_file_name.length(); 
			if(file_length > 1.5e+7) //dont read books over 15 megs in size
			{
				System.out.println("Book to large to safely read!");
				return null;
			}
			FileInputStream fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();
			String str = new String(data, "UTF-8");		//convert to string lets do it!! :) 
			return str; 
		}
		catch(Exception e)
		{
			System.out.println(e);
			return null; 
		}
	}
	
													//give it parse value one if we want to manually
													//parse the text bro! 
	public String read_file_parse_txt(String da_file_name, int parse_decide)
	{
		//return a big string with the book in it. then we can 
		//parse out the text part using regex. 
		
		//Pattern pattern = Pattern.compile("START OF THIS PROJECT(.+?)***");
		
		try {
		File file = new File(da_file_name);
		int file_length = da_file_name.length(); 
		if(file_length > 1.5e+7) //dont read books over 15 megs in size
		{
			System.out.println("Book to large to safely read!");
			return null;
		}
		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		fis.read(data);
		fis.close();
		
		String book_text_str = new String(data, "UTF-8");	
		if(data.length == 0)
		{
			System.out.println("ERROR: SOMETHING WENT WRONG");
			return null; 
		}
		
		String tag_title = "Title: ";
		byte [] dat_tag_title = tag_title.getBytes();
		
		String author_title = "Author: "; 
		byte [] dat_tag_author = author_title.getBytes(); 
		
		String parser_tag_1 = "START OF THIS PROJECT";
		byte [] dat_tag_1 = parser_tag_1.getBytes();
		String parser_tag_2 = "***"; 
		byte [] dat_tag_2 = parser_tag_2.getBytes();
		int start_index = 0; 
		int end_index = 0;
		int title_index = 0; 
		String str = ""; 
		boolean keep_going = true; 
		
		String book_title = ""; 	//WE REQUIRE THE BOOK TITLE
		String book_author = "";  //FINDING BOOK AUTHOR IS OPTIONAL (not in structured format!)
		int temp_index = 0; 
		
		if(parse_decide == 1)
		{
			KMPMatch searcher = new KMPMatch(); 
			title_index = searcher.indexOf(data, dat_tag_title, 0);
			if(title_index == -1)
			{
				return null; //cant parse book bro! 
			}
			
			temp_index = title_index; 
			temp_index = temp_index + dat_tag_title.length; 
			while(keep_going)
			{
				byte temper = data[temp_index]; 
				if(temper == 0x0D | temper == 0x0a)
				{
					break; //end of title lets get out of here! 
				}
				else
				{
					book_title += (char)data[temp_index]; 	//PARSE OUT BOOK 
				}											//TITLE MANUALLY! 
				temp_index += 1; 
			}
			System.out.println("BOOK Title: " + book_title);
			//parse author
			
			title_index = searcher.indexOf(data, dat_tag_author, 0);
			if(title_index == -1)
			{
				return null; //cant parse book bro! 
			}
			
			temp_index = title_index; 
			temp_index = temp_index + dat_tag_author.length; 
			while(keep_going)
			{
				byte temper = data[temp_index]; 
				if(temper == 0x0D | temper == 0x0a)
				{
					break; //end of title lets get out of here! 
				}
				else
				{
					book_author += (char)data[temp_index]; 	//PARSE OUT BOOK 
				}											//TITLE MANUALLY! 
				temp_index += 1; 
			}
			
			specialized_ops local_instance = new specialized_ops(); 
			int cloud_check = local_instance.mongo_check_if_book_exist(book_title, "txt"); 
			
			if(cloud_check == 1)
			{
				System.out.println("NOTE: book already in cloud dont parse again");
				return null; 
			}
			
			//parse author
			
			start_index = searcher.indexOf(data, dat_tag_1, start_index);
			if(start_index == -1)
			{
				System.out.println("ERROR: Cant find start of book");
				return null; //cant parse book bro! 
			}
			
			start_index = searcher.indexOf(data, dat_tag_2, start_index);
			if(start_index == -1)
			{
				System.out.println("ERROR: Cant find start of book");
				return null; //cant parse book bro! 
			}
			
			start_index = start_index + dat_tag_2.length; 
			
			end_index = searcher.indexOf(data, dat_tag_2, start_index);
			if(end_index == -1)
			{
				System.out.println("ERROR: Cant find END of book");
				return null; //cant parse book bro! 
			}
			
			end_index = end_index - 1; 
			int new_buff_size = end_index - start_index; 
			byte[] dat_data = new byte[new_buff_size];
			int tout = 0;
			int ii = 0; 
			while(tout != new_buff_size)
			{
				dat_data[ii] = data[start_index + tout];
				tout += 1; 
				ii += 1; 
			}
			str = new String(dat_data, "UTF-8");		//convert to string lets do it!! :) 
			str = str.trim(); //just trim off the crud! CONTAINS ENTIRE BOOK
			//System.out.println(str);
		}
		
		if(str.length() == 0)
		{
			return null; //somethings wrong
		}
		
		//LETS PARSE SENTENCES USING DA REGEX!
		
		//String [] da_book_lines = splitAndKeep(what_u_got, "[A-Z]*\\.\\s");
		String [] da_book_lines = splitAndKeep(str, "[a-z]\\.");
		String [] cleaned_up_lines = new String[da_book_lines.length]; 
		int move_throttle = 0; 
		boolean copy_it = true; 
		for(int i = 0; i < da_book_lines.length; i++)
		{
			copy_it = true; 
			String temp = da_book_lines[i]; 
			temp = temp.trim(); 
			temp = temp.replace("\r\n", " ");
			boolean is_upper = Character.isUpperCase(temp.codePointAt(0));
			if(is_upper == false)
			{
				copy_it = false; 
			}
			String [] split_count = temp.split(" "); 
			if(split_count.length < 4)
			{
				copy_it = false; 
			}
			
			if(copy_it)
			{
				cleaned_up_lines[move_throttle] = temp; 
				move_throttle += 1; 
			}
		}
		/*
		for(String i : cleaned_up_lines)
		{
			System.out.println(i);
		}
		*/
		//PARSE SENTECES!
		
		book_containers store_locker = new book_containers();
		if(this.diskorcloud == 0)
		{
			store_locker.title = book_title; 
			store_locker.author = book_author; 
			store_locker.file_location = da_file_name;
			store_locker.book_sentences = cleaned_up_lines; 
			store_locker.in_cloud = 0; //not in cloud unless gets marked 
										//and stored in cloud below
			
		}
		
		if(this.diskorcloud == 1) //you have to tell the class 
			//you want to store the book in the cloud! 
			{
			specialized_ops da_insert = new specialized_ops();
			
			int result = da_insert.mongo_cloud_insert_book(book_title, str, book_author,  
					cleaned_up_lines , "txt", da_file_name, true);
			
			if(result == 0)
			{
				System.out.println("ERROR: Could not store book in cloud");
				return null; 
			}
			//store_locker.in_cloud  = 1; //let indexer know we stored him in the cloud! 
			}
		
		return str; 
		
		}
		catch(Exception e)
		{
			System.out.println("ERROR READING FILE");
			return null; 
		}
		
	}
	
	public static List<String> read_file(String da_file) //RETURNS FILE DATA LINE BY LINE
	{
		List<String> for_return = new Vector<String>();

		    try {
				Scanner scan = new Scanner(new File(da_file));
				//scan.
				while(scan.hasNextLine()){
				    String line = scan.nextLine();
				    if(line != "") //NO POINT in adding empty lines! 
				    {	
				    for_return.add(line);
				    }
				    //Here you can manipulate the string the way you want
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				for_return.clear();
				System.out.println("ERROR READING BOOK");
				return for_return; 	
			}
		
		return for_return;
	}
	
	public static int stream_dir_contents(String da_directory, List<String> da_file_names, String ext_type) {
		boolean parse_success = true; 
		DirectoryStream<Path> stream = null;
		try{
	          Path dir = Paths.get(da_directory);
	    	  stream = Files.newDirectoryStream(dir);
	          for (Path p: stream) {
	        	  		Path file_namee = p.getFileName();	//doesnt need "/" string on dir
	        	  		String abs_path = da_directory + "/" + file_namee.toString(); 
	        	  	if(!Files.isDirectory(p))
	        	  	{ 
	        	  	if(abs_path.endsWith(ext_type))
		        	  	{
			        System.out.println(abs_path);
			        da_file_names.add(abs_path);
		        	  	}
	        	  	}
	        	  	else {
	        	  		//System.out.println("Call Func Recursively");
	        	  		stream_dir_contents(abs_path, da_file_names, ext_type); 
	        	  	}
		       }
		   } catch (IOException ex) {
		       ex.printStackTrace();
		   }finally{
			   try {
				stream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   }
		if(da_file_names.size() == 0)
		{
			return 0;
		}
		
		return 1; 
		
	}
	
	public List<String> unzip_file(String da_file_name) //UNZIP THE BOOKS
	{		
		boolean book_already_unzipped = false; 
		//RETURN THEIR DATA IN BYTE ARRAY LIST
		List<String> file_list = new Vector<String>();
		Random random = new Random();
		try {
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(da_file_name));
        ZipEntry zipEntry = zis.getNextEntry();
        while(zipEntry != null){
        	String logFileName = new SimpleDateFormat("yyyyMMddHHmm'.txt'").format(new Date());
        	double rand_int = random.nextDouble();
        	logFileName = logFileName + rand_int + "._parsed_book"; 
        	logFileName = book_storage_location + "/" + logFileName; 
            String fileName = zipEntry.getName();
            File newFile = new File(logFileName);
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            
            //test hash
            
            String file_hash = get_da_MD5(logFileName); 
			//List<String> hashes_array = read_file(hash_check_file); 
			book_already_unzipped = false; 
			
			if(check_log(file_hash, 1) == 1)
			{
				book_already_unzipped = true; 
				File newFile2 = new File(logFileName);
				newFile.delete(); 
			}
  
            //test hash
            if(book_already_unzipped == false)
            {
            file_list.add(logFileName);
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
		}
		catch (Exception e)
		{
			System.out.println("Could Not Unzip File!");
			System.out.println(e);
		}
		
		return file_list; 
		
	}
	
}
