package honorsthesis.gabriella.honorsthesis.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import honorsthesis.gabriella.honorsthesis.BackEnd.Step;
import honorsthesis.gabriella.honorsthesis.BackEnd.Process;
import honorsthesis.gabriella.honorsthesis.Views.ListProcessFragment.OnListFragmentProcessInteractionListener;
import honorsthesis.gabriella.honorsthesis.R;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Process} and makes a call to the
 * specified {@link OnListFragmentProcessInteractionListener}.
 */
public class StepRecyclerViewAdapter extends RecyclerView.Adapter<StepRecyclerViewAdapter.ViewHolder> {

    private List<Step> mValues;
    private final Process mParent;
    private final OnListFragmentProcessInteractionListener mListener;
    private Context mContext;

    public StepRecyclerViewAdapter(Context context, List<Step> items, Process parentProcess, OnListFragmentProcessInteractionListener listener) {
        mContext = context;
        mValues = items;
        mParent = parentProcess;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    //TODO: implement this in the process view and create activities
                    mListener.onListFragmentStepInteraction(holder.mItem, mParent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void refresh(List<Step> steps){
        if(null != mValues){
            mValues.clear();
            mValues.addAll(steps);
        }else{
            mValues = steps;

        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public Step mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
