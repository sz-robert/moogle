import java.util.Arrays;

import org.bson.BsonDocument;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Retriever {
	
	MongoClient serverConnection = new MongoClient("localhost", 27017);
	MongoDatabase db = serverConnection.getDatabase("library");
/*	MongoClient mongo = new MongoClient( "...amazonaws.com" , 1234 );
	String password = ""; 
	ServerAddress get_to_em = new ServerAddress("...amazonaws.com" , 1234); 
	MongoCredential credential = MongoCredential.createCredential("....", "...", password.toCharArray());
	MongoDatabase db = mongo.getDatabase("library");*/
	
	MongoCollection<Document> wordsCollection = db.getCollection("bulkwords1");
	MongoCollection<Document> booksCollection = db.getCollection("bulkbooks1");
	
		public String[] findSearchTerms(String text, String logicalOperator, String text2) {
		
			return findWord(text);
		}
		public String[] findWord(String words) {
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
		public String[] findSearchTerms(String text, String logicalOperator, String text2) {
			return findWord(text);
		}
		public String[] findWord(String words) {
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
