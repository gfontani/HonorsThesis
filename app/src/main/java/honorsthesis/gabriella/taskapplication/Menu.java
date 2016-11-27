package honorsthesis.gabriella.taskapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
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

public class Menu extends AppCompatActivity{
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

//    ExpandableListAdapter listAdapter;
//    ExpandableListView expListView;
//    List<String> listDataHeader;
//    HashMap<String, List<String>> listDataChild;

    List<ThesisList> lists = new ArrayList<ThesisList>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //Navigation side menu setup
        //Set Toolbar to replace the ActionBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        nvDrawer = (NavigationView) findViewById(R.id.nav_view);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

//        //collapsable list setup
//        // get the listview
//        expListView = (ExpandableListView) findViewById(R.id.lvExp);
//
//        // preparing list data
//        prepareListData();
//
//        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
//
//        // setting list adapter
//        expListView.setAdapter(listAdapter);

    }

    private void setupDrawerContent(NavigationView navigationView) {
        setupListMenu(navigationView.getMenu().getItem(0).getSubMenu());
        setupProcessMenu(navigationView.getMenu().getItem(1).getSubMenu());
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private void setupListMenu(android.view.Menu listMenu){
        for(int i = 0; i < lists.size(); i++){
            MenuItem item = listMenu.add(lists.get(i).getName());

        }
    }

    private void setupProcessMenu(android.view.Menu processMenu){
        for(int i = 0; i < lists.size(); i++){
            MenuItem item = processMenu.add(lists.get(i).getName());
        }
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_list_fragment:
                fragmentClass = list_view_screen.class;
                break;
            case R.id.nav_process_fragment:
                fragmentClass = list_view_screen.class;
                break;
            default:
                fragmentClass = list_view_screen.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * Preparing the list data
     */
//    private void prepareListData() {
//        listDataHeader = new ArrayList<String>();
//        listDataChild = new HashMap<String, List<String>>();
//
//        // Adding child data
//        listDataHeader.add("Lists");
//        listDataHeader.add("Processes");
//
//        // Adding child data
//        List<String> listNames = new ArrayList<String>();
//        listNames.add("All Task Lists");
//        for(int i = 0; i < lists.size(); i++){
//            listNames.add(lists.get(i).getName());
//        }
//        listNames.add("Create List");
//
//        List<String> processNames = new ArrayList<String>();
//        processNames.add("All Processes");
//
//        for(int i = 0; i < lists.size(); i++){
//            processNames.add("Processes for " + lists.get(i).getName());
//        }
//
//        listDataChild.put(listDataHeader.get(0), listNames); // Header, Child data
//        listDataChild.put(listDataHeader.get(1), processNames);}
}
