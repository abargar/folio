package com.projects.aliciamarie.folio.utility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.projects.aliciamarie.folio.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Alicia Marie on 3/27/2015.
 */
public class FileHandler {
    private static final String LOG_TAG = FileHandler.class.getSimpleName();

    private static String APP_DIRECTORY = "folio";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_VIDEO = "video";
    public static final String TYPE_AUDIO = "audio";
    public static final String TYPE_DOCUMENT = "document";
    public static final String TYPE_UNKNOWN = "unknown";

    public static String getType(Uri content){
        String contentStr = content.toString();
        if(contentStr.endsWith(".jpg")){
            return TYPE_IMAGE;
        }
        else if(contentStr.endsWith(".mp4")){
            return TYPE_VIDEO;
        }
        else if(contentStr.endsWith(".mp3")){
            return TYPE_AUDIO;
        }
        else{
            return TYPE_UNKNOWN;
        }
    }

    public static File createFile(String fileType){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = fileType + "_" + timeStamp + "_";

        File storageDir;
        switch(fileType){
            case TYPE_IMAGE:
                storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_DIRECTORY);
                break;
            case TYPE_VIDEO:
                storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), APP_DIRECTORY);
                break;
            case TYPE_AUDIO:
                storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS), APP_DIRECTORY);
                break;
            case TYPE_DOCUMENT:
                storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), APP_DIRECTORY);
                break;
            default:
                storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_DIRECTORY);
                break;
        }
        if(! storageDir.exists()){
            if (! storageDir.mkdirs()) {
                Log.e(LOG_TAG, "Directory not created");
            }
        }

        File file;
        switch(fileType){
            case TYPE_IMAGE:
                file = new File(storageDir.getPath() + File.separator + filename + ".jpg");
                break;
            case TYPE_VIDEO:
                file = new File(storageDir.getPath() + File.separator + filename + ".mp4");
                break;
            case TYPE_AUDIO:
                file = new File(storageDir.getPath() + File.separator + filename + ".mp3");
                break;
            case TYPE_DOCUMENT:
                file = new File(storageDir.getPath() + File.separator + filename + ".doc");
            default:
                file = new File(storageDir.getPath() + File.separator + filename + ".txt");
                break;
        }
        return file;
    }

    public static Boolean deleteFile(Context context, Uri content){
        String contentPath = content.getPath();
        File file = new File(contentPath);
        Boolean deleted = file.delete();
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(contentPath))));
        return deleted;
    }

    public static Bitmap getThumbnail(Context context, Uri content){
        Bitmap thumbnail = null;
        String type = getType(content);
        switch(type){
            case TYPE_IMAGE:
                try{
                    thumbnail = MediaStore.Images.Media.getBitmap(context.getContentResolver(), content);
                }
                catch(Exception FileNotFoundException) {
                    Log.e(LOG_TAG, "Failed to locate content at uri: " + content.toString());
                }
                break;

            case TYPE_VIDEO:
                try{
                    thumbnail = ThumbnailUtils.createVideoThumbnail(content.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
                }
                catch(Exception FileNotFoundException) {
                    Log.e(LOG_TAG, "Failed to locate content at uri: " + content.toString());
                }
                break;

            case TYPE_AUDIO:
            try{
                    Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.sound_icon);
                    thumbnail = ThumbnailUtils.extractThumbnail(bm, 50, 50);
                }
                catch(Exception FileNotFoundException) {
                    Log.e(LOG_TAG, "Failed to locate content at uri: " + content.toString());
                }
                break;

            default:
                break;
        }
        return thumbnail;
    }
}
