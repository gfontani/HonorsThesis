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
    private String parentTask;
    private String parentList;
    private List<Task> children;

    public Task(String name){
        this.name = name;
        this.notes = "";
        this.priority = null;
        this.date = null;
        this.parentTask = null;
        this.children = new ArrayList<Task>();
    }

    public Task(String name, String notes, Priority priority, Date date){
        this.name = name;
        this.notes = notes;
        this.priority = priority;
        this.date = date;
        this.parentTask = null;
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

    public String getParentTask() {
        return parentTask;
    }

    public void setParentTask(String parentTask) {
        this.parentTask = parentTask;
    }

    public String getParentList() {
        return parentList;
    }

    public void setParentList(String parentList) {
        this.parentList = parentList;
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
        if(null != name){
            dest.writeString(name);
        }
        if(null != notes){
            dest.writeString(notes);
        }else{
            dest.writeString("");
        }
        if(null != priority){
            dest.writeString(priority.toString());
        }else{
            dest.writeString("");
        }
        if(null != date){
            dest.writeLong(date.getTime());
        }else {
            dest.writeLong(-1);
        }
        if(null != parentTask){
            dest.writeString(parentTask);
        }else{
            dest.writeString("");
        }
        if(null != parentList){
            dest.writeString(parentList);
        }else{
            dest.writeString("");
        }
        if(null != children){
            dest.writeList(children);
        }
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
        String priorityString = in.readString();
        if(null != priorityString && !priorityString.isEmpty()){
            priority = Priority.valueOf(priorityString);
        }
        Long dateLong = in.readLong();
        if(dateLong != -1){
            date = new Date(dateLong);
        }
        parentTask = in.readString();
        if(parentTask.isEmpty()){
            parentTask = null;
        }
        parentList = in.readString();
        if(parentList.isEmpty()){
            parentList = null;
        }
        children = new ArrayList<Task>();
        in.readList(children, Task.class.getClassLoader());
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Task){
            Task object = (Task) o;
            if(object.getName().equalsIgnoreCase(this.getName())){
                return true;
            }
        }
        return false;
    }
}
