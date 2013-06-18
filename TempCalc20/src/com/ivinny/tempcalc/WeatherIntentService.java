package com.ivinny.tempcalc;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.ivinny.json.CitiesT;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Mikel on 5/22/13.
 */
public class WeatherIntentService extends IntentService {
    public static final String PARAM_IN_MSG = "imsg";

    public WeatherIntentService(){
        super("WeatherIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String url = intent.getStringExtra(PARAM_IN_MSG);
        String response = "";
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

            Log.i("JSONSTRING", response);

            Bundle bundle = intent.getExtras();
            Bundle myB = new Bundle();
            myB.putString("json", response);


            //3rd party library GSON
            JsonObject jsonObj = new Gson().fromJson(response, JsonObject.class);
            final JsonObject json_data = jsonObj.getAsJsonObject("current_observation");
            final String city =  json_data.getAsJsonObject("display_location").get("city").getAsString();
            final String theUrl = json_data.get("forecast_url").getAsString();
            final double temp =  json_data.get("temp_f").getAsDouble();

            Uri uri = Uri.parse("content://com.ivinny.application.weathercontentprovider");
            Log.i("LOOOOK", uri.toString());
            ContentValues cv = new ContentValues();
            cv.put(WeatherContentProvider.WeatherData.CITY_COLUMN, city);
            cv.put(WeatherContentProvider.WeatherData.TEMP_COLUMN, temp);
            cv.put(WeatherContentProvider.WeatherData.URL_COLUMN, theUrl);
            Uri thir = getContentResolver().insert(uri, cv);

            Log.i("LOOOOK", thir.toString());
//            Messenger messenger = (Messenger) bundle.get("messenger");
//            Message msg = Message.obtain();
//            msg.setData(myB);
//            try {
//                messenger.send(msg);
//            } catch (RemoteException e) {
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
