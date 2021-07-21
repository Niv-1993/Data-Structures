import org.w3c.dom.Node;

public class Memo {
    private int currentIndex;
    private String value;
    private String preValue;
    private boolean inStash;

    public Memo(int currentIndex, String value, String preValue, boolean inStash){
        this.currentIndex=currentIndex;
        this.preValue = preValue;
        this.value = value;
        this.inStash = inStash;

    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public String getPreValue() {
        return preValue;
    }

    public String getValue() {
        return value;
    }

    public boolean isInStash() {
        return inStash;
    }

    public void setInStash(boolean inStash) {
        this.inStash = inStash;
    }
}

