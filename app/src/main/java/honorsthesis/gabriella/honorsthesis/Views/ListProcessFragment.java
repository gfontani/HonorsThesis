package honorsthesis.gabriella.honorsthesis.Views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import honorsthesis.gabriella.honorsthesis.Adapters.ProcessRecyclerViewAdapter;
import honorsthesis.gabriella.honorsthesis.BackEnd.Process;
import honorsthesis.gabriella.honorsthesis.BackEnd.Step;
import honorsthesis.gabriella.honorsthesis.BackEnd.ThesisList;
import honorsthesis.gabriella.honorsthesis.DataRepo.DataRepo;
import honorsthesis.gabriella.honorsthesis.R;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentProcessInteractionListener}
 * interface.
 */
public class ListProcessFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private static final String LIST_NAME = "All Processes";
    private ThesisList list;
    private DataRepo mDataRepo;
    private OnListFragmentProcessInteractionListener mListener;

    public ProcessRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListProcessFragment() {
    }

    @SuppressWarnings("unused")
    public static ListProcessFragment newInstance(int columnCount, String listName) {
        ListProcessFragment fragment = new ListProcessFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(LIST_NAME, listName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mDataRepo = new DataRepo(this.getContext());

        if (getArguments() != null) {
            list = new ThesisList(getArguments().getString(LIST_NAME));
            if (list.getName().equals(getResources().getText(R.string.all_processes).toString())) {
                list.setTasks(mDataRepo.getAllTasks());
                list.setProcesses(mDataRepo.getAllProcesses());
            } else {
                list.setTasks(mDataRepo.getTasks(list.getName()));
                list.setProcesses(mDataRepo.getProcesses(list.getName()));
            }
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_process, container, false);
        if(list.getName().equals(getResources().getText(R.string.all_processes).toString())){
            getActivity().setTitle(list.getName());
        } else{
            getActivity().setTitle("Processes for " + list.getName());
        }
        if (list.getProcesses().size() > 0) {
            ((TextView) view.findViewById(R.id.noProcessesText)).setVisibility(View.GONE);
            ((RecyclerView) view.findViewById(R.id.list)).setVisibility(View.VISIBLE);
            View recView = view.findViewById(R.id.list);
            // Set the adapter
            if (recView instanceof RecyclerView) {
                Context context = view.getContext();
                mRecyclerView = (RecyclerView) recView;
                mLayoutManager = new LinearLayoutManager(context);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mAdapter = new ProcessRecyclerViewAdapter(list.getProcesses(), list.getName(), mListener);
                mRecyclerView.setAdapter(mAdapter);
            }
        } else {
            view.findViewById(R.id.list).setVisibility(View.GONE);
            view.findViewById(R.id.noProcessesText).setVisibility(View.VISIBLE);
        }

        FloatingActionButton addTask = (FloatingActionButton) view.findViewById(R.id.add_process);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to the create process activity
                MainActivity ma = ((MainActivity) getActivity());
                Intent intent = new Intent(ma, CreateProcessActivity.class);
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
        inflater.inflate(R.menu.list_process_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_all) {
            for (Process process : list.getProcesses()) {
                mDataRepo.removeProcess(process);
            }
            list.getProcesses().clear();
            mAdapter.notifyDataSetChanged();
            ((RecyclerView) getView().findViewById(R.id.list)).setVisibility(View.GONE);
            ((TextView) getView().findViewById(R.id.noProcessesText)).setVisibility(View.VISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentProcessInteractionListener) {
            mListener = (OnListFragmentProcessInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentProcessInteractionListener");
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
    public interface OnListFragmentProcessInteractionListener {
        void onListFragmentProcessInteraction(Process item, String listName);
    }

    public interface OnListFragmentStepInteractionListener {
        void onListFragmentStepInteraction(Step item, Process parent);
    }
}
