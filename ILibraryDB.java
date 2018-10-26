
public interface ILibraryDB {

	public boolean createLibrary(String name);
	public boolean insert(Book book);
	public String[] findSentences(String[] words);
	
}
