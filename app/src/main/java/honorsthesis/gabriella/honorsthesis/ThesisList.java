package honorsthesis.gabriella.honorsthesis;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriella on 11/3/2016.
 */
public class ThesisList {
    private String name;
    private List<Task> tasks;
    private List<Process> processes;

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
