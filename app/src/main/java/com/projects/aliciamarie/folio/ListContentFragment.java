package com.projects.aliciamarie.folio;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.projects.aliciamarie.folio.data.Datapiece;
import com.projects.aliciamarie.folio.utility.DatapieceAdapter;

import java.util.ArrayList;

/**
 * Created by Alicia Marie on 3/25/2015.
 */
public class ListContentFragment extends ListFragment {

    private static final String LOG_TAG = ListContentFragment.class.getSimpleName();
    protected Button orderByTimeBtn;
    protected Button orderByNameBtn;
    protected ArrayList<Datapiece> mContentList = new ArrayList<>();
    protected DatapieceAdapter mContentListAdapter;
    private onDatapieceSelectedListener mCallback;

    private boolean timeAscending = true;
    private boolean nameAscending = false;

    public static ListContentFragment newInstance(ArrayList<Datapiece> datapieces) {
        ListContentFragment fragment = new ListContentFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(MainActivity.DATAPIECES, datapieces);
        fragment.setArguments(args);
        return fragment;
    }

    public ListContentFragment(){}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
       try {
            mCallback = (onDatapieceSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDatapieceSelectedListener");
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
        View rootView = inflater.inflate(R.layout.fragment_listcontent, container, false);
        mContentList = getArguments().getParcelableArrayList(MainActivity.DATAPIECES);
        mContentListAdapter = new DatapieceAdapter(getActivity(), mContentList);
        this.setListAdapter(mContentListAdapter);
        orderByTimeBtn = (Button) rootView.findViewById(R.id.action_orderby_time);
        orderByTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderByTime();
            }
        });
        orderByNameBtn = (Button) rootView.findViewById(R.id.action_orderby_name);
        orderByNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderByName();
            }
        });
        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Datapiece datapiece = mContentListAdapter.getItem(position);
        mCallback.onDatapieceSelected(datapiece);
    }

    public void orderByTime() {
        timeAscending  = ! timeAscending;
        mContentListAdapter.sortByTime(timeAscending);
    }

    public void orderByName(){
        nameAscending = ! nameAscending;
        mContentListAdapter.sortByName(nameAscending);
    }

    protected void updateContent(ArrayList<Datapiece> datapieces){
        mContentList.clear();
        mContentList.addAll(datapieces);
        if(mContentListAdapter == null){
            mContentListAdapter = new DatapieceAdapter(getActivity(), mContentList);
            this.setListAdapter(mContentListAdapter);
        }
        mContentListAdapter.notifyDataSetChanged();
    }

    public interface onDatapieceSelectedListener {
        public void onDatapieceSelected(Datapiece datapiece);
    }
}
