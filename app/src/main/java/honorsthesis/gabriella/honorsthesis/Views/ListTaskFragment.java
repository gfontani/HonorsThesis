package honorsthesis.gabriella.honorsthesis.Views;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
        mDataRepo = new DataRepo(this.getContext());
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);

            list = new ThesisList(getArguments().getString(ARG_LIST_NAME));
            list.tasks = mDataRepo.getTasks(list.getName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_task, container, false);

        if(list.getTasks().size() > 0) {
            ((TextView)view.findViewById(R.id.noTasksText)).setVisibility(View.GONE);
            View recView = view.findViewById(R.id.list);
            // Set the adapter
            if (recView instanceof RecyclerView) {
                Context context = view.getContext();
                RecyclerView recyclerView = (RecyclerView) recView;
                if (mColumnCount <= 1) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                } else {
                    recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                }
                recyclerView.setAdapter(new TaskRecyclerViewAdapter(list.getTasks(), list.getName(), mListener));
            }
        }else{
            ((RecyclerView)view.findViewById(R.id.list)).setVisibility(View.GONE);
        }

        FloatingActionButton addTask = (FloatingActionButton) view.findViewById(R.id.add_task2);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        return view;
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentTaskInteractionListener {
        void onListFragmentTaskInteraction(Task item, String listName);
    }
}