package com.ivinny.tempcalc;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.Menu;
import android.widget.TextView;

@SuppressLint("UseValueOf")
public class MainActivity extends Activity implements MainFragment.OnHeadlineSelectedListener {

    @Override
    public void onCitySelected(String city, String url, String temp) {
        if(getResources().getConfiguration().orientation == 2){
            ConversionFragment viewer = (ConversionFragment) getFragmentManager()
                    .findFragmentById(R.id.confragid);
            viewer.updateUI(url, city, temp);

        }else{
            Intent intent = new Intent(this, ConversionActivity.class);
            Bundle bundleObj = new Bundle();
            //Just pass a username to other screen
            bundleObj.putString("city", city);
            bundleObj.putString("url", url);
            bundleObj.putString("temp", temp);
            intent.putExtras(bundleObj);
            startActivity(intent);
        }
    }

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTheme(R.style.myTheme);
		setContentView(R.layout.main_fragment);

		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}


