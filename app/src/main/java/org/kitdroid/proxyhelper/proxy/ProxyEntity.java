package org.kitdroid.proxyhelper.proxy;

import java.util.List;

/**
 * Created by huiyh on 2016/4/18.
 */
public class ProxyEntity {
    private String host;
    private int port;
    private List<String> exclusionList;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<String> getExclusionList() {
        return exclusionList;
    }

    public void setExclusionList(List<String> exclusionList) {
        this.exclusionList = exclusionList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProxyEntity that = (ProxyEntity) o;

        if (port != that.port) return false;
        if (host != null ? !host.equals(that.host) : that.host != null) return false;
        return exclusionList != null ? exclusionList.equals(that.exclusionList) : that.exclusionList == null;

    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + port;
        result = 31 * result + (exclusionList != null ? exclusionList.hashCode() : 0);
        return result;
    }
}
