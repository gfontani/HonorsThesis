package honorsthesis.gabriella.taskapplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriella on 11/3/2016.
 */
public class ThesisList {
    private String name = "Name";
    private List<Task> tasks = new ArrayList<Task>();
    private List<Process> processes = new ArrayList<Process>();

    public String getName(){
        return name;
    }

    public List<Task> getTasks(){
        return tasks;
    }
}
