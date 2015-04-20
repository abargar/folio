package com.projects.aliciamarie.folio;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by Alicia Marie on 3/31/2015.
 */
public class SearchFragment extends Fragment {
    private static final String LOG_TAG = SearchFragment.class.getSimpleName();
    SearchListener mCallback;
    private EditText searchBox;
    private Spinner searchSpinner;
    private String searchCategory = "Name";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (SearchListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SearchListener");
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
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        searchBox = (EditText) rootView.findViewById(R.id.viewoptions_edittext_search);

        searchSpinner = (Spinner) rootView.findViewById(R.id.spinner_search);
        ArrayAdapter searchCategoryAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.search_array, android.R.layout.simple_spinner_item);
        searchCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchSpinner.setAdapter(searchCategoryAdapter);
        searchSpinner.setOnItemSelectedListener(new searchSpinnerListener());

        Button searchButton = (Button) rootView.findViewById(R.id.button_search);
        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String searchTerm = searchBox.getText().toString();
//                Log.v(LOG_TAG, "Search term: " + searchTerm);
                mCallback.search(searchCategory, searchTerm);
            }
        });

        return rootView;
    }

    public interface SearchListener {
        public void search(String searchCategory, String searchTerm);
    }

    class searchSpinnerListener  implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            searchCategory = searchSpinner.getSelectedItem().toString();
            Log.v(LOG_TAG, "Selected category: " + searchCategory);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

}
