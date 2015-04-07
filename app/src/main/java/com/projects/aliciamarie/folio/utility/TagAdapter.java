package com.projects.aliciamarie.folio.utility;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.projects.aliciamarie.folio.DetailActivity;
import com.projects.aliciamarie.folio.R;

import java.util.List;

/**
 * Created by Alicia Marie on 4/6/2015.
 */
public class TagAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mTags;

    public TagAdapter(Context context, List<String> data) {
        mContext = context;
        mTags = data;
    }

    @Override
    public int getCount() {
        return mTags.size();
    }

    @Override
    public String getItem(int position) {
        return mTags.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(String tag){
        if(mContext instanceof DetailActivity){
            ((DetailActivity) mContext).addTag(tag);
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        TagHolder holder;

        if(convertView == null){
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.row_tag, parent, false);
            holder = new TagHolder();
            holder.tag = (TextView) row.findViewById(R.id.tag_label);
            holder.deleteBtn = (Button) row.findViewById(R.id.action_delete_tag);

            row.setTag(holder);
        }
        else{
            row = convertView;
            holder = (TagHolder) row.getTag();
        }

        String tag = mTags.get(position);
        if(tag != null){
            holder.tag.setText(tag);
            holder.deleteBtn.setTag(position);
            holder.deleteBtn.setOnClickListener(deleteTagListener);
        }
        return row;
    }

    private View.OnClickListener deleteTagListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            String tag = mTags.get(position);
            if(mContext instanceof DetailActivity){
                ((DetailActivity) mContext).removeTag(tag);
            }
            notifyDataSetChanged();
        }
    };

    static class TagHolder {
        TextView tag;
        Button deleteBtn;
    }
}
