package honorsthesis.gabriella.honorsthesis.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import honorsthesis.gabriella.honorsthesis.Adapters.StepRecyclerViewAdapter;
import honorsthesis.gabriella.honorsthesis.BackEnd.Process;
import honorsthesis.gabriella.honorsthesis.BackEnd.Step;
import honorsthesis.gabriella.honorsthesis.DataRepo.DataRepo;
import honorsthesis.gabriella.honorsthesis.R;

/**
 * A screen that allows users to edit a process.
 */
public class EditProcessActivity extends AppCompatActivity implements ListProcessFragment.OnListFragmentStepInteractionListener{
    //constants
    private Process process;
    private String oldProcessName;

    // UI references.
    private EditText mProcessNameView;
    private EditText mStepNameView;
    private EditText mNotesView;
    public StepRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private ListProcessFragment.OnListFragmentStepInteractionListener mListener;

    DataRepo mDataRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set process name
        Intent i = getIntent();
        process = (Process) i.getParcelableExtra("process");
        oldProcessName = process.getName();

        mDataRepo = new DataRepo(this);

        setContentView(R.layout.activity_create_edit_process);
        // Set up the create process form.
        mProcessNameView = (EditText) findViewById(R.id.process_name);
        mProcessNameView.setText(process.getName());
        ((TextView)findViewById(R.id.list_name)).setText(process.getParentList());
        mNotesView = (EditText) findViewById(R.id.notes);
        if(null != process.getNotes() && !process.getNotes().isEmpty()){
            mNotesView.setText(process.getNotes());
        }
        //show empty list of steps with option to create a step
        View recView = findViewById(R.id.list);
        // Set the adapter
        if (recView instanceof RecyclerView) {
            mRecyclerView = (RecyclerView) recView;
            mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new StepRecyclerViewAdapter(this, process.getSteps(), process, mListener);
            mRecyclerView.setAdapter(mAdapter);
        }

        //set listener for add step
        ImageView mAddStep = (ImageView) findViewById(R.id.create_step);
        mAddStep.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view){
                String stepName = mStepNameView.getText().toString();
                boolean cancel = false;
                View focusView = null;
                if (TextUtils.isEmpty(stepName)) {
                    mStepNameView.setError(getString(R.string.error_field_required));
                    focusView = mStepNameView;
                    cancel = true;
                }
                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    Step step = new Step(stepName);
                    step.setParentProcess(process.getName());
                    process.addStep(step);
                    mAdapter.notifyDataSetChanged();
                    mStepNameView.clearFocus();
                    mStepNameView.setText("");
                    mDataRepo.addStep(step);
                }
            }
        });
        mStepNameView = (EditText) findViewById(R.id.step_name);

        //set up toolbar
        // toolbar = (Toolbar) getLayoutInflater().inflate(R.layout.app_bar_main, null).findViewById(R.id.toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_edit_process));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    private void onBackOrUpPressed(){
        setResult(RESULT_CANCELED, null);
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
            case R.id.action_save:
                // app icon in action bar clicked; goto parent activity.
                updateProcess();
                return true;
            case R.id.action_delete:
                // app icon in action bar clicked; goto parent activity.
                deleteProcess();
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
     * attempts to create a process
     * adds the process to the database and goes back to the list that the process is from
     */
    private void updateProcess() {
        // Reset errors.
        mProcessNameView.setError(null);
        mNotesView.setError(null);

        // Store values at the time of the login attempt.
        String processName = mProcessNameView.getText().toString();
        String notes = mNotesView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid values.
        if (TextUtils.isEmpty(processName)) {
            mProcessNameView.setError(getString(R.string.error_field_required));
            focusView = mProcessNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            //make process and add it to the database
            process.setName(processName);
            process.setNotes(notes);
            for(Step step : process.getSteps()){
                step.setParentProcess(process.getName());
            }
            mDataRepo.updateProcess(process, oldProcessName, process.getParentList());
            Intent intent = new Intent();
            intent.putExtra("newProcess", process);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void deleteProcess(){
        mDataRepo.removeProcess(process);
        this.finish();
        Intent mainActivity = new Intent(this, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mainActivity.putExtra("process", process.getParentList());
        startActivity(mainActivity);
    }

    @Override
    public void onListFragmentStepInteraction(Step item, Process parent) {
        Intent intent = new Intent(this, ViewStepActivity.class);
        intent.putExtra("step", item);
        intent.putExtra("parent", parent);
        startActivity(intent);
    }
}

