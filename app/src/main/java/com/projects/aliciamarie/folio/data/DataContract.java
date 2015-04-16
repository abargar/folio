package com.projects.aliciamarie.folio.data;

import android.provider.BaseColumns;

/**
 * Created by Alicia Marie on 3/12/2015.
 */
public class DataContract {

    public static final class DatapieceEntry implements BaseColumns {

        public static final String TABLE_NAME = "datapiece";

        //URI represented as ... text?
        public static final String COLUMN_CONTENT_URI = "content_uri";

        //name
        public static final String COLUMN_NAME = "name";
        //timestamp
        public static final String COLUMN_TIMESTAMP = "time";
        //long representing latitude in degrees
        public static final String COLUMN_COORD_LAT = "coord_lat";
        //long representing longitude in degrees
        public static final String COLUMN_COORD_LONG = "coord_long";
        //float representing location accuracy in meters
        public static final String COLUMN_COORD_ACC = "coord_acc";
        //text, can be null
        public static final String COLUMN_CONTENT_DESCR = "description";

    }

    public static final class SpecimenEntry implements BaseColumns {
        public static final String TABLE_NAME = "specimen";

        public static final String COLUMN_ID = "specimen_id";
        //string, can be null
        public static final String COLUMN_DESCR = "description";
    }

    public static final class SpecimenDatapiecePairing implements BaseColumns {
        public static final String TABLE_NAME = "specimen_data_pairing";
        //foreign key in table specimen
        public static final String COLUMN_SPECIMEN_ID = "specimen_id";
        //foreign key in table evidence
        public static final String COLUMN_DATAPIECE_ID = "datapiece_id";
    }

/*
    public static final class TripEntry implements BaseColumns {
        public static final String TABLE_NAME = "trip";

        */
/*
        public static final String COLUMN_TRIP_START = "trip_start";
        public static final String COLUMN_TRIP_END = "trip_end";
        *//*

        //string, can be null
        public static final String COLUMN_TRIP_DESCR = "description";
    }
*/

}
