package honorsthesis.gabriella.honorsthesis.Views;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
 * Created by Gabriella on 4/19/2017.
 */

public class DatePickerFragment extends DialogFragment{

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Calendar mCalendar;

    public DatePickerFragment(){
        //this is needed to not break other things, but does not have to be implemented
    }

    public DatePickerFragment(DatePickerDialog.OnDateSetListener listener){
        mDateSetListener = (DatePickerDialog.OnDateSetListener) listener;
        mCalendar = Calendar.getInstance();
    }

    public DatePickerFragment(DatePickerDialog.OnDateSetListener listener, Calendar calendar){
        mDateSetListener = (DatePickerDialog.OnDateSetListener) listener;
        mCalendar = calendar;
    }

    public void setDate(Calendar calendar){
        mCalendar = calendar;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(),
                mDateSetListener, mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
    }
}
