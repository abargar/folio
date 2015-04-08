package com.projects.aliciamarie.folio;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.projects.aliciamarie.folio.data.Datapiece;
import com.projects.aliciamarie.folio.utility.DatapieceAdapter;

import java.util.ArrayList;

/**
 * Created by Alicia Marie on 3/25/2015.
 */
public class ListContentFragment extends ListFragment {

    private static final String LOG_TAG = ListContentFragment.class.getSimpleName();
    protected ArrayList<Datapiece> mContentList = new ArrayList<>();
    protected DatapieceAdapter mContentListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listcontent, container, false);
        mContentList = getArguments().getParcelableArrayList(ViewContentActivity.DATAPIECES);
        mContentListAdapter = new DatapieceAdapter(getActivity(), mContentList);
        this.setListAdapter(mContentListAdapter);

        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.v(LOG_TAG, "What is mContentListAdapter? " + mContentListAdapter);
        Datapiece datapiece = mContentListAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(DetailActivity.DATAPIECE, datapiece);
        startActivity(intent);
    }

    protected void updateList(ArrayList<Datapiece> datapieces){
        mContentList.clear();
        mContentList.addAll(datapieces);
        if(mContentListAdapter == null){
            Log.v(LOG_TAG, "mContentListAdapter is null");
            mContentListAdapter = new DatapieceAdapter(getActivity(), mContentList);
            this.setListAdapter(mContentListAdapter);
        }
        mContentListAdapter.notifyDataSetChanged();
    }
}
