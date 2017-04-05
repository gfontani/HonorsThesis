package honorsthesis.gabriella.honorsthesis.Views;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import honorsthesis.gabriella.honorsthesis.Adapters.ProcessRecyclerViewAdapter;
import honorsthesis.gabriella.honorsthesis.Adapters.StepRecyclerViewAdapter;
import honorsthesis.gabriella.honorsthesis.BackEnd.*;
import honorsthesis.gabriella.honorsthesis.BackEnd.Process;
import honorsthesis.gabriella.honorsthesis.DataRepo.DataRepo;
import honorsthesis.gabriella.honorsthesis.R;

/**
 * A login screen that offers login via email/password.
 */
public class CreateProcessActivity extends AppCompatActivity{
//TODO: change this to edit
    //constants
    private String listName;
    private Process process;
    public StepRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private ListProcessFragment.OnListFragmentProcessInteractionListener mListener;

    // UI references.
    private EditText mProcessNameView;
    private EditText mNotesView;

    List<Step> steps;

    DataRepo mDataRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set process name
        Intent i = getIntent();
        listName = i.getStringExtra("listName");

        mDataRepo = new DataRepo(this);

        steps = new ArrayList<Step>();
        steps.add(new Step("name"));

        setContentView(R.layout.activity_create_process);
        // Set up the create process form.
        mProcessNameView = (AutoCompleteTextView) findViewById(R.id.process_name);
        ((TextView)findViewById(R.id.list_name)).setText(listName);
        mNotesView = (AutoCompleteTextView) findViewById(R.id.notes);

        //show empty list of steps with option to create a step
        View recView = findViewById(R.id.list);
        // Set the adapter
        if (recView instanceof RecyclerView) {
            mRecyclerView = (RecyclerView) recView;
            mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new StepRecyclerViewAdapter(this, steps, process, mListener);
            mRecyclerView.setAdapter(mAdapter);
        }

        //set listener for add step
        TextView mAddStep = (TextView) findViewById(R.id.create_subTask);
        mAddStep.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent createStep = new Intent(CreateProcessActivity.this, CreateStepActivity.class);
                //createStep.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                createStep.putExtra("listName", listName);
                if(null == process){
                    createStep.putExtra("parentProcess", new Process("Create Process"));
                }
                else{
                    createStep.putExtra("parentProcess", process);
                }
                startActivityForResult(createStep, 1);
            }
        });

        Button mCreateProcessButton = (Button) findViewById(R.id.create_process_button);
        mCreateProcessButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                createProcess();
            }
        });

        //set up toolbar
        // toolbar = (Toolbar) getLayoutInflater().inflate(R.layout.app_bar_main, null).findViewById(R.id.toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_create_process));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem create = menu.add(0, 1, 0, "Create");
        create.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        create.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                createProcess();
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                Step step = data.getParcelableExtra("newStep");
                steps.add(step);
                //mAdapter = new StepRecyclerViewAdapter(this, steps, process, mListener);
                //mRecyclerView.setAdapter(mAdapter);
                //TODO: figure out why this isn't working
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * attempts to create a process
     * adds the process to the database and goes back to the list that the process is from
     */
    private void createProcess() {
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy k:mm");

        // Reset errors.
        mProcessNameView.setError(null);
        mNotesView.setError(null);

        // Store values at the time of the login attempt.
        String processName = mProcessNameView.getText().toString();
        String notes = mNotesView.getText().toString();


        boolean cancel = false;
        View focusView = null;

        //TODO: only make name required
        // Check for a valid values.
        if (TextUtils.isEmpty(processName)) {
            mProcessNameView.setError(getString(R.string.error_field_required));
            focusView = mProcessNameView;
            cancel = true;
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

            //make process and add it to the database
            Process process = new Process(processName);
            process.setNotes(notes);
            //delete temp steps
            for(Step step : steps){
                step.setParent(process);
            }
            process.setSteps(steps);
            mDataRepo.addProcess(process, listName);
            Intent mainActivity = new Intent(this, MainActivity.class);
            mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mainActivity.putExtra("listName", listName);
            startActivity(mainActivity);
        }
    }



//    private void initiatePopupWindow(View v) {
//        try {
//            //We need to get the instance of the LayoutInflater, use the context of this activity
//            LayoutInflater inflater = (LayoutInflater) ProfileView.this
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            //Inflate the view from a predefined XML layout
//            View layout = inflater.inflate(R.layout.popup,
//                    (ViewGroup) findViewById(R.id.popup_element));
//            // create a 300px width and 470px height PopupWindow
//            pw = new PopupWindow(layout, 300, 470, true);
//            // display the popup in the center
//            pw.showAtLocation(v, Gravity.CENTER, 0, 0);
//
//            TextView mResultText = (TextView) layout.findViewById(R.id.server_status_text);
//            Button cancelButton = (Button) layout.findViewById(R.id.end_data_send_button);
//            cancelButton.setOnClickListener(cancel_button_click_listener);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}

