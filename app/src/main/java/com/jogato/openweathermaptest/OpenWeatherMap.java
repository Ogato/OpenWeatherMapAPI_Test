package com.jogato.openweathermaptest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class OpenWeatherMap extends AppCompatActivity {

    private static final String API_URL = "http://api.openweathermap.org/data/2.5/forecast?id=";
    private static final String API_KEY = "&APPID=2685bf8ee586a5edb31c0706655832bc";
    public static final String CITY_KEY = "city_key";

    private List<TextView> mDate;
    private List<TextView> mTemp;
    private List<TextView> mDescription;
    private long mCityID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_openweathermap);

        mDate = new ArrayList<TextView>();
        mTemp = new ArrayList<TextView>();
        mDescription = new ArrayList<TextView>();


        mDate.add((TextView)findViewById(R.id.date0));
        mDate.add((TextView)findViewById(R.id.date1));
        mDate.add((TextView)findViewById(R.id.date2));
        mTemp.add((TextView)findViewById(R.id.temp0));
        mTemp.add((TextView)findViewById(R.id.temp1));
        mTemp.add((TextView)findViewById(R.id.temp2));
        mDescription.add((TextView)findViewById(R.id.description0));
        mDescription.add((TextView)findViewById(R.id.description1));
        mDescription.add((TextView)findViewById(R.id.description2));

        mCityID = getIntent().getLongExtra(CITY_KEY, 4122986);
        new WeatherInfo().execute(API_URL + mCityID + API_KEY);
    }

    private class WeatherInfo extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("JO_INFO", "JUST_EXECUTED");
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            try{
                URL url = new URL(strings[0]);
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
                    return new JSONObject(stringBuilder.toString());
                }finally {
                    urlConnection.disconnect();
                }

            }
            catch (Exception e){
                return null;
            }

        }

        protected void onPostExecute(JSONObject response) {
            if(response == null){
                Log.i("JO_ERROR", "THERE WAS AN ERROR");
            }
            else{
                try {
                    JSONArray jsonArray = response.getJSONArray("list");
                    for(int i = 0; i < 3; i++){
                        TextView tempTemp = mTemp.get(i);
                        TextView tempDate = mDate.get(i);
                        TextView tempDescription = mDescription.get(i);
                        JSONObject j = (JSONObject) jsonArray.get(i);
                        tempTemp.setText(Double.toString((j.getJSONObject("main").getDouble("temp") * 1.8) - 459.67));
                        tempDescription.setText(((JSONObject)j.getJSONArray("weather").get(0)).getString("description"));
                        tempDate.setText(j.getString("dt_txt").substring(0, 10));
                    }
                }catch (JSONException e){
                    Log.e("App", "Failure", e);
                }
            }

        }
    }

}
