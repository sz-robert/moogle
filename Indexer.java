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
import java.util.UUID;
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
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
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

public class Indexer {
	
	MongoClient serverConnection = new MongoClient("localhost", 27017);
	MongoDatabase db = serverConnection.getDatabase("library");
	
/*
	MongoClient mongo = new MongoClient( "ec2-34-222-109-70.us-west-2.compute.amazonaws.com" , 9999 );
	MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder()
	.connectTimeout(3000)
	.socketTimeout(3000);
	String password = "password123";  
	ServerAddress get_to_em = new ServerAddress("ec2-54-185-61-81.us-west-2.compute.amazonaws.com" , 9999);
	MongoCredential credential = MongoCredential.createCredential("terminator", "t1000", password.toCharArray());
	MongoDatabase db = mongo.getDatabase("library");
	*/
	
	MongoCollection<Document> wordsCollection = db.getCollection("wordsM2");
	MongoCollection<Document> booksCollection = db.getCollection("booksM2");

	Hashtable<String, Word> wordStatistics = new Hashtable<>();
	
	public Indexer () {}

	public void insertRemote(String book_title, String book_author, String[] book_senteces2) {
		insert(book_title, book_author, "unzipped", book_senteces2);
	}
	
	public void insert(String book_title, String book_author, String unzipped, String[] book_sentences) {
		String uniqueId = UUID.randomUUID().toString();
		int sentencePosition = 1;
		Document bookDocument = new Document("bookId", uniqueId)
				.append("title", book_title)
                .append("author", book_author);
                //.append("date", book_year)
                for(String sentence : book_sentences) {
                	bookDocument.append("sentence-" + String.valueOf(sentencePosition), sentence);
                	System.out.println("Inserted: " + sentencePosition + " sentence for: " + book_title );
                	String[] words = sentence.split(" "); 
                	for(String word : words) {
                		if(!(word.length() < 1) && (word != "") && (!word.isEmpty()) && (word != null) && (word.charAt(0) != '$')) {
                			updateWordStatistics(word, sentencePosition);
                		}
                	}
                	sentencePosition++;
                }
        booksCollection.insertOne(bookDocument);
        System.out.println("Inserted: "+ book_title );
        for(String key : wordStatistics.keySet()) {
    		Word currentWord = wordStatistics.get(key);
    		System.out.println("Inserting: " );
    		Document wordDocument = new Document("word", currentWord.getName())
    		.append("bookId", uniqueId)
			.append("title", book_title)
            .append("author", book_author)
            .append("totalOccurrences", currentWord.getTotal())
            .append("locations", currentWord.getLocations());
            wordsCollection.insertOne(wordDocument);
            System.out.println("Inserted: "+ currentWord.getName() + " for " +  book_title);
    	}
                //.append("versions", Arrays.asList("v3.2", "v3.0", "v2.6")) // might use for locations later
	}
	
	private void updateWordStatistics(String word, int sentencePosition) {
		System.out.println("Attempting to insert word from updateWords: " + word);
		if (wordStatistics.containsKey(word)) {
			Word existingWord = wordStatistics.get(word);
			//existingWord.setName(word);
			existingWord.incrementTotal();
			existingWord.appendLocations(String.valueOf(sentencePosition));
		}
		else {
			Word newWord = new Word();
			newWord.setName(word);
			newWord.incrementTotal();
			newWord.appendLocations(String.valueOf(sentencePosition));
			wordStatistics.put(word, newWord);
		}
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

class Result {
	String titleAuthor = "";
	ArrayList<String> quotes = new ArrayList<>();
}
