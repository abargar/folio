package com.projects.aliciamarie.folio.utility;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Alicia Marie on 3/6/2015.
 */
public class LocationProvider implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    public abstract interface LocationCallback {
        public void handleLocation(Location location);
    }

    private static String LOG_TAG = LocationProvider.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Context mContext;
    protected Boolean isConnected = false;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;



    public LocationProvider(Context context, LocationCallback callback) {
        mLocationCallback = callback;
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds


    }

    public void connect(){
        mGoogleApiClient.connect();
        isConnected = true;
    }

    public void disconnect(){
        mGoogleApiClient.disconnect();
        Log.v(LOG_TAG, "Connection removed.");
        isConnected = false;
    }

    public Boolean isConnected(){
        return isConnected;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v(LOG_TAG, "Connection established.");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation == null){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            mLocationCallback.handleLocation(mLastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(LOG_TAG, "Connection suspended, unable to retrieve location.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution() && mContext instanceof Activity) {
            try {
                Activity activity = (Activity) mContext;
                connectionResult.startResolutionForResult(activity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(LOG_TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocationCallback.handleLocation(location);
    }

    public Location getLastLocation(){ return mLastLocation; }

}
