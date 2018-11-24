import java.io.File;

import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Stream;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteException;
import com.mongodb.BulkWriteResult;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.bulk.BulkWriteError;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.WriteModel;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;

public class Indexer2 {
	
	MongoClient serverConnection = new MongoClient("localhost", 27017);
	MongoDatabase db = serverConnection.getDatabase("library");
/*	MongoClient mongo = new MongoClient( "--------------" , ------ );
	String password = "--------"; 
	ServerAddress get_to_em = new ServerAddress(------------- , -------); 
	MongoCredential credential = MongoCredential.createCredential(---------, ---------, --------------);
	MongoDatabase db = mongo.getDatabase("library");*/
	
	HashSet<String> commonWords = new CommonWords().filteredWords;
	//MongoCollection<Document> wordsCollection = db.getCollection("wordsS5");
	MongoCollection<Document> booksCollection = db.getCollection("books8");

	ArrayList<Word> books = new ArrayList<>();
	List<WriteModel<Document>> wordDocuments = new ArrayList<>();
	List<WriteModel<Document>> occurrenesDocuments = new ArrayList<>();
	Hashtable<String, Word> wordsTable = new Hashtable<>();
	Hashtable<String, Word> wordsData = new Hashtable<>();
	
	public Indexer2 () {}
		public void insertNewDocument(String book_title, String book_author, String unzipped, String[] book_sentences2) {
			 int sentencePosition = 1;
			 Document bookDocument = new Document("title", book_title)
		                .append("author", book_author);
		                //.append("date", book_year)
		                for(String sentence : book_sentences2) {
		                	bookDocument.append("sentence-" + String.valueOf(sentencePosition), sentence);
		                	System.out.println("Inserted: " + sentencePosition + " sentence for: " + book_title );
		                	String[] words = sentence.split(" "); 
		                	for(String word : words) {
		                		if(!(word.length() < 1) && (word != "") && (!word.isEmpty()) && (word != null) && (word.charAt(0) != '$')) {
		                			updateWordsData(word, sentencePosition);
		                		}
		                		
		                	}
		                	sentencePosition++;
		                }
		                for(String key : wordsData.keySet()) {
		                		String indexTotal = key + ".totalOccurrences";
		                		//booksCollection.createIndex(Indexes.descending(indexTotal));
		                		Word currentWord = wordsData.get(key);
		                		System.out.println("Inserting: " );
		                		bookDocument.append(currentWord.getName(), new Document("totalOccurrences", currentWord.getTotal()).append("locations", currentWord.getLocations()));
		                		System.out.println("Inserted: " + currentWord.getName() + " into document for: " + book_title );
		                	}
		                booksCollection.insertOne(bookDocument);
		                System.out.println("Inserted: "+ book_title );
		}
		private void updateWordsData(String word, int sentencePosition) {
			System.out.println("Attempting to insert word from updateWords: " + word);
			if (wordsData.containsKey(word)) {
				Word existingWord = wordsData.get(word);
				//existingWord.setName(word);
				existingWord.incrementTotal();
				existingWord.appendLocations(String.valueOf(sentencePosition));
			}
			else {
				Word newWord = new Word();
				newWord.setName(word);
				newWord.incrementTotal();
				newWord.appendLocations(String.valueOf(sentencePosition));
				wordsData.put(word, newWord);
			}
		}

		public String[] getQuotes(String words) {
			String searchTerms[] = words.split(" ");
			String[] results = new String[10];

			for (String searchTerm : searchTerms) {
				String occurrence = searchTerm + ".totalOccurrences";
				String locations = searchTerm + ".locations";
				System.out.println("things: " + occurrence + " " + locations);
		        Document projectFields = new Document("_id", 1);
		       projectFields.put("title", 1);
		       projectFields.put("author", 1);
		       projectFields.put(occurrence, 1);
		      projectFields.put(locations, 1);

				AggregateIterable<Document> aggregate = booksCollection.aggregate(
			            Arrays.asList(
			                    new Document("$match", new Document(occurrence, new Document("$gt", 0))),
			                    new Document("$limit", 10),
			                    new Document("$project", new Document(projectFields)
			                    )));
				int resultsCounter = 0;
				for (Document doc : aggregate) {
					System.out.println("doc.tojson: " + doc.toJson()); 
					
					if(resultsCounter < 10) {
						results[resultsCounter] = doc.toJson();
						resultsCounter++;
					}

					String maxField = "$" + searchTerm + ".totalOccurrences";
					String maxField2 = searchTerm + ".totalOccurrences";
					String locationsFromMax = searchTerm + ".locations";
					List<Document> resultsMax = booksCollection.aggregate(
							Arrays.asList(
							      match(
							            and(

							                  gte(maxField2, 0)
							              )      
							           ),
							      group(
							           "_id:0", 

							           max("max", maxField)
							      )           
							   )            
							).into(new ArrayList<>());
					for(Document docum : resultsMax) {
						System.out.println("doc.tojson: max:  " + docum.toJson()); 
					}
					return results;
				}
			}
			return results;
		}
}
class Word{
	private String name = "";
	private int totalOccurrences = 0;
	private String locations = "";
	
	String getName() {
		return name;
	}
	String getLocations() {
		return locations;
	}
	Integer getTotal() {
		return totalOccurrences;
	}
	void setName(String name) {
		this.name = name;
	}
	void incrementTotal() {
		this.totalOccurrences++;
	}
	void appendLocations(String location) {
		this.locations += location + "-";
	}
}
