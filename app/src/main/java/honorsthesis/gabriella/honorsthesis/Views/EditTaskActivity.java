package honorsthesis.gabriella.honorsthesis.Views;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import honorsthesis.gabriella.honorsthesis.Adapters.TaskRecyclerViewAdapter;
import honorsthesis.gabriella.honorsthesis.BackEnd.Priority;
import honorsthesis.gabriella.honorsthesis.BackEnd.Task;
import honorsthesis.gabriella.honorsthesis.DataRepo.DataRepo;
import honorsthesis.gabriella.honorsthesis.R;

/**
 * A login screen that offers login via email/password.
 */
public class EditTaskActivity extends AppCompatActivity implements ListTaskFragment.OnListFragmentTaskInteractionListener, DatePickerDialog.OnDateSetListener {
    //constants
    private String oldName;
    private Task task;
    DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

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

    DataRepo mDataRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set parent list and task names
        Intent i = getIntent();
        task = (Task) i.getParcelableExtra("task");
        oldName = task.getName();

        mDataRepo = new DataRepo(this);

        setContentView(R.layout.activity_create_edit_task);
        // Set up the create task form.
        mTaskNameView = (EditText) findViewById(R.id.task_name);
        mTaskNameView.setText(task.getName());
        ((TextView) findViewById(R.id.list_name)).setText(task.getParentList());
        if (null != task.getParentTask() && !task.getParentTask().isEmpty()) {
            ((TextView) findViewById(R.id.parent_name)).setText(task.getParentTask());
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
            mAdapter = new TaskRecyclerViewAdapter(this, task.getChildren(), task.getParentList(), mListener);
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
                    subTask.setParentTask(task.getName());
                    subTask.setParentList(task.getParentList());
                    task.addChild(subTask);
                    mAdapter.notifyDataSetChanged();
                    mSubTaskView.clearFocus();
                    mSubTaskView.setText("");
                    mDataRepo.addTask(subTask);
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
        if(null != task.getDate()){
            Date date = task.getDate();
            mDueDateView.setText(formatter.format(date));
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            mDatePicker.setDate(calendar);
        }

        mPriorityView = (Spinner) findViewById(R.id.priority);
        if(null != task.getPriority()){
            mPriorityView.setSelection(task.getPriority().ordinal());
        }

        mNotesView = (EditText) findViewById(R.id.notes);
        if(null != task.getNotes()){
            mNotesView.setText(task.getNotes());
        }

        //set up toolbar
        // toolbar = (Toolbar) getLayoutInflater().inflate(R.layout.app_bar_main, null).findViewById(R.id.toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_edit_task));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                setResult(RESULT_CANCELED, null);
                this.finish();
                return true;
            case R.id.action_save:
                // app icon in action bar clicked; goto parent activity.
                updateTask();
                return true;
            case R.id.action_delete:
                // app icon in action bar clicked; goto parent activity.
                deleteTask();
                return true;
            case R.id.action_cancel:
                // app icon in action bar clicked; goto parent activity.
                setResult(RESULT_CANCELED, null);
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
    private void updateTask() {
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
            task.setName(taskName);
            task.setNotes(notes);
            task.setPriority(priority);
            task.setDate(dueDate);
            //make task and add it to the database

            for (Task subTask : task.getChildren()) {
                subTask.setParentTask(task.getName());
            }
            mDataRepo.updateTask(task, oldName, task.getParentList());
            Intent intent = new Intent();
            intent.putExtra("newTask", task);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void deleteTask() {
        mDataRepo.removeTask(task);
        finish();
    }

@Override
public void onListFragmentTaskClick(Task item, String listName) {
        try {
        Intent intent = new Intent(this, ViewTaskActivity.class);
        intent.putExtra("task", item);
        intent.putExtra("list", listName);
        startActivity(intent);
        }catch( Exception e){
        System.out.println("here");
        }
        }

@Override
public void onListFragmentTaskCheck(Task task, String listName) {
        //mDataRepo.removeTask(task);
        //TODO: figure out what to do with check
//        Fragment fragment = ListTaskFragment.newInstance(1, listName);
//
//        // Insert the fragment by replacing any existing fragment
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        mDueDateView.setText(formatter.format(cal.getTime()));
        mDatePicker.setDate(cal);
    }
}

