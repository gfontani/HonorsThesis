package honorsthesis.gabriella.honorsthesis.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import honorsthesis.gabriella.honorsthesis.BackEnd.Task;
import honorsthesis.gabriella.honorsthesis.R;
import honorsthesis.gabriella.honorsthesis.Views.ListTaskFragment;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Task} and makes a call to the
 * specified {@link ListTaskFragment.OnListFragmentTaskInteractionListener}.
 */
public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder> {

    private final List<Task> mValues;
    private final String mParentListName;
    private final ListTaskFragment.OnListFragmentTaskInteractionListener mListener;
    private Context mContext;
    private boolean mHasCheckbox;

    public TaskRecyclerViewAdapter(Context context, List<Task> items, String listName, ListTaskFragment.OnListFragmentTaskInteractionListener listener, boolean hasCheckbox) {
        mContext = context;
        mValues = items;
        mParentListName = listName;
        mListener = listener;
        mHasCheckbox = hasCheckbox;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(mHasCheckbox) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_item_with_checkbox, parent, false);
        } else{
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_item, parent, false);
        }
        return new ViewHolder(view, mHasCheckbox);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        //holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).getName());

        if(mHasCheckbox) {
            //handle checkbox
            holder.mCheckBox.setOnCheckedChangeListener(null);

            holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //delete task
                    if (null != mListener) {
                        mListener.onListFragmentTaskCheck(holder.mItem, mParentListName);
                    }
                }
            });
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentTaskClick(holder.mItem, mParentListName);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final CheckBox mCheckBox;
        public Task mItem;

        public ViewHolder(View view, boolean hasCheckbox) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
            if(hasCheckbox){
                mCheckBox = (CheckBox) view.findViewById(R.id.task_check_box);
            } else{
                mCheckBox = null;
            }
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
