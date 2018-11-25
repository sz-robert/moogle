import java.util.ArrayList;
import java.util.Hashtable;
import java.util.UUID;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Indexer {
	
	private MongoClient serverConnection = new MongoClient("localhost", 27017);
	private MongoDatabase db = serverConnection.getDatabase("library");
	
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
	
	private MongoCollection<Document> wordsCollection = db.getCollection("wordsM2");
	private MongoCollection<Document> booksCollection = db.getCollection("booksM2");

	private Hashtable<String, Word> wordStatistics = new Hashtable<>();
	
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
