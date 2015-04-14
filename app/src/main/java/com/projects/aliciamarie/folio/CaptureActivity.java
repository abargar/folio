package com.projects.aliciamarie.folio;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.projects.aliciamarie.folio.data.Datapiece;
import com.projects.aliciamarie.folio.utility.FileHandler;
import com.projects.aliciamarie.folio.utility.LocationProvider;

import java.io.File;


public class CaptureActivity extends ActionBarActivity implements LocationProvider.LocationCallback {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_VIDEO_CAPTURE = 2;
    private static final int REQUEST_SOUND_CAPTURE = 3;
    Datapiece mDatapiece;
    protected LocationProvider mLocationProvider;
    private static String LOG_TAG = CaptureActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatapiece = new Datapiece(null, null);
        mLocationProvider = new LocationProvider(this, this);
        mLocationProvider.disconnect();
        setContentView(R.layout.activity_capture);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .commit();
        }
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
            return true;
        }
        else if (id == R.id.action_view_content){
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
        Intent intent = new Intent(MediaStore.RECORD_SOUND);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File soundFile = FileHandler.createFile(FileHandler.TYPE_AUDIO);
            if(soundFile != null) {
                Uri fileUri = Uri.fromFile(soundFile);
                mDatapiece.setUri(fileUri);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, REQUEST_SOUND_CAPTURE);
            }
        }
        else{
            Log.e(LOG_TAG, "Unable to find application to resolve sound activity.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLocationProvider.connect();
        if ((requestCode == REQUEST_IMAGE_CAPTURE || requestCode == REQUEST_VIDEO_CAPTURE || requestCode == REQUEST_SOUND_CAPTURE)
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
