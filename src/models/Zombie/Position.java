package models.Zombie;

public class Position {
    private String initialId;
    private int x;
    private int y;

    public Position(String id, int x, int y) {
        this.initialId = id;
        this.x = x;
        this.y = y;
    }

    // >>>>>>>>>>>>>>>>>>>>>>>>>> Setter
    
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }   

    // <<<<<<<<<<<<<<<<<<<<<<<<<< Getter

    public String getId() {
        return this.initialId;

    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
