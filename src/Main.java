import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    /*curser parking spot
    |  |  |
     */




    private final List<ToDoList> toDoLists = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Main main = new Main();
    }

    public Main() {
        boolean running = true;
        while(running){
            System.out.println("Enter 1 to create a new TODO list" +
                    "\nEnter 2 to view an existing TODO list" /* +
                             "\nEnter 3 to edit an existing TODO list" */);
            try{
                int option = Integer.parseInt(scanner.nextLine());

                switch(option){
                    case 1:
                        createTodoList();
                        break;
                    case 2:
                        viewTodoList();
                        break;
                    //case 3:
                    //    editTodoList();
                    //    break;
                }
            } catch(NumberFormatException ignored){
                System.err.println("Please enter a number");
            }
        }
    }

    public void createTodoList(){
        System.out.println("What is the name of this TODO list?");
        String name = scanner.nextLine();

        if(name.equalsIgnoreCase("completed") || name.equalsIgnoreCase("incomplete")){
            System.err.println("You cannot name a TODO list 'completed' or 'incomplete'");
            createTodoList();
            return;
        }

        toDoLists.add(new ToDoList(name));
    }

    public void viewTodoList(){

        if (toDoLists.isEmpty()){
            System.err.println("No TODO lists exist!");
            return;
        }

        System.out.println();
        for (int i = 0; i < toDoLists.size(); i++){
            System.out.println((i + 1) + " -> " + toDoLists.get(i).getName());
        }

        System.out.println("\nPlease enter the number of the TODO list you wish to open, or enter 0 to exit");
        int index = Integer.parseInt(scanner.nextLine()) - 1;

        if(index == -1){
            return;
        }

        if (index > toDoLists.size() - 1 || index < 0) {
            System.err.println("Please enter a number between 1 and " + (toDoLists.size()));
            viewTodoList();
            return;
        }

        //TODO make it so if the user enters it in hex they get told off for being too smart

        ToDoList toDoList = toDoLists.get(index);

        if (toDoList.getList().isEmpty()){
            System.err.println("This TODO list is empty!");
            viewTodoList();
            return;
        }

        for (Item item : toDoList.getList()) {
            System.out.printf(item.getName() + "\t" + item.isCompleted());
        }

    }

    public void editTodoList(){

    }
}