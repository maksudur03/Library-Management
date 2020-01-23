import java.util.ArrayList;
import java.util.ListIterator;

public class Library {
    private ArrayList<Book> bookList;


    public Library() {
        this.bookList = new ArrayList<>();
    }

    public void addBook(Book book) {
        getBookList().add(book);
    }

    public void removeBook(long bookId) {
        ListIterator<Book> itr = getBookList().listIterator();
        while (itr.hasNext()) {
            if (itr.next().getId() == bookId)
                itr.remove();
        }
    }

    public Book searchById(long bookId) {
        for (Book book : bookList) {
            if (bookId == book.getId()) {
                return book;
            }
        }
        return null;
    }

    public ArrayList<Book> searchByName(String bookName) {
        ArrayList<Book> sameBooks= new ArrayList<>();
        for(Book book : bookList){
            if(bookName.equals(book.getName())){
                sameBooks.add(book);
            }
        }
        return sameBooks;
    }

    public ArrayList<Book> getBookList() {
        return bookList;
    }
}
