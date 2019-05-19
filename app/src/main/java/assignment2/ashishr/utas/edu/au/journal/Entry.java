package assignment2.ashishr.utas.edu.au.journal;

public class Entry {
    private int mEntryID;
    private String mEntryTitle;
    private String mEntryText;
    private String mEntryDate;
    private String mEntryTime;
    private String mEntryMood;

    public int getmEntryID(){ return mEntryID; }
    public void setEntryID(int id){this.mEntryID = id;}

    public String getmEntryTitle(){return mEntryTitle;}
    public void setmEntryTitle(String title){this.mEntryTitle = title;}

    public String getmEntryText(){return mEntryText;}
    public void setmEntryText(String text){this.mEntryText= text;}

    public String getmEntryDate (){return mEntryDate;}
    public void setmEntryDate(String date){this.mEntryDate = date;}

    public String getmEntryTime(){return mEntryTime;}
    public void setmEntryTime(String time){this.mEntryTime = time;}

    public String getmEntryMood(){return mEntryMood;}
    public void setmEntryMood(String mood){this.mEntryMood = mood;}
}
