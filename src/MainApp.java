import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MainApp {

    private Library library;
    private boolean state = true;
    private InputController inputController;

    private MainApp() {
        library = new Library();
        inputController = new InputController();

    }

    public Library getLibrary() {
        return library;
    }

    private void execute() {
        showMenu();
        handleMenu(getIntInput());
    }

    private void showMenu() {
        // use StringBuilder -> read, ask question, understand then implement ?
        StringBuilder screen = new StringBuilder("Welcome to Library\n")
                .append("1.ADD BOOK\n")
                .append("2.SEE BOOK LIST\n")
                .append("3.REMOVE BOOK\n")
                .append("4.MENU\n0.EXIT\nPress the adjacent number");
        System.out.println(screen);
    }

    private int showSubMenu() {
        int input;
        System.out.println("Press 4 to go to menu\nPress 0 to Exit");
        input = getIntInput();
        return input;
    }

    private int showSubMenuOfBookList() {
        int input;
        System.out.println("Press 6 to read a book\nPress 4 to go to menu\nPress 0 to Exit");
        input = getIntInput();
        if (input == 6) {
            return readBookMenu();

        } else {
            return input;
        }
    }

    private int readBookMenu() {
        System.out.println("Press 1 to search by Id\nPress 2 to search by Name");
        int inputOfReadBook = getIntInput();
        switch (inputOfReadBook) {
            case 1:
                searchBookByIdOperation();
                break;
            case 2:
                searchBookByNameOperation();
                break;
            default:
                System.out.println("Invalid input");
        }
        return showSubMenu();
    }

    private void handleMenu(int input) {
        while (state) {
            switch (input) {
                case 1:
                    bookAddOperation();
                    input = showSubMenu();
                    break;
                case 2:
                    showBookList(library.getBookList());
                    input = showSubMenuOfBookList();
                    break;
                case 3:
                    bookRemoveOperation();
                    input = showSubMenu();
                    break;
                case 4:
                    showMenu();
                    input = getIntInput();
                    break;
                case 0:
                    state = false;
                    return;
                default:
                    input = showSubMenu();
            }
        }
    }

    private void bookAddOperation() {
        Book bookToBeAdded = inputController.takingInputForBook(getLibrary().getBookList());
        getLibrary().addBook(bookToBeAdded);
        System.out.println(bookToBeAdded.showBook());
        System.out.println("Press 1 to add another book");
    }

    private void bookRemoveOperation() {
        System.out.println("Type the ID of book you want to delete");
        library.removeBook(getLongInput());
        showBookList(library.getBookList());
    }

    private void bookRemoveAtList(Book book) {
        System.out.println("Press x to delete the book\nPress any button to continue");
        switch (getStringInput()) {
            case "x":
            case "X":
                getLibrary().removeBook(book.getId());
                break;
            default:
                break;
        }
    }
    private void bookRemoveAtList() {
        System.out.println("Press x to delete a book\nPress any button to continue");
        switch (getStringInput()) {
            case "x":
            case "X":
                bookRemoveOperation();
                break;
            default:
                break;
        }
    }

    private void showBookList(ArrayList<Book> bookList) {
        System.out.println("Showing list of books\n");
        for (Book book : bookList) { // for-each, for-loop. while-loop ? less priority
            System.out.println(book.showBookSummary());
        }
    }

    private void searchBookByIdOperation() {
        System.out.println("Type id of the book :");
        Book book = getLibrary().searchById(getLongInput());
        if (book == null) {
            System.out.println("Book not found\n");
        } else {
            System.out.println(book.showBook());
            bookRemoveAtList(book);
        }
    }

    private void searchBookByNameOperation() {
        System.out.println("Type name of the book");
        String bookName = getStringInput();
        if (getLibrary().searchByName(bookName).size() == 0) {
            System.out.println("Book not found\n");
        } else {
            showBookList(getLibrary().searchByName(bookName));
            bookRemoveAtList();
        }
    }

    public static int getIntInput() {
        Scanner scanner = new Scanner(System.in);
        int input = -1;
        while (input == -1) {
            try {
                input = scanner.nextInt();
            } catch (InputMismatchException exception) {
                System.out.println("Invalid input. Try again");
            }
            scanner.nextLine();
        }
        return input;
    }

    public long getLongInput() {
        Scanner scanner = new Scanner(System.in);
        long input = -1;
        while (input == -1) {
            try {
                input = scanner.nextLong();
            } catch (InputMismatchException exception) {
                System.out.println("Invalid input. Try again");
            }
            scanner.nextLine();
        }
        return input;
    }

    private static String getStringInput() {
        return new Scanner(System.in).next();
    }

    public static void main(String[] args) {
        MainApp mainApp = new MainApp();
        mainApp.execute();
    }
}
