package honorsthesis.gabriella.honorsthesis.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import honorsthesis.gabriella.honorsthesis.Adapters.TaskRecyclerViewAdapter;
import honorsthesis.gabriella.honorsthesis.BackEnd.Priority;
import honorsthesis.gabriella.honorsthesis.BackEnd.Task;
import honorsthesis.gabriella.honorsthesis.DataRepo.DataRepo;
import honorsthesis.gabriella.honorsthesis.R;

/**
 * A login screen that offers login via email/password.
 */
public class EditTaskActivity extends AppCompatActivity implements ListTaskFragment.OnListFragmentTaskInteractionListener{
    //constants
    private String oldName;
    private Task task;
    DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy k:mm");

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
        task = (Task) i.getParcelableExtra("task");
        oldName = task.getName();

        mDataRepo = new DataRepo(this);

        setContentView(R.layout.activity_create_task);
        // Set up the create task form.
        mTaskNameView = (AutoCompleteTextView) findViewById(R.id.task_name);
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
        mSubTaskView = (AutoCompleteTextView) findViewById(R.id.subTask_name);
        mDueDateView = (AutoCompleteTextView) findViewById(R.id.date);
        if(null != task.getDate()){
            mDueDateView.setText(formatter.format(task.getDate()));
        }
        mPriorityView = (AutoCompleteTextView) findViewById(R.id.priority);
        if(null != task.getPriority()){
            mPriorityView.setText(task.getPriority().toString());
        }

        mNotesView = (AutoCompleteTextView) findViewById(R.id.notes);
        if(null != task.getNotes()){
            mNotesView.setText(task.getNotes());
        }
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
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
    private void updateTask() {
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
            task.setName(taskName);
            task.setNotes(notes);
            task.setPriority(priority);
            task.setDate(dueDate);
            //make task and add it to the database

//            if(null != parentTask){
//                task.setParentTask(parentTask);
//                Intent intent = new Intent();
//                intent.putExtra("newSubTask", task);
//                setResult(Activity.RESULT_OK, intent);
//                finish();
//            } else {
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
}

