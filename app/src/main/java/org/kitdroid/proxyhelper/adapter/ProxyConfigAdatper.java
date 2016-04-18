package org.kitdroid.proxyhelper.adapter;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import org.kitdroid.proxyhelper.R;
import org.kitdroid.proxyhelper.proxy.ProxyEntity;
import org.kitdroid.proxyhelper.proxy.ProxyDataManager;

import java.util.List;

/**
 * Created by huiyh on 2016/4/18.
 */
public class ProxyConfigAdatper extends BaseAdapter {
    private final LayoutInflater inflater;
    private final ProxyDataManager proxyManager;
    private List<ProxyEntity> proxys;
    private String currentHost;
    private int currentPort;

    public ProxyConfigAdatper(LayoutInflater inflater) {
        this.inflater = inflater;
        proxyManager = ProxyDataManager.getInstance();
        proxys = proxyManager.getProxys();
    }

    public void setCurrentProxy(String host, int port){
        currentHost = host;
        currentPort = port;
    }

    private boolean isCurrentProxy(ProxyEntity entity){
        return TextUtils.equals(entity.getHost(),currentHost) && entity.getPort() == currentPort;
    }

    @Override
    public int getCount() {
        return proxys.size();
    }

    @Override
    public Object getItem(int position) {
        return proxys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = createView();
        }
        bindView(position,convertView,parent);

        return convertView;
    }

    @NonNull
    private View createView() {
        View convertView;
        convertView = inflater.inflate(R.layout.item_proxy_info,null);
        Holder holder = new Holder();
        convertView.setTag(holder);

        holder.hostView = (TextView) convertView.findViewById(R.id.text_proxy_host);
        holder.portView = (TextView) convertView.findViewById(R.id.text_proxy_port);
        holder.proxySwitch = (Switch) convertView.findViewById(R.id.switch_proxy);
        return convertView;
    }

    private void bindView(int position, View convertView, ViewGroup parent) {
        Holder holder = (Holder) convertView.getTag();
        ProxyEntity item = (ProxyEntity) getItem(position);
        holder.hostView.setText(item.getHost());
        holder.portView.setText(String.valueOf(item.getPort()));
        holder.proxySwitch.setSelected(isCurrentProxy(item));
    }

    @Override
    public void notifyDataSetChanged() {
        proxys = proxyManager.getProxys();
        super.notifyDataSetChanged();
    }

    private static class Holder{
        TextView hostView;
        TextView portView;
        Switch proxySwitch;
    }
}
