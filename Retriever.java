import java.util.ArrayList;
import java.util.Arrays;
import org.bson.Document;
import org.json.simple.JSONObject;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Retriever {
	public Retriever () {}
	
	private MongoClient serverConnection = new MongoClient("localhost", 27017);
	private MongoDatabase db = serverConnection.getDatabase("library");
	
/*	MongoClient mongo = new MongoClient( "...amazonaws.com" , 1234 );
	String password = ""; 
	ServerAddress get_to_em = new ServerAddress("...amazonaws.com" , 1234); 
	MongoCredential credential = MongoCredential.createCredential("....", "...", password.toCharArray());
	MongoDatabase db = mongo.getDatabase("library");*/
	
	private MongoCollection<Document> wordsCollection = db.getCollection("wordsM2");
	private MongoCollection<Document> booksCollection = db.getCollection("booksM2");

	
	public ArrayList<String> findSearchTerms(String text, String logicalOperator, String text2) {
		ArrayList<String> results = findWords(text);
		return results;
	}
	private ArrayList<String> findWords(String words) {
		ArrayList<String> resultsList = new ArrayList<>();
		String searchTerms[] = words.split(" ");
		String bookId = "";
		String totalOccurrences = "";
		String locations = "";
		
		for (String searchTerm : searchTerms) {
	        Document projectWordFields = new Document("_id", 0);
	        projectWordFields.put("bookId", 1);
	        projectWordFields.put("totalOccurrences", 1);
	        projectWordFields.put("locations", 1);

			AggregateIterable<Document> aggregateWords = wordsCollection.aggregate(
		            Arrays.asList(
		                    new Document("$match", new Document("word", searchTerm)),
		                    new Document ("$sort", new Document("totalOccurrences", -1)),
		                    //new Document("$limit", 15),
		                    //new Document("$skip", 10),
		                    new Document("$project", new Document(projectWordFields)
		                    )));
			for (Document doc : aggregateWords) {
				String[] jsonSplit = doc.toJson().split("[\\s,;\"]+");
				bookId = jsonSplit[3];
				totalOccurrences = jsonSplit[6];
				locations = jsonSplit[9];
				String[] locationsList = locations.split("-");
				
				//Get title and sentences from books collection
		        Document projectBookFields = new Document("_id", 0);
		        projectBookFields.put("title", 1);
		        projectBookFields.put("author", 1);
		        for(String location : locationsList) {
		        	projectBookFields.put("sentence-" + location, 1);
		        }
				AggregateIterable<Document> aggregateSentences = booksCollection.aggregate(
			            Arrays.asList(
			                    new Document("$match", new Document("bookId", bookId)),
			                    //new Document("$limit", 15),
			                    new Document("$project", new Document(projectBookFields)
			                    )));
				for (Document bookDoc : aggregateSentences) {
					JSONObject bookResult = new JSONObject(bookDoc);
					String bookTitle = (String) bookResult.get("title");
					String bookAuthor = (String) bookResult.get("author");
					String citation = bookTitle + ", " + bookAuthor + " (" + totalOccurrences + " occurrences)";
					resultsList.add(citation);
					int occurrencesCounter = 1;
					for (String location : locationsList) {
						String currentSentence = "sentence-" + location;
						String sentence = (String) bookResult.get(currentSentence);
						resultsList.add(occurrencesCounter + "          " + sentence);
						occurrencesCounter++;
					}
				}
			}
		}
		return resultsList;
	}
}
