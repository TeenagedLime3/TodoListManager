import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    List<ToDoList> toDoLists = new ArrayList<>();

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Main main = new Main();
    }

    public Main() {
        while(true){
            try{
                int option = Integer.parseInt(scanner.nextLine());

                switch(option){
                    case 1:
                        createTodoList();
                    case 2:
                        viewTodoList();
                    case 3:
                        editTodoList();
                }
            } catch(NumberFormatException e){
                System.out.println("Please enter a number");
            }
        }
    }

    public void createTodoList(){

    }

    public void viewTodoList(){

    }

    public void editTodoList(){

    }
}