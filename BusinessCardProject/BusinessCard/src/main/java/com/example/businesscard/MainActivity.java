package com.example.businesscard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {

    Context con;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        con = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView myWebView = (WebView) findViewById(R.id.webView);
        myWebView.loadUrl("file:///android_asset/index.html");

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.addJavascriptInterface(new myJsInterface(this), "Android");
    }

    public class myJsInterface {
        Context _context;

        public myJsInterface(Context context) {
            _context = context;
        }

        public void showToast(String mssg) {
            Toast.makeText(_context, mssg, Toast.LENGTH_LONG).show();


        }

        public void saveName(String name){
            String content = "";
            File file = new File(_context.getCacheDir(), "json");
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
