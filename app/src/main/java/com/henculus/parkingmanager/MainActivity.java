package com.henculus.parkingmanager;

import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements DownloadCallback<String> {

    private NetworkFragment _networkFragment;

    private boolean _downloading = false;

    private TextView fuck;

    private void startDownload() {
        if (!_downloading && _networkFragment != null) {
            _networkFragment.startDownload();
            _downloading = true;
            Log.v("FUCK", "startDownload:  ");

        } else {
            fuck.setText("Start download error");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fuck = findViewById(R.id.fuck);
        fuck.setText("FUck!");

        _networkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), "http://127.0.0.1:5000/");
        startDownload();
    }

    @Override
    public void updateFromDownload(String result) {
        Log.v("FUCK", result);
        fuck.setText(result);
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        return null;
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {

    }

    @Override
    public void finishDownloading() {
        Log.v("FUCK", "FINISHED");
    }
}
