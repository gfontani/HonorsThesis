package honorsthesis.gabriella.honorsthesis.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import honorsthesis.gabriella.honorsthesis.BackEnd.ThesisList;
import honorsthesis.gabriella.honorsthesis.DataRepo.DataRepo;
import honorsthesis.gabriella.honorsthesis.R;

/**
 * Screen that allows user to edit a new list.
 */
public class EditListActivity extends AppCompatActivity {
    //constants
    private ThesisList list;
    private String oldListName;
    private DataRepo mDataRepo;

    // UI references.
    private EditText mListNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        list = (ThesisList) i.getParcelableExtra("list");
        if (null != list) {
            oldListName = list.getName();
        }

        setUpContent();
    }

    private void setUpContent() {
        try {
            mDataRepo = new DataRepo(this);
            setContentView(R.layout.activity_create_edit_list);
            // Set up the login form.
            mListNameView = (EditText) findViewById(R.id.list_name);
            mListNameView.setText(list.getName());

            //set up toolbar
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.title_activity_edit_list));
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.action_save:
                updateList();
                return true;
            case R.id.action_delete:
                deleteList();
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
     * Creates a list and adds it to the database
     */
    private void updateList() {
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
            list.setName(listName);
            //add the list to the database and go the view of the newly created list
            mDataRepo.updateList(list, oldListName);
            finish();
            Intent mainActivity = new Intent(this, MainActivity.class);
            mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mainActivity.putExtra("task", listName);
            startActivity(mainActivity);
        }
    }

    private void deleteList() {
        mDataRepo.removeList(list);
        this.finish();
        Intent mainActivity = new Intent(this, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mainActivity.putExtra("task", getResources().getText(R.string.all_tasks).toString());
        startActivity(mainActivity);
    }
}

