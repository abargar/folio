package com.projects.aliciamarie.folio;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.projects.aliciamarie.folio.data.Datapiece;
import com.projects.aliciamarie.folio.utility.FileHandler;
import com.projects.aliciamarie.folio.utility.LocationProvider;

import java.io.File;

/*
*Capture Activity:  handles the capture and addition of all new data.  Opens a dialog with options for various data types.  Based on user selection,
* calls FileHandler for new file of appropriate type and starts relevant application (if exists).  Upon application completion, moves to detail view.
*
* Specific functions:  handles geotagging (via utility/LocationProvider); file creation (via utility/FileHandler);
 * starting camera, videorecorder, audiorecorder, or writer applications; signalling creation of file to other relevant applications (via android's MediaScanner);
 * and passing off new content to the remainder of the folio application.
*
*Created by Alicia Bargar, date lost
 */

public class CaptureActivity extends Activity implements LocationProvider.LocationCallback {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_VIDEO_CAPTURE = 2;
    private static final int REQUEST_AUDIO_CAPTURE = 3;
    private static final int REQUEST_DOCUMENT_CAPTURE = 4;
    Datapiece mDatapiece;
    protected LocationProvider mLocationProvider;
    private static String LOG_TAG = CaptureActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatapiece = new Datapiece(null, null, null);
        mLocationProvider = new LocationProvider(this, this);
        mLocationProvider.disconnect();
        setContentView(R.layout.activity_capture);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_capture){
            return true;
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
    public void onResume() {
        super.onResume();
        if (! mLocationProvider.isConnected()) {
                mLocationProvider.connect();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DetailActivity.DATAPIECE, mDatapiece);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mLocationProvider.isConnected()) {
                mLocationProvider.disconnect();
        }
    }

    public void handleLocation(Location location) {
        mDatapiece.setLocation(location);
    }


    public void takePicture(View view){
        mLocationProvider.connect();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = FileHandler.createFile(FileHandler.TYPE_IMAGE);
            if(photoFile != null) {
                Uri fileUri = Uri.fromFile(photoFile);
                mDatapiece.setUri(fileUri);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
        else{
            Log.e(LOG_TAG, "Unable to find application to resolve camera activity.");
        }
    }

    public void takeVideo(View view){
        mLocationProvider.connect();
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File videoFile = FileHandler.createFile(FileHandler.TYPE_VIDEO);
            if(videoFile != null) {
                Uri fileUri = Uri.fromFile(videoFile);
                mDatapiece.setUri(fileUri);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
            }
        }
        else{
            Log.e(LOG_TAG, "Unable to find application to resolve video activity.");
        }
    }


    public void takeAudio(View view){
        mLocationProvider.connect();
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File soundFile = FileHandler.createFile(FileHandler.TYPE_AUDIO);
            if(soundFile != null) {
                Uri fileUri = Uri.fromFile(soundFile);
                mDatapiece.setUri(fileUri);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, REQUEST_AUDIO_CAPTURE);
            }
        }
        else{
            Log.e(LOG_TAG, "Unable to find application to resolve sound activity.");
        }
    }

    public void takeDocument(View view){
        mLocationProvider.connect();
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File docFile = FileHandler.createFile(FileHandler.TYPE_DOCUMENT);
            if(docFile != null) {
                Uri fileUri = Uri.fromFile(docFile);
                mDatapiece.setUri(fileUri);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, REQUEST_DOCUMENT_CAPTURE);
            }
        }
        else{
            Log.e(LOG_TAG, "Unable to find application to resolve document activity.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLocationProvider.connect();
        if ((requestCode == REQUEST_IMAGE_CAPTURE || requestCode == REQUEST_VIDEO_CAPTURE ||
                requestCode == REQUEST_AUDIO_CAPTURE || requestCode == REQUEST_DOCUMENT_CAPTURE)
                && resultCode == RESULT_OK) {

            Intent intent = new Intent(this, DetailActivity.class);
            mLocationProvider.disconnect();
            if(mDatapiece.getLocation() == null){
                Log.w(LOG_TAG, "Failed to retrieve location");
                mDatapiece.setLocation(mLocationProvider.getLastLocation());
            }
            intent.putExtra(DetailActivity.DATAPIECE, mDatapiece);
            startActivity(intent);

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(mDatapiece.getUri());
            this.sendBroadcast(mediaScanIntent);
        }
    }


    private void viewContent(){
        startActivity(new Intent(this, MainActivity.class));
    }

}
