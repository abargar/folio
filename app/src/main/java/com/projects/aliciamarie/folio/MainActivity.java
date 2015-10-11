package com.projects.aliciamarie.folio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.projects.aliciamarie.folio.data.DataContract;
import com.projects.aliciamarie.folio.data.DatabaseUtilities;
import com.projects.aliciamarie.folio.data.Datapiece;

import java.util.ArrayList;

/**
 * MainActivity:  Class that handles logic for list, search, and map functionality.
 * More specifically: loads search, list, and map fragments; selecting an object in the list (used to start the DetailActivity for showing content);
 *  and performing search on the database and updating shown content.
 *
 * Created by Alicia Marie on 3/24/2015.
 *
 */

public class MainActivity extends ActionBarActivity
                          implements ListContentFragment.onDatapieceSelectedListener,
        SearchFragment.SearchListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    protected static final String DATAPIECES = "datapieces";
    protected ArrayList<Datapiece> mDatapieces;
    protected ListContentFragment listFragment;
    protected MapContentFragment mapFragment;

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
                    .add(R.id.options_container, new SearchFragment())
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

        if (id == R.id.action_map) {
            viewMap();
        }
        else if (id == R.id.action_capture){
            addContent();
        }
        else if (id == R.id.action_view_content){
            viewList();
        }
        return super.onOptionsItemSelected(item);
    }

    public void search(String searchCategory, String searchTerm){
        Log.v(LOG_TAG, "Search callback received");
        if(searchTerm.equals("")){
            mDatapieces = DatabaseUtilities.getDatapieces(this, null, null, null);
        }
        else{
            if(searchCategory.equals("Tag")){
                mDatapieces = DatabaseUtilities.getDatapiecesByTag(this, searchTerm);
            }
            else if(searchCategory.equals("Name")){
                searchCategory = DataContract.DatapieceEntry.COLUMN_NAME;
                String[] searchArg = { searchTerm + "%" };
                mDatapieces = DatabaseUtilities.getDatapieces(this, searchCategory + " LIKE ?", searchArg, null );
            }
        }
        if(listFragment != null) {
            listFragment.updateContent(mDatapieces);
        }
        if(mapFragment != null){
            mapFragment.updateContent(mDatapieces);
        }
    }

    public void viewList() {
        if(listFragment == null){
            listFragment = ListContentFragment.newInstance(mDatapieces);
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_container, listFragment, "list");
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void viewMap(){
        if(mapFragment == null){
            mapFragment = MapContentFragment.newInstance(mDatapieces);
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_container, mapFragment, "map");
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void onDatapieceSelected(Datapiece datapiece) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.DATAPIECE, datapiece);
        startActivity(intent);
    }

    private void addContent() {  startActivity(new Intent(this, CaptureActivity.class)); }

}
