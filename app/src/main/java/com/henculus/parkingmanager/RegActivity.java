package com.henculus.parkingmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class RegActivity extends FragmentActivity implements DownloadCallback<String>, TextWatcher {

    private static final int GET_FROM_GALLERY = 1;
    private boolean _downloading;

    private NetworkFragment _networkFragment;

    private Map<String, EditText> _inputFields;

    private Button _register;

    private TextView _debug;

    private ImageView _imageView;

    private Bitmap bitmap = null;

    private String bitmap_result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        _imageView = findViewById(R.id.imageView);

        _debug = findViewById(R.id.debug);

        _networkFragment = NetworkFragment.getInstance(getSupportFragmentManager());

        _register = findViewById(R.id.register);
        _register.setEnabled(false);

        _inputFields = new HashMap<>();

        _inputFields.put("first_name", (EditText) findViewById(R.id.first_name));
        _inputFields.put("second_name", (EditText) findViewById(R.id.second_name));
        _inputFields.put("address", (EditText) findViewById(R.id.address));
        _inputFields.put("phone", (EditText) findViewById(R.id.phone));
        _inputFields.put("car_id", (EditText) findViewById(R.id.car_id));



        for (EditText inputField : _inputFields.values()) {
            inputField.addTextChangedListener(this);
            setError(inputField, getResources().getString(R.string.empty_error));
        }
    }

    public void reserve(View view) {
        Map<String, String> params = new HashMap<>();

        for (Map.Entry<String, EditText> entry : _inputFields.entrySet()) {
            params.put(entry.getKey(), String.valueOf(entry.getValue().getText()));
        }
        request(SERVER_HOST + "register.php", params, bitmap_result);
    }

    public void request(String url, Map<String, String> getParams, String postParams) {
        if (!_downloading && _networkFragment != null) {
            _networkFragment.startDownload(url, getParams, postParams);
            _downloading = true;
        }
    }


    @Override
    public void updateFromDownload(String result) {
        JSONObject response;
        try {
            response = new JSONObject(result);

            if (response.getString("result").equals("success")) {
                Intent intent = new Intent(RegActivity.this, ReserveActivity.class);
                String txtData = String.valueOf(Objects.requireNonNull(_inputFields.get("car_id")).getText());
                intent.putExtra("car_id", txtData);
                startActivity(intent);
            } else {
                _debug.setText(result);
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
        for (EditText inputField : _inputFields.values()) {
            if (isEmpty(inputField)) {
                setError(inputField, getString(R.string.empty_error));
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
    }

    public void choose_photo(View view) {
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }
    public static String BitmapToString(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String temp = Base64.encodeToString(b, Base64.DEFAULT);
            return temp;
        } catch (NullPointerException e) {
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            _imageView.setImageURI(selectedImage);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                bitmap_result = BitmapToString(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
