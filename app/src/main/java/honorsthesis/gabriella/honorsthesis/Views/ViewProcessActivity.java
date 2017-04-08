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
import android.widget.Button;
import android.widget.TextView;

import honorsthesis.gabriella.honorsthesis.Adapters.StepRecyclerViewAdapter;
import honorsthesis.gabriella.honorsthesis.BackEnd.Process;
import honorsthesis.gabriella.honorsthesis.BackEnd.Step;
import honorsthesis.gabriella.honorsthesis.BackEnd.Task;
import honorsthesis.gabriella.honorsthesis.DataRepo.DataRepo;
import honorsthesis.gabriella.honorsthesis.R;

public class ViewProcessActivity extends AppCompatActivity {

    public StepRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ListProcessFragment.OnListFragmentProcessInteractionListener mListener;

    private int mColumnCount = 1;
    private String parentList = "list name";
    private Process process;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        process = (Process) i.getParcelableExtra("process");
        parentList = i.getStringExtra("list");

        setContentView(R.layout.activity_view_process);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(process.getName());

        setUpContent();
    }

    private void setUpContent(){
        //set up step list
        if(process.getSteps().size() > 0) {
            View recView = findViewById(R.id.steps_list);
            // Set the adapter
            if (recView instanceof RecyclerView) {
                mRecyclerView = (RecyclerView) recView;
                mLayoutManager = new LinearLayoutManager(this);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mAdapter = new StepRecyclerViewAdapter(this, process.getSteps(), process, mListener);
                mRecyclerView.setAdapter(mAdapter);
            }
        }else{
            (findViewById(R.id.steps_list)).setVisibility(View.GONE);
            (findViewById(R.id.steps_title)).setVisibility(View.GONE);

        }

        ((TextView)findViewById(R.id.process_parent_list_text)).setText(parentList);
        ((TextView)findViewById(R.id.process_notes)).setText(process.getNotes());

        Button createInstance = (Button) findViewById(R.id.create_instance_button);
        createInstance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createInstance();
            }
        });
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
                //TODO: go to edit processacivity
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createInstance(){
        DataRepo dataRepo = new DataRepo(this);
        Task task = new Task(process.getName());
        task.setParentList(parentList);
        if(null != process.getNotes()){
            task.setNotes(process.getNotes());
        }

        for(Step step: process.getSteps()){
            Task subtask = new Task(step.getName());
            if(null != step.getPriority()){
                subtask.setPriority(step.getPriority());
            }
            if(null != step.getNotes()){
                subtask.setNotes(step.getNotes());
            }
            subtask.setParentTask(task.getName());
            subtask.setParentList(parentList);
            task.addChild(subtask);
        }
        dataRepo.addTask(task);

        //go to the task list so user can see that their task was made
        Intent mainActivity = new Intent(this, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mainActivity.putExtra("task", parentList);
        startActivity(mainActivity);
    }
}
