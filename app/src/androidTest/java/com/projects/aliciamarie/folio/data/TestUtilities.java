package com.projects.aliciamarie.folio.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/*
    Students: These are functions and some test data to make it easier to test your database and
    Content Provider.  Note that you'll want your WeatherContract class to exactly match the one
    in our solution to use these as-given.
 */
public class TestUtilities extends AndroidTestCase {
    private static final String LOG_TAG = TestUtilities.class.getSimpleName();

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    /*
        Students: Use this to create some default weather values for your database tests.
     */
    static ContentValues createDatapieceValues(long rowId) {
        ContentValues datapieceValues = new ContentValues();
        datapieceValues.put(DataContract.DatapieceEntry._ID, rowId);
        datapieceValues.put(DataContract.DatapieceEntry.COLUMN_CONTENT_URI, "test/uri.jpg");
        datapieceValues.put(DataContract.DatapieceEntry.COLUMN_COORD_LAT, 64.7488);
        datapieceValues.put(DataContract.DatapieceEntry.COLUMN_COORD_LONG, -147.353);
        datapieceValues.put(DataContract.DatapieceEntry.COLUMN_COORD_ACC, 32);
        datapieceValues.put(DataContract.DatapieceEntry.COLUMN_TIMESTAMP, 123456);
        datapieceValues.put(DataContract.DatapieceEntry.COLUMN_CONTENT_DESCR, "Testing");

        return datapieceValues;
    }


    static ContentValues createTestDatapieceValues() {
        // Create a new map of values, where column names are the keys
        ContentValues datapieceValues = new ContentValues();
        datapieceValues.put(DataContract.DatapieceEntry.COLUMN_CONTENT_URI, "test/uri.jpg");
        datapieceValues.put(DataContract.DatapieceEntry.COLUMN_COORD_LAT, 64.7488);
        datapieceValues.put(DataContract.DatapieceEntry.COLUMN_COORD_LONG, -147.353);
        datapieceValues.put(DataContract.DatapieceEntry.COLUMN_COORD_ACC, 32);
        datapieceValues.put(DataContract.DatapieceEntry.COLUMN_TIMESTAMP, 123456);
        datapieceValues.put(DataContract.DatapieceEntry.COLUMN_CONTENT_DESCR, "Testing");

        return datapieceValues;
    }

    static ContentValues createTestSpecimenValues() {
        // Create a new map of values, where column names are the keys
        ContentValues specimenValues = new ContentValues();
        specimenValues.put(DataContract.SpecimenEntry.COLUMN_ID, "cactus001");
        specimenValues.put(DataContract.DatapieceEntry.COLUMN_CONTENT_DESCR, "Unusual for climate");

        return specimenValues;
    }

    static ContentValues createTestSpecimenDatapiecePairing(long specimenId, long datapieceId) {
        // Create a new map of values, where column names are the keys
        ContentValues specimenDatapiecePairing = new ContentValues();
        specimenDatapiecePairing.put(DataContract.SpecimenDatapiecePairing.COLUMN_SPECIMEN_ID, specimenId);
        specimenDatapiecePairing.put(DataContract.SpecimenDatapiecePairing.COLUMN_DATAPIECE_ID, datapieceId);

        return specimenDatapiecePairing;
    }

    static long insertTestDatapieceValues(Context context) {
        // insert our test records into the database
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createTestDatapieceValues();

        long datapieceRowId;
        datapieceRowId = db.insert(DataContract.DatapieceEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Test Datapiece Values", datapieceRowId != -1);

        return datapieceRowId;
    }

    static long insertTestSpecimenValues(Context context) {
        // insert our test records into the database
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createTestSpecimenValues();

        long specimenRowId;
        specimenRowId = db.insert(DataContract.SpecimenEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Test Specimen Values", specimenRowId != -1);

        return specimenRowId;
    }

    static long insertTestSpecimenDatapiecePairing(Context context, ContentValues testValues) {
        // insert our test records into the database
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long pairRowId;
        pairRowId = db.insert(DataContract.SpecimenDatapiecePairing.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Test Specimen-Datapiece Pairing", pairRowId != -1);

        return pairRowId;
    }
}

/*
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

    }*/
