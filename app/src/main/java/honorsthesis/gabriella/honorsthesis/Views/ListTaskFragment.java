package honorsthesis.gabriella.honorsthesis.Views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import honorsthesis.gabriella.honorsthesis.Adapters.TaskRecyclerViewAdapter;
import honorsthesis.gabriella.honorsthesis.BackEnd.Task;
import honorsthesis.gabriella.honorsthesis.BackEnd.ThesisList;
import honorsthesis.gabriella.honorsthesis.DataRepo.DataRepo;
import honorsthesis.gabriella.honorsthesis.R;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentTaskInteractionListener}
 * interface.
 */
public class ListTaskFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private static final String ARG_LIST_NAME = "All Tasks";
    private ThesisList list;
    private DataRepo mDataRepo;
    private OnListFragmentTaskInteractionListener mListener;

    public TaskRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListTaskFragment() {
    }

    @SuppressWarnings("unused")
    public static ListTaskFragment newInstance(int columnCount, String listName) {
        ListTaskFragment fragment = new ListTaskFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(ARG_LIST_NAME, listName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mDataRepo = new DataRepo(this.getContext());
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            list = new ThesisList(getArguments().getString(ARG_LIST_NAME));
            if (list.getName().equals(getResources().getText(R.string.all_tasks).toString())) {
                list.setTasks(mDataRepo.getAllTasks());
                list.setProcesses(mDataRepo.getAllProcesses());
            } else {
                list.setTasks(mDataRepo.getTasks(list.getName()));
                list.setProcesses(mDataRepo.getProcesses(list.getName()));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_task, container, false);
        getActivity().setTitle(list.getName());
        if (list.getTasks().size() > 0) {
            view.findViewById(R.id.noTasksText).setVisibility(View.GONE);
            view.findViewById(R.id.list).setVisibility(View.VISIBLE);
            View recView = view.findViewById(R.id.list);
            // Set the adapter
            if (recView instanceof RecyclerView) {
                Context context = view.getContext();
                mRecyclerView = (RecyclerView) recView;
                mLayoutManager = new LinearLayoutManager(context);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mAdapter = new TaskRecyclerViewAdapter(context, list.getTasks(), list.getName(), mListener);
                mRecyclerView.setAdapter(mAdapter);
            }
        } else {
            view.findViewById(R.id.list).setVisibility(View.GONE);
            view.findViewById(R.id.noTasksText).setVisibility(View.VISIBLE);
        }

        FloatingActionButton addTask = (FloatingActionButton) view.findViewById(R.id.add_task2);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to the create task activity
                MainActivity ma = ((MainActivity) getActivity());
                Intent intent = new Intent(ma, CreateTaskActivity.class);
                intent.putExtra("listName", list.getName());
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.list_task_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_list) {
            mDataRepo.removeList(list);
            FragmentManager fragManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
            ListTaskFragment nextFrag = new ListTaskFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_COLUMN_COUNT, 1);
            args.putString(ARG_LIST_NAME, getResources().getText(R.string.all_tasks).toString());
            nextFrag.setArguments(args);
            fragmentTransaction.hide(ListTaskFragment.this);
            fragmentTransaction.replace(R.id.content_frame, nextFrag);
            fragmentTransaction.commit();

            //reset the nav drawer menu to reflect deleted item
            MainActivity ma = ((MainActivity) getActivity());
            NavigationView navigationView = (NavigationView) ma.findViewById(R.id.nav_view);
            ma.setUpNavDrawer(navigationView);
            //navigationView.inflateMenu(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(ma);

            return true;
        } else if (id == R.id.action_sort_list) {
            //TODO: implement this!
        } else if (id == R.id.action_delete_all) {
            for (Task task : list.getTasks()) {
                mDataRepo.removeTask(task);
            }
            list.getTasks().clear();
            mAdapter.notifyDataSetChanged();
            ((RecyclerView) getView().findViewById(R.id.list)).setVisibility(View.GONE);
            ((TextView) getView().findViewById(R.id.noTasksText)).setVisibility(View.VISIBLE);
        } else if (id == R.id.action_edit) {
            try {
                MainActivity ma = ((MainActivity) getActivity());
                Intent intent = new Intent(ma, EditListActivity.class);
                intent.putExtra("list", list);
                startActivity(intent);
            } catch (Exception e) {
                System.out.println("here");
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentTaskInteractionListener) {
            mListener = (OnListFragmentTaskInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentTaskInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnListFragmentTaskInteractionListener {
        void onListFragmentTaskClick(Task item, String listName);

        void onListFragmentTaskCheck(Task task, String listName);
    }
}
