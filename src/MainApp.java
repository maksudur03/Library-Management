import java.util.Scanner;

public class MainApp {

    public static void main(String[] args) {
        MainApp mainApp = new MainApp();
        mainApp.execute();
    }

    private Library library;
    private InputController inputController;
    private boolean state = true;

    private MainApp() {
        library = new Library();
        inputController = new InputController();
    }

    public Library getLibrary() {
        return library;
    }

    private void execute() {
        handleMenu("4");
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

    private String showSubMenu() {
        String input;
        System.out.println("Press 4 to go to menu\nPress 0 to Exit");
        input = getStringInput();
        return input;
    }

    private void handleMenu(String input) {
        while (state) {
            switch (input) {
                case "1":
                    addBook();
                    input = showSubMenu();
                    break;
                case "2":
                    seeBookList();
                    input = showSubMenuOfBookList();
                    break;
                case "3":
                    removeBook();
                    break;
                case "4":
                    showMenu();
                    input = getStringInput();
                    break;
                case "0":
                    state = false;
                    break;
            }
        }

    }

    private void addBook() {
        Book bookToBeAdded = inputController.takingInputForBook(library.getBookList());
        library.getBookList().add(bookToBeAdded);
        System.out.println(bookToBeAdded.showBook());
    }

    private void removeBook() {

    }

    private void searchBook() {

    }

    private void seeBookList() {
        System.out.println("Showing list of books\n");
        for (Book book : library.getBookList()) { // for-each, for-loop. while-loop ? less priority
            System.out.println(book.showBookSummary());
        }
    }


    private static long getLongInput() {
        try {
            return new Scanner(System.in).nextLong();

        }catch (Exception ex){

        }

        return 4;
    }

    private static String getStringInput() {
        return new Scanner(System.in).next();
    }

    private String showSubMenuOfBookList() {
        String input;
        System.out.println("Press 6 to read a book\nPress 4 to go to menu\nPress 0 to Exit");
        input = getStringInput();
        if (input.equals("6")) {
              return readBookMenu();

        } else {
            return input;
        }
    }

    private String readBookMenu(){
        System.out.println("Press 1 to search by Id\nPress 2 to search by Name");
        String inputOfReadBook=getStringInput();
        switch (inputOfReadBook){
            case  "1":
                System.out.println("Type id of the book :");
                System.out.println(library.searchById(getLongInput()).showBook());
                break;
            case "2":
                System.out.println("Type name of the book");
                break;
            default:
                System.out.println("Invalid input");
                return "6";
        }
    }

}
