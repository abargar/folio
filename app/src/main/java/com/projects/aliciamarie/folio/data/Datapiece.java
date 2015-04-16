package com.projects.aliciamarie.folio.data;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Alicia Marie on 3/25/2015.
 */
public class Datapiece implements Parcelable {
    protected static final String URI_ARG = "CONTENT_URI";
    protected static final String NAME_ARG = "CONTENT_NAME";
    protected static final String LOCATION_ARG = "CONTENT_LOCATION";
    protected static final String TAGS_ARG = "CONTENT_TAGS";

    private Uri uri;
    private String name = "";
    private Location location = new Location("");
    private ArrayList<String> tags = new ArrayList();

    public Datapiece(String name, Uri u, Location loc){
        this.uri = u;
        this.location = loc;
        this.name = name;
    }

    public Datapiece(Uri u, Location loc, String name, ArrayList<String> tags){
        this.uri = u;
        this.location = loc;
        this.name = name;
        this.tags = tags;
    }

    public String toString() { return this.uri.toString() + " : " + this.tags.toString();}

    public String getName(){ return this.name; }

    public void setName(String name){this.name = name; }

    public Uri getUri() {
        return this.uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public double getLongitude() {
        return this.location.getLongitude();
    }

    public void setLongitude(double coord_long) {
        this.location.setLatitude(coord_long);
    }

    public double getLatitude() {
        return this.location.getLatitude();
    }

    public void setLatitude(double coord_lat) {
        this.location.setLatitude(coord_lat);
    }

    public double getLocationAccuracy() {
        return this.location.getAccuracy();
    }

    public void setLocationAccuracy(float loc_accuracy) {
        this.location.setAccuracy(loc_accuracy);
    }

    public Location getLocation() { return this.location;}

    public void setLocation(Location loc) {this.location = loc;}

    public long getTime() {
        return this.location.getTime();
    }

    public void setTime(long time) {this.location.setTime(time); }

    public void setTags(ArrayList<String> tags){this.tags = tags; }

    public void addTag(String tag){this.tags.add(tag);}

    public void removeTag(String tag){this.tags.remove(tag);}

    public ArrayList<String> getTags(){return this.tags;}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(URI_ARG, this.uri);
        bundle.putParcelable(LOCATION_ARG, this.location);
        bundle.putString(NAME_ARG, this.name);
        bundle.putStringArrayList(TAGS_ARG, this.tags);
        dest.writeBundle(bundle);
    }

    public static final Parcelable.Creator<Datapiece> CREATOR = new Creator<Datapiece>() {

        @Override
        public Datapiece createFromParcel(Parcel source) {
            Bundle bundle = source.readBundle();
            Uri uri = bundle.getParcelable(URI_ARG);
            Location location = bundle.getParcelable(LOCATION_ARG);
            String name = bundle.getString(NAME_ARG);
            ArrayList<String> tags = bundle.getStringArrayList(TAGS_ARG);
            return new Datapiece(uri,location,name,tags);
        }

        @Override
        public Datapiece[] newArray(int size) {
            return new Datapiece[size];
        }

    };
}
