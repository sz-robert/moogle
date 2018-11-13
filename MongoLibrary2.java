import java.io.File;

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
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.Filters;
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

public class MongoLibrary2 implements ILibraryDB{
	
	MongoClient serverConnection = new MongoClient("localhost", 27017);
	MongoDatabase db = serverConnection.getDatabase("library");
/*	MongoClient mongo = new MongoClient( "...amazonaws.com" , 1234 );
	String password = ""; 
	ServerAddress get_to_em = new ServerAddress("...amazonaws.com" , 1234); 
	MongoCredential credential = MongoCredential.createCredential("....", "...", password.toCharArray());
	MongoDatabase db = mongo.getDatabase("library");*/
	
	HashSet<String> commonWords = new CommonWords().filteredWords;
	MongoCollection<Document> wordsCollection = db.getCollection("bulkwords1");
	MongoCollection<Document> booksCollection = db.getCollection("bulkbooks1");

	ArrayList<Word> books = new ArrayList<>();
	List<WriteModel<Document>> wordDocuments = new ArrayList<>();
	List<WriteModel<Document>> occurrenesDocuments = new ArrayList<>();
	Hashtable<String, Word> wordsTable = new Hashtable<>();
	
	public MongoLibrary2 () {
		
	}

	@Override
	public boolean createLibrary(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean insert(Book book) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] findSentences(String[] words) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String[] findSentences(String words) {
		String searchTerms[] = words.split(" ");
		String[] results = new String[10];
		
		for (String searchTerm : searchTerms) {
	        Document projectFields = new Document("_id", 0);
	        projectFields.put("occurrences.title", 1);
	        projectFields.put("occurrences.total", 1);
	        projectFields.put("occurrences.locations", 1);

			AggregateIterable<Document> aggregate = wordsCollection.aggregate(
		            Arrays.asList(
		                    new Document("$unwind", "$occurrences"),
		                    new Document("$match", new Document("name", searchTerm)),
		                    new Document("$limit", 15),
		                    new Document("$project", new Document(projectFields)
		                    )));

			for (Document doc : aggregate) {
				//System.out.println("doc.tojson: " + doc.toJson()); 
				String[] jsonSplit = doc.toJson().split("\"");
				//System.out.println("Title: " + jsonSplit[5]);
				//System.out.println("Total: " + jsonSplit[8]);
				//System.out.println("Locations: " + jsonSplit[11]);
				String[] locationsSplit = jsonSplit[11].split("-");
				//System.out.println("First Location: " + locationsSplit[0]);
				//System.out.println("Last Location: " + locationsSplit[locationsSplit.length - 1]);
				
				int resultsCounter = 0;
				for(String location : locationsSplit) {
					String quote = retrieveSentence(location);
					if(resultsCounter < 10) {
						results[resultsCounter] = quote;
						resultsCounter++;
					}
				}
				
			}
		}
		return results;
	}

	public void insert(String book_title, String book_author, String unzipped, String[] book_senteces2) {

		List<Document> textDocs = new ArrayList<>();
		Document bookDocument = new Document();
		
		bookDocument.append("title", book_title);
		bookDocument.append("author", book_author);
		int counter = 1;
		for (String sentence : book_senteces2) {
			Document sentenceDocument = new Document();
			sentenceDocument.put("SentenceNumber", counter);
			sentenceDocument.put("Sentence", sentence);
			textDocs.add(sentenceDocument);
			
			String[] splitWords = sentence.split(" ");
			for (String word : splitWords) {
				updateWordsList(word);
				updateWordsTable(word, counter, book_title);
			}
			counter++;
		}
		bookDocument.put("text", textDocs);
		booksCollection.insertOne(bookDocument);
		System.out.println("Sentences inserted into books collection.");
		bulkInsert(wordDocuments, wordsCollection);
		System.out.println("Words inserted into words collection.");
		createOccurrencesList(wordsTable);	
		bulkInsert(occurrenesDocuments, wordsCollection);
		System.out.println("Occurrances inserted into words in words collection.");
	}
		private void createOccurrencesList(Hashtable<String, Word> wordsTable) {
			for(String key : wordsTable.keySet()) {
				
				List<Document> occurrencesArray = new ArrayList<>();

				Word currentWord = wordsTable.get(key);
				    Document filterDoc = new Document()
				    		.append("name", key);
				    Document setOccurrence = new Document()
				    		.append("occurrences", new Document()
				        			.append("title", currentWord.occurrence.title)
				        			.append("total", currentWord.occurrence.total)
				        			.append("locations", currentWord.occurrence.locations));
				    Document pushOccurrence = new Document()
				    		.append("$push", setOccurrence);

			    UpdateOptions upOptions = new UpdateOptions();
			    upOptions.upsert(true); 
			    upOptions.bypassDocumentValidation(true); 

			    occurrenesDocuments.add(new UpdateOneModel<Document>(filterDoc, pushOccurrence, upOptions));
			    System.out.println("Occurrence added. size of occurrenesDocuments is now: " + occurrenesDocuments.size());
			}
	}

		private void updateWordsTable(String word, int counter, String book_title) {
		
			if (wordsTable.containsKey(word)) {
				Word existingWord = wordsTable.get(word);
				existingWord.occurrence.title = book_title;
				existingWord.occurrence.total++;
				existingWord.occurrence.locations += Integer.toString(counter) + "-" ;
			}
			else {
				Word newWord = new Word(word, book_title, 1, counter);
				wordsTable.put(word, newWord);
			}
			
		}

		private void updateWordsList(String word) {
		    Document filterDoc = new Document()
		    		.append("name", word);
		    Document setWord = new Document()
		    		.append("name", word);
		    Document updateWord = new Document()
		    		.append("$set", setWord);

		    UpdateOptions upOptions = new UpdateOptions();
		    upOptions.upsert(true); 
		    upOptions.bypassDocumentValidation(true); 


		    wordDocuments.add(new UpdateOneModel<Document>(filterDoc, updateWord, upOptions));
		   System.out.println("word added. size of wordDocuments is now: " + wordDocuments.size());
		}
		    
		private void bulkInsert(List<WriteModel<Document>> docs, MongoCollection<Document> collection) {

		    BulkWriteOptions bulkWriteOptions = new BulkWriteOptions();
		    bulkWriteOptions.ordered(false);
		    bulkWriteOptions.bypassDocumentValidation(true);
		    
		    com.mongodb.bulk.BulkWriteResult bulkWriteResult = null;
		    try {
		        bulkWriteResult = collection.bulkWrite(docs, bulkWriteOptions);
		    } catch (BulkWriteException e) {
		        List<com.mongodb.BulkWriteError> bulkWriteErrors = e.getWriteErrors();
		        for (com.mongodb.BulkWriteError bulkWriteError : bulkWriteErrors) {
		            int failedIndex = bulkWriteError.getIndex();
		            System.out.println("Failed record: " + failedIndex);
		        }
		    }
		
		}

	public void randomSentence() {
        Document projectFields = new Document("_id", 0);
        projectFields.put("text.Sentence", 1);

		AggregateIterable<Document> aggregate = booksCollection.aggregate(
	            Arrays.asList(
	                    new Document("$unwind", "$text"),
	                    new Document("$match", new Document("text.SentenceNumber", 1010)),
	                    new Document("$project", new Document(projectFields)
	                    )));

		for (Document doc : aggregate) {
			//System.out.println("Aggregate: " + aggregate.toString());
			BsonDocument mybsondoc = doc.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry());
			String response = mybsondoc.toString();
			String[] responseSplit = response.split(":");
			//System.out.println(responseSplit[2]);
			
		}
	}
	public String retrieveSentence(String loc) {
		String sentence = "";
		int senLoc = Integer.valueOf(loc);
		
        Document projectFields = new Document("_id", 0);
        projectFields.put("text.Sentence", 1);

		AggregateIterable<Document> aggregate = booksCollection.aggregate(
	            Arrays.asList(
	                    new Document("$unwind", "$text"),
	                    new Document("$match", new Document("text.SentenceNumber", senLoc)),
	                    new Document("$project", new Document(projectFields)
	                    )));
		
		for (Document doc : aggregate) {
			// Convert to BsonDocument to use its toStirng to create string for splitting.
			//System.out.println("Aggregate: " + aggregate.toString());
			BsonDocument mybsondoc = doc.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry());
			String response = mybsondoc.toString();
			String[] responseSplit = response.split(":");
			sentence = responseSplit[2];
		}
		return sentence;
	}
}

class Word {
	String name;
	Occurrence occurrence;
	
	Word(String name, String title, Integer total, Integer location) {
		this.name = name;
		occurrence = new Occurrence(title, total, location);

	}
}

class Occurrence {
	String title;
	Integer total;
	String locations = "";
	
	Occurrence(String title, Integer total, Integer location) {
		this.title = title;
		this.total = total;
		this.locations += location.toString() + "-";
	}
}

class Result {
	String titleAuthor = "";
	ArrayList<String> quotes = new ArrayList<>();
}
