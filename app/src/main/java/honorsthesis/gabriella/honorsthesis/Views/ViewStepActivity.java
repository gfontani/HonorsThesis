package honorsthesis.gabriella.honorsthesis.Views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import honorsthesis.gabriella.honorsthesis.BackEnd.Step;
import honorsthesis.gabriella.honorsthesis.R;

public class ViewStepActivity extends AppCompatActivity {

    private Step step;
    boolean wasEdited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        step = i.getParcelableExtra("step");
        wasEdited = i.getExtras().getBoolean("wasEdited");

        setContentView(R.layout.activity_view_step);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(step.getName());

        setUpContent();
    }

    private void setUpContent() {
        ((TextView) findViewById(R.id.step_parent_process_text)).setText(step.getParentProcess());
        if (null != step.getNotes() || step.getNotes().isEmpty()) {
            (findViewById(R.id.step_notes_title)).setVisibility(View.GONE);
            (findViewById(R.id.step_notes)).setVisibility(View.GONE);
        } else {
            ((TextView) findViewById(R.id.step_notes)).setText(step.getNotes());
        }
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
                if (wasEdited) {
                    Intent wasEditedIntent = getIntent();
                    wasEditedIntent.putExtra("wasEdited", true);
                    setResult(RESULT_OK, wasEditedIntent);
                } else {
                    setResult(Activity.RESULT_CANCELED, null);
                }
                this.finish();
                return true;
            case R.id.action_edit:
                Intent intent = new Intent(this, EditStepActivity.class);
                intent.putExtra("step", step);
                startActivityForResult(intent, 2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                //dataSet was changed refresh the view
                Step step = data.getParcelableExtra("newStep");
                wasEdited = true;
                Intent refresh = new Intent(this, ViewStepActivity.class);
                refresh.putExtra("step", step);
                refresh.putExtra("wasEdited", true);
                startActivity(refresh);
                this.finish();
            }
        }
    }
}
