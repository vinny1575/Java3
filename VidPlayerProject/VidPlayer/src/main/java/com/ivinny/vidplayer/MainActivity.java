/*
*    Author:       Alan Gonzalez
*
*    Project:      MuseumContentProvider
*
*    Package:      com.ivinny.vidplayer
*
*    File:         MainActivity.java
*
*    Purpose:      To Display the list of videos and a small video playing screen.
*
*/

package com.ivinny.vidplayer;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends Activity {

    ProgressDialog mProgressDialog;
    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;
    final Context ctx = this;
    String itemName = null;
    String thePath = null;
    String theURL = null;
    VideoView video= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the ListView resource.
        mainListView = (ListView) findViewById( R.id.listView );
        video = (VideoView)findViewById(R.id.videoView);

        // Create and populate a List of planet names.
        final String[] urls = new String[] { "http://ivinnyapps.com/test/Baby%20Neptune.m4v", "http://ivinnyapps.com/test/Baby%20Neptune.m4v", "http://ivinnyapps.com/test/Baby%20Neptune.m4v"};
        final String[] videos = new String[] { "Baby Neptune", "Baby Newton", "Baby Noah"};
        ArrayList<String> planetList = new ArrayList<String>();
        planetList.addAll( Arrays.asList(videos) );

        // Create ArrayAdapter using the planet list.
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, planetList);

        // Set the ArrayAdapter as the ListView's adapter.
        mainListView.setAdapter( listAdapter );

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                itemName = videos[position];
                thePath = ctx.getFilesDir() + "/" + itemName + ".m4v";
                theURL = urls[position];
                // instantiate the progress dialog
                mProgressDialog = new ProgressDialog(ctx);
                mProgressDialog.setMessage(videos[position] + " Downloading to " + thePath);
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMax(100);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

                File file = new File(thePath);
                if (file.exists()){
                    Toast.makeText(getApplicationContext(), "Playing from local storage.", Toast.LENGTH_LONG).show();
                    playVidAtPath(thePath);
                }else{

                    //checks if on wifi
                    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    boolean isWifi = ni.isConnected();

                    if (isWifi){
                        // execute this when the downloader must be fired
                        DownloadFile downloadFile = new DownloadFile();
                        downloadFile.execute(urls[position]);
                    }else{
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ctx);
                        builder1.setMessage("Must be on wifi to download a video.");
                        builder1.show();
                    }
                }
//                playVidAtUrl("http://ivinnyapps.com/test/Baby%20Neptune.m4v");
            }
        });

        //setup pause button
        Button pauseBtn = (Button)findViewById(R.id.pauseBtn);
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                video.pause();
            }
        });

        //setup play button
        Button playBtn = (Button)findViewById(R.id.playBtn);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                video.start();
            }
        });
    }

    // usually, subclasses of AsyncTask are declared inside the activity class.
    // that way, you can easily modify the UI thread from here
    private class DownloadFile extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... sUrl) {
            try {
                //creating url connection
                URL url = new URL(sUrl[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // this will be useful so that you can show a typical 0-100% progress bar
                int fileLength = connection.getContentLength();

                // download the file and SAVE
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(thePath);

                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress.... doing calculations (calls onProgressUpdate)
                    publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        //before download happends
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
            //stream while downloading?
            playVidAtUrl(theURL);
        }

        //updates the progress
        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            mProgressDialog.setProgress(progress[0]);
            //see onitemclicklistener for list
        }
    }


    public void playVidAtUrl(String path){

        Uri uri=Uri.parse(path);

        video.setVideoURI(uri);
        video.start();

        flip(video);
    }

    public void playVidAtPath(String path){

        Uri uri=Uri.parse(path);

        video.setVideoPath(path);
        video.start();

        flip(video);
    }

    //andimation flips any view, we are flipping the video player
    private void flip(View target){
        ObjectAnimator anim = ObjectAnimator.ofFloat(ctx, "rotationY", 0.0f, 360.0f);
        anim.setTarget(target);
        anim.setDuration(1000);
        anim.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    
}
