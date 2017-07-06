package com.gtk.localsearch;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResultListFragment extends Fragment{

    ArrayList<ResultPlace> list = new ArrayList<>();
    private ListView resultView;

    public ResultListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_result_list, container, false);
        list = (ArrayList<ResultPlace>) getArguments().getSerializable("RESULT_PLACE");
        resultView = (ListView) v.findViewById(R.id.list_results);
        setValues();
        return v;
    }

    private void setValues() {
        ResultAdapter adapter = new ResultAdapter(getContext() , R.layout.row , list);
        if (adapter == null)
            Log.d("resultt" , "nulllll");

        if (resultView == null)
        {   
            Log.d("resultt" , "nulllll");
        }else {
            resultView.invalidate();
            resultView.setAdapter(adapter);
            resultView.setOnItemClickListener((AdapterView.OnItemClickListener) getContext());
        }
    }


}
