import java.util.ArrayList;
import java.util.List;

public class TodoList {
    private String name;
    private final ArrayList<Item> list = new ArrayList<>();
    private AnsiColor color;
    private boolean pinned;
    //constructor

    public TodoList(String name, AnsiColor color, Boolean pinned){
        this.name = name;
        this.color = color;
        this.pinned = pinned;
    }

    //getters
    public String getName(){
        return this.name;
    }

    public ArrayList<Item> getList(){
        return this.list;
    }

    public AnsiColor getColor(){
        return this.color;
    }

    public Boolean isPinned(){
        return this.pinned;
    }

    public void printItemsInTable() { // TODO: This method should be in the TodoList class
        if (!this.getList().isEmpty()) {
            System.out.println();

            int longestItemSize = this.longestItem();
            List<Item> itemList = this.getList();
            //TODO possible to have the same line print headings for both before if statement?
            //CHECK NEXT COMMIT - this can lead to easy implementation of lines above and below the table
            //TODO centering the index with numbers greater than 1 digit
            if (longestItemSize > "Item name    ".length()) {
                String heading = "Index    Item name" + " ".repeat(longestItemSize - "Item name".length() + 4) + "Item Completion"; //+4 is the gap between the two heading elements

                System.out.println(this.getName());
                System.out.println("_".repeat(heading.length()));

                System.out.println(heading);

                for (int i = 0; i < itemList.size(); i++) {
                    System.out.println("  " + (i + 1) + "      " + itemList.get(i).getName() + " ".repeat(4) + itemList.get(i).isCompleted());
                }

            } else {
                String heading = "Index    Item name    Item Completion";
                System.out.println(this.getName());
                System.out.println("_".repeat(heading.length()));

                System.out.println(heading);

                for (int i = 0; i < itemList.size(); i++) {
                    System.out.println("  " + (i + 1) + "      " + itemList.get(i).getName() + " ".repeat(13 - itemList.get(i).getName().length()) + itemList.get(i).isCompleted());
                }
            }
        }
    }

    //setters
    public void setName(String name){
        this.name = name;
    }

    public void setColor(AnsiColor color){
        this.color = color;
    }

    public void setPinned(Boolean isPinned){
        this.pinned = isPinned;
    }

    public boolean doesItemExist(String name){
        for (Item item : this.list){
            if(item.getName().equals(name)){
                return true;
            }
        }

        return false;
    }

    public void addItem(Item item){
        this.list.add(item);
    }

    public void removeItem(Item item){
        this.list.remove(item);
    }

    //attempts to remove the item, if unsuccessful (i.e. the user enters an index that is higher than the max index
    public void removeItem(int index){
        this.list.remove(index - 1); //IMPORTANT: users enter 1 not 0 for the first element
    }

    //checks each item in the list, if it exists sets the flag itemExists to true and removes it
    //if the item doesn't exist (itemExists is false) the method returns false
    public boolean removeItem(String itemName){
        boolean itemExists = false;
        Item itemFound = null;
        for(Item item : this.list){
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
        for (Item item : this.list){
            if(item.getName().length() > longestItem){
                longestItem = item.getName().length();
            }
        }
        return longestItem;
    }
}
