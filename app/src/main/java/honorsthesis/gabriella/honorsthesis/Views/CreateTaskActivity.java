package honorsthesis.gabriella.honorsthesis.Views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import honorsthesis.gabriella.honorsthesis.Adapters.StepRecyclerViewAdapter;
import honorsthesis.gabriella.honorsthesis.Adapters.TaskRecyclerViewAdapter;
import honorsthesis.gabriella.honorsthesis.BackEnd.Priority;
import honorsthesis.gabriella.honorsthesis.BackEnd.Process;
import honorsthesis.gabriella.honorsthesis.BackEnd.Step;
import honorsthesis.gabriella.honorsthesis.BackEnd.Task;
import honorsthesis.gabriella.honorsthesis.BackEnd.ThesisList;
import honorsthesis.gabriella.honorsthesis.DataRepo.DataRepo;
import honorsthesis.gabriella.honorsthesis.R;

/**
 * A login screen that offers login via email/password.
 */
public class CreateTaskActivity extends AppCompatActivity {
    //constants
    private String listName;
    private String parentTask;
    private Task task;
    private List<Task> subTasks;


    private ListTaskFragment.OnListFragmentTaskInteractionListener mListener;
    // UI references.
    private EditText mTaskNameView;
    private EditText mSubTaskView;
    private EditText mDueDateView;
    private EditText mPriorityView;
    private EditText mNotesView;
    public TaskRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    DataRepo mDataRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set parent list and task names
        Intent i = getIntent();
        listName = i.getStringExtra("listName");
        //parentTask = i.getParcelableExtra("parentTask");

        mDataRepo = new DataRepo(this);
        subTasks = new ArrayList<Task>();

        setContentView(R.layout.activity_create_task);
        // Set up the create task form.
        mTaskNameView = (AutoCompleteTextView) findViewById(R.id.task_name);
        ((TextView) findViewById(R.id.list_name)).setText(listName);
        if (null != parentTask) {
            ((TextView) findViewById(R.id.parent_name)).setText(parentTask);
        } else {
            findViewById(R.id.parent_form).setVisibility(View.GONE);
        }
        //subtasks
        //show empty list of steps with option to create a step
        View recView = findViewById(R.id.list);
        // Set the adapter
        if (recView instanceof RecyclerView) {
            mRecyclerView = (RecyclerView) recView;
            mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new TaskRecyclerViewAdapter(this, subTasks, listName, mListener);
            mRecyclerView.setAdapter(mAdapter);
        }

        //set listener for add step
        ImageView mAddStep = (ImageView) findViewById(R.id.create_subTask);
        mAddStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent createSubTask = getIntent();
                //createStep.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                createSubTask.putExtra("listName", listName);
                if(null == task){
                    createSubTask.putExtra("parentTask", new Task("Create Task"));
                }
                else{
                    createSubTask.putExtra("parentTask", task);
                }
                try{
                    startActivityForResult(createSubTask, 1);
                    //finish();

                }catch(Exception e)
                {
                    System.out.println("here!");
                }*/
                String subTaskName = mSubTaskView.getText().toString();
                boolean cancel = false;
                View focusView = null;
                if (TextUtils.isEmpty(subTaskName)) {
                    mSubTaskView.setError(getString(R.string.error_field_required));
                    focusView = mSubTaskView;
                    cancel = true;
                }
                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    subTasks.add(new Task(subTaskName));
                    mAdapter.notifyDataSetChanged();
                    mSubTaskView.clearFocus();
                    mSubTaskView.setText("");
                }

            }
        });
        mSubTaskView = (AutoCompleteTextView) findViewById(R.id.subTask_name);
        mDueDateView = (AutoCompleteTextView) findViewById(R.id.date);
        mPriorityView = (AutoCompleteTextView) findViewById(R.id.priority);
        mNotesView = (AutoCompleteTextView) findViewById(R.id.notes);

        //remove button in favor of toolbar option
//        Button mCreateTaskButton = (Button) findViewById(R.id.create_task_button);
//        mCreateTaskButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                createTask();
//            }
//        });

        //set up toolbar
        // toolbar = (Toolbar) getLayoutInflater().inflate(R.layout.app_bar_main, null).findViewById(R.id.toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_create_task));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem create = menu.add(0, 1, 0, "Create");
        create.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        create.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                createTask();
                return false;
            }
        });
        MenuItem cancel = menu.add(0, 0, 0, "Cancel");
        cancel.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        cancel.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                finish();
                return false;
            }
        });
        return true;
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//            if(resultCode == RESULT_OK) {
//                Task subTask = data.getParcelableExtra("newSubTask");
//                subTasks.add(subTask);
//                mAdapter.notifyDataSetChanged();
//            }
//        }
//    }

    /**
     * attempts to create a task
     * adds the task to the database and goes back to the list that the task is from
     */
    private void createTask() {
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy k:mm");

        // Reset errors.
        mTaskNameView.setError(null);
        mDueDateView.setError(null);
        mPriorityView.setError(null);
        mNotesView.setError(null);

        // Store values at the time of the login attempt.
        String taskName = mTaskNameView.getText().toString();
        String dateString = mDueDateView.getText().toString();
        Date dueDate = null;
        String priorityString = mPriorityView.getText().toString();
        Priority priority = null;
        String notes = mNotesView.getText().toString();


        boolean cancel = false;
        View focusView = null;

        // Check for a valid values.
        if (TextUtils.isEmpty(taskName)) {
            mTaskNameView.setError(getString(R.string.error_field_required));
            focusView = mTaskNameView;
            cancel = true;
        }
        if (!TextUtils.isEmpty(dateString)) {
            try {
                dueDate = formatter.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(priorityString)) {
            priority = Priority.valueOf(priorityString.toUpperCase());
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            task = new Task(taskName, notes, priority, dueDate);
            //make task and add it to the database
            if (null != parentTask) {
                task.setParent(parentTask);
            }

//            if(null != parentTask){
//                task.setParent(parentTask);
//                Intent intent = new Intent();
//                intent.putExtra("newSubTask", task);
//                setResult(Activity.RESULT_OK, intent);
//                finish();
//            } else {
            for (Task subTask : subTasks) {
                subTask.setParent(task.getName());
                mDataRepo.addTask(subTask, listName);
            }
            task.setChildren(subTasks);
            mDataRepo.addTask(task, listName);

            //TODO: change this to updating the list in the mainActivityView
            Intent mainActivity = new Intent(this, MainActivity.class);
            mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mainActivity.putExtra("task", listName);
            startActivity(mainActivity);
            //}
        }
    }
}

