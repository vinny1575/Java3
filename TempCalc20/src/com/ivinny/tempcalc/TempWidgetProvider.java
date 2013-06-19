package com.ivinny.tempcalc;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by Mikel on 6/19/13.
 */
public class TempWidgetProvider extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tempwidget);
            views.setOnClickPendingIntent(R.id.button, pendingIntent);

            views.setTextViewText(R.id.tempfTxt, "tester");

            Uri uri = Uri.parse("content://com.ivinny.application.weathercontentprovider");
            Cursor cursor = context.getContentResolver().query(uri, null,null,null,null);
//            if (cursor.moveToFirst() == true){
//                HashMap<String, String> displayMap = new HashMap<String, String>();
//                displayMap.put("city", cursor.getString(1));
//                displayMap.put("url", cursor.getString(3));
//                displayMap.put("temp", cursor.getString(2));
//
//                views.setTextViewText(R.id.cityT, cursor.getString(1));
//                views.setTextViewText(R.id.tempfTxt, cursor.getString(2));
//            }

            String url = "http://api.wunderground.com/api/87a8ccdf84e0370d/conditions/q/Fl/Jacksonville.json";
            WidgetAsync downloadFile = new WidgetAsync();
            try {
                String returnString = downloadFile.execute(url).get();
                JsonObject jsonObj = new Gson().fromJson(returnString, JsonObject.class);
                final JsonObject json_data = jsonObj.getAsJsonObject("current_observation");
                final String city =  json_data.getAsJsonObject("display_location").get("city").getAsString();
                final String theUrl = json_data.get("forecast_url").getAsString();
                final String temp =  json_data.get("temp_f").getAsString();

                views.setTextViewText(R.id.cityT, city);
                views.setTextViewText(R.id.tempfTxt, temp);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    class WidgetAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            for (String url : urls) {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                try {
                    HttpResponse execute = client.execute(httpGet);
                    InputStream content = execute.getEntity().getContent();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
//      textView.setText(result);
        }
    }

}
