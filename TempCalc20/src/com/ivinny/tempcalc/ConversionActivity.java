package com.ivinny.tempcalc;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;

public class ConversionActivity extends Activity implements MainFragment.OnHeadlineSelectedListener{
	
	String[] types;
	TextView result;

    @Override
    public void onCitySelected(String city, String url, String temp) {
        ConversionFragment viewer = (ConversionFragment) getFragmentManager()
                .findFragmentById(R.id.confragid);
        viewer.updateUI(url, city, temp);
    }

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setTheme(R.style.myTheme);

		setContentView(R.layout.conversion_fragment);
        MainFragment mf = (MainFragment)getFragmentManager().findFragmentById(R.id.mainfragid);
        if(getResources().getConfiguration().orientation == 2){
            mf.downloadBtnPress(this);
        }

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
	}


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
}
