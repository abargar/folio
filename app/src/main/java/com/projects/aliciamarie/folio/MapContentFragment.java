package com.projects.aliciamarie.folio;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.projects.aliciamarie.folio.data.Datapiece;
import com.projects.aliciamarie.folio.utility.FileHandler;

import java.util.ArrayList;

public class MapContentFragment extends Fragment
                                implements OnMapReadyCallback {
    private static final String LOG_TAG = MapContentFragment.class.getSimpleName();
    private ArrayList<Datapiece> mDatapieces = new ArrayList();
    private SupportMapFragment mFragment;
    private GoogleMap mMap;
    private ArrayList<Marker> mMarkers = new ArrayList();

    // TODO: Rename and change types and number of parameters
    public static MapContentFragment newInstance(ArrayList<Datapiece> datapieces) {
        MapContentFragment fragment = new MapContentFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(MainActivity.DATAPIECES, datapieces);
        fragment.setArguments(args);
        return fragment;
    }

    public MapContentFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDatapieces = getArguments().getParcelableArrayList(MainActivity.DATAPIECES);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        FragmentManager fm = getChildFragmentManager();
        mFragment = ((SupportMapFragment) fm.findFragmentById(R.id.map));
        if (mFragment == null) {
            mFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, mFragment).commit();
        }
        mFragment.getMapAsync(this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
/*        if (mMap == null) {
            mFragment.getMapAsync(this);
        }*/
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(mMap != null){
            setUpMap();
        }
    }


    private void setUpMap() {
        mMap.clear();
        LatLngBounds.Builder llBuilder = new LatLngBounds.Builder();

        for(Datapiece datapiece: mDatapieces){
            LatLng ll = new LatLng(datapiece.getLatitude(), datapiece.getLongitude());
            llBuilder.include(ll);
            BitmapDescriptor marker;
            switch( FileHandler.getType(datapiece.getUri()) ) {
                case FileHandler.TYPE_IMAGE:
                    marker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                    break;
                case FileHandler.TYPE_VIDEO:
                    marker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                    break;
                case FileHandler.TYPE_AUDIO:
                    marker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                    break;
                default:
                    marker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                    break;
            }
            mMarkers.add(mMap.addMarker(new MarkerOptions().position(ll).title(datapiece.getTags().toString())
                    .snippet(datapiece.toString()).icon(marker)));
        }
    }
}
