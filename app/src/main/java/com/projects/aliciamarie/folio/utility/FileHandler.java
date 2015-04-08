package com.projects.aliciamarie.folio.utility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Alicia Marie on 3/27/2015.
 */
public class FileHandler {
    private static final String LOG_TAG = FileHandler.class.getSimpleName();

    private static String APP_DIRECTORY = "folio";
    public static final String TYPE_IMAGE = "IMAGE";
    public static final String TYPE_VIDEO = "VIDEO";
    public static final String TYPE_AUDIO = "AUDIO";

    public static String getType(Uri content){
        String contentStr = content.toString();
        if(contentStr.endsWith(".jpg")){
            return TYPE_IMAGE;
        }
        else if(contentStr.endsWith(".mp4")){
            return TYPE_VIDEO;
        }
        else{
            return null;
        }
    }

    public static File createFile(String fileType){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = fileType + "_" + timeStamp + "_";

        //TODO: prep storageDir for other types of files
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_DIRECTORY);

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
        if(type == TYPE_IMAGE){
            try{
                thumbnail = MediaStore.Images.Media.getBitmap(context.getContentResolver(), content);
            }
            catch(Exception FileNotFoundException) {
                Log.e(LOG_TAG, "Failed to locate content at uri: " + content.toString());
            }
        }
        else if(type == TYPE_VIDEO){
            try{
                thumbnail = ThumbnailUtils.createVideoThumbnail(content.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
            }
            catch(Exception FileNotFoundException) {
                Log.e(LOG_TAG, "Failed to locate content at uri: " + content.toString());
            }
        }
        return thumbnail;
    }
}
