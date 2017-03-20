package honorsthesis.gabriella.honorsthesis.BackEnd;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Gabriella on 11/2/2016.
 */
public class Task implements Parcelable{
    private String name;
    private String notes;
    private Priority priority;
    private Date date;
    private Task parent;
    private List<Task> children;

    public Task(String name, String notes, Priority priority, Date date){
        this.name = name;
        this.notes = notes;
        this.priority = priority;
        this.date = date;
        this.parent = null;
        this.children = new ArrayList<Task>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Task getParent() {
        return parent;
    }

    public void setParent(Task parent) {
        this.parent = parent;
    }

    public List<Task> getChildren() {
        return children;
    }

    public void setChildren(List<Task> children) {
        this.children = children;
    }

    public void addChild(Task child){
        children.add(child);
    }

    public boolean removeChild(Task child) {
        if (children.contains(child)) {
            children.remove(child);
            return true;
        }else{
            return false;
        }
    }

    public boolean reorderChild(Task child, int newLocation){
        //TODO: check if this actually does what I want it to do
        if(children.contains(child)){
            children.remove(child);
            children.add(newLocation, child);
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(notes);
        dest.writeString(priority.toString());
        dest.writeLong(date.getTime());
        dest.writeParcelable(parent, flags);
        dest.writeList(children);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Task(Parcel in) {
        name = in.readString();
        notes = in.readString();
        priority = Priority.valueOf(in.readString());
        date = new Date(in.readLong());
        parent = in.readParcelable(Task.class.getClassLoader());
        children = new ArrayList<Task>();
        in.readList(children, Task.class.getClassLoader());
    }
}
