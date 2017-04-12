package honorsthesis.gabriella.honorsthesis.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import honorsthesis.gabriella.honorsthesis.BackEnd.*;
import honorsthesis.gabriella.honorsthesis.R;

public class ViewStepActivity extends AppCompatActivity {

    private Step step;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        step = i.getParcelableExtra("step");

        setContentView(R.layout.activity_view_step);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(step.getName());

        setUpContent();
    }

    private void setUpContent(){
        ((TextView)findViewById(R.id.step_parent_process_text)).setText(step.getParentProcess());
        ((TextView)findViewById(R.id.step_notes)).setText(step.getNotes());
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
                this.finish();
                Intent viewProcess = new Intent(this, ViewProcessActivity.class);
                viewProcess.putExtra("process", step.getParentProcess());
                startActivity(viewProcess);
                return true;
            //noinspection SimplifiableIfStatement
            case R.id.action_edit:
                finish();
                Intent intent = new Intent(this, EditStepActivity.class);
                intent.putExtra("step", step);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
