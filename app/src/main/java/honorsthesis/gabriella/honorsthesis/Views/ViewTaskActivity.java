package honorsthesis.gabriella.honorsthesis.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import honorsthesis.gabriella.honorsthesis.Adapters.TaskRecyclerViewAdapter;
import honorsthesis.gabriella.honorsthesis.BackEnd.Process;
import honorsthesis.gabriella.honorsthesis.BackEnd.Step;
import honorsthesis.gabriella.honorsthesis.BackEnd.Task;
import honorsthesis.gabriella.honorsthesis.DataRepo.DataRepo;
import honorsthesis.gabriella.honorsthesis.R;

public class ViewTaskActivity extends AppCompatActivity implements ListTaskFragment.OnListFragmentTaskInteractionListener{

    private int mColumnCount = 1;
    //private String parentList = "list name";
    private Task task;
    public TaskRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ListTaskFragment.OnListFragmentTaskInteractionListener mListener;

    private boolean wasEdited = false;
    private boolean isSubtask = false;

    DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListener = (ListTaskFragment.OnListFragmentTaskInteractionListener) this;

        Intent i = getIntent();
        task = (Task) i.getParcelableExtra("task");
        //TODO: remove parentList extra every time start this activity
        //parentList = i.getStringExtra("list");
        wasEdited = i.getExtras().getBoolean("wasEdited");
        isSubtask = i.getExtras().getBoolean("isSubtask");


        setContentView(R.layout.activity_view_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(task.getName());

        setUpContent();
    }

    private void setUpContent(){
        //set up subtask list
        //TODO: figure out why if subtask is first in the list it doesn't show up in parent task subtask list
        if(task.getChildren().size() > 0) {
            View recView = findViewById(R.id.subTask_list);
            // Set the adapter
            if (recView instanceof RecyclerView) {
                if (recView instanceof RecyclerView) {
                    mRecyclerView = (RecyclerView) recView;
                    mLayoutManager = new LinearLayoutManager(this);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mAdapter = new TaskRecyclerViewAdapter(this, task.getChildren(), task.getParentList(), mListener);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        }else{
            (findViewById(R.id.subTask_list)).setVisibility(View.GONE);
            (findViewById(R.id.subTask_title)).setVisibility(View.GONE);

        }

        ((TextView)findViewById(R.id.task_parent_list_text)).setText(task.getParentList());
        if(null == task.getParentTask()){
            findViewById(R.id.parent_task_text).setVisibility(View.GONE);
            findViewById(R.id.parent_task).setVisibility(View.GONE);
        }
        else{
            ((TextView)findViewById(R.id.parent_task_text)).setText(task.getParentTask());
        }
        if(null != task.getDate()){
            ((TextView)findViewById(R.id.due_date_text)).setText(formatter.format(task.getDate()));
        }else{
            findViewById(R.id.due_date).setVisibility(View.GONE);
            findViewById(R.id.due_date_text).setVisibility(View.GONE);
        }
        if(null != task.getPriority()){
            ((TextView)findViewById(R.id.priority_text)).setText(task.getPriority().toString());
        }else{
            findViewById(R.id.priority).setVisibility(View.GONE);
            findViewById(R.id.priority_text).setVisibility(View.GONE);
        }
        if(null != task.getNotes() && !task.getNotes().isEmpty()){
            ((TextView)findViewById(R.id.task_notes)).setText(task.getNotes());
        }else{
            findViewById(R.id.task_notes_title).setVisibility(View.GONE);
            findViewById(R.id.task_notes).setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed(){
        backOrUpPressed();
        super.onBackPressed();
    }

    private void backOrUpPressed(){
        if(wasEdited){
            if(isSubtask){
                Intent wasEditedIntent = new Intent();
                wasEditedIntent.putExtra("wasEdited", true);
                setResult(RESULT_OK, wasEditedIntent);
            } else {
                Intent mainActivity = new Intent(this, MainActivity.class);
                mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mainActivity.putExtra("task", task.getParentList());
                startActivity(mainActivity);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                backOrUpPressed();
                finish();
                return true;
            //noinspection SimplifiableIfStatement
            case R.id.action_edit:
                Intent intent = new Intent(this, EditTaskActivity.class);
                intent.putExtra("task", task);
                startActivityForResult(intent, 5);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if(resultCode == RESULT_OK || requestCode == 6) {
                //dataSet was changed refresh the view
                Task updatedTask;
                if(requestCode == 5){
                    //get the updated task
                    updatedTask = data.getParcelableExtra("newTask");
                } else if(requestCode == 6){
                    DataRepo dataRepo = new DataRepo(this);
                    updatedTask = dataRepo.getTask(task.getName(), task.getParentList());
                } else{
                    //wrong result code exit the method
                    finish();
                    return;
                }
                wasEdited = true;
                Intent refresh = new Intent(this, ViewTaskActivity.class);
                refresh.putExtra("task", updatedTask);
                refresh.putExtra("wasEdited", true);
                refresh.putExtra("isSubtask", isSubtask);
                startActivity(refresh);
                this.finish();
            }
    }

    @Override
    public void onListFragmentTaskClick(Task item, String listName) {
            Intent intent = new Intent(this, ViewTaskActivity.class);
            intent.putExtra("task", item);
            intent.putExtra("isSubtask", true);
            startActivityForResult(intent, 6);
    }

    @Override
    public void onListFragmentTaskCheck(Task task, String listName) {
        DataRepo dataRepo = new DataRepo(this);
        dataRepo.removeTask(task);
        task.removeChild(task);
        mAdapter.notifyDataSetChanged();
    }
}
