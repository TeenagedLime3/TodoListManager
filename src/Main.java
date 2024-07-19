import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    /*cursor parking spot
    |  |  |
     */

    private static final List<String> BLOCKED_WORDS = List.of("complete", "completed", "incomplete", "exit");

    private final List<TodoList> todoLists = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[91m";

    public static final Path indexFile = Path.of("To Do Lists/index.csv");

    public static void main(String[] args) {
        Main main = new Main();
    }


    //shows the main menu and lets the user select how they want to edit the To-Do lists
    public Main() {
        System.out.println("Imported " + importTodoLists() + " To-Do lists from file");
        while (true) {
            try {
                System.out.println("""
                                                
                        MAIN MENU
                        Enter 1 to create a new To-Do list
                        Enter 2 to view an existing To-Do list
                        Enter 3 to edit an existing To-Do list""");

                int option = Integer.parseInt(scanner.nextLine());

                switch (option) {
                    case 1:
                        TodoList createdTodoList = createTodoList(null);
                        if(createdTodoList == null){
                            break;
                        }
                        editTodoList(createdTodoList);

                        break;
                    case 2:
                        viewTodoList(selectTodoList());
                        break;
                    case 3:
                        TodoList toDoList = selectTodoList();
                        if (toDoList == null) {
                            break;
                        }
                        editTodoList(toDoList);
                        break;
                }
            } catch (NumberFormatException ignored) {
                printError("Please enter a number");
            }
        }
    }

    // UTILITY METHODS

    public static void printError(String text) {
        System.out.println(ANSI_RED + text + ANSI_RESET);
    }

    public static boolean isInt(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    // CREATING AND MANAGING To-Do LISTS

    public boolean isValidName(String name){
        if (BLOCKED_WORDS.contains(name.toLowerCase())) {
            printError("You cannot name a To-Do list 'completed' or 'incomplete'");
            return false;
        }

        if (isInt(name)) {
            printError("Please enter a string, not a number");
            return false;
        }

        return true;
    }
    public TodoList createTodoList(String name){
        if(name == null){
            System.out.println("What is the name of this To-Do list? Enter an empty string to exit");
            name = scanner.nextLine();
        }

        if (name.isEmpty()) {
            return null;
        }

        if(isValidName(name)){
            TodoList toDoList = new TodoList(name);
            todoLists.add(toDoList);
            return toDoList;
        } else {
            return createTodoList(null);
        }
    }

    public void viewTodoList(TodoList toDoList) {
        if (toDoList == null) {
            return;
        }
        if (toDoList.getList().isEmpty()) {
            printError("This To-Do list is empty!");
            viewTodoList(selectTodoList());
            return;
        }

        printItemsInTable(toDoList);

    }

    public TodoList selectTodoList() {
        if (todoLists.isEmpty()) {
            printError("No To-Do lists exist!");
            return null;
        }

        System.out.println();
        for (int i = 0; i < todoLists.size(); i++) {
            System.out.println("To-Do List " + (i + 1) + " -> " + todoLists.get(i).getName());
        }

        System.out.println("\nPlease enter the number of the To-Do list you wish to edit, or enter 0 to exit");

        String input = scanner.nextLine();
        if (input.contains("0x")) {
            printError("Stop being a smarty pants!");
            return selectTodoList();
        }

        try { //keep try catch as it is being converted to an int, not checking for variable type like isInt does
            int index = Integer.parseInt(input) - 1;
            if (index == -1) {
                return null;
            }

            if (index > todoLists.size() - 1 || index < 0) {
                printError("Please enter a number between 1 and " + (todoLists.size()));
                return selectTodoList();
            }

            return todoLists.get(index);
        } catch (NumberFormatException ignored) {
            System.out.println("Please enter a number");
            return selectTodoList();
        }
    }

    public void printItemsInTable(TodoList toDoList) {
        if (!toDoList.getList().isEmpty()) {
            System.out.println();

            int longestItemSize = toDoList.longestItem();
            List<Item> toDoListList = toDoList.getList(); //TODO CHANGE VARIABLE NAME

            //TODO possible to have the same line print headings for both before if statement?
            //CHECK NEXT COMMIT - this can lead to easy implementation of lines above and below the table
            //TODO centering the index with numbers greater than 1 digit
            if (longestItemSize > "Item name    ".length()) {
                String heading = "Index    Item name" + " ".repeat(longestItemSize - "Item name".length() + 4) + "Item Completion"; //+4 is the gap between the two heading elements

                System.out.println(toDoList.getName());
                System.out.println("_".repeat(heading.length()));

                System.out.println(heading);

                for (int i = 0; i < toDoListList.size(); i++) {
                    System.out.println("  " + (i + 1) + "      " + toDoListList.get(i).getName() + " ".repeat(4) + toDoListList.get(i).isCompleted());
                }

            } else {
                String heading = "Index    Item name    Item Completion";
                System.out.println(toDoList.getName());
                System.out.println("_".repeat(heading.length()));

                System.out.println(heading);

                for (int i = 0; i < toDoListList.size(); i++) {
                    System.out.println("  " + (i + 1) + "      " + toDoListList.get(i).getName() + " ".repeat(13 - toDoListList.get(i).getName().length()) + toDoListList.get(i).isCompleted());
                }
            }
        }
    }

    // EDITING To-Do LISTS

    public void editTodoList(TodoList toDoList) {
        printItemsInTable(toDoList);

        System.out.println();

        System.out.println("""
                EDIT TODO LIST
                0 - exit
                1 - add an item to the To-Do list""");

        if (!toDoList.getList().isEmpty()) {
            System.out.println("""
                    2 - remove an item from the To-Do list
                    3 - edit an existing item in the To-Do list""");
        }

        int option;
        try {
            option = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException ignored) {
            printError("Please enter a number");
            editTodoList(toDoList);
            return;
        }

        switch (option) {
            case 0:
                return;
            case 1:

                System.out.println("Please enter the name of the new item, or an empty string to exit");
                String name = scanner.nextLine();

                addItem(toDoList, name, false);
                saveAllTodoLists();
                editTodoList(toDoList);
                break;
            case 2:
                if (!toDoList.getList().isEmpty()) {
                    removeItem(toDoList);
                    saveAllTodoLists();
                }
                editTodoList(toDoList);
                break;
            case 3:
                if (!toDoList.getList().isEmpty()) {
                    Item foundItem;

                    while (true) {
                        foundItem = findCorrectItem(toDoList);
                        if (foundItem == null) {
                            continue;
                        }
                        break;

                    }

                    editItem(foundItem, toDoList);
                    saveAllTodoLists();

                } else {
                    editTodoList(toDoList);
                }
                break;
            default:
                System.out.println("Please enter a number between 1 and 3");
                editTodoList(toDoList);
                break;
        }
    }

    public List<TodoList> existsOnAnotherTodoList(String name) {
        List<TodoList> matchingTodoLists = new ArrayList<>();
        for (TodoList toDoList : todoLists) {
            if (toDoList.doesItemExist(name)) {
                matchingTodoLists.add(toDoList);
            }
        }

        return matchingTodoLists;
    }


    public void addItem(TodoList toDoList, String name, boolean completion) {
        if (name.isEmpty()) {
            return;
        }

        if (isInt(name)) {
            printError("An item cannot be called just an integer!");
            addItem(toDoList, name, completion);
            return;
        }

        if (toDoList.doesItemExist(name)) {
            printError("An item with this name already exists!");
            addItem(toDoList, name, completion);
            return;
        }

        if (BLOCKED_WORDS.contains(name)) {
            printError("\nThe name cannot contain those words, please try again with a different name\n");
            addItem(toDoList, name, completion);
            return;
        }

        Item item = new Item(name, completion);
        toDoList.addItem(item);
        saveAllTodoLists();
    }

    public void removeItem(TodoList toDoList) {
        if (toDoList.getList().isEmpty()) {
            printError("This To-Do list is empty!");
            return;
        }

        System.out.println("Please enter the name/index of the item to be removed item, or an empty string to exit");
        String input = scanner.nextLine();

        if (input.isEmpty()) {
            return;
        }


        if (isInt(input)) {
            if (Integer.parseInt(input) > toDoList.getList().size() || Integer.parseInt(input) <= 0) {
                printError("please enter a number between 1 and " + toDoList.getList().size() + "\n");
                removeItem(toDoList);
                saveAllTodoLists();
                return;
            }
            toDoList.removeItem(Integer.parseInt(input)); //minuses one in the To-Do List class so not needed here
            saveAllTodoLists();
        } else {
            if (!toDoList.removeItem(input)) { //TODO fix this so it works with index too
                printError("an item with this name does not exist, try again");

                List<TodoList> found = existsOnAnotherTodoList(input);
                if (!found.isEmpty()) {
                    System.out.println("This item exists in other To-Do list(s) named:");
                    for (TodoList todoListChecking : todoLists) {
                        for (Item item : todoListChecking.getList()) {
                            if (item.getName().equals(input)) {
                                System.out.println(todoListChecking.getName());
                            }
                        }
                    }
                }
            }
        }
    }

    public Item findCorrectItem(TodoList toDoList) {
        System.out.println("Please enter the index/name of the item you wish to edit");
        viewTodoList(toDoList);
        String input = scanner.nextLine();

        try {
            int index = Integer.parseInt(input);

            if (index > toDoList.getList().size() || Integer.parseInt(input) <= 0) {
                printError("please enter a number between 1 and " + toDoList.getList().size() + "\n");
                return findCorrectItem(toDoList);
            }
            return toDoList.getList().get(index - 1); //the table starts at 1, 0 is the first item in the list :)
        } catch (NumberFormatException e) {
            if (!toDoList.doesItemExist(input)) {
                return findCorrectItem(toDoList);
            }
            for (Item item : toDoList.getList()) {
                if (item.getName().equals(input)) {
                    return item;
                }
            }
        }
        return null; //should never be called as code checks if an item by the entered name exists
    }

    // EDITING ITEMS

    public void editItem(Item item, TodoList toDoList) {
        System.out.println("how do you wish to edit " + item.getName() + "?");
        System.out.println("""
                0 - back
                1 - change the name
                2 - change the completion
                """);

        int option;
        try {
            option = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a number");
            editItem(item, toDoList);
            saveAllTodoLists();
            return;
        }

        switch (option) {
            case 0:
                editTodoList(toDoList);
                return;
            case 1:
                changeItemName(item, toDoList);
                editTodoList(toDoList);
                break;
            case 2:
                changeItemCompletion(item);
                editTodoList(toDoList);
                return;
            default:
                editItem(item, toDoList);
        }
    }

    public void changeItemName(Item item, TodoList toDoList) {
        System.out.println("Please enter the new name of the item:");
        String newName = scanner.nextLine();
        try { //test if the string can be converted to an int
            Integer.parseInt(newName);
            System.out.println("Please do not name the item only a number");
            changeItemName(item, toDoList);
        } catch (NumberFormatException e) {
            if (BLOCKED_WORDS.contains(newName.toLowerCase())) {
                printError("\nThe name cannot contain those words, please try again with a different name\n");
                changeItemName(item, toDoList);
                return;
            }
            if (toDoList.doesItemExist(newName)) {
                System.out.println("This name already exists, try again with a different name");
                changeItemName(item, toDoList);
                return;
            }
            item.setName(newName);
            saveAllTodoLists();
        }
    }

    public void changeItemCompletion(Item item) {
        System.out.println("Please enter whether the item is completed or not");
        String itemCompletion = scanner.nextLine().toLowerCase();

        if (itemCompletion.equals("completed") ||
                itemCompletion.equals("done") ||
                itemCompletion.equals("true")) {
            item.setCompleted(true);

        } else if (itemCompletion.equals("incomplete") ||
                itemCompletion.equals("not completed") ||
                itemCompletion.equals("not complete") ||
                itemCompletion.equals("false")) {
            item.setCompleted(false);

        } else {
            System.out.println("Please enter completed or incomplete");
            changeItemCompletion(item);
        }
        saveAllTodoLists();
    }

    //To-Do LIST READING FROM FILE

    public int importTodoLists(){

        int totalImportedLists = 0;
        // Files.exists
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(indexFile);
        } catch(IOException e) {
            if(!createIndexFile()){
                System.exit(1);
            }
        }

        if(lines.size() <= 1){
            return totalImportedLists;
        }

        List<String> listsNotRead = new ArrayList<>();
        for(int i = 1; i < lines.size(); i++){ //i = 1 to skip the first line as it's the heading of the table
            String[] seperatedList = lines.get(i).split(",");
            String fileName = seperatedList[0];
            TodoList toDoList = createTodoList(fileName);

            Path file = Path.of("To Do Lists/" + fileName + ".csv");

            if(readTodoList(toDoList, file)){
                totalImportedLists++;
            } else {
                listsNotRead.add(fileName);
            }
        }
        return totalImportedLists;
    }

    public boolean readTodoList(TodoList toDoList, Path path){
        List<String> lines;

        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            return false;
        } catch (SecurityException e){
            printError("This folder does not have read access. Please install to a different location.\n" + e.getMessage());
            return false;
        }
        for(int i = 1; i < lines.size(); i++){
            String[] seperatedTodo = lines.get(i).split(",");
            addItem(toDoList, seperatedTodo[0], Boolean.parseBoolean(seperatedTodo[1]));
        }
        return true;
    }

    public boolean createIndexFile(){
        try{
            Files.writeString(indexFile, "Todo List Name, Path, Colour,TBC: Category, Urgency");
            return true;
        } catch (IOException e){
            printError("An I/ O error occurred writing to or creating the file, or the text cannot be encoded using UTF-8\n" + e.getMessage());
            return false;
        } catch (SecurityException e){
            printError("This folder does not have write access. Please install to a different location.\n" + e.getMessage());
            return false;
        }
    }

    //To-Do LIST SAVING TO FILE

    public void saveAllTodoLists(){
        try{
            Files.writeString(indexFile,"Todo List Name, Colour, TBC: Category, Urgency");
        } catch (IOException e){
            printError("Error writing to file\n" + e.getMessage());
        }

        for(TodoList todoList : todoLists){
            saveTodoListToFile(todoList);
        }
    }

    public void saveTodoListToFile(TodoList toDoList){
        Path todoListFile = Path.of("To Do Lists/" + toDoList.getName() + ".csv"); //create To-Do list file

        //adding the To-Do list to the index

        try{
            Files.writeString(indexFile, "\n" + toDoList.getName(), StandardOpenOption.APPEND); // StandardOpenOption.APPEND means the file is appended rather than overwritten
            Files.writeString(todoListFile, "Name,Completion");
            //TODO add color here when To-Do list color has been implemented
        } catch (IOException e){
            printError("Error writing to file\n" + e.getMessage());
        }


        for(Item item : toDoList.getList()){
            try{
                Files.writeString(todoListFile, "\n" + item.getName() + "," + item.isCompleted(), StandardOpenOption.APPEND);
            } catch (IOException e){
                printError("Error writing to file\n" + e.getMessage());
            }
        }
    }
}