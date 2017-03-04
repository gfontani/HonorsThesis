package honorsthesis.gabriella.honorsthesis;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriella on 11/3/2016.
 */
public class Process {
    private String name;
    private String notes;
    private List<Step> steps;

    public Process(String name){
        this.name = name;
        this.notes = "";
        this.steps = new ArrayList<Step>();
        //Add process to the database
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

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public void addStep(Step step){
        steps.add(step);
        //TODO: add steps to database under process
    }

    public boolean removeStep(Step step) {
        if (steps.contains(step)) {
            steps.remove(step);
            return true;
            //TODO: modify the database
        }else{
            return false;
        }
    }

    public boolean reorderStep(Step step, int newLocation){
        //TODO: check if this actually does what I want it to do
        if(steps.contains(step)){
            steps.remove(step);
            steps.add(newLocation, step);

            //TODO: modify the database
            return true;
        }
        else{
            return false;
        }
    }
}
