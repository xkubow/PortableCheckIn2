package cz.tsystems.data;

/**
 * Created by kubisj on 24.11.2014.
 */
public class DMBaseItem implements Comparable<DMBaseItem> {
    public long item_id;
    public String text;
    public boolean checked;

    @Override
    public int compareTo(DMBaseItem another) {
        return (this.item_id == another.item_id)?1:0;
    }
}
