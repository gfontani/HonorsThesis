package honorsthesis.gabriella.honorsthesis.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import honorsthesis.gabriella.honorsthesis.Adapters.StepRecyclerViewAdapter;
import honorsthesis.gabriella.honorsthesis.BackEnd.Process;
import honorsthesis.gabriella.honorsthesis.R;

public class ViewProcessActivity extends AppCompatActivity {

    public StepRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ListProcessFragment.OnListFragmentProcessInteractionListener mListener;

    private int mColumnCount = 1;
    private String parentList = "list name";
    private Process process;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        process = (Process) i.getParcelableExtra("process");
        parentList = i.getStringExtra("list");

        setContentView(R.layout.activity_view_process);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(process.getName());

        setUpContent();
    }

    private void setUpContent(){
        //set up step list
        if(process.getSteps().size() > 0) {
            View recView = findViewById(R.id.steps_list);
            // Set the adapter
            if (recView instanceof RecyclerView) {
                mRecyclerView = (RecyclerView) recView;
                mLayoutManager = new LinearLayoutManager(this);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mAdapter = new StepRecyclerViewAdapter(this, process.getSteps(), process, mListener);
                mRecyclerView.setAdapter(mAdapter);
            }
        }else{
            (findViewById(R.id.steps_list)).setVisibility(View.GONE);
            (findViewById(R.id.steps_title)).setVisibility(View.GONE);

        }

        ((TextView)findViewById(R.id.process_parent_list_text)).setText(parentList);
        ((TextView)findViewById(R.id.process_notes)).setText(process.getNotes());

        //TODO: make button to make an instance of the process
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            //noinspection SimplifiableIfStatement
            case R.id.action_delete:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
