import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    /*cursor parking spot
    |  |  |
     */

    private static final List<String> BLOCKED_WORDS = List.of("completed", "incomplete", "exit");

    private final List<ToDoList> toDoLists = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Main main = new Main();
    }


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
                System.err.println("Please enter a number");
            }
        }
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
        System.out.println("What is the name of this To-Do list? Enter an empty name to exit");
        String name = scanner.nextLine();
        if(!isInt(name)){
            if(name.isEmpty()){
                return null;
            }

            if(BLOCKED_WORDS.contains(name)){
                System.err.println("You cannot name a To-Do list 'completed' or 'incomplete'");
                createTodoList();
                return null;
            }
            ToDoList toDoList = new ToDoList(name);
            toDoLists.add(toDoList);

            return toDoList;
        } else {
            System.out.println("Please enter a string, not a number");
            createTodoList();
            return null;
        }
    }

    public void viewTodoList(ToDoList toDoList){
        if(toDoList == null){
            return;
        }
        if(toDoList.getList().isEmpty()){
            System.err.println("This To-Do list is empty!");
            selectTodoList();
            return;
        }

        printItemsInTable(toDoList);

    }

    public ToDoList selectTodoList(){
        if (toDoLists.isEmpty()){
            System.err.println("No To-Do lists exist!");
            return selectTodoList();
        }

        System.out.println();
        for (int i = 0; i < toDoLists.size(); i++){
            System.out.println("To-Do List " + (i + 1) + " -> " + toDoLists.get(i).getName());
        }

        System.out.println("\nPlease enter the number of the To-Do list you wish to edit, or enter 0 to exit");

        String input = scanner.nextLine();
        if(input.contains("0x")){
            System.err.println("Stop being a smarty pants!");
            return selectTodoList();
        }

        try { //keep try catch as it is being converted to an int, not checking for variable type like isInt does
            int index = Integer.parseInt(input) - 1;
            if (index == -1) {
                return selectTodoList();
            }

            if (index > toDoLists.size() - 1 || index < 0) {
                System.err.println("Please enter a number between 1 and " + (toDoLists.size()));
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
                 1 - add an item to the To-Do list
                 2 - remove an item from the To-Do list
                 3 - edit an existing item in the To-Do list""");

        int option;
        try{
            option = Integer.parseInt(scanner.nextLine());
        } catch(NumberFormatException ignored){
            System.err.println("Please enter a number");
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
                removeItem(toDoList);
                editTodoList(toDoList);
                break;
            case 3:
                Item foundItem;

                while(true){
                    foundItem = findCorrectItem(toDoList);
                    if(foundItem == null){
                        continue;
                    }
                    break;

                }
                editItem(foundItem, toDoList);
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
        System.out.println("Please enter the name of the new item, or empty to exit");
        String name = scanner.nextLine();

        if(name.isEmpty()){
            return;
        }

        if(toDoList.doesItemExist(name)){
            System.err.println("An item with this name already exists!");
            addItem(toDoList);
            return;
        }

        if(BLOCKED_WORDS.contains(name)){
            System.err.println("\nThe name cannot contain those words, please try again with a different name :)\n");
            addItem(toDoList);
            return;
        }

        Item item = new Item(name, false);
        toDoList.addItem(item);
        System.out.println("\nItem added!");
    }

    public void removeItem(ToDoList toDoList){
        System.out.println("Please enter the name/index of the item to be removed item, or empty to exit");
        String input = scanner.nextLine();

        if(input.isEmpty()){
            return;
        }

        if(isInt(input)){
            toDoList.removeItem(Integer.parseInt(input)); //minuses one in the ToDoList class so not needed here
        } else {
            if(!toDoList.removeItem(input)){ //TODO fix this so it works with index too
                System.out.println("an item with this name does not exist, try again");

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

            if(index > toDoList.getList().size()){
                return null;
            }
            return toDoList.getList().get(index - 1); //the table starts at 1, 0 is the first item in the list :)
        } catch (NumberFormatException e){
            if(!toDoList.doesItemExist(input)){
                findCorrectItem(toDoList);
                return null;
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
                return;
            case 1:
                changeItemName(item);
                editTodoList(toDoList);
                break;
            case 2:
                changeItemCompletion(item);
                editTodoList(toDoList);
        }
    }
    
    public void changeItemName(Item item){
        System.out.println("Please enter the new name of the item:");
        String newName = scanner.nextLine();
        try{ //test if the string can be converted to an int
            Integer.parseInt(newName);
            System.out.println("Please do not name the item only a number");
            changeItemName(item);
        } catch(NumberFormatException e) {
            item.setName(newName);
        }
    }
    //TODO AI suggested fix for this bad practice
//    public void changeItemName(Item item) {
//        String newName;
//        boolean isValidName;
//        do {
//            System.out.println("Please enter the new name of the item:");
//            newName = scanner.nextLine();
//            isValidName = true;
//
//            try {
//                Integer.parseInt(newName);
//                System.out.println("Please do not name the item only a number");
//                isValidName = false;
//            } catch (NumberFormatException e) {
//                // If an exception is thrown, it means the name is not a number, which is valid
//            }
//        } while (!isValidName);
//
//        item.setName(newName);
//    }

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
            changeItemName(item);
        }
    }
}