package assignment2.ashishr.utas.edu.au.journal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;


public class JournalFragment extends Fragment {
    private ListView myList;
    private FragmentManager fm;
    private JournalAdapter entryListAdapter;
    ArrayList<Entry> entries = new ArrayList<Entry>();
    Database databaseConnection;
    SQLiteDatabase db;
    private int mode = 1;
    private int selectedEntry;
    private Spinner moodSpinner;
    private MoodAdapter moodAdapter;

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

        //++ruihao   make sure the database can select journal by date when get open
        final Calendar calendar = Calendar.getInstance();
        final int currentDate = calendar.get(Calendar.DAY_OF_MONTH);
        final int currentMonth = calendar.get(Calendar.MONTH);
        final int currentYear = calendar.get(Calendar.YEAR);
        final TextView date = inflatedView.findViewById(R.id.dateText);//++++ruihao
        date.setText(currentYear+"/"+String.format("%02d",currentMonth+1)+"/"+String.format("%02d",currentDate));
        entries = JournalTable.selectByDate(db,date.getText().toString());//++++ruihao

        //show all the data
        //entries = JournalTable.selectAll(db);
        for (int i = 0; i < entries.size(); i++) {
            Entry j = entries.get(i);
            Log.d("FOUND", j.getmEntryID() + ": " + j.getmEntryTitle());
        }

        myList = inflatedView.findViewById(R.id.myList);
        entryListAdapter = new JournalAdapter(getActivity().getApplicationContext(), R.layout.custom_list_layout, entries);
        myList.setAdapter(entryListAdapter);

        moodSpinner = (Spinner) inflatedView.findViewById(R.id.spinnerMood);
        moodAdapter = new MoodAdapter(getContext(),
                new Integer[]{R.drawable.depressed_face, R.drawable.sad_face, R.drawable.neutral_face, R.drawable.smile_face, R.drawable.happy_face});
        moodSpinner.setAdapter(moodAdapter);
        moodSpinner.setSelection(2);

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
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    DeleteEntry(p.getmEntryID());
                                    Log.d("FOUND","DELETED");
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    Log.d("FOUND","NO");
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Delete").setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();

                    //Snackbar.make(arg0, "Entry Deleted", Snackbar.LENGTH_LONG).
                            //setAction("Action", null).show();
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
        //date +ruihao

        Button calenderButton = inflatedView.findViewById(R.id.datePick);
        calenderButton.setOnClickListener(new AdapterView.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(
                        getActivity(), new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                            {
                                String datestr = year+"/"+String.format("%02d",(month+1))+"/"+String.format("%02d",dayOfMonth);
                                date.setText(datestr);//month start from 0 to 11,so month should+1

                                entries = JournalTable.selectByDate(db,datestr);
                                entryListAdapter = new JournalAdapter(getActivity().getApplicationContext(), R.layout.custom_list_layout, entries);
                                myList.setAdapter(entryListAdapter);
                            }
                        },currentYear,currentMonth,currentDate);

                datePickerDialog.show();


            }

        });

        /*long myDate = System.currentTimeMillis();

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
        */

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
                        moodSpinner.setSelection(2);
                        EditText t1 = (EditText) inflatedView.findViewById(R.id.inputText);
                        EditText t2 = (EditText) inflatedView.findViewById(R.id.inputTitle);
                        t2.requestFocus();

                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(t2, InputMethodManager.SHOW_IMPLICIT);

                        long myTime = System.currentTimeMillis();

                        SimpleDateFormat format = new SimpleDateFormat("h:mm a");
                        String dateString = format.format(myTime);
                        TextView timeText = inflatedView.findViewById(R.id.inputTime);
                        timeText.setText(dateString);

                        DisplayFragment(true);
                        //Log.d("FOUND", "YAY");

                        moodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView adapterView, View view, int i, long l) {
                                //Toast.makeText(getActivity().getApplicationContext(),""+ a[i], Toast.LENGTH_SHORT).show();
                                Log.d("FOUND",""+moodAdapter.getMood(i));
                            }

                            @Override
                            public void onNothingSelected(AdapterView adapterView) {

                            }
                        });
                    }
                }
            }
        });




        if(entries.isEmpty()){
            inflatedView.findViewById(R.id.helpEntries).setVisibility(View.VISIBLE);
            //Log.d("FOUND","EMPTY");
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
        TextView date = getActivity().findViewById(R.id.dateText);//ruihao
        TextView timeText = getActivity().findViewById(R.id.inputTime);


        Log.d("FOUND",timeText.getText().toString());
        Entry entry = new Entry();
        entry.setmEntryTitle(title.getText().toString());
        entry.setmEntryText(text.getText().toString());
        entry.setmEntryDate(date.getText().toString());
        Log.d("entrydate",entry.getmEntryDate());
        entry.setmEntryTime(timeText.getText().toString());
        entry.setmEntryMood(moodSpinner.getSelectedItemPosition());

        JournalTable.insert(db,entry);
        //entries = JournalTable.selectAll(db);
        entries = JournalTable.selectByDate(db,entry.getmEntryDate());
        entryListAdapter = new JournalAdapter(getActivity().getApplicationContext(), R.layout.custom_list_layout, entries);
        myList.setAdapter(entryListAdapter);
    }


    public void SelectEntry(int id){
        Entry entry = JournalTable.selectByID(db,id);
        EditText title = getActivity().findViewById(R.id.inputTitle);
        EditText text = getActivity().findViewById(R.id.inputText);
        TextView time = getActivity().findViewById(R.id.inputTime);
        title.setText(entry.getmEntryTitle());
        text.setText(entry.getmEntryText());
        time.setText(entry.getmEntryTime());
        moodSpinner.setSelection(entry.getmEntryMood());
        DisplayFragment(true);
        title.requestFocus();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(title, InputMethodManager.SHOW_IMPLICIT);

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("FOUND","CLICK");
            }
        });
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
        TextView time = getActivity().findViewById(R.id.inputTime);
        entry.setmEntryTitle(title.getText().toString());
        entry.setmEntryText(text.getText().toString());
        entry.setmEntryTime(time.getText().toString());
        entry.setmEntryMood(moodSpinner.getSelectedItemPosition());
        //Log.d("FOUND","ID: "+entry.getmEntryID());
        //Log.d("FOUND","Title: "+entry.getmEntryTitle());

        JournalTable.update(db,entry,id);
        entries = JournalTable.selectByDate(db,entry.getmEntryDate());
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
