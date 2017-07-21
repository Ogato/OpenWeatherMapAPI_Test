package com.jogato.openweathermaptest;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mLocation;
    private TextView mTemp;
    private TextView mDescription;
    private EditText mCity;
    private String mSelectedCountry;
    private String mSelectedCity;
    private static final String API_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static final String API_KEY = "2685bf8ee586a5edb31c0706655832bc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocation = (TextView) findViewById(R.id.location);
        mTemp = (TextView) findViewById(R.id.temp);
        mDescription = (TextView) findViewById(R.id.description);
        mCity = (EditText) findViewById(R.id.city);
        mSelectedCountry = "us";
        mSelectedCity = "MountainView";
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.country_array, R.layout.array_layout);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.countries);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedCountry = adapterView.getItemAtPosition(i).toString();
                mSelectedCity = mCity.getText().toString().replace(" ", "");
                new WeatherInfo().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }




    private class WeatherInfo extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("JO_INFO", "JUST_EXECUTED");
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try{
                Log.i("LOG_URL", API_URL + mSelectedCity + "," + mSelectedCountry + "&" + "APPID=" + API_KEY);
                URL url = new URL(API_URL + mSelectedCity + "," + mSelectedCountry + "&" + "APPID=" + API_KEY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                try {
                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while((line = bufferedReader.readLine()) != null){
                        stringBuilder.append(line);
                    }
                    bufferedReader.close();
                    Log.i("JO_RESULTS", stringBuilder.toString());
                    return new JSONObject(stringBuilder.toString());
                }finally {
                    urlConnection.disconnect();
                    Log.i("JO_INFO", "DONE");
                }

            }
            catch (Exception e){
                Log.i("JO_ERROR", e.toString());
                return null;
            }

        }

        protected void onPostExecute(JSONObject response) {
            if(response == null){
                Log.i("JO_ERROR", "THERE WAS AN ERROR");
            }
            else{
                try {
                    Double tempInK = response.getJSONObject("main").getDouble("temp");
                    Double localTempRep = (tempInK * 1.8) - 459.67;
                    mTemp.setText("" +  localTempRep);
                    mLocation.setText(response.getString("name"));
                    mDescription.setText(response.getJSONArray("weather").getJSONObject(0).getString("description"));
                }catch (JSONException e){
                    Log.e("App", "Failure", e);
                }
            }

        }
    }

}

