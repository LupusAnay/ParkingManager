package com.henculus.parkingmanager;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InspectorActivity extends FragmentActivity implements DownloadCallback<String>, TextWatcher {

    private NetworkFragment _networkFragment;
    boolean _downloading;
    EditText carnumber_input;
    String _carnumber;
    TextView _debug;
    JSONObject customer_data;
    TextView first_name;
    TextView second_name;
    TextView address;
    TextView phone;
    TextView date;
    TextView place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspector);
        carnumber_input = findViewById(R.id.carnumber_input);
        _debug = findViewById(R.id.debug_field);
        first_name = findViewById(R.id.first_name);
        second_name = findViewById(R.id.second_name);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        date = findViewById(R.id.textViewDate);
        place = findViewById(R.id.textViewPlace);

    }

    public void search_by_number(View view) {
        _carnumber = carnumber_input.getText().toString();

    }

    public void search(View view) {
        Map<String, String> params = new HashMap<>();
        params.put("carnumber", _carnumber);
        request(SERVER_HOST + "search", params, null);
    }

    public void request(String url, Map<String, String> getParams, String postParams) {

        if (!_downloading && _networkFragment != null) {
            _networkFragment.startDownload(url, getParams, postParams);
            _downloading = true;
        }
    }

    @Override
    public void updateFromDownload(String result) {

        try {
            customer_data = new JSONObject(result);
            for (int i = 0; i < customer_data.length(); i++) {
                //String place = customer_data.getString(i);
                //AvailablePlaces.add(place);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            _debug.setText(getString(R.string.server_error));
        }
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

    public boolean isEmpty(EditText text) {
        String input = text.getText().toString().trim();
        return input.length() == 0;
    }

    public void setError(EditText editText, String errorString) {

        editText.setError(errorString);

    }

    public void clearError(EditText editText) {
        editText.setError(null);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //boolean haveEmpty = false;
       //for (EditText inputField : _inputFields.values()) {
           // if (isEmpty(inputField)) {
              //  setError(inputField, getString(R.string.empty_error));
             //   _register.setEnabled(false);
            //    haveEmpty = true;
           // } else {
           //     clearError(inputField);
          //  }
       // }
//
      //  if (!haveEmpty) {
          //  _register.setEnabled(true);
       // }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
