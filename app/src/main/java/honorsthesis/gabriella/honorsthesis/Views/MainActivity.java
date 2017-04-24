package honorsthesis.gabriella.honorsthesis.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import java.util.Collections;
import java.util.List;

import honorsthesis.gabriella.honorsthesis.BackEnd.Process;
import honorsthesis.gabriella.honorsthesis.BackEnd.Step;
import honorsthesis.gabriella.honorsthesis.BackEnd.Task;
import honorsthesis.gabriella.honorsthesis.DataRepo.DataRepo;
import honorsthesis.gabriella.honorsthesis.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ListTaskFragment.OnListFragmentTaskInteractionListener, ListProcessFragment.OnListFragmentProcessInteractionListener, ListProcessFragment.OnListFragmentStepInteractionListener {

    DataRepo mDataRepo;
    NavigationView navigationView;

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

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        setUpNavDrawer(navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        Intent i = getIntent();
        String taskListName = i.getStringExtra("task");
        String processListName = i.getStringExtra("process");

        Fragment fragment;
        if (null != taskListName) {
            fragment = ListTaskFragment.newInstance(1, taskListName);
            SubMenu taskMenu = navigationView.getMenu().findItem(R.id.nav_list_group).getSubMenu();
            setMenuItemChecked(taskMenu, taskListName);
        } else if (null != processListName) {
            fragment = ListProcessFragment.newInstance(1, processListName);
            SubMenu processMenu = navigationView.getMenu().findItem(R.id.nav_process_group).getSubMenu();
            setMenuItemChecked(processMenu, processListName);
        } else {
            fragment = ListTaskFragment.newInstance(1, getResources().getText(R.string.all_tasks).toString());
            SubMenu taskMenu = navigationView.getMenu().findItem(R.id.nav_list_group).getSubMenu();
            setMenuItemChecked(taskMenu, getResources().getText(R.string.all_tasks).toString());
        }
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    public void setUpNavDrawer(NavigationView navigationView) {
        List<String> lists = mDataRepo.getLists();
        Collections.sort(lists);
        Menu menu = navigationView.getMenu();
        SubMenu listMenu = menu.findItem(R.id.nav_list_group).getSubMenu();
        SubMenu processMenu = menu.findItem(R.id.nav_process_group).getSubMenu();
        listMenu.clear();
        processMenu.clear();
        listMenu.add(R.id.nav_list_group, R.id.nav_all_lists, 0, getResources().getText(R.string.all_tasks).toString());
        processMenu.add(R.id.nav_process_group, R.id.nav_all_processes, 0, getResources().getText(R.string.all_processes).toString());
        for (String list : lists) {
            listMenu.add(R.id.nav_list_group, R.id.nav_list, 1, list);
            processMenu.add(R.id.nav_process_group, R.id.nav_process, 1, list);
        }

        MenuItem item = listMenu.add(R.id.nav_list_group, R.id.nav_create_list, 2, "Create List");
        item.setIcon(R.drawable.ic_add_circle_outline_24dp);
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
            switch (menuItem.getItemId()) {
                case R.id.nav_all_lists:
                case R.id.nav_list:
                    fragment = ListTaskFragment.newInstance(1, menuItem.getTitle().toString());
                    break;
                case R.id.nav_create_list:
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                    Intent intent = new Intent(this, CreateListActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.nav_all_processes:
                case R.id.nav_process:
                    fragment = ListProcessFragment.newInstance(1, menuItem.getTitle().toString());
                    break;
                default:
                    fragment = ListTaskFragment.newInstance(1, menuItem.getTitle().toString());
            }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        if(!menuItem.isChecked()){
            unCheckAllMenuItems(navigationView.getMenu());
            menuItem.setChecked(true);
        }
        // Set action bar title
        setTitle(menuItem.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void unCheckAllMenuItems(Menu menu) {
        int size = menu.size();
        for (int i = 0; i < size; i++) {
            final MenuItem item = menu.getItem(i);
            if(item.hasSubMenu()) {
                // Un check sub menu items
                unCheckAllMenuItems(item.getSubMenu());
            } else {
                item.setChecked(false);
            }
        }
    }

    private void setMenuItemChecked(Menu menu, String itemName) {
        int size = menu.size();
        for (int i = 0; i < size; i++) {
            final MenuItem item = menu.getItem(i);
            if(item.getTitle().equals(itemName)) {
                //check menu item if it matches the name
                item.setChecked(true);
                return;
            }
        }
    }

    @Override
    public void onListFragmentTaskClick(Task item, String listName) {
        try {
            Intent intent = new Intent(this, ViewTaskActivity.class);
            intent.putExtra("task", item);
            intent.putExtra("list", listName);
            startActivity(intent);
        } catch (Exception e) {
            System.out.println("here");
        }
    }

    @Override
    public void onListFragmentTaskCheck(Task task, String listName) {
        mDataRepo.removeTask(task);
        Fragment fragment = ListTaskFragment.newInstance(1, listName);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

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
        Intent intent = new Intent(this, ViewStepActivity.class);
        intent.putExtra("step", item);
        intent.putExtra("parent", parent);
        startActivity(intent);
    }
}
