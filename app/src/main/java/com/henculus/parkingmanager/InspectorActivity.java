package com.henculus.parkingmanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InspectorActivity extends FragmentActivity implements DownloadCallback<String> {

    private NetworkFragment _networkFragment;
    boolean _downloading;
    EditText carnumber_input;
    String _carnumber;
    TextView _debug;
    TextView first_name;
    TextView second_name;
    TextView address;
    TextView phone;
    TextView date;
    TextView place;
    LinearLayout linearLayout;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspector);
        imageView =  findViewById(R.id.imageView);
        linearLayout = findViewById(R.id.linearLayout);
        carnumber_input = findViewById(R.id.carnumber_input);
        _debug = findViewById(R.id.debug_field);
        first_name = findViewById(R.id.first_name);
        second_name = findViewById(R.id.second_name);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        date = findViewById(R.id.textViewDate);
        place = findViewById(R.id.textViewPlace);
        _networkFragment = NetworkFragment.getInstance(getSupportFragmentManager());

    }


    public void search(View view) {
        _carnumber = carnumber_input.getText().toString();
        Map<String, String> params = new HashMap<>();
        params.put("car_id", _carnumber);
        request(SERVER_HOST + "search.php", params, "");
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
            JSONObject customer_data = new JSONObject(result);
            JSONObject user_data  = customer_data.getJSONObject("user_data");
            String user_pic = customer_data.getString("user_pic");
            JSONArray orders = customer_data.getJSONArray("orders");

            decodeImage(user_pic);
            first_name.setText(user_data.getString("FIRST_NAME"));
            second_name.setText(user_data.getString("SECOND_NAME"));
            address.setText(user_data.getString("ADDRESS"));
            phone.setText(user_data.getString("PHONE"));
            for (int i = 0; i < orders.length(); i++) {
                JSONObject orders_data = orders.getJSONObject(i);
                place.append(orders_data.getString("PLACE_ID")+"\n");
                date.append(orders_data.getString("ORDERDATE")+"\n");
            }
            linearLayout.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
            _debug.setText(result);
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
    public void decodeImage(String image_code){
        byte[] decodedString = Base64.decode(image_code, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imageView.setImageBitmap(decodedByte);
    }
}
