package org.kitdroid.proxyhelper;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

import org.kitdroid.helper.DialogHelper;
import org.kitdroid.proxyhelper.adapter.ProxyConfigAdatper;
import org.kitdroid.proxyhelper.proxy.ProxyDataManager;
import org.kitdroid.proxyhelper.proxy.ProxyEntity;
import org.kitdroid.proxyhelper.wifi.WiFiProxyManager;
import org.kitdroid.util.IntentUtils;


public class MainActivity extends AppCompatActivity implements OnItemLongClickListener, OnCheckedChangeListener, WiFiProxyManager.OnProxyChangeListener {

    public static final int REQUEST_CODE_EDIT_PROXY = 1;
    private ListView mListView;
    private ProxyConfigAdatper adapter;
    private WiFiProxyManager mWiFiProxyManager;

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
                IntentUtils.startActivityForResult(getActivity(), ProxyDetailEditActivity.class, REQUEST_CODE_EDIT_PROXY);

            }
        });


        mListView = (ListView) findViewById(R.id.listView);
        mListView.setOnItemLongClickListener(this);

        adapter = new ProxyConfigAdatper(LayoutInflater.from(getActivity()), this);
        mListView.setAdapter(adapter);

        mWiFiProxyManager = WiFiProxyManager.getInstance();

        mWiFiProxyManager.addOnProxyChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWiFiProxyManager.removeOnProxyChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCurrentProxy(mWiFiProxyManager.getCurrentWiFiProxy());
    }

    private void updateCurrentProxy(ProxyEntity entity) {
        if (entity != null) {
            adapter.setCurrentProxy(entity.getHost(), entity.getPort());
        } else {
            adapter.setCurrentProxy(null, 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: {
                IntentUtils.startActivity(getActivity(), SettingsActivity.class);
                return true;
            }
            case R.id.action_wificonfig: {
                IntentUtils.startActivity(getActivity(), WiFiConfigListActivity.class);
                return true;
            }
            case R.id.action_about: {
                IntentUtils.startActivity(getActivity(), SettingsAboutActivity.class);
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_EDIT_PROXY: {
                onProxysDataChanged();
                break;
            }
            default: {

            }
        }
    }

    private void onProxysDataChanged() {
        adapter.notifyDataSetChanged();
    }

    @NonNull
    private MainActivity getActivity() {
        return MainActivity.this;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ProxyEntity item = (ProxyEntity) adapter.getItem(position);
        showDelDailog(item);
        return true;
    }

    private void showDelDailog(final ProxyEntity entity) {
        String proxyDesc = entity.getHost() + ":" + entity.getPort();
        DialogHelper.showConfirmDialog(getActivity(), "删除代理:\n" + proxyDesc, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (entity.equals(mWiFiProxyManager.getCurrentWiFiProxy())) {
                    mWiFiProxyManager.setProxy(null);
                }
                ProxyDataManager.getInstance().removeProxy(entity);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            Object tag = buttonView.getTag();
            if (tag != null && tag instanceof ProxyEntity) {
                mWiFiProxyManager.setProxy((ProxyEntity) tag);
            }
        } else {
            mWiFiProxyManager.setProxy(null);
        }
    }

    @Override
    public void onProxyChange(ProxyEntity entity) {
        if (entity != null && !ProxyDataManager.getInstance().getProxys().contains(entity)) {
            ProxyDataManager.getInstance().addProxy(entity);
            onProxysDataChanged();
        }
        updateCurrentProxy(entity);
    }
}
