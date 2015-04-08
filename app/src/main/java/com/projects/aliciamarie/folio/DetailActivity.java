package com.projects.aliciamarie.folio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.projects.aliciamarie.folio.data.DatabaseUtilities;
import com.projects.aliciamarie.folio.data.Datapiece;
import com.projects.aliciamarie.folio.utility.FileHandler;
import com.projects.aliciamarie.folio.utility.LocationProvider;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Alicia Marie on 2/12/2015.
 */

public class DetailActivity extends ActionBarActivity implements LocationProvider.LocationCallback {

    private static String LOG_TAG = DetailActivity.class.getSimpleName();
    protected static final String DATAPIECE = "DATAPIECE";
    protected static final String LOCATION_EDITABLE = "LOCATION_EDITABLE";
    protected DetailFragment detailFragment;
    protected Datapiece mDatapiece;
    protected Set<String> deletedTags = new HashSet<String>();
    protected LocationProvider mLocationProvider;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: find proper location for isEditable
        mLocationProvider = new LocationProvider(this, this);
        loadContent(savedInstanceState);
        Bundle bundle = new Bundle();
        bundle.putParcelable(DATAPIECE, mDatapiece);
        detailFragment = createDetailFragment(bundle);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_container, detailFragment)
                    .commit();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_capture){
            addContent();
        }
        else if (id == R.id.action_view_content){
            viewContent();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(! mLocationProvider.isConnected()) {
            mLocationProvider.connect();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DATAPIECE, mDatapiece);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mLocationProvider.isConnected()) {
            mLocationProvider.disconnect();
        }
    }

    private void loadContent(Bundle savedInstanceState){
        if(savedInstanceState != null){
            mLocationProvider.disconnect();
            mDatapiece = savedInstanceState.getParcelable(DATAPIECE);
        }
        else{
            Intent intent = this.getIntent();
            if(intent != null){
                 if(! intent.getBooleanExtra(LOCATION_EDITABLE, false)){
                     mLocationProvider.disconnect();
                 }
                mDatapiece = intent.getParcelableExtra(DATAPIECE);
            }
        }
        if(mDatapiece == null){
            Log.e(LOG_TAG, "Failed to load content");
        }
    }

    private DetailFragment createDetailFragment(Bundle bundle){
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(bundle);
        return detailFragment;
    }

    public void handleLocation(Location location) {
        if(mDatapiece.getLocation() == null) {
            try {
                mDatapiece.setLocation(location);
                detailFragment.handleLocation(location);
            } catch (NullPointerException e) {
                Log.e(LOG_TAG, "Unable to handle location.  Content fragment not found.");
            }
        }
        mLocationProvider.disconnect();
    }

    public void addTag(String tag){
        mDatapiece.addTag(tag);
    }

    public void removeTag(String tag) {
        mDatapiece.removeTag(tag);
        deletedTags.add(tag);
    }

    public void saveContent(View view) {
        //DatabaseUtilities.deleteDatabase(this);
        long datapieceId = DatabaseUtilities.saveDatapieceValues(this, mDatapiece);
        for (String deletedTag : deletedTags) {
                DatabaseUtilities.deleteDatapieceTag(this, datapieceId, deletedTag);
        }
        for (String tag : mDatapiece.getTags()) {
                DatabaseUtilities.saveDatapieceTag(this, datapieceId, tag);
        }
        viewContent();
    }

    public void deleteContent(View view){
        long datapieceId = DatabaseUtilities.getDatapieceId(this, mDatapiece);
        if(datapieceId != -1){
            DatabaseUtilities.deleteDatapiece(this, datapieceId);
        }

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(R.string.delete_file_dialog)
                    .setPositiveButton(R.string.yes_delete_file_dialog, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Log.v(LOG_TAG, "Yes delete file");
                    FileHandler.deleteFile(getApplicationContext(), mDatapiece.getUri());
                    viewContent();
                }
            })
                    .setNegativeButton(R.string.no_delete_file_dialog, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.v(LOG_TAG, "No keep file");
                            viewContent();
                        }
                    });
           alertDialogBuilder.create();
            alertDialogBuilder.show();
    }

    private void viewContent(){
        startActivity(new Intent(this, ViewContentActivity.class));
    }

    private void addContent() {  startActivity(new Intent(this, MainActivity.class)); }

}
