import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class InputController {
    Scanner scanner = new Scanner(System.in);

    public Book takingInputForBook(ArrayList<Book> bookList) {
        Book book = new Book();
        long uncheckedId = 0;
        double uncheckedPrice = 0;
        String uncheckedAuthor;
        String uncheckedContent;

        boolean hasRepetition = false;
        System.out.println("ENTER BOOK ID");
        while (!hasRepetition) {
            uncheckedId =MainApp.getLongInput();
            hasRepetition = checkForRepetition(uncheckedId,bookList);
        }
        book.setId(uncheckedId);

        System.out.println("ENTER BOOK NAME");
        book.setName(scanner.next());

        System.out.println("ENTER BOOK AUTHOR");
        uncheckedAuthor = authorRangeCheck();
        book.setAuthor(uncheckedAuthor);

        System.out.println("ENTER BOOK CONTENT");
        uncheckedContent = contentRangeCheck();
        book.setContent(uncheckedContent);

        System.out.println("ENTER BOOK PRICE");
        uncheckedPrice = priceValidation();
        book.setPrice(uncheckedPrice);
        System.out.println();

        return book;
    }

    public String contentRangeCheck() {
        String content;
        while (true) {
            content = scanner.next();
            if (content.length() >= 10 && content.length() <= 25) {
                break;
            } else {
                if (content.length() < 10) {
                    System.out.println("Content is too small.Try Again");

                }
                if (content.length() > 25) {
                    System.out.println("Content is too large.Try Again");
                }
            }
        }
        return content;
    }

    public String authorRangeCheck(){
        String author;
        while (true) {
            author = scanner.next();
            if (author.length() >= 5 && author.length() <= 20) {
                break;
            } else {
                if (author.length() < 5) {
                    System.out.println("Author name is too small.Try Again");

                }
                if (author.length() > 20) {
                    System.out.println("Author name is too large.Try Again");
                }
            }
        }
        return author;
    }

    public boolean checkForRepetition(long id, ArrayList<Book> bookList) {
        for (Book book : bookList) {
            if (id == book.getId()) {
                System.out.println("This ID already exists.Give another ID");
                return false;
            }
        }
        return true;
    }

    public double priceValidation() {
        double input = 0;
        while (input == 0) {
            try {
                input = scanner.nextDouble();
            } catch (InputMismatchException exception) {
                System.out.println("Invalid input. Try again");
            }
            scanner.nextLine();
        }
        return input;
    }
}
