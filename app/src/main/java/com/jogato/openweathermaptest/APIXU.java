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


public class APIXU extends AppCompatActivity {

    public static final String CITY_KEY = "get_city";
    private static final String API_URL = "https://api.apixu.com/v1/forecast.json?key=606241cc9f67477abce55230172107&q=";

    private String mCity;
    private List<TextView> mDate;
    private List<TextView> mTemp;
    private List<TextView> mDescription;


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

        mCity = getIntent().getStringExtra(CITY_KEY);
        new WeatherInfo().execute(API_URL + mCity + "&days=3");
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
                Log.i("JO_INFO", response.toString());
                try{
                    JSONArray jsonArray = response.getJSONObject("forecast").getJSONArray("forecastday");
                    for(int i = 0; i < 3; i++){
                        TextView tempTemp = mTemp.get(i);
                        TextView tempDate = mDate.get(i);
                        TextView tempDescription = mDescription.get(i);
                        JSONObject j = (JSONObject) jsonArray.get(i);
                        tempTemp.setText(Double.toString(j.getJSONObject("day").getDouble("avgtemp_f")));
                        tempDescription.setText(j.getJSONObject("day").getJSONObject("condition").getString("text"));
                        tempDate.setText(j.getString("date"));
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }

        }
    }
}
