package com.projects.aliciamarie.folio.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Alicia Marie on 3/24/2015.
 */
public class DatabaseUtilities {

    private static final String LOG_TAG = DatabaseUtilities.class.getSimpleName();

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DatabaseHelper.DATABASE_NAME);
    }

    static ContentValues createDatapieceValues(Datapiece datapiece) {

        ContentValues datapieceValues = new ContentValues();
        datapieceValues.put(DataContract.DatapieceEntry.COLUMN_CONTENT_URI, datapiece.getUri().toString());
        datapieceValues.put(DataContract.DatapieceEntry.COLUMN_COORD_LAT, datapiece.getLatitude());
        datapieceValues.put(DataContract.DatapieceEntry.COLUMN_COORD_LONG, datapiece.getLongitude());
        datapieceValues.put(DataContract.DatapieceEntry.COLUMN_COORD_ACC, datapiece.getLocationAccuracy());
        datapieceValues.put(DataContract.DatapieceEntry.COLUMN_TIMESTAMP, datapiece.getTime());

        return datapieceValues;
    }

    static ContentValues createSpecimenDatapiecePair(String specimenId, long datapieceId){
        ContentValues specimenDatapiecePair = new ContentValues();
        specimenDatapiecePair.put(DataContract.SpecimenDatapiecePairing.COLUMN_SPECIMEN_ID, specimenId);
        specimenDatapiecePair.put(DataContract.SpecimenDatapiecePairing.COLUMN_DATAPIECE_ID, datapieceId);

        return specimenDatapiecePair;
    }

    public static long getDatapieceId(Context context, Datapiece datapiece){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection= DataContract.DatapieceEntry.COLUMN_CONTENT_URI + " LIKE ?";
        String[] selectionArgs = { String.valueOf(datapiece.getUri()) };

        Cursor cursor = db.query(
                DataContract.DatapieceEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if(cursor.moveToFirst()) {
            return cursor.getLong(cursor.getColumnIndexOrThrow(DataContract.DatapieceEntry._ID));
        }
        else{
            return -1;
        }

    }

    public static long saveDatapieceValues(Context context, Datapiece datapiece){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long existingId = getDatapieceId(context, datapiece);
        long rowId;

        if(existingId == -1) {
            ContentValues datapieceValues = createDatapieceValues(datapiece);
            rowId = db.insert(DataContract.DatapieceEntry.TABLE_NAME, null, datapieceValues);
        }
        else{
            Log.v(LOG_TAG, "Content values already exist in database.");
            return existingId;
        }

        if(rowId == -1) {
            Log.e(LOG_TAG, "Error: content values were not successfully saved in database");
        }

        db.close();
        return rowId;
    }


    public static Datapiece getDatapiece(Context context, long rowId){
        Datapiece datapiece;

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String contentUri = DataContract.DatapieceEntry.COLUMN_CONTENT_URI;
        String coordLat = DataContract.DatapieceEntry.COLUMN_COORD_LAT;
        String coordLong = DataContract.DatapieceEntry.COLUMN_COORD_LONG;
        String locAccuracy = DataContract.DatapieceEntry.COLUMN_COORD_ACC;
        String timestamp = DataContract.DatapieceEntry.COLUMN_TIMESTAMP;

        String[] datapieceProjection = {
                contentUri,
                coordLat,
                coordLong,
                locAccuracy,
                timestamp
        };

        String selection= DataContract.DatapieceEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(rowId) };

        Cursor c = db.query(
                DataContract.DatapieceEntry.TABLE_NAME,
                datapieceProjection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if(c.moveToFirst()){
            String uriStr = c.getString(c.getColumnIndexOrThrow(contentUri));
            Location location = new Location("");
            location.setLatitude(c.getDouble(c.getColumnIndexOrThrow(coordLat)));
            location.setLongitude(c.getDouble(c.getColumnIndexOrThrow(coordLong)));
            location.setAccuracy(c.getFloat(c.getColumnIndexOrThrow(locAccuracy)));
            location.setTime(c.getLong(c.getColumnIndexOrThrow(timestamp)));
            datapiece = new Datapiece(Uri.parse(uriStr), location);
        }
        else{
            datapiece = null;
        }
        db.close();
        c.close();
        return datapiece;
    }


        public static ArrayList<Datapiece> getDatapiecesByTime(Context context, @Nullable String order, @Nullable String selection, @Nullable String[] args){
        ArrayList<Datapiece> entries = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String contentId = DataContract.DatapieceEntry._ID;
        String contentUri = DataContract.DatapieceEntry.COLUMN_CONTENT_URI;
        String coordLat = DataContract.DatapieceEntry.COLUMN_COORD_LAT;
        String coordLong = DataContract.DatapieceEntry.COLUMN_COORD_LONG;
        String locAccuracy = DataContract.DatapieceEntry.COLUMN_COORD_ACC;
        String timestamp = DataContract.DatapieceEntry.COLUMN_TIMESTAMP;

        String[] datapieceProjection = {
                contentId,
                contentUri,
                coordLat,
                coordLong,
                locAccuracy,
                timestamp
        };
        String sortOrder;

        if(order == null){
            sortOrder = DataContract.DatapieceEntry.COLUMN_TIMESTAMP + " DESC";
        }
        else{
            sortOrder = DataContract.DatapieceEntry.COLUMN_TIMESTAMP + " " + order;
        }
        Cursor c = db.query(
            DataContract.DatapieceEntry.TABLE_NAME,
            datapieceProjection,
            selection,
            args,
            null,
            null,
            sortOrder
        );

        if(c.moveToFirst()) {
            do {
                String uriStr = c.getString(c.getColumnIndexOrThrow(contentUri));
                Location location = new Location("");
                location.setLatitude(c.getDouble(c.getColumnIndexOrThrow(coordLat)));
                location.setLongitude(c.getDouble(c.getColumnIndexOrThrow(coordLong)));
                location.setAccuracy(c.getFloat(c.getColumnIndexOrThrow(locAccuracy)));
                location.setTime(c.getLong(c.getColumnIndexOrThrow(timestamp)));
                Datapiece datapiece = new Datapiece(Uri.parse(uriStr), location);
                datapiece.setTags(getTags(context, c.getInt(c.getColumnIndexOrThrow(contentId))));
                entries.add(datapiece);
            } while(c.moveToNext());
        }
        else{
            Log.v(LOG_TAG, "No records returned.  Is the database empty?");
        }
        db.close();
        c.close();
        return entries;
    }

    public static ArrayList<String> getTags(Context context, long dataId) {
        ArrayList<String> tags = new ArrayList();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tagCol = DataContract.SpecimenDatapiecePairing.COLUMN_SPECIMEN_ID;

        String[] tagProjection = {
                tagCol
        };
        String selection= DataContract.SpecimenDatapiecePairing.COLUMN_DATAPIECE_ID + " = ?";
        String[] selectionArgs = { String.valueOf(dataId) };

        Cursor c = db.query(
                DataContract.SpecimenDatapiecePairing.TABLE_NAME,
                tagProjection,
                selection,
                selectionArgs,
                null,
                null,
                tagCol + " ASC"
        );

        if(c.moveToFirst()){
            do{
                tags.add(c.getString(c.getColumnIndexOrThrow(tagCol)));
            }
            while(c.moveToNext());
        }
        db.close();
        c.close();

        return tags;
    }

    public static long saveDatapieceTag(Context context, long datapieceId, String tag){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long tagId = -1;
        try {
                ContentValues pairValues = createSpecimenDatapiecePair(tag, datapieceId);
                tagId = db.insert(DataContract.SpecimenDatapiecePairing.TABLE_NAME, null, pairValues);
            }
        catch(Exception e){
            Log.e(LOG_TAG, "Exception occurred while storing tag: " + e.toString());
        }
        if(tagId == -1){Log.e(LOG_TAG, "Error saving tag " + tag);

        }
        db.close();
        return tagId;
    }

    public static long getDatapieceTagId(Context context, long datapieceId, String tag) {

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String idCol = DataContract.SpecimenDatapiecePairing._ID;
        String[] tagProjection = {
                idCol
        };
        String selection= DataContract.SpecimenDatapiecePairing.COLUMN_DATAPIECE_ID + " = ? AND "
                + DataContract.SpecimenDatapiecePairing.COLUMN_SPECIMEN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(datapieceId), String.valueOf(tag) };

        Cursor c = db.query(
                DataContract.SpecimenDatapiecePairing.TABLE_NAME,
                tagProjection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        Log.v(LOG_TAG, "Number of returned rows for tag " + tag + ": " + c.getCount());

        long tagId;
        if(c.moveToFirst()){

            tagId = c.getLong(c.getColumnIndexOrThrow(idCol));
        }
        else{
            tagId = -1;
        }
        db.close();
        c.close();
        return tagId;
    }

    public static void deleteDatapieceTag(Context context, long datapieceId, String tag){
        long datapieceTagId = getDatapieceTagId(context, datapieceId, tag);
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] idArg = {Long.toString(datapieceTagId)};
        int result = db.delete(DataContract.SpecimenDatapiecePairing.TABLE_NAME, DataContract.SpecimenDatapiecePairing._ID + " = ?", idArg);
        if(result != 1){
            Log.e(LOG_TAG, "Error deleting tag " + tag + " for datapiece id " + datapieceId + " from the datapiece-tag database.");
        }
        db.close();
    }

    public static void getDatapiecesByTag(Context context, String tag){
        
    }
}
