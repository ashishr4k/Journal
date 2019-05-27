package assignment2.ashishr.utas.edu.au.journal;

import android.app.Service;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class JournalAdapter extends ArrayAdapter<Entry> implements Filterable {

    private int[] moods = {R.drawable.depressed_face, R.drawable.sad_face, R.drawable.neutral_face, R.drawable.smile_face, R.drawable.happy_face};

    private int mLayoutResourceID;

    private List<Entry>originalData = null;
    private List<Entry>filteredData = null;
    private ItemFilter mFilter = new ItemFilter();

    public JournalAdapter(Context context, int resource, List<Entry> objects)
    {
        super(context, resource, objects);
        this.mLayoutResourceID = resource;
        this.filteredData = objects ;
        this.originalData = objects ;
    }

    public int getCount() {
        return filteredData.size();
    }
    public Filter getFilter() {
        return mFilter;
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

        ImageView lblImage = row.findViewById(R.id.lblImage);

        String imageURI = j.getmEntryImage();
        if(!imageURI.equals("")){
            Uri imageU = Uri.parse(imageURI);
            lblImage.setImageURI(null);
            lblImage.setImageURI(imageU);

        }else{
            lblImage.setVisibility(View.GONE);
        }
        return row;
    }
    public Entry getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Entry> list = originalData;

            int count = list.size();
            final ArrayList<Entry> nlist = new ArrayList<Entry>();

            String filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getmEntryText();
                if (filterableString.toLowerCase().contains(filterString) && !filterString.isEmpty()) {
                    nlist.add(list.get(i));
                    //Log.d("debugger","Item: " +list.get(i).getmEntryTitle());
                }else if(filterString.isEmpty()){
                    nlist.add(list.get(i));
                    //Log.d("debugger","Item: " +list.get(i).getmEntryTitle());
                }
                if (list.get(i).getmEntryTitle().toLowerCase().contains(filterString) && !filterString.isEmpty()) {
                    nlist.add(list.get(i));
                    //Log.d("debugger","Item: " +list.get(i).getmEntryTitle());
                }
                //Log.d("debugger","Count: "+i + " Text: " + list.get(i).getmEntryText());
            }
            //Log.d("debugger","--------------------");

            results.values = nlist;
            results.count = nlist.size();

            //Log.d("debugger","Count: "+results.count);
            return results;
        }
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<Entry> temp;
            filteredData = (ArrayList<Entry>) results.values;

            Log.d("debugger","--------------------");
            for (int i = 0; i < results.count; i++) {
                Log.d("debugger","Item: " +filteredData.get(i).getmEntryTitle());
            }
            notifyDataSetChanged();
        }
    }
}
