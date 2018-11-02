package com.henculus.parkingmanager;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends FragmentActivity implements DownloadCallback<String>,
        DatePickedCallback<String> {

    private static final String SERVER_HOST = "http://192.168.0.100:5000/";

    private NetworkFragment _networkFragment;

    private boolean _downloading = false;

    private Spinner spinner;

    private Button button;

    TextView debugField;

    String _carNumber;

    ArrayList<String> AvailablePlaces;

    private String _date;


    public final void startDownload(URL url, Map<String, String> params) {
        if (!_downloading && _networkFragment != null) {
            _networkFragment.startDownload(url, params);
            _downloading = true;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        _carNumber = Objects.requireNonNull(intent.getExtras()).getString("carNumber", "");


        spinner = findViewById(R.id.spinner);
        spinner.setEnabled(false);

        debugField = findViewById(R.id.debug_field);
        button = findViewById(R.id.button);

        _networkFragment = NetworkFragment.getInstance(getSupportFragmentManager());

        AvailablePlaces = new ArrayList<>();


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
//        try {
//            AvailablePlaces.clear();
//            JSONObject jsonObject = new JSONObject(result);
//            if (jsonObject.getInt("success") == 1) {
//                JSONObject jsonObject2 = jsonObject.getJSONObject("0");
//                JSONArray jsonArray = jsonObject2.getJSONArray("PLACE_ID");
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    int jsonObject1 = jsonArray.getInt(i);
//                    String place = Integer.toString(jsonObject1);
//                    AvailablePlaces.add(place);
//
//                }
//            }
//            spinner.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, AvailablePlaces));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        debugField.setText(result);
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();

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
        Map<String, String> params = new HashMap<>();
        params.put("date", date);
        try {
            startDownload(new URL(SERVER_HOST + "places"), params);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void reserve(View view) {
        String place = spinner.getSelectedItem().toString();
        Map<String, String> params = new HashMap<>();
        params.put("date", _date);
        params.put("place", place);
        params.put("car_id", _carNumber);
        try {
            startDownload(new URL( SERVER_HOST + "reserve"), params);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}

