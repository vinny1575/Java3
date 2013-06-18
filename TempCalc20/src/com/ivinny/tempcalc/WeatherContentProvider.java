package com.ivinny.tempcalc;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Mikel on 5/23/13.
 */
public class WeatherContentProvider extends ContentProvider {
    static final String TAG = "ProviderDemo";

    static final String AUTHORITY = "com.ivinny.application.weathercontentprovider";
    public static final Uri CONTENT_URI = Uri.parse(AUTHORITY);
    static final String SINGLE_RECORD_MIME_TYPE = "vnd.android.cursor.item/vnd.marakana.android.lifecycle.status";
    static final String MULTIPLE_RECORDS_MIME_TYPE = "vnd.android.cursor.dir/vnd.marakana.android.lifecycle.status";

    public static class WeatherData implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/items");

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.ivinny.weathercontentprovider.item";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.ivinny.weathercontentprovider.item";

        public static final String CITY_COLUMN = "city";
        public static final String TEMP_COLUMN = "temp";
        public static final String URL_COLUMN = "url";

        private WeatherData() {};
    }

    public static final int ITEMS = 1;
    public static final int ITMES_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY,"items/",ITEMS);
        uriMatcher.addURI(AUTHORITY,"items/#", ITMES_ID);
    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate");
        clearJSONFile();//clears the json file before adding to it.
        return true;
    }

    @Override
    public String getType(Uri uri) {

        switch(uriMatcher.match(uri)){
            case ITEMS:
                return WeatherData.CONTENT_TYPE;

            case ITMES_ID:
                return WeatherData.CONTENT_ITEM_TYPE;
        }
        return null;
    }

    private void clearJSONFile(){
        String content = "";
        File file = new File(getContext().getCacheDir(), "json");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file.getAbsolutePath(), false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            fos.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        //get old json data first
        File cacheFile = new File(getContext().getCacheDir(), "json");
        FileInputStream fis = null;
        JSONArray jsonAr = null;
        if (cacheFile.exists()){

            try {
                fis = new FileInputStream(cacheFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            StringBuffer fileContent = new StringBuffer("");

            byte[] buffer = new byte[1024];

            try {
                while (fis.read(buffer) != -1) {
                    fileContent.append(new String(buffer));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            String jsonStr = fileContent.toString();
            try {
                jsonAr = new JSONArray(jsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("LOOOOK", jsonStr);

        }

        //add to json next
        if (jsonAr == null){
            jsonAr = new JSONArray();
        }
        try {
            JSONObject city = new JSONObject();
            city.put(WeatherData.CITY_COLUMN, values.get("city"));
            city.put(WeatherData.TEMP_COLUMN, values.get("temp"));
            city.put(WeatherData.URL_COLUMN, values.get("url"));
            jsonAr.put(city);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String content = jsonAr.toString();
        File file = new File(getContext().getCacheDir(), "json");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file.getAbsolutePath(), false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            fos.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "insert uri: " + uri.toString());
        return uri;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query with uri: " + uri.toString());

        MatrixCursor result = new MatrixCursor(new String[] {"_id","city","temp","url"});

        File cacheFile = new File(getContext().getCacheDir(), "json");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(cacheFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuffer fileContent = new StringBuffer("");

        byte[] buffer = new byte[1024];

        try {
            while (fis.read(buffer) != -1) {
                fileContent.append(new String(buffer));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String jsonStr = fileContent.toString();
        Log.i("JSONSTRING", jsonStr);
        JSONArray jsonAr = null;
        try {
            jsonAr = new JSONArray(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i=0; i < jsonAr.length(); i++)
        {
            try {
                JSONObject city = jsonAr.getJSONObject(i);
                result.addRow(new Object[] {i, city.get(WeatherData.CITY_COLUMN), city.get(WeatherData.TEMP_COLUMN), city.get(WeatherData.URL_COLUMN)});

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

//        switch(uriMatcher.match(uri)){
//            case ITEMS:
//                return WeatherData.CONTENT_TYPE;
//
//            case ITMES_ID:
//                return WeatherData.CONTENT_ITEM_TYPE;
//        }
        return result;

    }

}
