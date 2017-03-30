package honorsthesis.gabriella.honorsthesis.Views;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import honorsthesis.gabriella.honorsthesis.BackEnd.ThesisList;
import honorsthesis.gabriella.honorsthesis.DataRepo.DataRepo;
import honorsthesis.gabriella.honorsthesis.R;

/**
 * Screen that allows user to create a new list.
 */
public class CreateListActivity extends AppCompatActivity {
//TODO: Make this a popup instead
    //database
    private DataRepo mDataRepo;
    // UI references.
    private EditText mListNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            mDataRepo = new DataRepo(this);
            setContentView(R.layout.activity_create_list);
            // Set up the login form.
            mListNameView = (AutoCompleteTextView) findViewById(R.id.list_name);

            Button mCreateListButton = (Button) findViewById(R.id.create_list_button);
            mCreateListButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    createList();
                }
            });

            //set up toolbar
            // toolbar = (Toolbar) getLayoutInflater().inflate(R.layout.app_bar_main, null).findViewById(R.id.toolbar);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.title_activity_create_list));
        }catch(Exception e){
            throw e;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem create = menu.add(0, 1, 0, "Create");
        create.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        create.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                createList();
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
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Creates a list and adds it to the database
     */
    private void createList() {
        // Reset errors.
        mListNameView.setError(null);

        // Store values at the time of the login attempt.
        String listName = mListNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid list name.
        if (TextUtils.isEmpty(listName)) {
            mListNameView.setError(getString(R.string.error_field_required));
            focusView = mListNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            //add the list to the database and go the view of the newly created list
            mDataRepo.addList(new ThesisList(listName));
            Intent mainActivity = new Intent(this, MainActivity.class);
            mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mainActivity.putExtra("listName", listName);
            startActivity(mainActivity);
        }
    }
}

