package honorsthesis.gabriella.honorsthesis.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import honorsthesis.gabriella.honorsthesis.BackEnd.Step;
import honorsthesis.gabriella.honorsthesis.DataRepo.DataRepo;
import honorsthesis.gabriella.honorsthesis.R;

/**
 * A login screen that offers login via email/password.
 */
public class EditStepActivity extends AppCompatActivity {
    //constants
    private Step step;
    private String oldName;
    // UI references.
    private EditText mStepNameView;
    private EditText mNotesView;

    DataRepo mDataRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        step = i.getParcelableExtra("step");
        oldName = step.getName();

        mDataRepo = new DataRepo(this);

        setContentView(R.layout.activity_create_step);
        // Set up the create process form.
        mStepNameView = (EditText) findViewById(R.id.step_name);
        mStepNameView.setText(step.getName());
        ((TextView) findViewById(R.id.process_name)).setText(step.getParentProcess());

        mNotesView = (EditText) findViewById(R.id.notes);
        mNotesView.setText(step.getNotes());

        //set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_edit_step));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed(){
        setResult(RESULT_OK, null);
        super.onBackPressed();;
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
                updateStep();
                return true;
            case R.id.action_delete:
                // app icon in action bar clicked; goto parent activity.
                deleteStep();
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
    private void updateStep() {

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
            step.setName(stepName);
            step.setNotes(notes);
            mDataRepo.updateStep(step, oldName, step.getParentProcess());
            Intent intent = new Intent();
            intent.putExtra("newStep", step);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void deleteStep() {
        mDataRepo.removeStep(step);
        //TODO: deal with this result so that it goes to the right place!
        finish();
    }
}

