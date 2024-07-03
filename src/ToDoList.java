import java.util.ArrayList;

public class ToDoList {
    private String name;
    private final ArrayList<Item> list = new ArrayList<>();
    private String color;

    //constructor
    public ToDoList(String name){
        this.name = name;
    }

    //getters
    public String getName(){
        return this.name;
    }

    public ArrayList<Item> getList(){
        return this.list;
    }

    public int getIndexFromName(String queryString){
        for(int i = 0; i < this.list.size(); i++){
            if(this.list.get(i).getName().equalsIgnoreCase(queryString)){
                return i;
            }
        }
        return -1;
    }

    //setters
    public void setName(String name){
        this.name = name;
    }

    public void setColor(String color){
        this.color = color;
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

    public boolean removeItem(Item item){
        return this.list.remove(item);
    }

    //attempts to remove the item, if unsuccessful (i.e. the user enters an index that is higher than the max index
    public boolean removeItem(int index){
        try{
            this.list.remove(index - 1); //IMPORTANT: users enter 1 not 0 for the first element
            return true;
        } catch (IndexOutOfBoundsException exception) { //if the user
            return false;
        }
    }

    //checks each item in the list, if it exists sets the flag itemExists to true and removes it
    //if the item doesn't exist (itemExists is false) the method returns false
    public boolean removeItem(String itemName){
        boolean itemExists = false;
        for(Item item : this.list){
            if(item.getName().equals(itemName)){
                this.removeItem(item);
                itemExists = true;
            }
        }
        return itemExists;
    }
}
