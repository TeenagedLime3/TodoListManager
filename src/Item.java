public class Item {
    private String name;
    private boolean completed;

    //constructor
    public Item(String name, boolean completed){
        this.name = name;
        this.completed = completed;
    }

    //getters
    public String getName(){
        return this.name;
    }

    public boolean isCompleted(){
        return this.completed;
    }

    //setters
    public void setName(String name){
        this.name = name;
    }

    public void setCompleted(boolean completed){
        this.completed = completed;
    }



}
