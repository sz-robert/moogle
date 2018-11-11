

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.DatatypeConverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ParallelScanOptions;
import com.mongodb.ServerAddress;
import com.mongodb.*; 


public class parser {


	public static void main(String[] args) {
		
		
		//MONGO TEST
		

		//specialized_ops temp_er = new specialized_ops(); 
		////temp_er.do_mongo_cloud_query();
		
		
		//MONGO TEST
		
		
		// TODO Auto-generated method stub
		String hash_check_file = "/Users/user/Desktop/untitled folder 2/gutenberg_book_storage/parsed_hashes.txt";
		//String html_file = "/Volumes/Untitled 1/GUTENBERG/gut_books_2/etext00/eduha10h.htm";
		//String html_file = "/Volumes/Untitled 1/GUTENBERG/gut_books_2/etext05/8trsa10h.htm";
		String html_file = "/Volumes/Untitled 1/GUTENBERG/gut_books_2/etext04/fb10w11h/fb10w11h.html";
		specialized_ops test_i = new specialized_ops(); 
		test_i.parse_html(html_file, hash_check_file); 
		
		 String book_storage_location = "/Users/user/Desktop/untitled folder 2/gutenberg_book_storage";
		 //String hash_check_file = "parsed_hashes.txt"; 
		 String book_directory = "/Volumes/Untitled 1/GUTENBERG/gut_books"; 
		//String book_directory = "/Volumes/Untitled 1/GUTENBERG/gut_books_2"; 
		 //String book_directory = "/Users/user/Desktop/untitled folder 2/gutenberg_book_storage/test";
		parser_class main_parser = new parser_class(); 
		
		//test_i.mongo_retrieve_sentences("The Wandering Jew, Book V.", "txt"); 
		
		main_parser.setup_env(book_storage_location, hash_check_file, book_directory);
		
		int throttle = 0; 
		
		List <parser_class.book_containers> all_parsed_books = new Vector<parser_class.book_containers>(); 
		
		while(throttle != 3 + 1) //three iterations 1000 attempts each. 
		{						//well use option 1 for second arg to store in da cloud! 
			main_parser.start_parsing_em(500, 1);
			
			List <parser_class.book_containers> my_bookz = main_parser.get_parsed_books();
			
			System.out.println("TESTING PARSE");
			
			for (parser_class.book_containers element : my_bookz)
			{
				//System.out.println("AUTHOR: " + element.author);
				System.out.println("parsed TITLE: " + element.title);
			}
			
			for(parser_class.book_containers ii : my_bookz)
			{
				all_parsed_books.add(ii); //ADD TO MAIN LIST
			}
			
			main_parser.clear_books();
			throttle += 1; 
			
			break; //lets just do one pass this time for rest
		}
		
		int total_bookz_parsed = 0; 
		for(parser_class.book_containers i : all_parsed_books)
		{
			
			System.out.println("TITLE: " + i.title);
			if(i.title.startsWith("Book "))
			{
			String [] bible_sentences = i.book_sentences;
				for(String verse : bible_sentences)
				{
					System.out.println(verse + "\n"); 
				}
			}
				System.out.println("Location: " + i.file_location);
			total_bookz_parsed += 1; 
		}
		
		System.out.println("Total number of parsed books: " + total_bookz_parsed);
		
	}

	

}

	

