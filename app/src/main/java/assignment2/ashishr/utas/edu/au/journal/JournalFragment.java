package assignment2.ashishr.utas.edu.au.journal;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class JournalFragment extends Fragment {

    public JournalFragment(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View inflatedView = inflater.inflate(R.layout.fragment_journal, container,false);

        long myDate = System.currentTimeMillis();

        SimpleDateFormat format = new SimpleDateFormat("dd MMM");
        String dateString = format.format(myDate);


        TextView date = inflatedView.findViewById(R.id.dateText);
        date.setText(dateString);

        Button calenderButton = inflatedView.findViewById(R.id.datePick);
        CalendarView myCalendar = inflatedView.findViewById(R.id.calendarView);

        calenderButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DialogFragment dFragment = new DatePickerFragment();
                dFragment.show(getFragmentManager(), "Date Picker");
            }
        });
        ArrayList<String> items = new ArrayList<String>();
        items.add("Went to the beach");
        items.add("Something i did today");
        items.add("a very long sentence that doesn't make sense a very long sentence that doesn't make sense a very long sentence that doesn't make sense");
        items.add("Summer");
        items.add("Maybe");
        ArrayAdapter<String> myListAdapter = new ArrayAdapter<String>(
                getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1,
                items);
        ListView myList = inflatedView.findViewById(R.id.myList);
        myList.setAdapter(myListAdapter);

        TextView entryText = inflatedView.findViewById(R.id.editText);
        entryText.setVisibility(View.GONE);
        return inflatedView;
    }
}
