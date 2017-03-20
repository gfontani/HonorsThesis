package honorsthesis.gabriella.honorsthesis.BackEnd;

import java.lang.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriella on 11/3/2016.
 */
public class ThesisList {
    private String name;
    public List<Task> tasks;
    public List<Process> processes;

    public ThesisList(String name){
        this.name = name;
        this.tasks = new ArrayList<Task>();
        this.processes = new ArrayList<Process>();
    }

    public String getName(){
        return name;
    }

    public List<Task> getTasks(){
        return tasks;
    }

    public List<Process> getProcesses() { return processes; }
}
