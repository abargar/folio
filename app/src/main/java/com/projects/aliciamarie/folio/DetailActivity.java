package com.projects.aliciamarie.folio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.projects.aliciamarie.folio.data.DatabaseUtilities;
import com.projects.aliciamarie.folio.data.Datapiece;
import com.projects.aliciamarie.folio.utility.FileHandler;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Alicia Marie on 2/12/2015.
 */

public class DetailActivity extends ActionBarActivity {

    private static String LOG_TAG = DetailActivity.class.getSimpleName();
    protected static final String DATAPIECE = "DATAPIECE";
    protected DetailFragment detailFragment;
    protected Datapiece mDatapiece;
    protected Set<String> deletedTags = new HashSet<String>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DATAPIECE, mDatapiece);
    }

    private void loadContent(Bundle savedInstanceState){
        if(savedInstanceState != null){
            mDatapiece = savedInstanceState.getParcelable(DATAPIECE);
        }
        else{
            Intent intent = this.getIntent();
            if(intent != null){
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

    public void addTag(String tag){
        mDatapiece.addTag(tag);
    }

    public void removeTag(String tag) {
        mDatapiece.removeTag(tag);
        deletedTags.add(tag);
    }

    public void shareContent(View view) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("*/*");
        String textDescription = String.format("Lat/Lng: %f, %f \nTime: %s\nTags: %s",
                mDatapiece.getLatitude(), mDatapiece.getLongitude(),
                (new Timestamp(mDatapiece.getTime())).toString(),
                mDatapiece.getTags().toString());
        shareIntent.putExtra(Intent.EXTRA_TEXT, textDescription);
        shareIntent.putExtra(Intent.EXTRA_STREAM, mDatapiece.getUri());
        startActivity(Intent.createChooser(shareIntent, "Share content using"));
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
        startActivity(new Intent(this, MainActivity.class));
    }

    private void addContent() {  startActivity(new Intent(this, CaptureActivity.class)); }

}
