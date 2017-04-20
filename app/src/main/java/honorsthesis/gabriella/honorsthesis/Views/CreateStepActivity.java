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
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

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
    Intent i = getIntent();
    parentProcess = i.getParcelableExtra("parentProcess");

    mDataRepo = new DataRepo(this);

    setContentView(R.layout.activity_create_step);
    // Set up the create process form.
    mStepNameView = (EditText) findViewById(R.id.step_name);
    if(!parentProcess.getName().equals("Create Process")){
        ((TextView)findViewById(R.id.process_name)).setText(parentProcess.getName());
    }
    else{
        ((TextView)findViewById(R.id.process_name)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.process_title)).setVisibility(View.GONE);
    }
    mNotesView = (EditText) findViewById(R.id.notes);

    //set up toolbar
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setTitle(getString(R.string.title_activity_create_step));
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
                createStep();
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
            step.setParentProcess(parentProcess.getName());
            mDataRepo.addStep(step);
            finish();
            Intent intent = new Intent();
            intent.putExtra("newStep", step);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
}

