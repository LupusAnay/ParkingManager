package com.henculus.parkingmanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class RegActivity extends FragmentActivity implements DownloadCallback<String>, android.text.TextWatcher {

    private boolean _downloading;

    private NetworkFragment _networkFragment;

    private List<EditText> _inputFields;

    private Button _register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        _networkFragment = NetworkFragment.getInstance(getSupportFragmentManager());

        _register = findViewById(R.id.register);
        _register.setEnabled(false);

        _inputFields = new ArrayList<>();

        _inputFields.add((EditText) findViewById(R.id.first_name));
        _inputFields.add((EditText) findViewById(R.id.second_name));
        _inputFields.add((EditText) findViewById(R.id.address));
        _inputFields.add((EditText) findViewById(R.id.phone));
        _inputFields.add((EditText) findViewById(R.id.car_id));

        for (EditText inputField : _inputFields) {
            inputField.addTextChangedListener(this);
            setError(inputField, getResources().getString(R.string.empty_error));
        }
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
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        boolean haveEmpty = false;
        for (EditText inputField : _inputFields) {
            if (isEmpty(inputField)) {
                setError(inputField, getResources().getString(R.string.empty_error));
                _register.setEnabled(false);
                haveEmpty = true;
            } else {
                clearError(inputField);
            }
        }

        if (!haveEmpty) {
            _register.setEnabled(true);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void afterTextChanged(Editable s) { }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) { }

}
