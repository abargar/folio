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

/**Detail Activity:  handles logic for showing a datapiece's detailed content, displayed either immediately after capture or upon selection
 * in the list.
 *
 * Specific functions:  shares, saves, and deletes content (the later with or without deleting the source file).  Handles the addition or
 * removal of tags as a callback from the display.  Loads and displays the content detail.
 *
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
        mDatapiece = loadContent(savedInstanceState);
        detailFragment = createDetailFragment(mDatapiece);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_container, detailFragment)
                    .commit();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_capture){
            addContent();
        }
        else if (id == R.id.action_view_content){
            viewContent();
        }
        else if (id == R.id.action_map){
            viewContent();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DATAPIECE, mDatapiece);
    }

    private Datapiece loadContent(Bundle savedInstanceState){
        Datapiece datapiece = null;
        if(savedInstanceState != null){
            datapiece = savedInstanceState.getParcelable(DATAPIECE);
        }
        else{
            Intent intent = this.getIntent();
            if(intent != null){
                datapiece = intent.getParcelableExtra(DATAPIECE);
            }
        }
        if(datapiece == null){
            Log.e(LOG_TAG, "Failed to load content");
        }
        return datapiece;
    }

    private DetailFragment createDetailFragment(Datapiece datapiece){
        Bundle bundle = new Bundle();
        bundle.putParcelable(DATAPIECE, datapiece);
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(bundle);
        return detailFragment;
    }

    public void setDatapieceName(String name){ mDatapiece.setName(name);}

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
                        FileHandler.deleteFile(getApplicationContext(), mDatapiece.getUri());
                        viewContent();
                        }
                })
                .setNegativeButton(R.string.no_delete_file_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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
