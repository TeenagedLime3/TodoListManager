import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    /*cursor parking spot
    |  |  |
     */

    private static final List<String> BLOCKED_WORDS = List.of("complete", "completed", "incomplete", "exit");

    private final List<ToDoList> toDoLists = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[91m";

    public static void main(String[] args) {
        Main main = new Main();
    }


    //shows the main menu and lets the user select how they want to edit the to-do lists
    public Main() {
        while(true){

            try{
                System.out.println("""
                        
                        MAIN MENU
                        Enter 1 to create a new To-Do list
                        Enter 2 to view an existing To-Do list
                        Enter 3 to edit an existing To-Do list""");

                int option = Integer.parseInt(scanner.nextLine());

                switch(option){
                    case 1:
                        ToDoList createdToDoList = createTodoList();
                        if(createdToDoList == null){
                            break;
                        }
                        editTodoList(createdToDoList);
                        break;
                    case 2:
                        viewTodoList(selectTodoList());
                        break;
                    case 3:
                        ToDoList toDoList = selectTodoList();
                        if (toDoList == null){
                            break;
                        }
                        editTodoList(toDoList);
                        break;
                }
            } catch(NumberFormatException ignored){
                printError("Please enter a number");
            }
        }
    }

    public static void printError(String text){
        System.out.println(ANSI_RED + text + ANSI_RESET);
    }

    public static boolean isInt(String input){
        try{
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException ignored){
            return false;
        }
    }


    public ToDoList createTodoList(){
        System.out.println("What is the name of this To-Do list? Enter an empty string to exit");
        String name = scanner.nextLine();
        if(!isInt(name)){
            if(name.isEmpty()){
                return null;
            }

            if(BLOCKED_WORDS.contains(name.toLowerCase())){
                printError("You cannot name a To-Do list 'completed' or 'incomplete'");
                return createTodoList();
            }
            ToDoList toDoList = new ToDoList(name);
            toDoLists.add(toDoList);

            return toDoList;
        } else {
            printError("Please enter a string, not a number");
            return createTodoList();
        }
    }

    public void viewTodoList(ToDoList toDoList){
        if(toDoList == null){
            return;
        }
        if(toDoList.getList().isEmpty()){
            printError("This To-Do list is empty!");
            viewTodoList(selectTodoList());
            return;
        }

        printItemsInTable(toDoList);

    }

    public ToDoList selectTodoList(){
        if (toDoLists.isEmpty()){
            printError("No To-Do lists exist!");
            return null;
        }

        System.out.println();
        for (int i = 0; i < toDoLists.size(); i++){
            System.out.println("To-Do List " + (i + 1) + " -> " + toDoLists.get(i).getName());
        }

        System.out.println("\nPlease enter the number of the To-Do list you wish to edit, or enter 0 to exit");

        String input = scanner.nextLine();
        if(input.contains("0x")){
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
        } catch (NumberFormatException ignored){
            System.out.println("Please enter a number");
            return selectTodoList();
        }
    }

    public void printItemsInTable(ToDoList toDoList){
        if(!toDoList.getList().isEmpty()){
            System.out.println();

            int longestItemSize = toDoList.longestItem();
            List<Item> toDoListList = toDoList.getList(); //TODO CHANGE VARIABLE NAME

            //TODO possible to have the same line print headings for both before if statement?
            //CHECK NEXT COMMIT - this can lead to easy implementation of lines above and below the table
            //TODO centering the index with numbers greater than 1 digit
            if(longestItemSize > "Item name    ".length()) {
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

    public void editTodoList(ToDoList toDoList){
        printItemsInTable(toDoList);

        System.out.println();

        System.out.println("""
                 EDIT TODO LIST
                 0 - exit
                 1 - add an item to the To-Do list""");

        if(!toDoList.getList().isEmpty()){
            System.out.println("""
                 2 - remove an item from the To-Do list
                 3 - edit an existing item in the To-Do list""");
        }

        int option;
        try{
            option = Integer.parseInt(scanner.nextLine());
        } catch(NumberFormatException ignored){
            printError("Please enter a number");
            editTodoList(toDoList);
            return;
        }

        switch (option){
            case 0:
                return;
            case 1:
                addItem(toDoList);
                editTodoList(toDoList);
                break;
            case 2:
                if(!toDoList.getList().isEmpty()){
                    removeItem(toDoList);
                }
                editTodoList(toDoList);
                break;
            case 3:
                if(!toDoList.getList().isEmpty()){
                    Item foundItem;

                    while(true){
                        foundItem = findCorrectItem(toDoList);
                        if(foundItem == null){
                            continue;
                        }
                        break;

                    }

                    editItem(foundItem, toDoList);
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

    public List<ToDoList> existsOnAnotherTodoList(String name){
        List <ToDoList> matchingTodoLists = new ArrayList<>();
        for(ToDoList toDoList : toDoLists){
            if(toDoList.doesItemExist(name)){
                matchingTodoLists.add(toDoList);
            }
        }

        return matchingTodoLists;
    }


    public void addItem(ToDoList toDoList){
        System.out.println("Please enter the name of the new item, or an empty string to exit");
        String name = scanner.nextLine();

        if(name.isEmpty()){
            return;
        }

        if(isInt(name)){
            printError("An item cannot be called just an integer!");
            addItem(toDoList);
            return;
        }

        if(toDoList.doesItemExist(name)){
            printError("An item with this name already exists!");
            addItem(toDoList);
            return;
        }

        if(BLOCKED_WORDS.contains(name)){
            printError("\nThe name cannot contain those words, please try again with a different name\n");
            addItem(toDoList);
            return;
        }

        Item item = new Item(name, false);
        toDoList.addItem(item);
        System.out.println("\nItem added!");
    }

    public void removeItem(ToDoList toDoList){
        if(toDoList.getList().isEmpty()){
            printError("This To-Do list is empty!");
            return;
        }

        System.out.println("Please enter the name/index of the item to be removed item, or an empty string to exit");
        String input = scanner.nextLine();

        if(input.isEmpty()){
            return;
        }



        if(isInt(input)){
            if(Integer.parseInt(input) > toDoList.getList().size() || Integer.parseInt(input) <= 0){
                printError("please enter a number between 1 and " + toDoList.getList().size() + "\n");
                removeItem(toDoList);
                return;
            }
            toDoList.removeItem(Integer.parseInt(input)); //minuses one in the ToDoList class so not needed here
        } else {
            if(!toDoList.removeItem(input)){ //TODO fix this so it works with index too
                printError("an item with this name does not exist, try again");

                List<ToDoList> found = existsOnAnotherTodoList(input);
                if(!found.isEmpty()) {
                    System.out.println("This item exists in other To-Do list(s) named:");
                    for(ToDoList toDoListChecking : toDoLists){
                        for(Item item : toDoListChecking.getList()){
                            if(item.getName().equals(input)){
                                System.out.println(toDoListChecking.getName());
                            }
                        }
                    }
                }
            }
        }
    }

    public Item findCorrectItem(ToDoList toDoList){
        System.out.println("Please enter the index/name of the item you wish to edit");
        viewTodoList(toDoList);
        String input = scanner.nextLine();

        try{
            int index = Integer.parseInt(input);

            if(index > toDoList.getList().size() || Integer.parseInt(input) <= 0){
                printError("please enter a number between 1 and " + toDoList.getList().size() + "\n");
                return findCorrectItem(toDoList);
            }
            return toDoList.getList().get(index - 1); //the table starts at 1, 0 is the first item in the list :)
        } catch (NumberFormatException e){
            if(!toDoList.doesItemExist(input)){
                return findCorrectItem(toDoList);
            }
            for(Item item : toDoList.getList()){
                if(item.getName().equals(input)){
                    return item;
                }
            }
        }
        return null; //should never be called as code checks if an item by the entered name exists
    }

    public void editItem(Item item, ToDoList toDoList){

        System.out.println("how do you wish to edit " + item.getName() + "?");
        System.out.println("""
                 0 - back
                 1 - change the name
                 2 - change the completion
                 """);

        int option;
        try{
            option = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e){
            System.out.println("Please enter a number");
            editItem(item, toDoList);
            return;
        }

        switch (option){
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
    
    public void changeItemName(Item item, ToDoList toDoList){
        System.out.println("Please enter the new name of the item:");
        String newName = scanner.nextLine();
        try{ //test if the string can be converted to an int
            Integer.parseInt(newName);
            System.out.println("Please do not name the item only a number");
            changeItemName(item, toDoList);
        } catch(NumberFormatException e) {
            if(BLOCKED_WORDS.contains(newName.toLowerCase())) {
                printError("\nThe name cannot contain those words, please try again with a different name\n");
                changeItemName(item, toDoList);
                return;
            }
            if(toDoList.doesItemExist(newName)){
                System.out.println("This name already exists, try again with a different name");
                changeItemName(item, toDoList);
                return;
            }
            item.setName(newName);
        }
    }

    public void changeItemCompletion(Item item){
        System.out.println("Please enter whether the item is completed or not");
        String itemCompletion = scanner.nextLine().toLowerCase();

        if (itemCompletion.equals("completed") ||
        itemCompletion.equals("done") ||
        itemCompletion.equals("true")){
            item.setCompleted(true);

        } else if (itemCompletion.equals("incomplete") ||
        itemCompletion.equals("not completed") ||
        itemCompletion.equals("not complete") ||
        itemCompletion.equals("false")){
            item.setCompleted(false);

        } else {
            System.out.println("Please enter completed or incomplete");
            changeItemCompletion(item);
        }
    }
}