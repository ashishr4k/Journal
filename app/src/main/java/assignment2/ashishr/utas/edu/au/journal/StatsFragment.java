package assignment2.ashishr.utas.edu.au.journal;

import android.app.DatePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class StatsFragment extends Fragment {

    Database databaseConnection;
    SQLiteDatabase db;
    ArrayList<Entry> entries = new ArrayList<Entry>();
    String dateString;
    int depressed;
    int sad;
    int neutral;
    int happy;
    int excited;


    BarChart barChart;
    BarData theData;
    ArrayList<BarEntry> barEntries;
    BarDataSet barDataSet;

    public String[] moods = new String[] {"Depressed","Unhappy","Neutral","Happy","Excited"};

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    public StatsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View inflatedView = inflater.inflate(R.layout.fragment_stats, container,false);

        databaseConnection = new Database(getActivity());
        db = databaseConnection.open();

        depressed = 0;
        sad = 0;
        neutral = 0;
        happy = 0;
        excited = 0;
        entries =null;


        barChart = inflatedView.findViewById(R.id.bargraph);

        barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, depressed));
        barEntries.add(new BarEntry(1, sad));
        barEntries.add(new BarEntry(2, neutral));
        barEntries.add(new BarEntry(3, happy));
        barEntries.add(new BarEntry(4, excited));
        barDataSet = new BarDataSet(barEntries, "");

        barDataSet.setColors(ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark),
                ContextCompat.getColor(getContext(), android.R.color.holo_blue_light),
                ContextCompat.getColor(getContext(), android.R.color.darker_gray),
                ContextCompat.getColor(getContext(), android.R.color.holo_green_light),
                ContextCompat.getColor(getContext(), android.R.color.holo_orange_light));
        barChart.getLegend().setEnabled(false);


        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setDrawAxisLine(false);

        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return moods[(int) value];
            }
        });
        barChart.getAxisRight().setDrawGridLines(false);

        LastSevenDays();

        theData = new BarData(barDataSet);
        barChart.setData(theData);

        barChart.getDescription().setEnabled(false);
        barChart.setTouchEnabled(false);
        barChart.setScaleEnabled(false);


        Button buttonWeek = inflatedView.findViewById(R.id.btnWeek);
        buttonWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LastSevenDays();
                Log.d("debugger", "week: ");
                theData = new BarData(barDataSet);
                barChart.setData(theData);

            }
        });

        Button buttonMonth = inflatedView.findViewById(R.id.btnMonth);
        buttonMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH,-7);

                int currentDate = calendar.get(Calendar.DAY_OF_MONTH);
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentYear = calendar.get(Calendar.YEAR);

                DatePickerDialog dialog = new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        currentYear,
                        currentMonth,
                        currentDate);


                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                //Log.d("debugger", "month: ");


            }
        });

        mDateSetListener  = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectedMonth(month);
                theData = new BarData(barDataSet);
                barChart.setData(theData);
                Log.d("debugger",""+(month+1));
            }
        };

        Button buttonAll = inflatedView.findViewById(R.id.btnAll);
        buttonAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetCountersAll();
                SetChartData();
                Log.d("debugger", "all: ");
                theData = new BarData(barDataSet);
                barChart.setData(theData);

            }
        });

        return inflatedView;
    }

    public void LastSevenDays(){


        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,-7);

        int currentDate = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);
        dateString = currentYear+"/"+String.format("%02d",currentMonth+1)+"/"+String.format("%02d",currentDate);

        SetCounters();
        SetChartData();

        //Log.d("debugger", "depressed: "+depressed);
        //Log.d("debugger", "sad: "+sad);
        //Log.d("debugger", "Neutral: "+neutral);
        //Log.d("debugger", "happy: "+happy);
        //Log.d("debugger", "excited: "+excited);
    }

    public void selectedMonth(int month){


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,1);

        int currentDate = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);
        dateString = currentYear+"/"+String.format("%02d",currentMonth+1)+"/"+String.format("%02d",currentDate);

        SetCountersMonth();
        SetChartData();
    }

    public void SetCounters(){

        depressed = 0;
        sad = 0;
        neutral = 0;
        happy = 0;
        excited = 0;
        entries =null;

        String formatDate = "";
        for(int c = 0; c < dateString.length();c++){
            if(dateString.charAt(c)!='/') {
                formatDate += dateString.charAt(c);
            }
        }
       //Log.d("debugger","date " + formatDate);

        entries = JournalTable.selectAll(db);


        for (int i = 0; i < entries.size(); i++) {
            Entry j = entries.get(i);
            //Log.d("debugger", "item: "+ i + " mood: "+j.getmEntryMood());

            String temp = j.getmEntryDate();
            String format = "";
            for(int c = 0; c < temp.length();c++){
                if(temp.charAt(c)!='/') {
                    format += temp.charAt(c);
                }
            }
            int curr = Integer.parseInt(formatDate);
            int tempD = Integer.parseInt(format);
            if(tempD>=curr) {
                // Log.d("debugger", "i: " + i + "format : " + format);
                switch (j.getmEntryMood()){
                    case 0: depressed++;
                        break;
                    case 1: sad++;
                        break;
                    case 2: neutral++;
                        break;
                    case 3: happy++;
                        break;
                    case 4: excited++;
                        break;
                }
            }
        }
    }

    public void SetCountersMonth(){

        depressed = 0;
        sad = 0;
        neutral = 0;
        happy = 0;
        excited = 0;
        entries =null;

        String formatDate = "";
        for(int c = 0; c < dateString.length();c++){
            if(dateString.charAt(c)!='/') {
                formatDate += dateString.charAt(c);
            }
        }
        Log.d("debugger","date " + formatDate);

        entries = JournalTable.selectAll(db);


        for (int i = 0; i < entries.size(); i++) {
            Entry j = entries.get(i);
            //Log.d("debugger", "item: "+ i + " mood: "+j.getmEntryMood());

            String temp = j.getmEntryDate();
            String format = "";
            for(int c = 0; c < temp.length();c++){
                if(temp.charAt(c)!='/') {
                    format += temp.charAt(c);
                }
            }
            int curr = Integer.parseInt(formatDate);
            int tempD = Integer.parseInt(format);
            if(tempD>=curr && tempD <= curr+30) {
                 //Log.d("debugger", "i: " + i + "format : " + (tempD+30));
                switch (j.getmEntryMood()){
                    case 0: depressed++;
                        break;
                    case 1: sad++;
                        break;
                    case 2: neutral++;
                        break;
                    case 3: happy++;
                        break;
                    case 4: excited++;
                        break;
                }
            }
        }
    }

    public void SetCountersAll(){

        depressed = 0;
        sad = 0;
        neutral = 0;
        happy = 0;
        excited = 0;
        entries =null;

        entries = JournalTable.selectAll(db);

        for (int i = 0; i < entries.size(); i++) {
            Entry j = entries.get(i);
            switch (j.getmEntryMood()) {
                case 0:
                    depressed++;
                    break;
                case 1:
                    sad++;
                    break;
                case 2:
                    neutral++;
                    break;
                case 3:
                    happy++;
                    break;
                case 4:
                    excited++;
                    break;
            }
        }
    }

    public void SetChartData(){
        //barChart.notifyDataSetChanged();
        barChart.clear();
        barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, depressed));
        barEntries.add(new BarEntry(1, sad));
        barEntries.add(new BarEntry(2, neutral));
        barEntries.add(new BarEntry(3, happy));
        barEntries.add(new BarEntry(4, excited));
        barDataSet = new BarDataSet(barEntries, "");

        barDataSet.setColors(ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark),
                ContextCompat.getColor(getContext(), android.R.color.holo_blue_light),
                ContextCompat.getColor(getContext(), android.R.color.darker_gray),
                ContextCompat.getColor(getContext(), android.R.color.holo_green_light),
                ContextCompat.getColor(getContext(), android.R.color.holo_orange_light));
        barChart.getLegend().setEnabled(false);
    }
}
