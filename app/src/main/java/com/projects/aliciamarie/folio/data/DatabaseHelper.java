package com.projects.aliciamarie.folio.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.projects.aliciamarie.folio.data.DataContract.DatapieceEntry;
import com.projects.aliciamarie.folio.data.DataContract.SpecimenDatapiecePairing;
/**
 * Created by Alicia Marie on 3/12/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    public static final String DATABASE_NAME = "folio.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_DATAPIECE_TABLE = "CREATE TABLE " + DatapieceEntry.TABLE_NAME +  " (" +
            DatapieceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DatapieceEntry.COLUMN_CONTENT_URI + " URI NOT NULL, " +
            DatapieceEntry.COLUMN_TIMESTAMP + " LONG NOT NULL, " +
            DatapieceEntry.COLUMN_COORD_LAT + " LONG NOT NULL, " +
            DatapieceEntry.COLUMN_COORD_LONG + " LONG NOT NULL, " +
            DatapieceEntry.COLUMN_COORD_ACC + " FLOAT NOT NULL, " +
            DatapieceEntry.COLUMN_CONTENT_DESCR + " TEXT, " +
            " UNIQUE (" + DatapieceEntry.COLUMN_CONTENT_URI + ", " +
            DatapieceEntry.COLUMN_TIMESTAMP + ") ON CONFLICT REPLACE" +
            ");";

        db.execSQL(SQL_CREATE_DATAPIECE_TABLE);

 /*       final String SQL_CREATE_SPECIMEN_TABLE = "CREATE TABLE " + SpecimenEntry.TABLE_NAME + " (" +
                SpecimenEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SpecimenEntry.COLUMN_ID + " TEXT UNIQUE ON CONFLICT REPLACE, " +
                SpecimenEntry.COLUMN_DESCR + " TEXT" +
                ");";

        db.execSQL(SQL_CREATE_SPECIMEN_TABLE);
*/
        final String SQL_CREATE_SPECIMEN_DATAPIECE_PAIRING = "CREATE TABLE " + SpecimenDatapiecePairing.TABLE_NAME + " (" +
                SpecimenDatapiecePairing._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SpecimenDatapiecePairing.COLUMN_SPECIMEN_ID + " TEXT NOT NULL, " +
                SpecimenDatapiecePairing.COLUMN_DATAPIECE_ID + " INTEGER NOT NULL, " +
                /*" FOREIGN KEY (" + SpecimenDatapiecePairing.COLUMN_SPECIMEN_ID + ") REFERENCES " +
                SpecimenEntry.TABLE_NAME + " (" + SpecimenEntry.COLUMN_ID + "), " +*/
                " FOREIGN KEY (" + SpecimenDatapiecePairing.COLUMN_DATAPIECE_ID + ") REFERENCES " +
                DatapieceEntry.TABLE_NAME + " (" + DatapieceEntry._ID + "), " +
                " UNIQUE (" + SpecimenDatapiecePairing.COLUMN_SPECIMEN_ID + ", " +
                SpecimenDatapiecePairing.COLUMN_DATAPIECE_ID + ") ON CONFLICT REPLACE" +
                ");";

        db.execSQL(SQL_CREATE_SPECIMEN_DATAPIECE_PAIRING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatapieceEntry.TABLE_NAME);
//        db.execSQL("DROP TABLE IF EXISTS " + SpecimenEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SpecimenDatapiecePairing.TABLE_NAME);
        onCreate(db);
    }
}
