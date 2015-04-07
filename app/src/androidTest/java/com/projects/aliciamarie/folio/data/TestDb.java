/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.projects.aliciamarie.folio.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(DatabaseHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(DataContract.DatapieceEntry.TABLE_NAME);
        tableNameHashSet.add(DataContract.SpecimenEntry.TABLE_NAME);
        tableNameHashSet.add(DataContract.SpecimenDatapiecePairing.TABLE_NAME);

        mContext.deleteDatabase(DatabaseHelper.DATABASE_NAME);
        SQLiteDatabase db = new DatabaseHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without the datapiece entry, specimen entry, and specimen-datapiece pairing tables",
                tableNameHashSet.isEmpty());

        db.close();
    }



    public void testDatapieceTable() {

        verifyDatapieceTable();
        insertDatapiece();
    }

    public void testSpecimenTable() {
        verifySpecimenTable();
        insertSpecimen();
    }

    public void testSpecimenDatapieceTable() {
        verifySpecimenDatapieceTable();
        insertSpecimenDatapiecePairing();
    }

    public void verifyDatapieceTable(){
        SQLiteDatabase db = new DatabaseHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        c = db.rawQuery("PRAGMA table_info(" + DataContract.DatapieceEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: Unable to query the database for datapiece table information.",
                c.moveToFirst());

        final HashSet<String> datapieceColumnHashSet = new HashSet<String>();
        datapieceColumnHashSet.add(DataContract.DatapieceEntry._ID);
        datapieceColumnHashSet.add(DataContract.DatapieceEntry.COLUMN_CONTENT_URI);
        datapieceColumnHashSet.add(DataContract.DatapieceEntry.COLUMN_COORD_LAT);
        datapieceColumnHashSet.add(DataContract.DatapieceEntry.COLUMN_COORD_LONG);
        datapieceColumnHashSet.add(DataContract.DatapieceEntry.COLUMN_COORD_ACC);
        datapieceColumnHashSet.add(DataContract.DatapieceEntry.COLUMN_TIMESTAMP);
        datapieceColumnHashSet.add(DataContract.DatapieceEntry.COLUMN_CONTENT_DESCR);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            datapieceColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required datapiece entry columns",
                datapieceColumnHashSet.isEmpty());

        db.close();
    }

    public void verifySpecimenTable() {
        SQLiteDatabase db = new DatabaseHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        c = db.rawQuery("PRAGMA table_info(" + DataContract.SpecimenEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: Unable to query the database for specimen table information.",
                c.moveToFirst());

        final HashSet<String> specimenColumnHashSet = new HashSet<String>();
        specimenColumnHashSet.add(DataContract.SpecimenEntry._ID);
        specimenColumnHashSet.add(DataContract.SpecimenEntry.COLUMN_ID);
        specimenColumnHashSet.add(DataContract.SpecimenEntry.COLUMN_DESCR);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            specimenColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required specimen entry columns",
                specimenColumnHashSet.isEmpty());

        db.close();
    }


    public void verifySpecimenDatapieceTable() {
        SQLiteDatabase db = new DatabaseHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        c = db.rawQuery("PRAGMA table_info(" + DataContract.SpecimenDatapiecePairing.TABLE_NAME + ")",
                null);

        assertTrue("Error: Unable to query the database for specimen-datapiece pairing information.",
                c.moveToFirst());

        final HashSet<String> specimenDatapieceColumnHashSet = new HashSet<String>();
        specimenDatapieceColumnHashSet.add(DataContract.SpecimenDatapiecePairing._ID);
        specimenDatapieceColumnHashSet.add(DataContract.SpecimenDatapiecePairing.COLUMN_SPECIMEN_ID);
        specimenDatapieceColumnHashSet.add(DataContract.SpecimenDatapiecePairing.COLUMN_DATAPIECE_ID);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            specimenDatapieceColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required specimen-datapiece entry columns",
                specimenDatapieceColumnHashSet.isEmpty());

        db.close();
    }

    public long insertDatapiece() {
        // First step: Get reference to writable database
        SQLiteDatabase folioDB = new DatabaseHelper(mContext).getWritableDatabase();

        ContentValues testValues = TestUtilities.createTestDatapieceValues();

        long datapieceRowId = TestUtilities.insertTestDatapieceValues(getContext());
        assertTrue(datapieceRowId != -1);

        Cursor cursor = folioDB.query(DataContract.DatapieceEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // Move the cursor to a valid database row
        assertTrue("Error:  no records returned for query", cursor.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)

        TestUtilities.validateCurrentRecord("Resulting query did not return expected values", cursor, testValues);

        assertFalse("Error: more than one record returned from query", cursor.moveToNext());

        // Finally, close the cursor and database

        cursor.close();
        folioDB.close();

        return datapieceRowId;
    }

    public long insertSpecimen() {
        // First step: Get reference to writable database
        SQLiteDatabase folioDB = new DatabaseHelper(mContext).getWritableDatabase();


        ContentValues testValues = TestUtilities.createTestSpecimenValues();

        // Insert ContentValues into database and get a row ID back
        long specimenRowId = TestUtilities.insertTestSpecimenValues(getContext());
        assertTrue(specimenRowId != -1);
        // Query the database and receive a Cursor back

        Cursor cursor = folioDB.query(DataContract.SpecimenEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // Move the cursor to a valid database row
        assertTrue("Error:  no records returned for query", cursor.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)

        TestUtilities.validateCurrentRecord("Resulting query did not return expected values", cursor, testValues);

        assertFalse("Error: more than one record returned from query", cursor.moveToNext());

        // Finally, close the cursor and database

        cursor.close();
        folioDB.close();

        return specimenRowId;
    }

    public long insertSpecimenDatapiecePairing(){
        SQLiteDatabase folioDB = new DatabaseHelper(mContext).getWritableDatabase();

        long specimenId = insertSpecimen();
        long datapieceId = insertDatapiece();
        ContentValues testValues = TestUtilities.createTestSpecimenDatapiecePairing(specimenId, datapieceId);

        long pairRowId = TestUtilities.insertTestSpecimenDatapiecePairing(getContext(), testValues);
        assertTrue(pairRowId != -1);
        // Query the database and receive a Cursor back

        Cursor cursor = folioDB.query(DataContract.SpecimenDatapiecePairing.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // Move the cursor to a valid database row
        assertTrue("Error:  no records returned for query", cursor.moveToFirst());
        TestUtilities.validateCurrentRecord("Resulting query did not return expected values", cursor, testValues);

        assertFalse("Error: more than one record returned from query", cursor.moveToNext());

        cursor.close();
        folioDB.close();

        return pairRowId;
    };
}
