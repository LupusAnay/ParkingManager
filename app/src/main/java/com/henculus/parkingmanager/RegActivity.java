package com.henculus.parkingmanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.util.Map;


public class RegActivity extends FragmentActivity implements DownloadCallback<String> {

    private boolean _downloading;

    private NetworkFragment _networkFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        _networkFragment = NetworkFragment.getInstance(getSupportFragmentManager());
    }

    public void request(String url, Map<String, String> params) {
        if (!_downloading && _networkFragment != null) {
            _networkFragment.startDownload(url, params);
            _downloading = true;
        }
    }

    @Override
    public void updateFromDownload(String result) {

    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
    }

    @Override
    public void finishDownloading() {
        _downloading = false;
        if (_networkFragment != null) {
            _networkFragment.cancelDownload();
        }
    }


}
