package com.projects.aliciamarie.folio;

import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.projects.aliciamarie.folio.data.Datapiece;
import com.projects.aliciamarie.folio.utility.FileHandler;
import com.projects.aliciamarie.folio.utility.TagAdapter;

import java.sql.Timestamp;


/**
 * Created by Alicia Marie on 2/24/2015.
 */

public class DetailFragment extends Fragment{

    private static String LOG_TAG = DetailFragment.class.getSimpleName();
    private View rootView;

    protected TagAdapter mTagAdapter;
    protected EditText addTagText;
    protected Button addTagBtn;


    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Bundle bundle = getArguments();
        Datapiece datapiece = bundle.getParcelable(DetailActivity.DATAPIECE);
        Uri uri = datapiece.getUri();
        if(uri != null) {
            ImageView imageView = (ImageView) rootView.findViewById(R.id.imageview_capture);
            Bitmap preview = FileHandler.getThumbnail(getActivity(), uri);
            imageView.setImageBitmap(preview);
        }
        Location location = datapiece.getLocation();
        if(location != null){
            handleLocation(location);
        }
        ListView listView = (ListView) rootView.findViewById(R.id.content_tags);

        mTagAdapter = new TagAdapter(getActivity(), datapiece.getTags());
        listView.setAdapter(mTagAdapter);

        addTagText = (EditText) rootView.findViewById(R.id.edittext_add_tag);
        addTagBtn = (Button) rootView.findViewById(R.id.action_add_tag);
        addTagBtn.setOnClickListener(
                new View.OnClickListener(){

                    public void onClick(View view){
                        if (addTagText.length() > 0) {
                            Editable tag = addTagText.getText();
                            mTagAdapter.add(tag.toString());
                            tag.clear();
                        }
                    }
                });

        return rootView;
    }

    public void handleLocation(Location location){
        TextView locationView = (TextView) rootView.findViewById(R.id.textview_capture_location);
        TextView locationAccuracyView = (TextView) rootView.findViewById(R.id.textview_capture_location_accuracy);
        TextView timeView = (TextView) rootView.findViewById(R.id.textview_capture_timestamp);

        locationView.setText(String.format("%f, %f", location.getLatitude(), location.getLongitude()));
        locationAccuracyView.setText("Accuracy: " + location.getAccuracy() + "m");

        Timestamp time = new Timestamp(location.getTime());
        timeView.setText("Time: " + time.toString());
    }

}