package assignment2.ashishr.utas.edu.au.journal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EntryFragment extends Fragment {

    public EntryFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View inflatedView = inflater.inflate(R.layout.custom_entry_layout, container,false);

        return inflatedView;
    }
}