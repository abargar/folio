package com.projects.aliciamarie.folio.utility;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.projects.aliciamarie.folio.R;
import com.projects.aliciamarie.folio.data.Datapiece;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Alicia Marie on 3/30/2015.
 */
public class DatapieceAdapter extends BaseAdapter {
    private static final String LOG_TAG = DatapieceAdapter.class.getSimpleName();
    private Context mContext;
    private List<Datapiece> datapieces;

    public DatapieceAdapter(Context context, List<Datapiece> data) {
        mContext = context;
        datapieces = data;
    }

    @Override
    public int getCount() {
        return datapieces.size();
    }

    @Override
    public Datapiece getItem(int position) {
        return datapieces.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        DatapieceHolder holder;

        if(convertView == null){
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.row_datapiece, parent, false);
            holder = new DatapieceHolder();
            holder.icon = (ImageView) row.findViewById(R.id.list_item_content_icon);
            holder.tags = (TextView) row.findViewById(R.id.list_item_content_tags);
            holder.type = (TextView) row.findViewById(R.id.list_item_content_type);
            holder.time = (TextView) row.findViewById(R.id.list_item_content_time);

            row.setTag(holder);
        }
        else{
            row = convertView;
            holder = (DatapieceHolder) row.getTag();
        }
        Datapiece datapiece = datapieces.get(position);
        if(datapiece != null) {
            Uri datapieceUri = datapiece.getUri();
            holder.icon.setImageBitmap(FileHandler.getThumbnail(mContext, datapieceUri));
            holder.tags.setText(datapiece.getTags().toString());
            holder.type.setText(FileHandler.getType(datapieceUri));
            Timestamp datapieceTime = new Timestamp(datapiece.getTime());
            holder.time.setText(datapieceTime.toString());
        }

        return row;
    }

    static class DatapieceHolder {
        ImageView icon;
        TextView tags;
        TextView type;
        TextView time;
    }

}
