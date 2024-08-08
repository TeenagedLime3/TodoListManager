import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TodoList extends ArrayList<Item> {
    private String name;
    private AnsiColor color = AnsiColor.DEFAULT;
    private boolean pinned = false;
    private static final List<String> BLOCKED_WORDS = List.of("complete", "completed", "incomplete", "exit", "index");


    //constructor
    public TodoList(String name){
        this.name = name;
    }

    //getters
    public String getName(){
        return this.name;
    }

    public AnsiColor getColor(){
        return this.color;
    }

    public boolean isPinned(){
        return this.pinned;
    }

    public void printItemsInTable() {
        if (isEmpty())
            return;

        System.out.println();

        int longestItemSize = longestItem();
        //TODO centering the index with numbers greater than 1 digit
        if (longestItemSize > "Item name    ".length()) {
            String heading = "Index    Item name" + " ".repeat(longestItemSize - "Item name".length() + 4) + "Item Completion"; //+4 is the gap between the two heading elements

            System.out.println(getName());
            System.out.println("_".repeat(heading.length()));

            System.out.println(heading);

            for (int i = 0; i < size(); i++) {                              //THSP = THIN SPACE (HALF A NORMAL SPACE)
                System.out.println(((String.valueOf(i+1).length() > 1) ? "  " : "  ") + (i + 1) + "      " + get(i).getName() + " ".repeat(4) + get(i).isCompleted());
            }

        } else {
            String heading = "Index    Item name    Item Completion";
            System.out.println(getName());
            System.out.println("_".repeat(heading.length()));

            System.out.println(heading);

            for (int i = 0; i < size(); i++) {                              //THSP = THIN SPACE (HALF A NORMAL SPACE)
                System.out.println(((String.valueOf(i+1).length() > 1) ? "  " : "  ") + (i + 1) + "      " + get(i).getName() + " ".repeat(13 - get(i).getName().length()) + get(i).isCompleted());
            }
        }
    }

    public static boolean isValidName(String name){
        if (BLOCKED_WORDS.contains(name.toLowerCase()) || name.contains("/") || name.contains("\\")) {
            Main.printError("You cannot name a To-Do list complete, completed, incomplete, exit or index, or contain slashes of any kind");
            return false;
        }

        //checks if the file can be made without causing errors, characters such as " cannot be in a file name in windows
        try{
            Path ignored = Path.of("To Do Lists/" + name + ".csv");
        } catch (InvalidPathException e){
            Main.printError("This name is invalid, please use another name");
            return false;
        }

        if (Main.isInt(name)) {
            Main.printError("Please enter a string, not a number");
            return false;
        }

        return true;
    }

    //setters
    public void setName(String name){
        this.name = name;
    }

    public void setColor(AnsiColor color){
        this.color = color;
    }

    public void setPinned(boolean isPinned){
        this.pinned = isPinned;
    }

    public boolean doesItemExist(String name){
        for (Item item : this){
            if(item.getName().equals(name)){
                return true;
            }
        }

        return false;
    }

    public void addItem(Item item){
        add(item);
    }

    public void removeItem(Item item){
        remove(item);
    }

    //attempts to remove the item, if unsuccessful (i.e. the user enters an index that is higher than the max index
    public void removeItem(int index){
        remove(index - 1); //IMPORTANT: users enter 1 not 0 for the first element
    }

    //checks each item in the list, if it exists sets the flag itemExists to true and removes it
    //if the item doesn't exist (itemExists is false) the method returns false
    public boolean removeItem(String itemName){
        boolean itemExists = false;
        Item itemFound = null;
        for(Item item : this){
            if(item.getName().equals(itemName)){
                itemFound = item;
                itemExists = true;
                break;
            }
        }

        if(itemExists){
            removeItem(itemFound);
        }

        return itemExists;
    }

    public int longestItem(){
        int longestItem = 0;
        for (Item item : this){
            if(item.getName().length() > longestItem){
                longestItem = item.getName().length();
            }
        }
        return longestItem;
    }
}
