package com.twituji.dlts;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * ネットワークに関するUtilityクラス
 * <pre>
 * このクラスを利用するには、AndroidManifest.xmlに以下を追加する必要がある
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 * </pre>
 */
public class NetworkManager {
    /**
     * ネットワークの情報を返す
     * <pre>
     * Activityから利用する場合、引数にthisを渡すとメモリリークの可能性があるため、
     * this.getApplicationContext()を渡す点に注意
     * 例:
     * NetworkInfo info = NetworkManager.getNetworkInfo(this.getApplicationContext());
     * </pre>
     * 
     * @param context
     * @return
     */
    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        context = null;
        return cm.getActiveNetworkInfo();
    }
    
    /**
     * オフラインの場合にtrueを返す
     * <pre>
     * Activityから利用する場合、引数にthisを渡すとメモリリークの可能性があるため、
     * this.getApplicationContext()を渡す点に注意
     * 例:
     * boolean offline = NetworkManager.isOffline(this.getApplicationContext());
     * </pre>
     * 
     * @param context
     * @return true=オフライン、false=オンライン
     */
    public static boolean isOffline(Context context) {
        boolean res = isOnline(context);
        if(res) {
            return false;
        }
        return true;
    }

    /**
     * オンラインの場合にtrueを返す
     * <pre>
     * Activityから利用する場合、引数にthisを渡すとメモリリークの可能性があるため、
     * this.getApplicationContext()を渡す点に注意
     * 例:
     * boolean online = NetworkManager.isOnline(this.getApplicationContext());
     * </pre>
     * 
     * @param context
     * @return true=オンライン、false=オフライン
     */
    public static boolean isOnline(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean online = false;
        if(info != null) {
            online = cm.getActiveNetworkInfo().isConnected();
        }
        context = null;
        return online;
    }
}

