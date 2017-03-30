package honorsthesis.gabriella.honorsthesis.Views;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import honorsthesis.gabriella.honorsthesis.BackEnd.*;
import honorsthesis.gabriella.honorsthesis.BackEnd.Process;
import honorsthesis.gabriella.honorsthesis.DataRepo.DataRepo;
import honorsthesis.gabriella.honorsthesis.R;

/**
 * A login screen that offers login via email/password.
 */
public class CreateProcessActivity extends AppCompatActivity{
//TODO: figure out how to make steps
    //constants
    private String listName;
    // UI references.
    private EditText mProcessNameView;
    private EditText mNotesView;

    DataRepo mDataRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set process name
        Intent i = getIntent();
        listName = i.getStringExtra("listName");

        mDataRepo = new DataRepo(this);

        setContentView(R.layout.activity_create_process);
        // Set up the create process form.
        mProcessNameView = (AutoCompleteTextView) findViewById(R.id.process_name);
        ((TextView)findViewById(R.id.list_name)).setText(listName);
        mNotesView = (AutoCompleteTextView) findViewById(R.id.notes);


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
            mDataRepo.addProcess(process, listName);
            Intent mainActivity = new Intent(this, MainActivity.class);
            mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mainActivity.putExtra("listName", listName);
            startActivity(mainActivity);
        }
    }
}

