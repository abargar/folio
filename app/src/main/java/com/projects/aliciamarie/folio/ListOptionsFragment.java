package com.projects.aliciamarie.folio;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Alicia Marie on 3/31/2015.
 */
public class ListOptionsFragment extends Fragment implements View.OnClickListener {
    ViewOptionsListener mCallback;
    private EditText searchBox;
    protected Boolean newestFirst = true;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (ViewOptionsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ViewOptionsListener");
        }
    }

    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_viewoptions, container, false);
        searchBox = (EditText) rootView.findViewById(R.id.viewoptions_edittext_search);
        Button searchButton = (Button) rootView.findViewById(R.id.viewoptions_button_search);
        Button orderByTimeButton = (Button) rootView.findViewById(R.id.viewoptions_button_orderby_time);
        Button mapButton = (Button) rootView.findViewById(R.id.viewoptions_button_map);

        searchButton.setOnClickListener(this);
        orderByTimeButton.setOnClickListener(this);
        mapButton.setOnClickListener(this);


        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            //search
            case R.id.viewoptions_button_search:
                String searchTerm = searchBox.getText().toString();
                if(! searchTerm.equals("")){
                    mCallback.search(searchTerm);
                }
                break;
            //order by time
            case R.id.viewoptions_button_orderby_time:
                newestFirst = ! newestFirst;
                mCallback.orderByTime(newestFirst);
                break;
            //view map
            case R.id.viewoptions_button_map:
                mCallback.viewMap();
                break;
            default:
                break;
        }
    }


    public interface ViewOptionsListener {
        public void search(String searchTerm);

        public void orderByTime(Boolean newestFirst);

        public void viewMap();
    }

}
