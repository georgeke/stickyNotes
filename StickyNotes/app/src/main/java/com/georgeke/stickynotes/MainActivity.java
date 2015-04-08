package com.georgeke.stickynotes;

import android.app.Activity;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.app.Notification.*;

public class MainActivity extends Activity {
    public int curNotificationId = 0;
    public NotificationManager notificationManager;
    public ArrayList<ToDo> notifications = new ArrayList<>();
    public NotificationArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String ns = Context.NOTIFICATION_SERVICE;
        notificationManager = (NotificationManager) this.getSystemService(ns);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Setup list view
        ListView lv = (ListView) findViewById(R.id.listView);
        adapter = new NotificationArrayAdapter(this, notifications);

        // Try to get saved notifications, if any
        SharedPreferences prefs = getSharedPreferences("gk", MODE_PRIVATE);
        String notificationIds = prefs.getString("notificationIds", "");
        if (notificationIds.length() > 0) {
            notifications = new ArrayList<>();
            String[] notificationIdsArray = notificationIds.split(" ");
            for (int i = 0 ; i < notificationIdsArray.length ; i++) {
                String todo = prefs.getString(notificationIdsArray[i], "ToDo not found! :(");
                int id = Integer.parseInt(notificationIdsArray[i]);
                ToDo todoObj = new ToDo(id, todo);
                notifications.add(todoObj);

                // Recreate notifications for phone restart
                createNotificationInternal(todoObj, id);
            }
        }

        adapter = new NotificationArrayAdapter(this, notifications);
        lv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createNotification(View v) {
        EditText et = (EditText) findViewById(R.id.editText);
        ToDo todo = new ToDo(curNotificationId, et.getText().toString());
        notifications.add(todo);
        adapter.notifyDataSetChanged();

        createNotificationInternal(todo, curNotificationId);

        // Clear text
        et.setText("");

        // Increment notification id
        curNotificationId ++;
    }

    public void createNotificationInternal(ToDo todo, int id) {
        Intent intent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Builder builder = new Builder(this)
                .setContentTitle("To-Do")
                .setContentText(todo.todo)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.icon)
                ;
        Notification n;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            n = builder.build();
        } else {
            n = builder.getNotification();
        }

        n.flags |= FLAG_NO_CLEAR | FLAG_ONGOING_EVENT;

        notificationManager.notify(id, n);
    }

    public void removeNotification(View v) {
        int id = (int)v.getTag(R.id.done);

        for (int i = 0 ; i < notifications.size() ; i++) {
            if (id == notifications.get(i).notificationId) {
                notifications.remove(i);
            }
        }
        adapter.notifyDataSetChanged();

        notificationManager.cancel(id);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Always start from scratch
        SharedPreferences.Editor editor = getSharedPreferences("gk", MODE_PRIVATE).edit();
        editor.clear();

        if (notifications.size() > 0) {
            StringBuilder s = new StringBuilder();

            // Store current notifs
            for (int i = 0; i < notifications.size() - 1; i++) {
                s.append(notifications.get(i).notificationId);
                s.append(" ");

                editor.putString(i + "", notifications.get(i).todo);
            }
            int lastIndex = notifications.size() - 1;
            int lastId = notifications.get(lastIndex).notificationId;
            editor.putString(lastIndex + "", notifications.get(lastIndex).todo);

            s.append(lastId);
            editor.putString("notificationIds", s.toString());
        }

        editor.apply();
    }
}
