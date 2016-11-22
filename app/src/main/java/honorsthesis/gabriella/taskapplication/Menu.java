package honorsthesis.gabriella.taskapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Menu extends Activity{
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Lists");
        listDataHeader.add("Processes");

        // Adding child data
        List<String> lists = new ArrayList<String>();
        lists.add("The Shawshank Redemption");
        lists.add("The Godfather");
        lists.add("The Godfather: Part II");
        lists.add("Pulp Fiction");
        lists.add("The Good, the Bad and the Ugly");
        lists.add("The Dark Knight");
        lists.add("12 Angry Men");

        List<String> processes = new ArrayList<String>();
        processes.add("The Conjuring");
        processes.add("Despicable Me 2");
        processes.add("Turbo");
        processes.add("Grown Ups 2");
        processes.add("Red 2");
        processes.add("The Wolverine");

        listDataChild.put(listDataHeader.get(0), lists); // Header, Child data
        listDataChild.put(listDataHeader.get(1), processes);}
}
