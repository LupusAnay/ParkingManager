package com.henculus.parkingmanager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;


public class MainActivity extends FragmentActivity implements DownloadCallback<String>,
        DatePickedCallback<String> {

    private NetworkFragment _networkFragment;

    private boolean _downloading = false;

    private TextView fuck;

    private Spinner spinner;

    public void startDownload(String date) {
        if (!_downloading && _networkFragment != null) {
            _networkFragment.startDownload(date);
            _downloading = true;

        } else {
            fuck.setText("Start download error");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.spinner1);
        spinner.setEnabled(false);

        fuck = findViewById(R.id.fuck);

        _networkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), "http://192.168.0.100:5000/");
        fuck.setText("FUCK");
        //friendDownload();
    }

    @Override
    public void updateFromDownload(String result) {
        spinner.setEnabled(true);
        fuck.setText(result);
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;

    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch (progressCode) {
            // You can add UI behavior for progress updates here.
            case Progress.ERROR:
                Log.v("Message", "so_big_error");
                break;
            case Progress.CONNECT_SUCCESS:
                Log.v("Message", "so_big_success");
                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:
                Log.v("Message", "input_stream_success");
                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                Log.v("Message", "INPUT_STREAM_IN_PROGRESS");
                break;
            case Progress.PROCESS_INPUT_STREAM_SUCCESS:
                Log.v("Message", "INPUT_STREAM_SUCCESS");
                break;
        }

    }

    @Override
    public void finishDownloading() {
        _downloading = false;
        if (_networkFragment != null) {
            _networkFragment.cancelDownload();
        }

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void datePicked(String date) {
        startDownload(date);
    }
}

