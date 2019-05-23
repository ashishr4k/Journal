package assignment2.ashishr.utas.edu.au.journal;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class JournalTable {
    public static final String TABLE_NAME = "journal";

    public static final String ENTRY_ID = "entry_id";
    public static final String ENTRY_TITLE = "entry_title";
    public static final String ENTRY_TEXT = "entry_text";
    public static final String ENTRY_DATE = "entry_date";
    public static final String ENTRY_TIME = "entry_time";
    public static final String ENTRY_MOOD = "mood";
    //public static final String ENTRY_IMAGE = "imageUri";

    public static final String CREATE_STATEMENT = "CREATE TABLE "
            + TABLE_NAME
            + " (" + ENTRY_ID + " integer primary key autoincrement, "
            + ENTRY_TITLE + " string not null, "
            + ENTRY_TEXT + " string not null, "
            + ENTRY_DATE + " date not null, "
            + ENTRY_TIME + " time not null, "
            //+ ENTRY_IMAGE + " string not null, "
            + ENTRY_MOOD + " int not null "
            +");";

    //insert data into journal table
    public static void insert(SQLiteDatabase db, Entry j)
    {
        ContentValues values = new ContentValues();
        values.put(ENTRY_TITLE,j.getmEntryTitle());
        values.put(ENTRY_TEXT,j.getmEntryText());
        values.put(ENTRY_DATE,j.getmEntryDate());
        values.put(ENTRY_TIME,j.getmEntryTime());
        values.put(ENTRY_MOOD,j.getmEntryMood());
        //values.put(ENTRY_IMAGE,j.getmEntryImage());
        db.insert(TABLE_NAME,null,values);
    }


    public static ArrayList<Entry> selectAll(SQLiteDatabase db)
    {
        ArrayList<Entry> results =new ArrayList<Entry>();
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
        //check for error
        if (c != null)
        {
            //make sure the cursor is at the start of the list
            c.moveToFirst();
            //loop through until we are at the end of the list

            while (!c.isAfterLast())
            {
                Entry j = createFromCursor(c);
                results.add(j);
                //increment the cursor
                c.moveToNext();
            }
        }
        return results;
    }

    public static Entry createFromCursor(Cursor c)
    {
        if (c == null || c.isAfterLast() || c.isBeforeFirst())
        {
            return null;
        }
        else
        {
            Entry j = new Entry();
            j.setEntryID(c.getInt(c.getColumnIndex(ENTRY_ID)));
            j.setmEntryTitle(c.getString(c.getColumnIndex(ENTRY_TITLE)));
            j.setmEntryText(c.getString(c.getColumnIndex(ENTRY_TEXT)));
            j.setmEntryDate(c.getString(c.getColumnIndex(ENTRY_DATE)));
            j.setmEntryTime(c.getString(c.getColumnIndex(ENTRY_TIME)));
            j.setmEntryMood(c.getInt(c.getColumnIndex(ENTRY_MOOD)));
            //j.getmEntryImage(c.getString(c.getColumnIndex(ENTRY_IMAGE)));

            return j;
        }
    }

    //a function which return the journal with specific journal ID
    public static Entry selectByID(SQLiteDatabase db, int id)
    {
        Entry result = null;

        Cursor c = db.query(TABLE_NAME, null, ENTRY_ID + "= ?",new String[]{""+id}, null, null, null);
        //check for error
        if (c != null)
        {
            //make sure the cursor is at the start of the list
            c.moveToFirst();
            result = createFromCursor(c);

        }
        return result;
    }

    public static void deleteById(SQLiteDatabase db, int id)
    {
        db.delete(JournalTable.TABLE_NAME,JournalTable.ENTRY_ID+"=?",new String[]{""+id});
    }

    //a function for updating data
    public static void update(SQLiteDatabase db, Entry j, int id)
    {
        ContentValues values = new ContentValues();
        values.put(ENTRY_ID,j.getmEntryID());
        values.put(ENTRY_TITLE,j.getmEntryTitle());
        values.put(ENTRY_TEXT,j.getmEntryText());
        values.put(ENTRY_DATE,j.getmEntryDate());
        values.put(ENTRY_TIME,j.getmEntryTime());
        values.put(ENTRY_MOOD,j.getmEntryMood());
        //values.put(ENTRY_IMAGE,j.getmEntryImage());

        db.update(JournalTable.TABLE_NAME,values,ENTRY_ID +"= ?",new String[]{""+id});
        //Log.d("FOUND","DB: "+id);
    }

    //a function for selecting journal on specific date****add at 5/21
    public static ArrayList<Entry> selectByDate(SQLiteDatabase db, String date)
    {
        ArrayList<Entry> result =new ArrayList<Entry>();

        Cursor c = db.query(TABLE_NAME, null, ENTRY_DATE + "= ?",new String[]{date}, null, null, ENTRY_TIME+" DESC");
        //check for error
        if (c != null)
        {
            //make sure the cursor is at the start of the list
            c.moveToFirst();
            //loop through until we are at the end of the list

            while (!c.isAfterLast())
            {
                Entry entry = createFromCursor(c);
                result.add(entry);
                //increment the cursor
                c.moveToNext();
            }
        }
        return result;
    }
}
