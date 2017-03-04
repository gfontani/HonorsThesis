package honorsthesis.gabriella.honorsthesis;

import android.content.Context;
import android.net.Uri;
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
 * {@link ListProcessView.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListProcessView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListProcessView extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LIST_NAME = "All Tasks";

    private ThesisList list;

    private OnFragmentInteractionListener mListener;

    public ListProcessView() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment ListProcessView.
     */
    public static ListProcessView newInstance(String listName) {
        ListProcessView fragment = new ListProcessView();
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
            //TODO: get processes from database
            list = new ThesisList(getArguments().getString(LIST_NAME));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_process_view, container, false);
        displayList(view);
        setHasOptionsMenu(true);
        FloatingActionButton addTask = (FloatingActionButton) view.findViewById(R.id.add_process);
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
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction("name");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void displayList(View view){
        View relativeLayout =  view.findViewById(R.id.justForReference);
        List<Process> processes = list.getProcesses();
        if(0 != processes.size()){
            ((TextView)view.findViewById(R.id.noProcessesText)).setVisibility(View.GONE);
            for(int i = 0; i < processes.size(); i++){
                TextView task = (TextView) view.findViewById(R.id.processText);
                task.setText(processes.get(i).getName());
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String name);
    }
}
