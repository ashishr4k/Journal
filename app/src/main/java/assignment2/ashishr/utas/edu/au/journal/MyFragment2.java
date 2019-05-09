package assignment2.ashishr.utas.edu.au.journal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyFragment2 extends Fragment {

    public MyFragment2(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View inflatedView = inflater.inflate(R.layout.fragment_myfragment2, container,false);

        //Button button = inflatedView.findViewById(R.id.button);

        return inflatedView;
    }
}
