package honorsthesis.gabriella.honorsthesis.BackEnd;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import honorsthesis.gabriella.honorsthesis.DataRepo.DataRepo;

/**
 * Created by Gabriella on 11/3/2016.
 */
public class Process implements Parcelable{
    private String name;
    private String notes;



    private String parentList;
    private List<Step> steps;

    public Process(String name){
        this.name = name;
        this.notes = "";
        this.parentList = "";
        this.steps = new ArrayList<Step>();
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

    public String getParentList() {
        return parentList;
    }

    public void setParentList(String parentList) {
        this.parentList = parentList;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public void addStep(Step step){
        steps.add(step);
    }

    public boolean removeStep(Step step) {
        if (steps.contains(step)) {
            steps.remove(step);
            return true;
        }else{
            return false;
        }
    }

    public boolean reorderStep(Step step, int newLocation, DataRepo dataRepo, String listName){
        //TODO: check if this actually does what I want it to do
        if(steps.contains(step)){
            steps.remove(step);
            steps.add(newLocation, step);
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
        dest.writeString(parentList);
        dest.writeList(steps);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Process> CREATOR = new Parcelable.Creator<Process>() {
        public Process createFromParcel(Parcel in) {
            return new Process(in);
        }

        public Process[] newArray(int size) {
            return new Process[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Process(Parcel in) {
        name = in.readString();
        notes = in.readString();
        parentList = in.readString();
        steps = new ArrayList<Step>();
        in.readList(steps, Step.class.getClassLoader());
    }
}
