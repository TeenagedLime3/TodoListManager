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
                System.out.flush();
                System.err.println("Please enter a number");
            }
        }
    }

    public ToDoList createTodoList(){
        System.out.println("What is the name of this To-Do list? Enter an empty name to exit");
        String name = scanner.nextLine();
        try{ //test if the string can be converted to an int
            Integer.parseInt(name);
            return null;
        } catch(NumberFormatException e) {

        }


        if(name.isEmpty()){
             return null;
        }

        if(BLOCKED_WORDS.contains(name)){
            System.out.flush();
            System.err.println("You cannot name a To-Do list 'completed' or 'incomplete'");
            createTodoList();
            return null;
        }
        ToDoList toDoList = new ToDoList(name);
        toDoLists.add(toDoList);

        return toDoList;
    }

    public void viewTodoList(ToDoList toDoList){

        if(toDoList == null){
            return;
        }
        if(toDoList.getList().isEmpty()){
            System.out.flush();
            System.err.println("This To-Do list is empty!");
            selectTodoList();
            return;
        }

        printItemsInTable(toDoList);

    }

    public ToDoList selectTodoList(){
        if (toDoLists.isEmpty()){
            System.out.flush();
            System.err.println("No To-Do lists exist!");
            return null;
        }

        System.out.println();
        for (int i = 0; i < toDoLists.size(); i++){
            System.out.println("To-Do List " + (i + 1) + " -> " + toDoLists.get(i).getName());
        }

        System.out.println("\nPlease enter the number of the To-Do list you wish to edit, or enter 0 to exit");

        String input = scanner.nextLine();
        if(input.contains("0x")){
            System.out.flush();
            System.err.println("Stop being a smarty pants!");
            return null;
        }

        int index = Integer.parseInt(input) - 1;
        if(index == -1){
            return null;
        }

        if (index > toDoLists.size() - 1 || index < 0) {
            System.out.flush();
            System.err.println("Please enter a number between 1 and " + (toDoLists.size()));
            selectTodoList();
            return null;
        }

        return toDoLists.get(index);
    }

    public void printItemsInTable(ToDoList toDoList){
        if(!toDoList.getList().isEmpty()){
            int longestItemSize = toDoList.longestItem();
            List<Item> toDoListList = toDoList.getList(); //TODO CHANGE VARIABLE NAME

            //TODO possible to have the same line print headings for both before if statement?
            //TODO centering the index with numbers greater than 1 digit
            if(longestItemSize > "Item name    ".length()) {
                System.out.println("Index    Item name" + " ".repeat(longestItemSize - "Item name".length() + 4) + "Item Completion"); //+4 is the gap between the two headings
                for (int i = 0; i < toDoListList.size(); i++) {
                    System.out.println("  " + (i + 1) + "      " + toDoListList.get(i).getName() + " ".repeat(4) + toDoListList.get(i).isCompleted());
                }
                /*for (Item item : toDoList.getList()) {
                    System.out.println(item.getName() + " ".repeat(4) + item.isCompleted());
                }*/

            } else {
                System.out.println("Index    Item name    Item Completion");

                for (int i = 0; i < toDoListList.size(); i++) {
                    System.out.println("  " + (i + 1) + "      " + toDoListList.get(i).getName() + " ".repeat(13 - toDoListList.get(i).getName().length()) + toDoListList.get(i).isCompleted());
                }

               /* for (Item item : toDoList.getList()) {
                    System.out.println(item.getName() + " ".repeat(13 - item.getName().length()) + item.isCompleted());
                }*/
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
                 3 - edit an existing item in the To-Do list
                 4 - batch edit items from a To-Do list""");

        int option;
        try{
            option = Integer.parseInt(scanner.nextLine());
        } catch(NumberFormatException ignored){
            System.out.flush();
            System.err.println("Please enter a number");
            editTodoList(toDoList);
            return;
        }

        switch (option){
            case 0:
                return;
            case 1:
                addItem(toDoList, "");
                editTodoList(toDoList);
                break;
            case 2:
                removeItem(toDoList,"");
                editTodoList(toDoList);
                break;
            case 3:
                Item foundItem;

                //Exception in thread "main" java.lang.NullPointerException: Cannot invoke "Item.getName()" because "item" is null
                //	at Main.editItem(Main.java:282)
                //	at Main.editTodoList(Main.java:204)
                //	at Main.editTodoList(Main.java:197)
                //	at Main.<init>(Main.java:40)
                //	at Main.main(Main.java:17)
                // when findCorrectItem is null (if an item by the name entered does not exist)
                if(findCorrectItem(toDoList) == null){
                    findCorrectItem(toDoList);
                }
                editItem(findCorrectItem(toDoList)); //findCorrectItem returns the item to be edited which editItem uses
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


    public void addItem(ToDoList toDoList, String name){
        System.out.println("Please enter the name of the new item");
        name = scanner.nextLine();

        if(toDoList.doesItemExist(name)){
            System.out.flush();
            System.err.println("An item with this name already exists!");
            addItem(toDoList, name);
            return;
        }

        if(BLOCKED_WORDS.contains(name)){
            System.out.flush();
            System.err.println("\nThe name cannot contain those words, please try again with a different name :)\n");
            return;
        }

        Item item = new Item(name, false);
        toDoList.addItem(item);
        System.out.println("\nItem added!\n");
    }

    public void removeItem(ToDoList toDoList, String name){
        System.out.println("Please enter the name/index of the item to be removed item");
        name = scanner.nextLine();

        if(!toDoList.removeItem(name)){ //TODO fix this so it works with index too
            System.out.println("an item with this name does not exist, try again");

            List<ToDoList> found = existsOnAnotherTodoList(name);
            if(!found.isEmpty()) {
                System.out.println("This item exists in other To-Do list(s) named:");
            }
        }
    }

    public Item findCorrectItem(ToDoList toDoList){
        System.out.println("Please enter the index/name of the item you wish to edit");
        viewTodoList(toDoList);
        String input = scanner.nextLine();
        if(!toDoList.doesItemExist(input)){
            System.out.println("An item by that name does not exist, please try again or use the item index instead");
            findCorrectItem(toDoList);
            //return null; this breaks, can do without?
        }
        try{
            int index = Integer.parseInt(input);
            return toDoList.getList().get(index - 1); //the table starts at 1, 0 is the first item in the list :)
        } catch (NumberFormatException e){
            String name = input;
            for(Item item : toDoList.getList()){
                if(item.getName().equals(name)){
                    return item;
                }
            }
        }
        return null; //should never be called as code checks if an item by the entered name exists
    }

    public void editItem(Item item){

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
            editItem(item);
            return;
        }

        switch (option){
            case 0:
                return;
            case 1:
                changeItemName(item);
                break;
            case 2:
                changeItemCompletion(item);
                break;

        }
    }
    
    public void changeItemName(Item item){
        System.out.println("Please enter the new name of the item:");
        String newName = scanner.nextLine();
        try{ //test if the string can be converted to an int
            Integer.parseInt(newName);
            System.out.println("Please do not name the item only a number");
            changeItemName(item);
            return; //might not need?
        } catch(NumberFormatException e) {
            item.setName(newName);
            return;
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
            changeItemName(item);
        }
    }
}