package assignment2.ashishr.utas.edu.au.journal;

import android.database.sqlite.SQLiteDatabase;
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

    public int page = -1;
    public StatsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View inflatedView = inflater.inflate(R.layout.fragment_stats, container,false);

        databaseConnection = new Database(getActivity());
        db = databaseConnection.open();

        final Calendar calendar = Calendar.getInstance();
        /////////
        calendar.add(Calendar.DAY_OF_MONTH,-7);

        final int currentDate = calendar.get(Calendar.DAY_OF_MONTH);
        final int currentMonth = calendar.get(Calendar.MONTH);
        final int currentYear = calendar.get(Calendar.YEAR);
        dateString = currentYear+"/"+String.format("%02d",currentMonth+1)+"/"+String.format("%02d",currentDate);
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

        ArrayList<String> days = new ArrayList<>();
        days.add("Mon");
        days.add("Tue");
        days.add("Wed");

        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setDrawAxisLine(false);

        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xaxis.setAxisMinimum(0.0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return moods[(int) value];
            }
        });
        //barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setDrawGridLines(false);
        //barChart.getAxisLeft().setAxisMinimum(0);
        //barChart.getAxisRight().setAxisMinimum(0);

        LastSevenDays();

        theData = new BarData(barDataSet);
        barChart.setData(theData);

        barChart.getDescription().setEnabled(false);
        barChart.setTouchEnabled(false);
        barChart.setScaleEnabled(false);


        Button buttonSeven = inflatedView.findViewById(R.id.btnStats7);
        buttonSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LastSevenDays();
                //depressed++;
                //Log.d("debugger", "depressed: "+depressed);


                theData = new BarData(barDataSet);
                barChart.setData(theData);

                //Log.d("debugger", "depressed: "+depressed);

            }
        });
        return inflatedView;
    }

    public void LastSevenDays(){

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

        //Log.d("debugger", "depressed: "+depressed);
        //Log.d("debugger", "sad: "+sad);
        //Log.d("debugger", "Neutral: "+neutral);
        //Log.d("debugger", "happy: "+happy);
        //Log.d("debugger", "excited: "+excited);
    }

    public void selectedMonth(int month){

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

        //Log.d("debugger", "depressed: "+depressed);
        //Log.d("debugger", "sad: "+sad);
        //Log.d("debugger", "Neutral: "+neutral);
        //Log.d("debugger", "happy: "+happy);
        //Log.d("debugger", "excited: "+excited);
    }
}
