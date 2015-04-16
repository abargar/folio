package com.projects.aliciamarie.folio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.projects.aliciamarie.folio.data.DataContract;
import com.projects.aliciamarie.folio.data.DatabaseUtilities;
import com.projects.aliciamarie.folio.data.Datapiece;

import java.util.ArrayList;

/**
 * Created by Alicia Marie on 3/24/2015.
 */
public class MainActivity extends ActionBarActivity
                          implements ListContentFragment.onDatapieceSelectedListener,
                                     ListOptionsFragment.ViewOptionsListener {


    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    protected static final String DATAPIECES = "datapieces";
    protected ArrayList<Datapiece> mDatapieces;
    protected ListContentFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null) {
            mDatapieces = DatabaseUtilities.getDatapieces(this, null, null, null);
        }
        else{
            mDatapieces = savedInstanceState.getParcelableArrayList(DATAPIECES);
        }
        listFragment = ListContentFragment.newInstance(mDatapieces);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.options_container, new ListOptionsFragment())
                    .add(R.id.content_container, listFragment)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(DATAPIECES, mDatapieces);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_capture){
            addContent();
        }
        else if (id == R.id.action_view_content){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void search(String searchTerm){
            mDatapieces = DatabaseUtilities.getDatapiecesByTag(this, searchTerm);
            if(listFragment != null) {
                listFragment.updateList(mDatapieces);
            }
    }

    public void viewMap(){
        MapContentFragment mapFragment = MapContentFragment.newInstance(mDatapieces);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_container, mapFragment, "map");
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void orderByTime(Boolean newestFirst){
        String orderByTime = DataContract.DatapieceEntry.COLUMN_TIMESTAMP + " ";
        if(newestFirst){
            mDatapieces = DatabaseUtilities.getDatapieces(this, null, null, orderByTime + "DESC");
        }
        else{
            mDatapieces = DatabaseUtilities.getDatapieces(this, null, null, orderByTime + "ASC");
        }
        listFragment.updateList(mDatapieces);
    }

    public void onDatapieceSelected(Datapiece datapiece) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.DATAPIECE, datapiece);
        startActivity(intent);
    }

    private void addContent() {  startActivity(new Intent(this, CaptureActivity.class)); }

}
