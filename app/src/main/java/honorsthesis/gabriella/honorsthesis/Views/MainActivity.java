package honorsthesis.gabriella.honorsthesis.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.EditText;

import java.util.Collections;
import java.util.List;

import honorsthesis.gabriella.honorsthesis.BackEnd.*;
import honorsthesis.gabriella.honorsthesis.BackEnd.Process;
import honorsthesis.gabriella.honorsthesis.DataRepo.DataRepo;
import honorsthesis.gabriella.honorsthesis.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ListTaskFragment.OnListFragmentTaskInteractionListener, ListProcessFragment.OnListFragmentProcessInteractionListener {

    DataRepo mDataRepo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataRepo = new DataRepo(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        setUpNavDrawer(navigationView);
        //navigationView.inflateMenu(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setUpNavDrawer(NavigationView navigationView){
        List<String> lists = mDataRepo.getLists();
        Collections.sort(lists);
        Menu menu = navigationView.getMenu();
        SubMenu listMenu = menu.findItem(R.id.nav_list_group).getSubMenu();
        SubMenu processMenu = menu.findItem(R.id.nav_process_group).getSubMenu();
        listMenu.clear();
        processMenu.clear();
        listMenu.add(R.id.nav_list_group, R.id.nav_all_lists, 0, "All Lists");
        processMenu.add(R.id.nav_process_group, R.id.nav_all_processes, 0, "All Processes");
        for (String list:lists) {
            listMenu.add(R.id.nav_list_group, R.id.nav_list, 1, list);
            processMenu.add(R.id.nav_process_group, R.id.nav_process, 1, list);
        }
        listMenu.add(R.id.nav_list_group, R.id.nav_create_list, 2, "Create List");
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        try{


        switch(menuItem.getItemId()) {
            case R.id.nav_all_lists:
            case R.id.nav_list:
                fragment = ListTaskFragment.newInstance(1, menuItem.getTitle().toString());
                break;
            case R.id.nav_create_list:
                //TODO: make his go to new activity: create list
                fragment = ListTaskFragment.newInstance(1, "Creating list");
                break;
            case R.id.nav_all_processes:
            case R.id.nav_process:
                fragment = ListProcessFragment.newInstance(1, menuItem.getTitle().toString());
                break;
            default:
                fragment = ListTaskFragment.newInstance(1, menuItem.getTitle().toString());
        }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        //menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentTaskInteraction(Task item, String listName) {
        Intent intent = new Intent(this, ViewTaskActivity.class);
        intent.putExtra("task", item);
        intent.putExtra("list", listName);
        startActivity(intent);
    }

    @Override
    public void onListFragmentProcessInteraction(Process item, String listName) {
        Intent intent = new Intent(this, ViewProcessActivity.class);
        intent.putExtra("process", item);
        intent.putExtra("list", listName);
        startActivity(intent);
    }

    @Override
    public void onListFragmentStepInteraction(Step item, Process parent) {
        //TODO: create step view and replace ViewProcessAtivity with ViewStepActivity
        Intent intent = new Intent(this, ViewStepActivity.class);
        intent.putExtra("step", item);
        intent.putExtra("parent", parent);
        startActivity(intent);
    }

}
