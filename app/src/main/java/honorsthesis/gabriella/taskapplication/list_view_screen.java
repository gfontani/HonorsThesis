package honorsthesis.gabriella.taskapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Gabriella on 11/22/2016.
 */
public class list_view_screen extends Fragment {

    private ThesisList list = new ThesisList();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_view_screen, container, false);
        displayList(v);
        return v;
        //get list from database
    }

    public void onClickTask(View view){

    }

    private void displayList(View view){
        View relativeLayout =  view.findViewById(R.id.listView);
        ((TextView)view.findViewById(R.id.listTitleText)).setText(list.getName());
        List<Task> tasks = list.getTasks();
        if(0 == tasks.size()){
            ((TextView)view.findViewById(R.id.noTasksText)).setVisibility(View.VISIBLE);
        }
        else{
            for(int i = 0; i < tasks.size(); i++){
                TextView task = (TextView) view.findViewById(R.id.tasksText);
                task.setText(tasks.get(i).getName());
                task.setVisibility(View.VISIBLE);
                ((RelativeLayout) relativeLayout).addView(task);
            }
        }
    }
}
