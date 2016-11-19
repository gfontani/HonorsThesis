package honorsthesis.gabriella.taskapplication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Gabriella on 11/2/2016.
 */
public class Task {
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
        //TODO: add steps to database under process
    }

    public boolean removeChild(Task child) {
        if (children.contains(child)) {
            children.remove(child);
            return true;
            //TODO: modify the db
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
}
