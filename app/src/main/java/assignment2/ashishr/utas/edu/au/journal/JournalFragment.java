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
import android.util.SparseBooleanArray;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.util.ArrayList;


public class JournalFragment extends Fragment {
    private ListView myList;
    private FragmentManager fm;
    private JournalAdapter entryListAdapter;
    ArrayList<Entry> entries = new ArrayList<Entry>();
    Database databaseConnection;
    SQLiteDatabase db;
    private int mode = 1;
    private int selectedEntry;

    public JournalFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflatedView = inflater.inflate(R.layout.fragment_journal, container, false);

        databaseConnection = new Database(getActivity());
        db = databaseConnection.open();

        /*Entry entry1 = new Entry();
        entry1.setmEntryTitle("One");
        entry1.setmEntryText("A LINE OF TEXT");
        entry1.setmEntryDate("10/10/10");
        entry1.setmEntryTime("5:00");
        entry1.setmEntryMood("SAD");
        JournalTable.insert(db,entry1);*/

        entries = JournalTable.selectAll(db);
        //show all the data
        for (int i = 0; i < entries.size(); i++) {
            Entry j = entries.get(i);
            Log.d("FOUND", j.getmEntryID() + ": " + j.getmEntryTitle());
        }

        myList = inflatedView.findViewById(R.id.myList);
        entryListAdapter = new JournalAdapter(getActivity().getApplicationContext(), R.layout.custom_list_layout, entries);
        myList.setAdapter(entryListAdapter);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Entry p = entries.get(i);
                SelectEntry(p.getmEntryID());
                //DeleteEntry(p.getmEntryID());
                //Log.d("FOUND","ID: "+p.getmEntryID());
                //DisplayFragment(true);
            }
        });
        myList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                //Log.d("FOUND", "pos: " + pos);
                final Entry p = entries.get(pos);
                if(mode == 1) {
                    DeleteEntry(p.getmEntryID());
                    Snackbar.make(arg0, "Entry Deleted", Snackbar.LENGTH_LONG).
                            setAction("Action", null).show();
                }

                Toolbar bar = (Toolbar) inflatedView.findViewById(R.id.barSelectEntry);
                if (bar.getVisibility() == View.GONE) {
                    //bar.setVisibility(View.VISIBLE);
                    //Log.d("FOUND", "" + bar.getY());
                } else {
                    //Log.d("FOUND", "" + bar.getHeight());
                    /*
                    bar.animate().translationY(144f)
                            .alpha(200).setDuration(1000)
                            .setInterpolator(new DecelerateInterpolator());
                    */
                    //bar.setVisibility(View.GONE);
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

        calenderButton.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dFragment = new DatePickerFragment();
                dFragment.show(getFragmentManager(), "Date Picker");
            }
        });

        fm = getChildFragmentManager();
        DisplayFragment(false);

        FloatingActionButton fab = (FloatingActionButton) inflatedView.findViewById(R.id.btnAddEntry);
        fab.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                inflatedView.findViewById(R.id.helpEntries).setVisibility(View.GONE);
                if (fm == null) {
                    //Log.d("FOUND", "NULL");
                } else {

                    if (fm.findFragmentById(R.id.entryFragment).isVisible()) {
                        if(mode == 1) {
                            AddEntry();
                            //Log.d("FOUND","ADDED");
                        }
                        else if(mode == 2){
                            UpdateEntry(selectedEntry);
                            mode = 1;
                            //Log.d("FOUND","EDIT");
                        }
                        //entryListAdapter.notifyDataSetChanged();

                        DisplayFragment(false);

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
                        mode = 1;
                        EditText t1 = (EditText) inflatedView.findViewById(R.id.inputText);
                        EditText t2 = (EditText) inflatedView.findViewById(R.id.inputTitle);
                        t2.requestFocus();

                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(t2, InputMethodManager.SHOW_IMPLICIT);
                        DisplayFragment(true);
                        //Log.d("FOUND", "YAY");

                    }
                }
            }
        });
        Spinner spinner = (Spinner) inflatedView.findViewById(R.id.spinnerMood);
        MoodAdapter adapter = new MoodAdapter(getContext(),
                new Integer[]{R.drawable.disappointed, R.drawable.frowning, R.drawable.neutral, R.drawable.slightlysmiling, R.drawable.openmouth});
        spinner.setAdapter(adapter);

        if(entries.isEmpty()){
            inflatedView.findViewById(R.id.helpEntries).setVisibility(View.VISIBLE);
            Log.d("FOUND","EMPTY");
        }else{
            inflatedView.findViewById(R.id.helpEntries).setVisibility(View.GONE);
        }
        return inflatedView;
    }

    public void AddEntry(){
        //Database databaseConnection = new Database(getActivity());
        //final SQLiteDatabase db = databaseConnection.open();

        EditText title = getActivity().findViewById(R.id.inputTitle);
        EditText text = getActivity().findViewById(R.id.inputText);

        Entry entry = new Entry();
        entry.setmEntryTitle(title.getText().toString());
        entry.setmEntryText(text.getText().toString());
        entry.setmEntryDate("");
        entry.setmEntryTime("");
        entry.setmEntryMood("");

        JournalTable.insert(db,entry);
        entries = JournalTable.selectAll(db);
        entryListAdapter = new JournalAdapter(getActivity().getApplicationContext(), R.layout.custom_list_layout, entries);
        myList.setAdapter(entryListAdapter);
    }
    public void SelectEntry(int id){
        Entry entry = JournalTable.selectByID(db,id);
        EditText title = getActivity().findViewById(R.id.inputTitle);
        EditText text = getActivity().findViewById(R.id.inputText);
        title.setText(entry.getmEntryTitle());
        text.setText(entry.getmEntryText());
        DisplayFragment(true);
        //Log.d("FOUND","ID: "+entry.getmEntryID());
        //Log.d("FOUND","Title: "+entry.getmEntryTitle());
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.btnAddEntry);
        //fab.setVisibility(View.GONE);
        mode = 2;
        selectedEntry = id;

    }
    public void UpdateEntry(int id){
        //Entry entry = new Entry();
        Entry entry = JournalTable.selectByID(db,id);

        EditText title = getActivity().findViewById(R.id.inputTitle);
        EditText text = getActivity().findViewById(R.id.inputText);

        entry.setmEntryTitle(title.getText().toString());
        entry.setmEntryText(text.getText().toString());
        //Log.d("FOUND","ID: "+entry.getmEntryID());
        //Log.d("FOUND","Title: "+entry.getmEntryTitle());

        JournalTable.update(db,entry,id);
        entries = JournalTable.selectAll(db);
        entryListAdapter = new JournalAdapter(getActivity().getApplicationContext(), R.layout.custom_list_layout, entries);
        myList.setAdapter(entryListAdapter);
    }
    public void DeleteEntry(int id){
        JournalTable.deleteById(db,id);
        //Log.d("FOUND","DELETING: " + id);
        entries = JournalTable.selectAll(db);
        entryListAdapter = new JournalAdapter(getActivity().getApplicationContext(), R.layout.custom_list_layout, entries);
        myList.setAdapter(entryListAdapter);
        if(entries.isEmpty()){
            getActivity().findViewById(R.id.helpEntries).setVisibility(View.VISIBLE);
            Log.d("FOUND","EMPTY");
        }else{
            getActivity().findViewById(R.id.helpEntries).setVisibility(View.GONE);
        }
    }
    public void DisplayFragment(boolean state) {
        if (state) {
            fm.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .show(fm.findFragmentById(R.id.entryFragment))
                    .commit();
        } else {
            fm.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .hide(fm.findFragmentById(R.id.entryFragment))
                    .commit();
        }
    }
}
