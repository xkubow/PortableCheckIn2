package cz.tsystems.data;

/**
 * Created by kubisj on 24.11.2014.
 */
public abstract class DMBaseItem implements Comparable<DMBaseItem> {
    public abstract long get_id();
    public abstract void set_id(final long id);
    public abstract String getText();
    public abstract void setText(final String text);
    public abstract boolean getChecked();
    public abstract void setChecked(final boolean checked);

    @Override
    public int compareTo(DMBaseItem another) {
        return (this.get_id() == another.get_id())?1:0;
    }

}
