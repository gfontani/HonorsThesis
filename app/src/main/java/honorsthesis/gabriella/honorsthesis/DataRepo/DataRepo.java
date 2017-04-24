package honorsthesis.gabriella.honorsthesis.DataRepo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import honorsthesis.gabriella.honorsthesis.BackEnd.Priority;
import honorsthesis.gabriella.honorsthesis.BackEnd.Step;
import honorsthesis.gabriella.honorsthesis.BackEnd.Task;
import honorsthesis.gabriella.honorsthesis.BackEnd.Process;
import honorsthesis.gabriella.honorsthesis.BackEnd.ThesisList;


/**
 * Created by Gabriella on 3/4/2017.
 */
public class DataRepo {

    private String filename;
    private Context mContext;
    private DatabaseHelper mDbHelper;

    public DataRepo(Context context){
        mContext = context;
        filename = "data.txt";
        mDbHelper = new DatabaseHelper(context);
    }

    public List<String> getLists(){
        List<String> lists = new ArrayList<String>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                DatabaseContract.List.COLUMN_NAME
        };

        String sortOrder = DatabaseContract.List.COLUMN_NAME + " DESC";
        Cursor cursor = db.query(
                DatabaseContract.List.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        while(cursor.moveToNext()){
            String listName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.List.COLUMN_NAME));
            lists.add(listName);
        }
        return lists;
    }

    public void removeList(ThesisList list){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        //remove all the tasks in the list
        // Define 'where' part of task query.
        String taskSelection = DatabaseContract.Task.COLUMN_PARENT_LIST + "=?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { list.getName() };
        db.delete(DatabaseContract.Task.TABLE_NAME, taskSelection, selectionArgs);

        if(null != list.getProcesses()){
            for(Process process : list.getProcesses()){
                removeProcess(process);
            }
        }

        // Define 'where' part of query.
        String listSelection = DatabaseContract.List.COLUMN_NAME + "=?";
        // Issue SQL statement.
        db.delete(DatabaseContract.List.TABLE_NAME, listSelection, selectionArgs);
    }

    public void addList(ThesisList list){
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.List.COLUMN_NAME, list.getName());
        //add tasks to database
        List<Task> tasks = list.getTasks();
        if(null != tasks && 0 != tasks.size()){
            for(Task task : tasks){
                addTask(task);
            }
        }

        //add processes to database
        List<Process> processes = list.getProcesses();
        if(null != processes && 0 != processes.size()){
            for(Process process : processes){
                addProcess(process);
            }
        }
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DatabaseContract.List.TABLE_NAME, null, values);
    }

    public void updateList(ThesisList list, String oldName){
        if(!list.getName().equals(oldName)) {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

            // New value for one column
            ContentValues values = new ContentValues();
            if (null != list.getName()) {
                values.put(DatabaseContract.List.COLUMN_NAME, list.getName());
            }

            // Which row to update, based on the title
            String selection = DatabaseContract.List.COLUMN_NAME + "=?";
            String[] selectionArgs = {oldName};

            int count = db.update(
                    DatabaseContract.List.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);

            //update tasks
            for(Task task: list.getTasks()){
                task.setParentList(list.getName());
                updateTask(task, task.getName(), oldName);
            }

            //update processes
            for(Process process:list.getProcesses()){
                process.setParentList(list.getName());
                updateProcess(process, process.getName(), oldName);
            }
        }

    }

    public List<Task> getAllTasks(){
        List<Task> tasks = new ArrayList<Task>();
        List<String> lists = getLists();
        for(String list : lists){
            tasks.addAll(getTasks(list));
        }
        return tasks;
    }

    public Task getTask(String taskName, String listName){
        List<Task> allTasks = getTasks(listName);
        Task tempTask = new Task(taskName);
        Task task = allTasks.get(allTasks.indexOf(tempTask));
        return task;
    }

    public List<Task> getTasks(String listName){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                DatabaseContract.Task.COLUMN_NAME,
                DatabaseContract.Task.COLUMN_NOTES,
                DatabaseContract.Task.COLUMN_PRIORITY,
                DatabaseContract.Task.COLUMN_DATE,
                DatabaseContract.Task.COLUMN_PARENT_TASK,
                DatabaseContract.Task.COLUMN_PARENT_LIST
        };

// Filter results WHERE "title" = 'My Title'
        String selection = DatabaseContract.Task.COLUMN_PARENT_LIST + " = ?";
        String[] selectionArgs = { listName };

// How you want the results sorted in the resulting Cursor
        //String sortOrder = DatabaseContract.Task.COLUMN_PRIORITY + " DESC";

        Cursor cursor = db.query(
                DatabaseContract.Task.TABLE_NAME,         // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                     // The sort order
        );

        List<Task> tasks = new ArrayList<Task>();
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy k:mm");
        String name;
        String dateString;
        Date date = new Date();
        String priorityString;
        Priority priority;
        String notes;
        String parentList;

        try {
            while(cursor.moveToNext()) {
                name = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseContract.Task.COLUMN_NAME));
                notes = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseContract.Task.COLUMN_NOTES));
                dateString = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseContract.Task.COLUMN_DATE));
                priorityString = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseContract.Task.COLUMN_PRIORITY));
                parentList = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseContract.Task.COLUMN_PARENT_LIST));

                Task newTask = new Task(name);
                if(null != notes){
                    newTask.setNotes(notes);
                }
                if(null != priorityString && !priorityString.isEmpty()){
                    priority = Priority.valueOf(priorityString);
                    newTask.setPriority(priority);
                }
                if(null != dateString && !dateString.isEmpty()){
                    date = formatter.parse(dateString);
                    newTask.setDate(date);
                }
                if(null != parentList){
                    newTask.setParentList(parentList);
                }
                tasks.add(newTask);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //go through tasks and assign correct subtasks and parent tasks
        cursor.moveToFirst();
        String parentTaskName = "";
        while(cursor.moveToNext()) {
            name = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseContract.Task.COLUMN_NAME));
            parentTaskName = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseContract.Task.COLUMN_PARENT_TASK));
            if(null != parentTaskName){
                Task tempParentTask = new Task(parentTaskName, "", Priority.NONE, new Date());
                Task tempTask = new Task(name, "", Priority.NONE, new Date());

                //get the task
                int taskIndex = tasks.indexOf(tempTask);
                int parentTaskIndex = tasks.indexOf(tempParentTask);
                if(taskIndex >= 0 && taskIndex < tasks.size()  && parentTaskIndex >= 0 && parentTaskIndex < tasks.size()) {
                    Task task = tasks.get(taskIndex);
                    Task parentTask = tasks.get(parentTaskIndex);

                    //add the parent task to the task
                    task.setParentTask(parentTaskName);
                    //add the task to the parent task's children
                    parentTask.addChild(task);
                }
            }
        }
        cursor.close();
        return tasks;
    }

    public void removeTask(Task task){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Define 'where' part of task child query.
        String childSelection = DatabaseContract.Task.COLUMN_PARENT_TASK + "=? AND " + DatabaseContract.Task.COLUMN_PARENT_LIST + "=?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { task.getName(), task.getParentList()};
        // Issue SQL statement.
        db.delete(DatabaseContract.Task.TABLE_NAME, childSelection, selectionArgs);
        // Define 'where' part of task parent query.
        String parentSelection = DatabaseContract.Task.COLUMN_NAME + "=? AND " + DatabaseContract.Task.COLUMN_PARENT_LIST + "=?";
        // Specify arguments in placeholder order.
        // Issue SQL statement.
        db.delete(DatabaseContract.Task.TABLE_NAME, parentSelection, selectionArgs);

        //remove subtasks
        for(Task subTask:task.getChildren()){
            removeTask(subTask);
        }
    }

    public void addTask(Task task){
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy k:mm");

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Task.COLUMN_NAME, task.getName());
        if(null != task.getNotes()){
            values.put(DatabaseContract.Task.COLUMN_NOTES, task.getNotes());
        }
        if(null != task.getPriority()){
            values.put(DatabaseContract.Task.COLUMN_PRIORITY, task.getPriority().toString());
        }
        if(null != task.getDate()){
            values.put(DatabaseContract.Task.COLUMN_DATE, formatter.format(task.getDate()));
        }
        if(null != task.getParentTask()){
            values.put(DatabaseContract.Task.COLUMN_PARENT_TASK, task.getParentTask());
        }
        values.put(DatabaseContract.Task.COLUMN_PARENT_LIST, task.getParentList());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DatabaseContract.Task.TABLE_NAME, null, values);

        //add subTasks
        for(Task subTask:task.getChildren()){
            addTask(subTask);
        }
    }

    public void updateTask(Task task, String oldName, String oldListName){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy k:mm");

        // New value for one column
        ContentValues values = new ContentValues();
        if(null != task.getName()){
            values.put(DatabaseContract.Task.COLUMN_NAME, task.getName());
        }
        if(null != task.getNotes()){
            values.put(DatabaseContract.Task.COLUMN_NOTES, task.getNotes());
        }
        if(null != task.getPriority()){
            values.put(DatabaseContract.Task.COLUMN_PRIORITY, task.getPriority().toString());
        }
        if(null != task.getDate()){
            values.put(DatabaseContract.Task.COLUMN_DATE, formatter.format(task.getDate()));
        }
        if(null != task.getParentTask()){
            values.put(DatabaseContract.Task.COLUMN_PARENT_TASK, task.getParentTask());
        }
        values.put(DatabaseContract.Task.COLUMN_PARENT_LIST, task.getParentList());

        // Which row to update, based on the title
        String selection = DatabaseContract.Task.COLUMN_NAME + "=? AND " + DatabaseContract.Task.COLUMN_PARENT_LIST + "=?";
        String[] selectionArgs = { oldName, oldListName };

        int count = db.update(
                DatabaseContract.Task.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        //update all  if necessary
        if(!task.getName().equals(oldName) || !task.getParentList().equals(oldListName)) {
            for (Task subTask : task.getChildren()) {
                subTask.setParentTask(task.getName());
                subTask.setParentList(task.getParentList());
                updateTask(subTask, subTask.getName(), oldListName);
            }
        }
    }

    public List<Process> getAllProcesses(){
        List<Process> processes = new ArrayList<Process>();
        List<String> lists = getLists();
        for(String list : lists){
            processes.addAll(getProcesses(list));
        }
        return processes;
    }

    public Process getProcess(String processName, String listName){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                DatabaseContract.Process.COLUMN_NAME,
                DatabaseContract.Process.COLUMN_NOTES,
        };

// Filter results WHERE "title" = 'My Title'
        String selection = DatabaseContract.Process.COLUMN_PARENT_LIST + "=? AND " + DatabaseContract.Process.COLUMN_NAME + "=?";
        String[] selectionArgs = { listName, processName};

// How you want the results sorted in the resulting Cursor
        //String sortOrder = DatabaseContract.Task.COLUMN_PRIORITY + " DESC";

        Cursor cursor = db.query(
                DatabaseContract.Process.TABLE_NAME,      // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                     // The sort order
        );

        while(cursor.moveToNext()) {
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseContract.Process.COLUMN_NAME));
            String notes = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseContract.Process.COLUMN_NOTES));
            Process process = new Process(name);
            process.setNotes(notes);
            process.setParentList(listName);
            List<Step> steps = getSteps(process);
            process.setSteps(steps);
            return process;
        }
        return null;
    }

    public List<Process> getProcesses(String listName){
        List<Process> processes = new ArrayList<Process>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                DatabaseContract.Process.COLUMN_NAME,
                DatabaseContract.Process.COLUMN_NOTES,
        };

// Filter results WHERE "title" = 'My Title'
        String selection = DatabaseContract.Process.COLUMN_PARENT_LIST + " = ?";
        String[] selectionArgs = { listName };

// How you want the results sorted in the resulting Cursor
        //String sortOrder = DatabaseContract.Task.COLUMN_PRIORITY + " DESC";

        Cursor cursor = db.query(
                DatabaseContract.Process.TABLE_NAME,      // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                     // The sort order
        );

        String name;
        String notes;
        while(cursor.moveToNext()) {
            name = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseContract.Process.COLUMN_NAME));
            notes = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseContract.Process.COLUMN_NOTES));
            Process process = new Process(name);
            process.setNotes(notes);
            process.setParentList(listName);
            processes.add(process);
            List<Step> steps = getSteps(process);
            process.setSteps(steps);

        }
        return processes;
    }

    public void removeProcess(Process process) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        //Delete all of the steps of the process
        // Define 'where' part of step query.
        String stepSelection = DatabaseContract.Step.COLUMN_PARENT_PROCESS + "=?";
        // Specify arguments in placeholder order.
        String[] stepSelectionArgs = { process.getName()};
        // Issue SQL statement.
        db.delete(DatabaseContract.Step.TABLE_NAME, stepSelection, stepSelectionArgs);

        //Delete the process
        // Define 'where' part of process query.
        String processSelection = DatabaseContract.Process.COLUMN_NAME + "=? AND " + DatabaseContract.Process.COLUMN_PARENT_LIST + "=?";
        // Specify arguments in placeholder order.
        String[] processSelectionArgs = { process.getName(), process.getParentList() };
        // Issue SQL statement.
        db.delete(DatabaseContract.Process.TABLE_NAME, processSelection, processSelectionArgs);

        //remove all steps
        for(Step step: process.getSteps()){
            removeStep(step);
        }
    }

    public void addProcess(Process process){
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Process.COLUMN_NAME, process.getName());
        if(null != process.getNotes()){
            values.put(DatabaseContract.Process.COLUMN_NOTES, process.getNotes());
        }
        values.put(DatabaseContract.Process.COLUMN_PARENT_LIST, process.getParentList());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DatabaseContract.Process.TABLE_NAME, null, values);

        //add steps to database
        List<Step> steps = process.getSteps();
        if(null != steps){
            for(Step step : steps){
                addStep(step);
            }
        }
    }

    public void updateProcess(Process process, String oldName, String oldParentListName){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

// New value for one column
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Process.COLUMN_NAME, process.getName());
        values.put(DatabaseContract.Process.COLUMN_NOTES, process.getNotes());
        values.put(DatabaseContract.Process.COLUMN_PARENT_LIST, process.getParentList());

// Which row to update, based on the title
        String selection = DatabaseContract.Process.COLUMN_NAME + "=? AND " + DatabaseContract.Process.COLUMN_PARENT_LIST + "=?";
        String[] selectionArgs = { oldName, oldParentListName };

        int count = db.update(
                DatabaseContract.Process.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        //update steps if necessary
        if(!process.getName().equals(oldName) || !process.getParentList().equals(oldParentListName)){
            for(Step step: process.getSteps()){
                step.setParentProcess(process.getName());
                updateStep(step, step.getName(), oldName);
            }
        }
    }

    public List<Step> getSteps(Process process){
        List<Step> steps = new ArrayList<Step>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                DatabaseContract.Step.COLUMN_NAME,
                DatabaseContract.Step.COLUMN_NOTES,
                DatabaseContract.Step.COLUMN_PRIORITY,
        };

// Filter results WHERE "title" = 'My Title'
        String selection = DatabaseContract.Step.COLUMN_PARENT_PROCESS + " = ?";
        String[] selectionArgs = { process.getName() };

// How you want the results sorted in the resulting Cursor
        //String sortOrder = DatabaseContract.Task.COLUMN_PRIORITY + " DESC";

        Cursor cursor = db.query(
                DatabaseContract.Step.TABLE_NAME,      // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                     // The sort order
        );

        String name;
        String notes;
        String priorityString;
        while(cursor.moveToNext()) {
            name = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseContract.Step.COLUMN_NAME));
            notes = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseContract.Step.COLUMN_NOTES));
            priorityString = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseContract.Step.COLUMN_PRIORITY));
            Step step = new Step(name);
            if(null != priorityString && !priorityString.isEmpty()){
                step.setPriority(Priority.valueOf(priorityString));
            }
            step.setNotes(notes);
            step.setParentProcess(process.getName());
            steps.add(step);
        }
        return steps;
    }

    public void removeStep(Step step){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String stepSelection = DatabaseContract.Step.COLUMN_NAME + "=? AND " + DatabaseContract.Step.COLUMN_PARENT_PROCESS + "=?";
        // Specify arguments in placeholder order.
        String[] stepSelectionArgs = { step.getName(), step.getParentProcess()};
        // Issue SQL statement.
        db.delete(DatabaseContract.Step.TABLE_NAME, stepSelection, stepSelectionArgs);
    }

    public void addStep(Step step){
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Step.COLUMN_NAME, step.getName());
        if(null != step.getNotes()){
            values.put(DatabaseContract.Step.COLUMN_NOTES, step.getNotes());
        }
        if(null != step.getPriority()){
            values.put(DatabaseContract.Step.COLUMN_PRIORITY, step.getPriority().toString());
        }
        values.put(DatabaseContract.Step.COLUMN_PARENT_PROCESS, step.getParentProcess());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DatabaseContract.Step.TABLE_NAME, null, values);
    }

    public void updateStep (Step step, String oldName, String oldParentName){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        if(null != step.getNotes()){
            values.put(DatabaseContract.Step.COLUMN_NAME, step.getName());
        }
        if(null != step.getNotes()){
            values.put(DatabaseContract.Step.COLUMN_NOTES, step.getNotes());
        }
        if(null != step.getPriority()){
            values.put(DatabaseContract.Step.COLUMN_PRIORITY, step.getPriority().toString());
        }
        if(null != step.getParentProcess()){
            values.put(DatabaseContract.Step.COLUMN_PARENT_PROCESS, step.getParentProcess());
        }

// Which row to update, based on the title
        String selection = DatabaseContract.Step.COLUMN_NAME + "=? AND " + DatabaseContract.Step.COLUMN_PARENT_PROCESS + "=?";
        String[] selectionArgs = { oldName, oldParentName };

        int count = db.update(
                DatabaseContract.Step.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }
}
