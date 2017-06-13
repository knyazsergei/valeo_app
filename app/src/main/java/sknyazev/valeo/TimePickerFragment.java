package sknyazev.valeo;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Sergey on 01.06.2017.
 */

public class TimePickerFragment extends android.support.v4.app.DialogFragment implements TimePickerDialog.OnTimeSetListener{

    // declaration of interface, need to be implemented in MainActivity
    public interface OnTimeChangeListenerInterface{
        void onTimeChangeListener(String time);    // this method needs to be override in MainActivity to get the value of selected time
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    // callback to the onTimeChangeListener method in MainActivity to pass value of selected time
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // passing selected value to the MainActivity through onTimeChangeListener method
        ((OnTimeChangeListenerInterface) getActivity()).onTimeChangeListener(String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
    }

}