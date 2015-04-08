package com.georgeke.stickynotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.georgeke.stickynotes.R;

import java.util.ArrayList;

/**
 * Created by George Ke on 07/04/2015.
 */
public class NotificationArrayAdapter extends ArrayAdapter {
    private final Context context;
    private ArrayList<ToDo> values = new ArrayList<>();

    public NotificationArrayAdapter(Context context, ArrayList<ToDo> values) {
        super(context, R.layout.notification_object, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.notification_object, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.notificationText);
        textView.setText(values.get(position).todo);
        Button done = (Button) rowView.findViewById(R.id.done);
        done.setTag(R.id.done, values.get(position).notificationId);

        return rowView;
    }

    public void removeNotif(int id) {
        for (int i = 0 ; i < values.size() ; i++) {
            if (id == values.get(i).notificationId) {
                values.remove(i);
                return;
            }
        }

        notifyDataSetChanged();
    }
}
