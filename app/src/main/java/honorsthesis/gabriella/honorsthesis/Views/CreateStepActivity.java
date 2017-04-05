package honorsthesis.gabriella.honorsthesis.Views;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import honorsthesis.gabriella.honorsthesis.BackEnd.*;
import honorsthesis.gabriella.honorsthesis.BackEnd.Process;
import honorsthesis.gabriella.honorsthesis.DataRepo.DataRepo;
import honorsthesis.gabriella.honorsthesis.R;

/**
 * A login screen that offers login via email/password.
 */
public class CreateStepActivity extends AppCompatActivity {
    //constants
    private Process parentProcess;
    // UI references.
    private EditText mStepNameView;
    private EditText mNotesView;

    DataRepo mDataRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //set process name
    Intent i = getIntent();
    parentProcess = i.getParcelableExtra("parentProcess");

    mDataRepo = new DataRepo(this);

    setContentView(R.layout.activity_create_step);
    // Set up the create process form.
    mStepNameView = (AutoCompleteTextView) findViewById(R.id.step_name);
    if(!parentProcess.getName().equals("Create Process")){
        ((TextView)findViewById(R.id.process_name)).setText(parentProcess.getName());
    }
    else{
        ((TextView)findViewById(R.id.process_name)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.process_title)).setVisibility(View.GONE);
    }
    mNotesView = (AutoCompleteTextView) findViewById(R.id.notes);


    Button mCreateProcessButton = (Button) findViewById(R.id.create_step_button);
    mCreateProcessButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
            createStep();
        }
    });

    //set up toolbar
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setTitle(getString(R.string.title_activity_create_step));
}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem create = menu.add(0, 1, 0, "Create");
        create.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        create.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                createStep();
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
     * attempts to create a process
     * adds the process to the database and goes back to the list that the process is from
     */
    private void createStep() {

        // Reset errors.
        mStepNameView.setError(null);
        mNotesView.setError(null);

        // Store values at the time of the login attempt.
        String stepName = mStepNameView.getText().toString();
        String notes = mNotesView.getText().toString();


        boolean cancel = false;
        View focusView = null;

        // Check for a valid values.
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
            //make process and add it to the database
            Step step = new Step(stepName);
            step.setNotes(notes);
            step.setParent(parentProcess);
            mDataRepo.addStep(step);
            Intent intent = new Intent();
            intent.putExtra("newStep", step);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
}

