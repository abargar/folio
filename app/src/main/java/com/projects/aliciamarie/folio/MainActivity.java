package com.projects.aliciamarie.folio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.projects.aliciamarie.folio.data.DatabaseUtilities;
import com.projects.aliciamarie.folio.data.Datapiece;

import java.util.ArrayList;

/**
 * Created by Alicia Marie on 3/24/2015.
 */
public class MainActivity extends ActionBarActivity {


    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    protected static final String DATAPIECES = "datapieces";
    protected ArrayList<Datapiece> mDatapieces;
    protected String order = "ASC";
    protected ListContentFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null) {
            mDatapieces = DatabaseUtilities.getDatapieces(this, null, null, "DESC");
        }
        else{
            mDatapieces = savedInstanceState.getParcelableArrayList(DATAPIECES);
        }
        if(mDatapieces == null){
            mDatapieces = new ArrayList();
        }

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(DATAPIECES, mDatapieces);
        listFragment = createListContentFragment(bundle);

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.view_content_container, new ViewOptionsFragment())
                    .add(R.id.view_content_container, listFragment)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(DATAPIECES, mDatapieces);
    }

    private ListContentFragment createListContentFragment(Bundle bundle) {
        ListContentFragment listFragment = new ListContentFragment();
        listFragment.setArguments(bundle);
        return listFragment;
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

    public void search(View view){
        EditText searchBox = (EditText) findViewById(R.id.viewoptions_edittext_search);
        if(searchBox == null){
            Log.v(LOG_TAG, "Search box not found");
        }
        else {
            String searchTerm = searchBox.getText().toString();
            if(searchTerm.equals("")){
                mDatapieces = DatabaseUtilities.getDatapieces(this, null, null, order);
            }
            else{
                Log.v(LOG_TAG, "Search term:" + searchTerm);
                mDatapieces = DatabaseUtilities.getDatapiecesByTag(this, searchTerm);
            }
            listFragment.updateList(mDatapieces);
        }
    }

    public void orderByTime(View view){
        if(order == "ASC"){
            order = "DESC";
        }
        else{
            order = "ASC";
        }
        mDatapieces = DatabaseUtilities.getDatapieces(this, null, null, order);
        listFragment.updateList(mDatapieces);
    }

    private void addContent() {  startActivity(new Intent(this, CaptureActivity.class)); }

}
