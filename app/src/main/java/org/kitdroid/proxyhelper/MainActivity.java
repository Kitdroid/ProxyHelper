package org.kitdroid.proxyhelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import org.kitdroid.proxyhelper.adapter.ProxyConfigAdatper;
import org.kitdroid.util.IntentUtils;

public class MainActivity extends AppCompatActivity implements OnItemLongClickListener {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentUtils.startActivity(getActivity(),ProxyDetailEditActivity.class);
            }
        });

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(new ProxyConfigAdatper(LayoutInflater.from(getActivity())));
        mListView.setOnItemLongClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            IntentUtils.startActivity(getActivity(),SettingsActivity.class);
            return true;
        }

        if (id == R.id.action_wificonfig) {
            IntentUtils.startActivity(getActivity(),WiFiConfigListActivity.class);
            return true;
        }

        if (id == R.id.action_about) {
            IntentUtils.startActivity(getActivity(),SettingsAboutActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @NonNull
    private MainActivity getActivity() {
        return MainActivity.this;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        IntentUtils.startActivity(getActivity(),ProxyDetailEditActivity.class);
        return false;
    }
}
