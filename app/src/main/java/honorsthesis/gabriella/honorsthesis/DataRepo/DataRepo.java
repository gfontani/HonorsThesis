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

    String filename;
    private Context mContext;
    DatabaseHelper mDbHelper;

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
                removeProcess(process, list.getName());
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
                addTask(task, list.getName());
            }
        }

        //add processes to database
        List<Process> processes = list.getProcesses();
        if(null != processes && 0 != processes.size()){
            for(Process process : processes){
                addProcess(process, list.getName());
            }
        }
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DatabaseContract.List.TABLE_NAME, null, values);
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
                DatabaseContract.Task.COLUMN_PARENT_TASK
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
        Date date = new Date();
        Priority priority;
        String notes;

        try {
            while(cursor.moveToNext()) {
                name = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseContract.Task.COLUMN_NAME));
                notes = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseContract.Task.COLUMN_NOTES));
                date = formatter.parse(cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseContract.Task.COLUMN_DATE)));
                priority = Priority.valueOf(cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseContract.Task.COLUMN_PRIORITY)));
                tasks.add(new Task(name, notes, priority, date));
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
                Task task = tasks.get(tasks.indexOf(tempTask));
                Task parentTask = tasks.get(tasks.indexOf(tempParentTask));

                //add the parent task to the task
                //TODO: check if this adds a reference or if it adds a copy
                task.setParent(parentTask);
                //add the task to the parent task's children
                parentTask.addChild(task);
            }
        }
        cursor.close();
        return tasks;
    }

    /*
    private Task getTask(String taskName, String listName){

        //TODO: do I need this?
        Task task = new Task("", "", Priority.HIGH, new Date());

        return task;
    }
    */

    public void removeTask(Task task, String listName){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Define 'where' part of task child query.
        String childSelection = DatabaseContract.Task.COLUMN_PARENT_TASK + "=? AND " + DatabaseContract.Task.COLUMN_PARENT_LIST + "=?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { task.getName(), listName};
        // Issue SQL statement.
        db.delete(DatabaseContract.Task.TABLE_NAME, childSelection, selectionArgs);
        // Define 'where' part of task parent query.
        String parentSelection = DatabaseContract.Task.COLUMN_NAME + "=? AND " + DatabaseContract.Task.COLUMN_PARENT_LIST + "=?";
        // Specify arguments in placeholder order.
        // Issue SQL statement.
        db.delete(DatabaseContract.Task.TABLE_NAME, parentSelection, selectionArgs);
    }

    public void addTask(Task task, String listName){
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
        if(null != task.getParent()){
            values.put(DatabaseContract.Task.COLUMN_PARENT_TASK, task.getParent().getName());
        }
        values.put(DatabaseContract.Task.COLUMN_PARENT_LIST, listName);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DatabaseContract.Task.TABLE_NAME, null, values);
    }

    public void updateTask(Task task, String listName){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy k:mm");

        // New value for one column
        ContentValues values = new ContentValues();
        if(null != task.getNotes()){
            values.put(DatabaseContract.Task.COLUMN_NOTES, task.getNotes());
        }
        if(null != task.getPriority()){
            values.put(DatabaseContract.Task.COLUMN_PRIORITY, task.getPriority().toString());
        }
        if(null != task.getDate()){
            values.put(DatabaseContract.Task.COLUMN_DATE, formatter.format(task.getDate()));
        }
        if(null != task.getParent()){
            values.put(DatabaseContract.Task.COLUMN_PARENT_TASK, task.getParent().getName());
        }
        values.put(DatabaseContract.Task.COLUMN_PARENT_LIST, listName);

        // Which row to update, based on the title
        String selection = DatabaseContract.Task.COLUMN_NAME + "=? AND " + DatabaseContract.Task.COLUMN_PARENT_LIST + "=?";
        String[] selectionArgs = { task.getName(), listName };

        int count = db.update(
                DatabaseContract.Task.TABLE_NAME,
                values,
                selection,
                selectionArgs);
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
            List<Step> steps = getSteps(name);
            Process process = new Process(name);
            process.setNotes(notes);
            process.setSteps(steps);
            processes.add(process);
        }
        return processes;
    }

    public void removeProcess(Process process, String listName) {
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
        String[] processSelectionArgs = { process.getName(), listName };
        // Issue SQL statement.
        db.delete(DatabaseContract.Process.TABLE_NAME, processSelection, processSelectionArgs);
    }

    public void addProcess(Process process, String listName){
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Process.COLUMN_NAME, process.getName());
        if(null != process.getNotes()){
            values.put(DatabaseContract.Process.COLUMN_NOTES, process.getNotes());
        }
        values.put(DatabaseContract.Process.COLUMN_PARENT_LIST, listName);

        List<Step> steps = process.getSteps();
        if(null != steps){
            for(Step step : steps){
                addStep(step);
            }
        }
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DatabaseContract.Process.TABLE_NAME, null, values);
    }

    public void updateProcess(Process process, String listName){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

// New value for one column
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Process.COLUMN_NOTES, process.getNotes());

// Which row to update, based on the title
        String selection = DatabaseContract.Process.COLUMN_NAME + "=? AND " + DatabaseContract.Process.COLUMN_PARENT_LIST + "=?";
        String[] selectionArgs = { process.getName(), listName };

        int count = db.update(
                DatabaseContract.Process.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    public List<Step> getSteps(String processName){
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
        String[] selectionArgs = { processName };

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
            steps.add(step);
        }
        return steps;
    }

    public void removeStep(Step step, String processName){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String stepSelection = DatabaseContract.Step.COLUMN_NAME + "=? AND " + DatabaseContract.Step.COLUMN_PARENT_PROCESS + "=?";
        // Specify arguments in placeholder order.
        String[] stepSelectionArgs = { step.getName(), step.getParent().getName()};
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
        values.put(DatabaseContract.Step.COLUMN_PARENT_PROCESS, step.getParent().getName());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DatabaseContract.Step.TABLE_NAME, null, values);
    }

    public void updateStep (Step step){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        if(null != step.getNotes()){
            values.put(DatabaseContract.Step.COLUMN_NOTES, step.getNotes());
        }
        if(null != step.getPriority()){
            values.put(DatabaseContract.Step.COLUMN_PRIORITY, step.getPriority().toString());
        }

// Which row to update, based on the title
        String selection = DatabaseContract.Step.COLUMN_NAME + "=? AND " + DatabaseContract.Step.COLUMN_PARENT_PROCESS + "=?";
        String[] selectionArgs = { step.getName(), step.getParent().getName() };

        int count = db.update(
                DatabaseContract.Step.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }
}
