package assignment2.ashishr.utas.edu.au.journal;

import android.app.Service;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class JournalAdapter extends ArrayAdapter<Entry> {

    private int[] moods = {R.drawable.depressed_face, R.drawable.sad_face, R.drawable.neutral_face, R.drawable.smile_face, R.drawable.happy_face};

    private int mLayoutResourceID;
    public JournalAdapter(Context context, int resource, List<Entry> objects)
    {
        super(context, resource, objects);
        this.mLayoutResourceID = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(mLayoutResourceID, parent, false);
        Entry j = this.getItem(position);

        TextView lblTitle = row.findViewById(R.id.lblTitle);
        lblTitle.setText(j.getmEntryTitle());

        TextView lblEntry = row.findViewById(R.id.lblEntry);
        lblEntry.setText(j.getmEntryText());

        TextView lblTime = row.findViewById(R.id.lblTime);
        lblTime.setText(j.getmEntryTime());

        ImageView lblMood = row.findViewById(R.id.lblMood);
        lblMood.setImageResource(moods[j.getmEntryMood()]);
        return row;
    }
}
