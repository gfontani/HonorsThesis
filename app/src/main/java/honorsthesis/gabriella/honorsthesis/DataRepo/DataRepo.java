package honorsthesis.gabriella.honorsthesis.DataRepo;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import honorsthesis.gabriella.honorsthesis.BackEnd.Priority;
import honorsthesis.gabriella.honorsthesis.BackEnd.Step;
import honorsthesis.gabriella.honorsthesis.BackEnd.Task;
import honorsthesis.gabriella.honorsthesis.BackEnd.Process;


/**
 * Created by Gabriella on 3/4/2017.
 */
public class DataRepo {

    String filename;
    private Context mContext;

    public DataRepo(Context context){
        mContext = context;
        filename = "data.txt";
    }

    public List<String> getLists(){
        List<String> lists = new ArrayList<String>();

        Scanner scan = null;
        try {
            scan = new Scanner(mContext.getAssets().open(filename));
            String line = "";
            while(scan.hasNext()){
                line = scan.nextLine();
                if(line.equals("Start List")){
                    lists.add(scan.nextLine());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return lists;
    }

    public void removeList(String listName){
        //TODO: implement this
    }

    public void addList(List list){
        //TODO: implement this
    }

    public List<Task> getTasks(String listName){
        Scanner scan = null;
        List<Task> tasks = new ArrayList<Task>();
        try {
            scan = new Scanner(mContext.getAssets().open(filename));
            if(listName.equalsIgnoreCase("All Lists")){
                tasks = getAllTasks(scan);
            }
            else {
                String line = "";
                while (scan.hasNext()) {
                    line = scan.nextLine();
                    if (line.equals("Start List")) {
                        line = scan.nextLine();
                        if (line.equals(listName)) {
                            tasks = getTasksForList(scan);
                            break;
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    private Task getTask(String taskName, Scanner scan){
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy k:mm");
        String name;
        Date date = new Date();
        Priority priority;
        String notes;
        String line = "";
        //get task name
        name = taskName.substring(11);
        line = scan.nextLine();
        //get task date
        try {
            date = (Date) formatter.parse(line);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        line = scan.nextLine();
        //get task priority
        priority = Priority.valueOf(line);
        line = scan.nextLine();
        //get task notes
        notes = line;
        //add new task to list

        //TODO: get subtasks

        //TODO: get parent
        Task task = new Task(name,notes, priority, date);
        //task.setChildren();
        //task.setParent();
        return task;
    }

    private List<Task> getAllTasks(Scanner scan){
        List<Task> tasks = new ArrayList<Task>();
        String line = "";

        while(scan.hasNext()){
            line = scan.nextLine();
            if(line.startsWith("Task Name: ")){
                getTask(line, scan);
            }
        }
        return tasks;
    }

    private List<Task> getTasksForList(Scanner scan){
        List<Task> tasks = new ArrayList<Task>();

        String line = "";
        while(scan.hasNext()){
            line = scan.nextLine();
            if(line.startsWith("Task Name: ")){
                getTask(line, scan);
            }else if(line.equals("End Tasks")){
                return tasks;
            }
        }
        return tasks;
    }

    public void removeTask(String task, String listName){
        //TODO: implement this
    }

    public void addTask(Task task, String listName){
        //TODO: implement this
    }

    public List<Process> getProcesses(String listName){
        List<Process> processes = new ArrayList<Process>();

        return processes;
    }

    public Process getProcess(String processName, Scanner scan){
        Process process = new Process("");

        return process;
    }

    public void removeProcess(String processName, String listName){

    }

    public void addProcess(Process process, String listName){

    }

    public Step getStep(Scanner scan){
        Step step = new Step("", Priority.HIGH);

        return step;
    }

    public void removeStep(String listName, String processName){

    }

    public void addStep(String listName, String processName){

    }
}
