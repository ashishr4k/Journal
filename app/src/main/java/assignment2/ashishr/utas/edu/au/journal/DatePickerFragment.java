package assignment2.ashishr.utas.edu.au.journal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/* code source https://android--code.blogspot.com/2015/08/android-datepickerdialog-example.html */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(getActivity(),this,year,month,day);
        return  dpd;
    }

    public void onDateSet(DatePicker view, int year, int month, int day){
        // Do something with the chosen date
        TextView dateText = getActivity().findViewById(R.id.dateText);

        // Create a Date variable/object with user chosen date
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(year, month, day, 0, 0, 0);
        Date chosenDate = cal.getTime();

        SimpleDateFormat format = new SimpleDateFormat("dd MMM");
        String dateString = format.format(chosenDate);

        // Display the chosen date to app interface
        dateText.setText(dateString);
    }
}
