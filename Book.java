import java.sql.Date;

public class Book {
	
	String location;
	String title;
	String author;
	Date publicationDate;
	String[] sentences;
	
	public Book(String location, String title, String author, Date publicationDate, String[] sentences) {
		this.location = location;
		this.title = title;
		this.author = author;
		this.publicationDate = publicationDate;
		this.sentences = sentences;
	}

}
