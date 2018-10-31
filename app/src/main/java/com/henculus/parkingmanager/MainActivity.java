package com.henculus.parkingmanager;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity implements DownloadCallback<String>,
        DatePickedCallback<String> {

    private NetworkFragment _networkFragment;

    private boolean _downloading = false;

    private Spinner spinner;

    private Button button;

    String _car_number;

    ArrayList<String> AvaliablePlaces;

    private String _date;


    public void startDownload(String file, String date, String place, String car_number) {
        if (!_downloading && _networkFragment != null) {
            _networkFragment.startDownload(file, date, place, car_number);
            _downloading = true;

        } else {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        _car_number = intent.getExtras().getString("carNumber", "");


        spinner = findViewById(R.id.spinner1);
        spinner.setEnabled(false);


        button = findViewById(R.id.button);

        _networkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), "http://192.168.100.15/");

        AvaliablePlaces = new ArrayList<>();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                button.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Чилим
            }
        });
    }

    @Override
    public void updateFromDownload(String result) {
        spinner.setEnabled(true);
        try {
            AvaliablePlaces.clear();
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("success") == 1) {
                JSONArray jsonArray = jsonObject.getJSONArray("PlacesList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    int jsonObject1 = jsonArray.getInt(i);
                    String place = Integer.toString(jsonObject1);
                    AvaliablePlaces.add(place);
                }
            }
            spinner.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, AvaliablePlaces));
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        _date = date;
        startDownload("test.php", date, "", "");
    }

    public void sendData(View view) {
        String _chosen_place = spinner.getSelectedItem().toString();
        startDownload("test.php", _date, _chosen_place, _car_number);
    }
}

