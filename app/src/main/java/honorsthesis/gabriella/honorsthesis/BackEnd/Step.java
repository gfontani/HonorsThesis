package honorsthesis.gabriella.honorsthesis.BackEnd;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.*;
import java.util.ArrayList;

/**
 * Created by Gabriella on 11/3/2016.
 */
public class Step implements Parcelable{
    private String name;
    private Priority priority;
    private Process parent;
    private String notes;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(notes);
        dest.writeString(priority.toString());
        dest.writeParcelable(parent, flags);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Step> CREATOR = new Parcelable.Creator<Step>() {
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        public Step[] newArray(int size) {
            return new Step[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Step(Parcel in) {
        name = in.readString();
        notes = in.readString();
        priority = Priority.valueOf(in.readString());
        in.readParcelable(Step.class.getClassLoader());
    }
}
