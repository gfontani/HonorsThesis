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

public class ViewProcessActivity extends AppCompatActivity implements ListProcessFragment.OnListFragmentStepInteractionListener {

    public StepRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ListProcessFragment.OnListFragmentStepInteractionListener mListener;

    private String parentList = "list name";
    private Process process;
    boolean wasEdited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListener = this;

        Intent i = getIntent();
        process = i.getParcelableExtra("process");
        parentList = process.getParentList();
        wasEdited = i.getExtras().getBoolean("wasEdited");

        setContentView(R.layout.activity_view_process);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(process.getName());

        setUpContent();
    }

    private void setUpContent() {
        //set up step list
        if (process.getSteps().size() > 0) {
            View recView = findViewById(R.id.steps_list);
            // Set the adapter
            if (recView instanceof RecyclerView) {
                mRecyclerView = (RecyclerView) recView;
                mLayoutManager = new LinearLayoutManager(this);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mAdapter = new StepRecyclerViewAdapter(this, process.getSteps(), process, mListener);
                mRecyclerView.setAdapter(mAdapter);
            }
        } else {
            (findViewById(R.id.steps_list)).setVisibility(View.GONE);
            (findViewById(R.id.steps_title)).setVisibility(View.GONE);
        }

        ((TextView) findViewById(R.id.process_parent_list_text)).setText(parentList);

        if (null == process.getNotes() || process.getNotes().isEmpty()) {
            (findViewById(R.id.process_notes_title)).setVisibility(View.GONE);
            (findViewById(R.id.process_notes)).setVisibility(View.GONE);
        } else {
            ((TextView) findViewById(R.id.process_notes)).setText(process.getNotes());
        }

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

    private void onBackOrUpPressed(){
        if (wasEdited) {
            Intent mainActivity = new Intent(this, MainActivity.class);
            mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mainActivity.putExtra("process", parentList);
            startActivity(mainActivity);
        }
    }

    @Override
    public void onBackPressed(){
        onBackOrUpPressed();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                onBackOrUpPressed();
                this.finish();
                return true;
            case R.id.action_edit:
                Intent intent = new Intent(this, EditProcessActivity.class);
                intent.putExtra("process", process);
                startActivityForResult(intent, 3);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createInstance() {
        DataRepo dataRepo = new DataRepo(this);
        Task task = new Task(process.getName());
        task.setParentList(parentList);
        if (null != process.getNotes()) {
            task.setNotes(process.getNotes());
        }

        for (Step step : process.getSteps()) {
            Task subtask = new Task(step.getName());
            if (null != step.getPriority()) {
                subtask.setPriority(step.getPriority());
            }
            if (null != step.getNotes()) {
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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            //result is from view step activity
            //TODO: figure out why the resultCode is always 0 here
            //(resultCode == RESULT_OK) {
            //the steps were modified. get the process from the database and restart it
            DataRepo dataRepo = new DataRepo(this);
            Process updatedProcess = dataRepo.getProcess(process.getName(), process.getParentList());
            Intent refresh = new Intent(this, ViewProcessActivity.class);
            refresh.putExtra("process", updatedProcess);
            refresh.putExtra("wasEdited", true);
            startActivity(refresh);
            this.finish();
            //}
        } else if (requestCode == 3) {
            //result is from edit process activity
            if (resultCode == RESULT_OK) {
                //the steps were modified. get the process from the database and restart it
                Process updatedProcess = data.getParcelableExtra("newProcess");
                Intent refresh = new Intent(this, ViewProcessActivity.class);
                refresh.putExtra("process", updatedProcess);
                refresh.putExtra("wasEdited", true);
                startActivity(refresh);
                this.finish();
            }
        }
    }

    @Override
    public void onListFragmentStepInteraction(Step item, Process parent) {
        Intent intent = new Intent(this, ViewStepActivity.class);
        intent.putExtra("step", item);
        intent.putExtra("parent", parent);
        startActivityForResult(intent, 1);
    }
}
