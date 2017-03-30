package honorsthesis.gabriella.honorsthesis.Views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
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
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import honorsthesis.gabriella.honorsthesis.BackEnd.Priority;
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
    private Task parentTask;
    // UI references.
    private EditText mTaskNameView;
    private EditText mDueDateView;
    private EditText mPriorityView;
    private EditText mNotesView;

    DataRepo mDataRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set parent list and task names
        Intent i = getIntent();
        listName = i.getStringExtra("listName");
        parentTask = i.getParcelableExtra("parentTaskName");

        mDataRepo = new DataRepo(this);

        setContentView(R.layout.activity_create_task);
        // Set up the create task form.
        mTaskNameView = (AutoCompleteTextView) findViewById(R.id.task_name);
        ((TextView)findViewById(R.id.list_name)).setText(listName);
        if(null != parentTask){
            ((TextView)findViewById(R.id.parent_name)).setText(parentTask.getName());
        }
        else{
            findViewById(R.id.parent_name).setVisibility(View.GONE);
        }
        mDueDateView = (AutoCompleteTextView) findViewById(R.id.date);
        mPriorityView = (AutoCompleteTextView) findViewById(R.id.priority);
        mNotesView = (AutoCompleteTextView) findViewById(R.id.notes);


        Button mCreateTaskButton = (Button) findViewById(R.id.create_task_button);
        mCreateTaskButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                createTask();
            }
        });

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
        Date dueDate = new Date();
        String priorityString = mPriorityView.getText().toString();
        Priority priority = Priority.NONE;
        String notes = mNotesView.getText().toString();


        boolean cancel = false;
        View focusView = null;

        //TODO: only make name required
        // Check for a valid values.
        if (TextUtils.isEmpty(taskName)) {
            mTaskNameView.setError(getString(R.string.error_field_required));
            focusView = mTaskNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(dateString)) {
            mDueDateView.setError(getString(R.string.error_field_required));
            focusView = mDueDateView;
            cancel = true;
        }else{
            try {
                dueDate = formatter.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (TextUtils.isEmpty(priorityString)) {
            mPriorityView.setError(getString(R.string.error_field_required));
            focusView = mPriorityView;
            cancel = true;
        }else{
            priority = Priority.valueOf(priorityString.toUpperCase());
        }

        if (TextUtils.isEmpty(notes)) {
            mNotesView.setError(getString(R.string.error_field_required));
            focusView = mNotesView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            //make task and add it to the database
            mDataRepo.addTask(new Task(taskName, notes, priority, dueDate), listName);
            Intent mainActivity = new Intent(this, MainActivity.class);
            mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mainActivity.putExtra("listName", listName);
            startActivity(mainActivity);
        }
    }
}

