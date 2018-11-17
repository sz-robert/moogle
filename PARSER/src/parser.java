

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
		//test_i.parse_html(html_file, hash_check_file, 0); 
		
		 String book_storage_location = "/Users/user/Desktop/untitled folder 2/gutenberg_book_storage";
		 //String book_directory = "/Users/user/Desktop/untitled folder 2/gutenberg_book_storage/test";
		 String book_directory = "/Users/user/Desktop/untitled folder 2/gutenberg_book_storage/test";
		 parser_class main_parser = new parser_class(); 
		
		main_parser.setup_env(book_storage_location, hash_check_file, book_directory);
		
		int throttle = 0; 
		
		List <parser_class.book_containers> all_parsed_books = new Vector<parser_class.book_containers>(); 
		
		int total_bookz_parsed = 0; 
		
		while(throttle != 1) 
		{						
			main_parser.start_parsing_em(50, 0);  //0 for DISK store, 1 for CLOUD store :) 
			
			List <parser_class.book_containers> my_bookz = main_parser.get_parsed_books();
			
			System.out.println("TESTING PARSE");
			
			for (parser_class.book_containers element : my_bookz)
			{
				System.out.println("parsed TITLE: " + element.title);
				
				//in some cases, (htm / html) parsing author was not mandatory
				//we may not have the author. 
				
				if(element.author != "" && element.author != null)
				{
					System.out.println("AUTHOR: " + element.author);
				}
				
				//**print individual sentences for test! 
				for(String i : element.book_sentences)
				{
					System.out.println(i); 
				}
				
				System.out.println("Book Physical Location: " + element.file_location);
				
				total_bookz_parsed += 1; 
			}
			
			main_parser.clear_books();
			throttle += 1; 
			
			break; //lets just do one pass this time for rest
		}
		
		
		System.out.println("Total number of parsed books: " + total_bookz_parsed);
		
	}

	

}

	

