public class rooms {
    private String name;
    public rooms(String roomname){
        this.name = roomname;
    }
    public String getName(){
        return name;
    }
    @Override
    public String toString(){
        return name;
    }
}
