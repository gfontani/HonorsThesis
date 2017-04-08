package honorsthesis.gabriella.honorsthesis.BackEnd;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Gabriella on 11/3/2016.
 */
public class ThesisList implements Parcelable{
    private String name;
    private List<Task> tasks;
    private List<Process> processes;

    public ThesisList(String name){
        this.name = name;
        this.tasks = new ArrayList<Task>();
        this.processes = new ArrayList<Process>();
    }

    public void setName(String name){
        this.name = name;
    }

    public void setTasks(List<Task> tasks){
        this.tasks = tasks;
    }

    public void setProcesses(List<Process> processes){
        this.processes = processes;
    }

    public String getName(){
        return name;
    }

    public List<Task> getTasks(){
        return tasks;
    }

    public List<Process> getProcesses() { return processes; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if(null != name){
            dest.writeString(name);
        }
        if(null != tasks){
            dest.writeList(tasks);
        }else{
            dest.writeList(new ArrayList<Task>());
        }
        if(null != processes){
            dest.writeList(processes);
        }else{
            dest.writeList(new ArrayList<Process>());
        }
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<ThesisList> CREATOR = new Parcelable.Creator<ThesisList>() {
        public ThesisList createFromParcel(Parcel in) {
            return new ThesisList(in);
        }

        public ThesisList[] newArray(int size) {
            return new ThesisList[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private ThesisList(Parcel in) {
        name = in.readString();
        tasks = new ArrayList<Task>();
        in.readList(tasks, Task.class.getClassLoader());
        processes = new ArrayList<Process>();
        in.readList(processes, Process.class.getClassLoader());
    }
}
