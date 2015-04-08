package com.projects.aliciamarie.folio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.projects.aliciamarie.folio.data.Datapiece;
import com.projects.aliciamarie.folio.utility.FileHandler;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_VIDEO_CAPTURE = 2;
    Uri mCurrentFileUri;
    private static String LOG_TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
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

    public void takePicture(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = FileHandler.createFile(FileHandler.TYPE_IMAGE);
            if(photoFile != null) {
                mCurrentFileUri = Uri.fromFile(photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentFileUri);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
        else{
            Log.e(LOG_TAG, "Unable to find application to resolve camera activity.");
        }
    }

    public void takeVideo(View view){
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File videoFile = FileHandler.createFile(FileHandler.TYPE_VIDEO);
            if(videoFile != null) {
                mCurrentFileUri = Uri.fromFile(videoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentFileUri);
                startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
            }
        }
        else{
            Log.e(LOG_TAG, "Unable to find application to resolve video activity.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_IMAGE_CAPTURE || requestCode == REQUEST_VIDEO_CAPTURE)
                && resultCode == RESULT_OK) {

            Intent intent = new Intent(this, DetailActivity.class);
            Datapiece datapiece = new Datapiece(mCurrentFileUri, null, new ArrayList<String>());
            intent.putExtra(DetailActivity.DATAPIECE, datapiece);
            startActivity(intent);

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(mCurrentFileUri);
            this.sendBroadcast(mediaScanIntent);
        }
    }


    private void viewContent(){
        startActivity(new Intent(this, ViewContentActivity.class));
    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_capture, container, false);
            return rootView;
        }
    }
}
