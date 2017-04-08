package honorsthesis.gabriella.honorsthesis.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import honorsthesis.gabriella.honorsthesis.Adapters.TaskRecyclerViewAdapter;
import honorsthesis.gabriella.honorsthesis.BackEnd.Task;
import honorsthesis.gabriella.honorsthesis.R;

public class ViewTaskActivity extends AppCompatActivity {

    private int mColumnCount = 1;
    private String parentList = "list name";
    private Task task;
    public TaskRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ListTaskFragment.OnListFragmentTaskInteractionListener mListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        task = (Task) i.getParcelableExtra("task");
        parentList = i.getStringExtra("list");

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
                    mAdapter = new TaskRecyclerViewAdapter(this, task.getChildren(), parentList, mListener);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        }else{
            (findViewById(R.id.subTask_list)).setVisibility(View.GONE);
            (findViewById(R.id.subTask_title)).setVisibility(View.GONE);

        }

        ((TextView)findViewById(R.id.task_parent_list_text)).setText(parentList);
        if(null == task.getParentTask()){
            findViewById(R.id.parent_task_text).setVisibility(View.GONE);
            findViewById(R.id.parent_task).setVisibility(View.GONE);
        }
        else{
            ((TextView)findViewById(R.id.parent_task_text)).setText(task.getParentTask());
        }
        if(null != task.getDate()){
            ((TextView)findViewById(R.id.due_date_text)).setText(task.getDate().toString());
        }else{
            ((TextView)findViewById(R.id.due_date_text)).setVisibility(View.GONE);
        }
        if(null != task.getPriority()){
            ((TextView)findViewById(R.id.priority_text)).setText(task.getPriority().toString());
        }else{
            ((TextView)findViewById(R.id.priority_text)).setVisibility(View.GONE);
        }
        if(null != task.getNotes()){
            ((TextView)findViewById(R.id.task_notes)).setText(task.getNotes());
        }else{
            ((TextView)findViewById(R.id.task_notes)).setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            //noinspection SimplifiableIfStatement
            case R.id.action_edit:
                //TODO: go to the edit task activity
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
