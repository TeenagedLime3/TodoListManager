import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    /*cursor parking spot
    |  |  |
     */

    private static final List<String> BLOCKED_WORDS = List.of("complete", "completed", "incomplete", "exit", "index");

    private final List<TodoList> toDoLists = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    public static final Path indexFile = Path.of("To Do Lists/index.csv");


    public static void main(String[] args) {
        new Main();
    }

    //shows the main menu and lets the user select how they want to edit the To-Do lists
    public Main() {
        long start = System.currentTimeMillis();
        System.out.println("Imported " + importTodoLists() + " To-Do lists from file");
        sortList();
        while (true) {
            System.out.println(System.currentTimeMillis() - start + "ms");
            try {
                System.out.println("""
                        MAIN MENU
                        Enter 1 to create a new To-Do list
                        Enter 2 to view an existing To-Do list
                        Enter 3 to edit an existing To-Do list""");

                int option = Integer.parseInt(scanner.nextLine());

                switch (option) {
                    case 1:
                        TodoList createdTodoList = createTodoList(null, AnsiColor.valueOf("DEFAULT"), false);
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
        System.out.println(AnsiColor.RED.getCode() + text + AnsiColor.RESET.getCode());
    }

    public static boolean isInt(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    public void sortList(){
        List<TodoList> newTodoLists = new ArrayList<>();
        for(TodoList todoList : toDoLists){
            if(todoList.isPinned()){
                newTodoLists.add(todoList);
            }
        }

        for(TodoList todoList : toDoLists){
            if(!todoList.isPinned()){
                newTodoLists.add(todoList);
            }
        }

        toDoLists.clear();
        toDoLists.addAll(newTodoLists);

    }

    // CREATING AND MANAGING To-Do LISTS

    public boolean isValidName(String name){
        if (BLOCKED_WORDS.contains(name.toLowerCase()) || name.contains("/") || name.contains("\\")) {
            printError("You cannot name a To-Do list complete, completed, incomplete, exit or index, or contain slashes of any kind");
            return false;
        }

        //checks if the file can be made without causing errors, characters such as " cannot be in a file name in windows
        try{
            Path ignored = Path.of("To Do Lists/" + name + ".csv");
        } catch (InvalidPathException e){
            printError("This name is invalid, please use another name");
            return false;
        }


        if (isInt(name)) {
            printError("Please enter a string, not a number");
            return false;
        }

        return true;
    }

    public TodoList createTodoList(String name, AnsiColor color, Boolean pinned){
        boolean isAutomated = name != null;
        //name being not null means its reading it from the file and running the method passing in the name from the file
        //when running from user input the value is null as the program asks for the name inside the method not outside

        if(!isAutomated){
            System.out.println("What is the name of this To-Do list? Enter an empty string to exit");
            name = scanner.nextLine();
            if (name.isEmpty()) {
                return null;
            }
        }

        for(TodoList toDoList : toDoLists){
            if(toDoList.getName().equalsIgnoreCase(name)){
                printError("A To-Do list with this name already exists, please enter a new name\n");
                return createTodoList(null, color, pinned);
            }
        }

        if(isValidName(name) && !name.contains("/") && !name.contains("\\")){
            TodoList toDoList = new TodoList(name, color, pinned);
            toDoLists.add(toDoList);
            if(!isAutomated){
                saveAllTodoLists();
            }
            return toDoList;
        } else {
            return createTodoList(null, AnsiColor.DEFAULT, pinned);
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

        toDoList.printItemsInTable();

    }

    public Boolean hasPinnedTodoList(){
        for(TodoList todoList : toDoLists){
            if(todoList.isPinned()){
                return true;
            }
        }
        return false;
    }

    public TodoList selectTodoList() {
        if (toDoLists.isEmpty()) {
            printError("No To-Do lists exist!");
            return null;
        }

        System.out.println();

        boolean hasPinned = hasPinnedTodoList();

        for (int i = 0; i < toDoLists.size(); i++) {
            TodoList list = toDoLists.get(i);
            if(list.isPinned()){
                System.out.print(AnsiColor.YELLOW.getCode() + "⚠ URGENT " + AnsiColor.RESET.getCode());
            }

            System.out.println((!list.isPinned() && hasPinned ? " ".repeat("⚠ URGENT ".length()) : "") +
                    "To-Do List " + (i + 1) + //To-Do List (i)
                    " ".repeat((String.valueOf(toDoLists.size())).length() - String.valueOf(i + 1).length()) + //[spaces before arrow]
                    " -> " + toDoLists.get(i).getColor().getCode() + toDoLists.get(i).getName() + AnsiColor.RESET.getCode()); // -> [list name]
        }

        System.out.println("\nPlease enter the number of the To-Do list you wish to view/edit/delete, or enter 0 to exit");

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

            if (index > toDoLists.size() - 1 || index < 0) {
                printError("Please enter a number between 1 and " + (toDoLists.size()));
                return selectTodoList();
            }

            return toDoLists.get(index);
        } catch (NumberFormatException ignored) {
            printError("Please enter a number");
            return selectTodoList();
        }
    }

    // EDITING To-Do LISTS

    public void editTodoList(TodoList toDoList) {
        toDoList.printItemsInTable();

        System.out.println();

        System.out.println("EDIT TO-DO LIST" + " ".repeat(14) +  "EDIT ITEMS");
        System.out.println("0 - exit" + " ".repeat(20) + "5 - add an item to the To-Do list");
        System.out.println("1 - delete the To-Do list" + " ".repeat(3) + (!toDoList.getList().isEmpty() ? "6 - remove items from the To-Do list" : ""));
        System.out.println("2 - edit To-Do list colour" + " ".repeat(2) + (!toDoList.getList().isEmpty() ? "7 - edit an existing item in the To-Do list" : ""));
        System.out.println("3 - edit To-Do list name");
        System.out.println(toDoList.isPinned() ? "4 - unpin this To-Do list" : "4 - pin this To-Do list");

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
                System.out.println("Are you sure you wish to delete this To-Do list? Please enter the name of the To-Do list to confirm");
                String toDoListNameCheck = scanner.nextLine();
                if(toDoList.getName().equals(toDoListNameCheck)){
                    toDoLists.remove(toDoList);
                }
                saveAllTodoLists();
                break;
            case 2:
                setTodolistColor(toDoList);
                break;
            case 3:
                changeTodoListName(toDoList);
                break;
            case 4:
                toDoList.setPinned(!toDoList.isPinned()); //set pinned to the opposite of whatever it is
                saveAllTodoLists();
                break;
            case 5:

                System.out.println("Please enter the name of the new item, or an empty string to exit");
                String name = scanner.nextLine();

                addItem(toDoList, name, false);
                saveAllTodoLists();
                editTodoList(toDoList);
                break;

            case 6:

                if (!toDoList.getList().isEmpty()) {
                    removeItem(toDoList);
                    saveAllTodoLists();
                }
                editTodoList(toDoList);
                break;

            case 7:
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
                printError("Please enter a number between 1 and 3");
                editTodoList(toDoList);
                break;
        }
    }

    public List<TodoList> existsOnAnotherTodoList(String name) {
        List<TodoList> matchingTodoLists = new ArrayList<>();
        for (TodoList toDoList : toDoLists) {
            if (toDoList.doesItemExist(name)) {
                matchingTodoLists.add(toDoList);
            }
        }
        return matchingTodoLists;
    }

    public void setTodolistColor(TodoList toDoList){
        for(AnsiColor color : AnsiColor.values()){
            if(color == AnsiColor.RESET || color == AnsiColor.DEFAULT){
                continue;
            }
            System.out.print(color.getCode() + color.name() + AnsiColor.RESET.getCode() + " ");
        }

        System.out.println("\nPlease enter a color, enter empty for default");
        String color = scanner.nextLine();

        if(color.isEmpty()){
            return;
        }

        try {
            AnsiColor ansiColor = AnsiColor.valueOf(color.toUpperCase().trim());
            if(ansiColor == AnsiColor.RESET || ansiColor == AnsiColor.DEFAULT){
                throw new IllegalArgumentException();
            }

            toDoList.setColor(ansiColor);
            saveAllTodoLists();
        } catch (IllegalArgumentException e){
            setTodolistColor(toDoList);
        }
    }

    public void changeTodoListName(TodoList toDoList){
        System.out.println("Please enter the new name for the To-Do list, enter empty to go back");
        String newName = scanner.nextLine();
        if(newName.isEmpty()){
            return;
        }
        if(!isValidName(newName)){
            changeTodoListName(toDoList);
            return;
        }

        toDoList.setName(newName);
        saveAllTodoLists();
    }

    public void addItem(TodoList toDoList, String name, boolean completion) {
        if (name.isEmpty()) {
            return;
        }

        if (isInt(name)) {
            printError("An item cannot be called just an integer!");
            editTodoList(toDoList);
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
                printError("Please enter a number between 1 and " + toDoList.getList().size() + "\n");
                removeItem(toDoList);
                saveAllTodoLists();
                return;
            }
            toDoList.removeItem(Integer.parseInt(input)); //minuses one in the To-Do List class so not needed here
            saveAllTodoLists();
        } else {
            if (!toDoList.removeItem(input)) {
                printError("An item with this name does not exist, try again");

                List<TodoList> found = existsOnAnotherTodoList(input);
                if (!found.isEmpty()) {
                    printError("This item exists in other To-Do list(s) named:");
                    for (TodoList toDoListChecking : toDoLists) {
                        for (Item item : toDoListChecking.getList()) {
                            if (item.getName().equals(input)) {
                                System.out.println(toDoListChecking.getName());
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
                printError("Please enter a number between 1 and " + toDoList.getList().size() + "\n");
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
        System.out.println("How do you wish to edit " + item.getName() + "?");
        System.out.println("""
                0 - back
                1 - change the name
                2 - change the completion
                """);

        int option;
        try {
            option = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            printError("Please enter a number");
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
            printError("Please do not name the item only a number");
            changeItemName(item, toDoList);
        } catch (NumberFormatException e) {
            if (BLOCKED_WORDS.contains(newName.toLowerCase())) {
                printError("\nThe name cannot contain those words, please try again with a different name\n");
                changeItemName(item, toDoList);
                return;
            }
            if (toDoList.doesItemExist(newName)) {
                printError("This name already exists, try again with a different name");
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
            printError("Please enter completed or incomplete");
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

        for(int i = 1; i < lines.size(); i++){ //i = 1 to skip the first line as it's the heading of the table
            String[] seperatedList = lines.get(i).split(",");
            String fileName = seperatedList[0];
            String color = seperatedList[1];
            Boolean pinned = Boolean.parseBoolean(seperatedList[2]);
            TodoList toDoList = createTodoList(fileName, AnsiColor.valueOf(color), pinned);

            Path file = Path.of("To Do Lists/" + fileName + ".csv");

            if(readTodoList(toDoList, file)){
                totalImportedLists++;
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
            Files.writeString(indexFile, "Todo List Name, Colour, Pinned");
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
            Files.writeString(indexFile,"Todo List Name, Colour, Pinned");
        } catch (IOException e){
            printError("Error writing to file\n" + e.getMessage());
        }

        deleteAllCSV(Path.of("To Do Lists/"));

        for(TodoList toDoList : toDoLists){
            saveTodoListToFile(toDoList);
        }
    }

    public void deleteAllCSV(Path path){
        try {
            for (Path file : Files.list(path).toList()) { //.list lists files in a path, .toList converts this to a
                if(file.getFileName().toString().equals("index.csv")){ //ignore deleting the index.csv
                    continue;
                }
                Files.delete(file);
            }
        } catch (IOException exception){
            printError(exception.getMessage());
        }
    }

    public void saveTodoListToFile(TodoList toDoList){
        Path toDoListFile = Path.of("To Do Lists/" + toDoList.getName() + ".csv"); //create To-Do list file

        try{
            //adding the To-Do list to the index
            Files.writeString(indexFile, "\n" + toDoList.getName() + "," + toDoList.getColor() + "," + toDoList.isPinned(), StandardOpenOption.APPEND); // StandardOpenOption.APPEND means the file is appended rather than overwritten
            //create the csv for the To-Do list
            Files.writeString(toDoListFile, "Name,Completion");
        } catch (IOException e){
            printError("Error writing to file\n" + e.getMessage());
        }

        for(Item item : toDoList.getList()){
            try{
                Files.writeString(toDoListFile, "\n" + item.getName() + "," + item.isCompleted(), StandardOpenOption.APPEND);
            } catch (IOException e){
                printError("Error writing to file\n" + e.getMessage());
            }
        }
    }
}