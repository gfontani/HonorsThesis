package honorsthesis.gabriella.honorsthesis;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnListSelectedListener} interface
 * to handle interaction events.
 * Use the {@link ListTaskView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListTaskView extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LIST_NAME = "All Tasks";

    private ThesisList list;

    private OnListSelectedListener mListener;

    public ListTaskView() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param listName name of list to display
     * @return A new instance of fragment ListTaskView.
     */
    public static ListTaskView newInstance(String listName) {
        ListTaskView fragment = new ListTaskView();
        Bundle args = new Bundle();
        ThesisList list = new ThesisList(listName);
        args.putString(LIST_NAME, listName);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //TODO: get list from database
            list = new ThesisList(getArguments().getString(LIST_NAME));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_task_view, container, false);
        displayList(view);
        setHasOptionsMenu(true);
        FloatingActionButton addTask = (FloatingActionButton) view.findViewById(R.id.add_task);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String listName) {
        if (mListener != null) {
            mListener.onListSelected(listName);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListSelectedListener) {
            mListener = (OnListSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void displayList(View view){
        View relativeLayout =  view.findViewById(R.id.list_view);
        List<Task> tasks = list.getTasks();
        if(0 != tasks.size()){
            ((TextView)view.findViewById(R.id.noTasksText)).setVisibility(View.GONE);
            for(int i = 0; i < tasks.size(); i++){
                TextView task = (TextView) view.findViewById(R.id.tasksText);
                task.setText(tasks.get(i).getName());
                task.setVisibility(View.VISIBLE);
                ((RelativeLayout) relativeLayout).addView(task);
            }
        }
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
    public interface OnListSelectedListener {
        void onListSelected(String listName);
    }
}
