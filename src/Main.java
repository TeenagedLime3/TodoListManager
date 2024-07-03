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
                        viewTodoList();
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
            System.err.println("You cannot name a To-Do list 'completed' or 'incomplete'");
            createTodoList();
            return null;
        }
        ToDoList toDoList = new ToDoList(name);
        toDoLists.add(toDoList);

        return toDoList;
    }

    public void viewTodoList(){
        ToDoList toDoList = selectTodoList();
        if(toDoList == null){
            return;
        }
        if(toDoList.getList().isEmpty()){
            System.err.println("This To-Do list is empty!");
            viewTodoList();
            return;
        }
        for (Item item : toDoList.getList()) {
            System.out.println(item.getName() + "\t" + item.isCompleted());
        }
    }

    public ToDoList selectTodoList(){
        if (toDoLists.isEmpty()){
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
            System.err.println("Please enter a number between 1 and " + (toDoLists.size()));
            selectTodoList();
            return null;
        }

        return toDoLists.get(index);
    }

    public void printItemsInTable(ToDoList toDoList){
        int longestItemSize = toDoList.longestItem();
        if(longestItemSize > "Item name\t".length()) {
            System.out.println("Item name\t" + " ".repeat(longestItemSize - "Item name\t".length()) + "Item Completion");
        }
        for (Item item : toDoList.getList()) {
            System.out.println(item.getName() + "\t" + item.isCompleted());
        }
    }

    public void editTodoList(ToDoList toDoList){
        printItemsInTable(toDoList);

        System.out.println("");

        System.out.println("""
                 How do you wish to edit the To-Do list
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
                addItem(toDoList, "");
                editTodoList(toDoList);
                break;
            case 2:
                removeItem(toDoList,"");
                editTodoList(toDoList);
                break;

            //case 3:
            //    editItem();
            //    editTodoList(toDoList);

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
            System.err.println("An item with this name already exists!");
            addItem(toDoList, name);
            return;
        }

        //its kinda brokey

        if(BLOCKED_WORDS.contains(name)){
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

        if(!toDoList.removeItem(name)){
            System.out.println("an item with this name does not exist, try again");

            List<ToDoList> found = existsOnAnotherTodoList(name);
            if(!found.isEmpty()) {
                System.out.println("This item exists in other To-Do list(s) named:");
            }
        }
    }

    public void editItem(ToDoList toDoList ){
        System.out.println("Please enter the index of the item you wish to edit");


    }
}