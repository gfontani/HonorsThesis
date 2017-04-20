package honorsthesis.gabriella.honorsthesis.Views;

import android.app.DatePickerDialog;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import honorsthesis.gabriella.honorsthesis.Adapters.TaskRecyclerViewAdapter;
import honorsthesis.gabriella.honorsthesis.BackEnd.Priority;
import honorsthesis.gabriella.honorsthesis.BackEnd.Task;
import honorsthesis.gabriella.honorsthesis.DataRepo.DataRepo;
import honorsthesis.gabriella.honorsthesis.R;

/**
 * A login screen that offers login via email/password.
 */
public class CreateTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
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
    private Spinner mPriorityView;
    private EditText mNotesView;
    public TaskRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private DatePickerFragment mDatePicker;
    DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

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
        mTaskNameView = (EditText) findViewById(R.id.task_name);
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
        ImageView mAddSubTask = (ImageView) findViewById(R.id.create_subTask);
        mAddSubTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    Task subTask = new Task(subTaskName);
                    subTask.setParentList(listName);
                    subTasks.add(subTask);
                    mAdapter.notifyDataSetChanged();
                    mSubTaskView.clearFocus();
                    mSubTaskView.setText("");
                }

            }
        });
        mSubTaskView = (EditText) findViewById(R.id.subTask_name);
        mDueDateView = (EditText) findViewById(R.id.date);
        mDatePicker = new DatePickerFragment(this);
        mDueDateView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                mDatePicker.show(ft, "date_dialog");
            }
        });
        mPriorityView = (Spinner) findViewById(R.id.priority);
        mNotesView = (EditText) findViewById(R.id.notes);

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
        getMenuInflater().inflate(R.menu.create_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.action_create:
                // app icon in action bar clicked; goto parent activity.
                createTask();
                return true;
            case R.id.action_cancel:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * attempts to create a task
     * adds the task to the database and goes back to the list that the task is from
     */
    private void createTask() {
        // Reset errors.
        mTaskNameView.setError(null);
        mDueDateView.setError(null);
        //mPriorityView.setError(null);
        mNotesView.setError(null);

        // Store values at the time of the login attempt.
        String taskName = mTaskNameView.getText().toString();
        String dateString = mDueDateView.getText().toString();
        Date dueDate = null;
        String priorityString = mPriorityView.getSelectedItem().toString();
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
            try {
                dueDate = new Date(dateString);
            } catch (Exception e) {
                e.printStackTrace();
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
                task.setParentTask(parentTask);
            }
            for (Task subTask : subTasks) {
                subTask.setParentTask(task.getName());
            }
            task.setChildren(subTasks);
            task.setParentList(listName);
            mDataRepo.addTask(task);
            finish();
            //TODO: change this to updating the list in the mainActivityView
            Intent mainActivity = new Intent(this, MainActivity.class);
            mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mainActivity.putExtra("task", listName);
            startActivity(mainActivity);
            //}
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        mDueDateView.setText(formatter.format(cal.getTime()));
        mDatePicker.setDate(cal);
    }
}

