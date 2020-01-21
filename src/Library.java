import java.util.ArrayList;

public class Library {
    private ArrayList<Book> bookList;

    public Library() {
        this.bookList = new ArrayList<>();
    }
    public void addBook(Book book){

    }
    public void removeBook(long bookId){

    }
    public Book searchById(long bookId){
        return null;
    }
    public ArrayList<Book> searchByName(String bookName){
        return null;
    }

    public ArrayList<Book> getBookList() {
        return bookList;
    }
}
