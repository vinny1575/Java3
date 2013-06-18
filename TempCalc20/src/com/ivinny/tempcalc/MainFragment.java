package com.ivinny.tempcalc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import java.util.logging.LogRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ivinny.json.CitiesT;
import com.ivinny.json.Json;

import android.R.bool;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainFragment extends Fragment {


	LinearLayout ll;
	LinearLayout.LayoutParams lp;
	EditText et;
	TextView result;
	String[] types;
	TextView dataTView;
	String jsonStr;
	boolean clicked = false;
	int index = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        insertContent();
    }

    OnHeadlineSelectedListener mCallBack;

    // Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        public void onCitySelected(String city, String url, String temp);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallBack = (OnHeadlineSelectedListener)activity;
    }

    @Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putBoolean("clicked", clicked);
		outState.putInt("index", index);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if(savedInstanceState != null){
			clicked = savedInstanceState.getBoolean("clicked");
			index = savedInstanceState.getInt("index");
		}
		// TODO Auto-generated method stub
		final Context context = getActivity();
		//adds the types from the resources 

		ll = (LinearLayout) inflater.inflate(R.layout.form, container, false);
		ll.setOrientation(LinearLayout.VERTICAL);
		lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		ll.setLayoutParams(lp);
		
		
		//button used to download json data 
		Button getDataBtn = (Button)ll.findViewById(R.id.button2);;
		getDataBtn.setText("Download Cities");
		
		//results of json data after formatted
		dataTView = (TextView)ll.findViewById(R.id.textView3);;
		
		//button click for loading json data
		getDataBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				downloadBtnPress(context);
			}
		});
		
		ScrollView sv = new ScrollView(getActivity());
		sv.addView(ll);
		
		if(clicked){
			downloadBtnPress(context);
			Log.i("HERE", "CLICKED");
		}else{
			Log.i("HERE", "NOT CLICKED");
		}
		return sv;
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

    public void insertContent(){
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni!=null){
            if(ni.isConnected()){
                JSONArray ar = null;
                try {
                    JSONObject citiesToGet = new JSONObject(Json.getJsonString());
                    ar = citiesToGet.getJSONArray("Cities");

                } catch (JSONException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                //Array of cities to add
                JSONArray cities = new JSONArray();

                //clear cache
                String content = "";
                File file = new File(getActivity().getCacheDir(), "appCache");
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

                //adds results for fetch (json string) to an array of cities info
                for(int i = 0; i<ar.length(); i++){
                    jsonStr = null;
                    try {
                        getTempForCityFromWebSvc(ar.getJSONObject(i).getString("city"), ar.getJSONObject(i).getString("state"));
                    } catch (JSONException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }else{
                showAlert();
            }
        }else{
            showAlert();

        }

    }

    private static ArrayList<View> getViewsByTag(ViewGroup root, String tag){
        ArrayList<View> views = new ArrayList<View>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }

            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }

        }
        return views;
    }

	
	public void downloadBtnPress(final Context context){
//		final ProgressBar progress = (ProgressBar)findViewById(R.id.progressBar1);
		clicked = true;

        //        clear text
        dataTView.setText("");

        //remove buttons
        ArrayList views = getViewsByTag(ll, "CityB");
        for (int i = 0; i<views.size(); i++){
            ll.removeView((View)views.get(i));
        }

		ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if(ni!=null){
			if(ni.isConnected()){
				Log.i("network","online");
                ArrayList list = new ArrayList();

                Uri uri = Uri.parse("content://com.ivinny.application.weathercontentprovider");
                Cursor cursor = getActivity().getContentResolver().query(uri, null,null,null,null);
                if (cursor.moveToFirst() == true){
                    for (int i = 0; i < cursor.getCount(); i++){
                        HashMap<String, String> displayMap = new HashMap<String, String>();
                        displayMap.put("city", cursor.getString(1));
                        displayMap.put("url", cursor.getString(3));
                        displayMap.put("temp", cursor.getString(2));

//                        cursor.moveToNext();

                        list.add(displayMap);

                        try {
                            addCityBtn(cursor.getString(1),cursor.getString(3),cursor.getString(2));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        cursor.moveToNext();
                    }
                }
			}else{
				showAlert();
			}
		}else{
			showAlert();

		}
	}

    public void addCityBtn(final String city, final String url, final String temp) throws JSONException, FileNotFoundException {


        CitiesT jType = CitiesT.fromLetter(city);
        // .. get all value here
        Log.i("City: ", city);
        Button cityB = new Button(getActivity());
//        cityB.setId(i);
        cityB.setTag("CityB");
        cityB.setText(city+": "+temp+" ("+jType.isCold(Double.parseDouble(temp))+")");
        cityB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                mCallBack.onCitySelected(city, url, temp);
                index = v.getId();
            }
        });
        ll.addView(cityB);

        String text = "\r\n"+city+": "+temp+" ("+jType.isCold(Double.parseDouble(temp))+")";
        //writing a cache file
        String content = text;
        File file = new File(getActivity().getCacheDir(), "appCache");
        FileOutputStream fos = new FileOutputStream(file.getAbsolutePath(), true);

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
	//alert for no internet
	public void showAlert(){
        AlertDialog.Builder alt = new AlertDialog.Builder(getActivity());
        alt.setMessage("Not Connected To The Internet.").setPositiveButton("OK", null);
        alt.show();
	}


	//get network data (specific weather info for city and state
	public void getTempForCityFromWebSvc(String city, String state){

        Handler messageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle reply = msg.getData();
//                String json = reply.getString("json");
//                try {
//                    addCityBtn(json);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
            }
        };

		String url = "http://api.wunderground.com/api/87a8ccdf84e0370d/conditions/q/"+state+"/"+city+".json";
        Intent msgIntent = new Intent(getActivity(), WeatherIntentService.class);
        msgIntent.putExtra(WeatherIntentService.PARAM_IN_MSG, url);
        msgIntent.putExtra("messenger", new Messenger(messageHandler));
        getActivity().startService(msgIntent);

//		WeatherAsync resp = new WeatherAsync();
//		try {
//			returnStr = resp.execute(url).get();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
}
