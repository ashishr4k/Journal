package assignment2.ashishr.utas.edu.au.journal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.os.Debug;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.ToolbarWidgetWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;


public class JournalFragment extends Fragment {
    private FragmentManager fm;

    public JournalFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View inflatedView = inflater.inflate(R.layout.fragment_journal, container,false);

        Database databaseConnection = new Database(getActivity());
        final SQLiteDatabase db = databaseConnection.open();

        Entry entry1 = new Entry();
        entry1.setmEntryTitle("One");
        entry1.setmEntryText("A LINE OF TEXT");
        entry1.setmEntryDate("10/10/10");
        entry1.setmEntryTime("5:00");
        entry1.setmEntryMood("SAD");

        Entry entry2 = new Entry();
        entry2.setmEntryTitle("TWO");
        entry2.setmEntryText("AAAAAAAAAAAAAA LINE OF TEXT AAAAAAAAAAAAAA LINE OF TEXTAAAAAAAAAAAAAA LINE OF TEXTAAAAAAAAAAAAAA LINE OF TEXT");
        entry2.setmEntryDate("11111");
        entry2.setmEntryTime("5222:00");
        entry2.setmEntryMood("WEWEWE");

        //JournalTable.insert(db,entry1);
        //JournalTable.insert(db,entry2);

        final ArrayList<Entry> entries = JournalTable.selectAll(db);
        //show all the data
        for (int i=0; i<entries.size(); i++)
        {
            Entry j = entries.get(i);
            Log.d("FOUND",j.getmEntryID() + ": " + j.getmEntryTitle());
        }

        ListView myList = inflatedView.findViewById(R.id.myList);
        JournalAdapter entryListAdapter = new JournalAdapter(getActivity().getApplicationContext(),R.layout.custom_list_layout,entries);
        myList.setAdapter(entryListAdapter);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Entry p = entries.get(i);
                //Log.d("FOUND",p.getmEntryTitle());
                //DisplayFragment(true);
            }
        });
        myList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                Log.d("FOUND","pos: " + pos);
                Toolbar bar = (Toolbar) inflatedView.findViewById(R.id.barSelectEntry);
                if(bar.getVisibility() == View.GONE) {
                    bar.setVisibility(View.VISIBLE);
                    Log.d("FOUND",""+bar.getY());
                }else{
                    Log.d("FOUND",""+bar.getHeight());
                    /*
                    bar.animate().translationY(144f)
                            .alpha(200).setDuration(1000)
                            .setInterpolator(new DecelerateInterpolator());
                    */
                    bar.setVisibility(View.GONE);
                }
                return true;
            }
        });

        long myDate = System.currentTimeMillis();

        SimpleDateFormat format = new SimpleDateFormat("dd MMM");
        String dateString = format.format(myDate);


        TextView date = inflatedView.findViewById(R.id.dateText);
        date.setText(dateString);

        Button calenderButton = inflatedView.findViewById(R.id.datePick);
        CalendarView myCalendar = inflatedView.findViewById(R.id.calendarView);

        calenderButton.setOnClickListener(new AdapterView.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DialogFragment dFragment = new DatePickerFragment();
                dFragment.show(getFragmentManager(), "Date Picker");
            }
        });

        fm = getChildFragmentManager();
        DisplayFragment(false);

        FloatingActionButton fab = (FloatingActionButton) inflatedView.findViewById(R.id.btnAddEntry);
        fab.setOnClickListener(new AdapterView.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Intent in = new Intent(getActivity(), InsertActivity.class);
                //startActivity(in);
                if(fm == null) {
                    //Log.d("FOUND", "NULL");
                }else {

                    if (fm.findFragmentById(R.id.entryFragment).isVisible()) {

                        EditText title = getActivity().findViewById(R.id.inputTitle);
                        EditText text = getActivity().findViewById(R.id.inputText);

                        Entry entry = new Entry();
                        entry.setmEntryTitle(title.getText().toString());
                        entry.setmEntryText(text.getText().toString());
                        entry.setmEntryDate("");
                        entry.setmEntryTime("");
                        entry.setmEntryMood("");

                        //JournalTable.insert(db,entry);
                        //entryListAdapter.clear();
                        //myList.setAdapter(entryListAdapter);
                        //entryListAdapter.notifyDataSetChanged();
                       //entryListAdapter.addAll(entries);
                        //Log.d("FOUND","Item: "+entryListAdapter.getItem(8).getmEntryID());

                        //entryListAdapter.notifyDataSetChanged();
                        //entryListAdapter.add(entry);
                        //myList.setAdapter(entryListAdapter);

                        DisplayFragment(false);
                        Log.d("FOUND", "ADDING");

                        final InputMethodManager imm = (InputMethodManager) getActivity()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                        Snackbar.make(view, "Entry Added", Snackbar.LENGTH_LONG).
                        setAction("Action", null).show();

                        EditText t1 = (EditText) inflatedView.findViewById(R.id.inputText);
                        EditText t2 = (EditText) inflatedView.findViewById(R.id.inputTitle);
                        t1.setText("");
                        t2.setText("");

                    } else {
                        DisplayFragment(true);
                        //Log.d("FOUND", "YAY");
                    }
                }
            }
        });



        return inflatedView;
    }

    public void DisplayFragment(boolean state){
        if(state){
            fm.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .show(fm.findFragmentById(R.id.entryFragment))
                    .commit();
        }else{
            fm.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .hide(fm.findFragmentById(R.id.entryFragment))
                    .commit();
        }
    }

    /*
    public void AddEntry(){
        Database databaseConnection = new Database(getActivity());
        final SQLiteDatabase db = databaseConnection.open();

        EditText title = getActivity().findViewById(R.id.inputTitle);
        EditText text = getActivity().findViewById(R.id.inputText);
        Log.d("FOUND",title.getText().toString());

        Entry entry = new Entry();
        entry.setmEntryTitle(title.getText().toString());
        entry.setmEntryText(text.getText().toString());
        entry.setmEntryDate("");
        entry.setmEntryTime("");
        entry.setmEntryMood("");
        JournalTable.insert(db,entry);


        final ArrayList<Entry> entries = JournalTable.selectAll(db);
        //show all the data
        for (int i=0; i<entries.size(); i++)
        {
            Entry j = entries.get(i);
            Log.d("FOUND",j.getmEntryTitle() + ": " + j.getmEntryText());
        }
    }*/
}
