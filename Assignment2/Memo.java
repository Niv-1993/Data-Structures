public class Memo {
    private int x;
    private int z;
    private int y;
    // each usage of this class is used differently in every class with the x,y,z and explained there.

    public Memo(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public int getX(){
        return x;
    }
    public int getZ(){
        return z;
    }
    public int getY(){
        return y;
    }
    public void setX(int newX){
        this.x = newX;
    }
    public void setY(int newY){
        this.y = newY;
    }
    public void setZ(int newZ){
        this.z = newZ;
    }
}
