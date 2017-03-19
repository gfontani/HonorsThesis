package honorsthesis.gabriella.honorsthesis.BackEnd;

import java.lang.*;

/**
 * Created by Gabriella on 11/3/2016.
 */
public class Step {
    private String name;
    private Priority priority;
    private Process parent;

    public Step(String name, Priority priority){
        this.name = name;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Process getParent() {
        return parent;
    }

    public void setParent(Process parent) {
        this.parent = parent;
    }
}
