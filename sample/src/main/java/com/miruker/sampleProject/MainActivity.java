package com.miruker.sampleProject;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.miruker.fabprogress.Fab;
import com.miruker.fabprogress.FabListener;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnShowProgress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((Fab) findViewById(R.id.fabAction)).isProgress())
                    ((Fab) findViewById(R.id.fabAction)).showProgress();
                else
                    ((Fab) findViewById(R.id.fabAction)).hideProgress();
            }
        });

        findViewById(R.id.btnFinish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((Fab) findViewById(R.id.fabAction)).isProgress())
                    ((Fab) findViewById(R.id.fabAction)).finishProgress();
            }
        });

        ((Fab) findViewById(R.id.fabAction)).addListener(new FabListener() {
            @Override
            public void finishAnimation(Fab fab) {
                Toast.makeText(getApplicationContext(),"finished!",Toast.LENGTH_SHORT).show();
            }
        });
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
}
