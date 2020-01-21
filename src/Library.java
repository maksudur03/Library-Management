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
        for (Book book : bookList) {
            if (bookId == book.getId()) {
                return book;
            }
        }
        System.out.println("Book not found");
        return null;
    }
    public ArrayList<Book> searchByName(String bookName){
        return null;
    }

    public ArrayList<Book> getBookList() {
        return bookList;
    }
}
