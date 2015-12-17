package com.project.cse570.networkinfo.activities;/**
 * @author: Basava R. Kanaparthi <basava.08@gmail.com> created on 12/6/2015.
 */

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.project.cse570.networkinfo.R;


/**
 * @author: Basava R. Kanaparthi <basava.08@gmail.com> created on 06,December,2015.
 */
public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        TextView textView = (TextView) findViewById(R.id.helpTextView);
        Typeface tf = Typeface.createFromAsset(this.getAssets(), "OpenSans-Regular.ttf");
        textView.setTypeface(tf);
    }
}
