package com.jogato.openweathermaptest;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.google.gson.stream.JsonReader;


public class MainActivity extends AppCompatActivity {

    private Button mOpenWeatherMap;
    private Button mAPIXU;
    private Button mSearch;
    private TextView mWarn;
    private EditText mGetCity;
    private ProgressBar mProgress;
    private String mCity;
    private long mCityId;
    private Map<String, CityInfo>cityInfoMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCity = "";
        cityInfoMap = new HashMap<String, CityInfo>();
        mWarn = (TextView) findViewById(R.id.warning);

        mProgress = (ProgressBar) findViewById(R.id.progress);
        mOpenWeatherMap = (Button) findViewById(R.id.open_weather);
        mAPIXU = (Button) findViewById(R.id.apixu);
        mSearch = (Button) findViewById(R.id.search_city);

        mGetCity = (EditText) findViewById(R.id.city);
        mGetCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(mWarn.getVisibility() == View.VISIBLE) {
                    mWarn.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mCity = editable.toString();
                mGetCity.clearFocus();
            }
        });

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mCity.isEmpty() && cityInfoMap.isEmpty()){
                    mProgress.setVisibility(View.VISIBLE);
                    mOpenWeatherMap.setEnabled(false);
                    mAPIXU.setEnabled(false);
                    mGetCity.setEnabled(false);
                    new GetCityID().execute(mCity);
                }
                else if (mCity.isEmpty()){
                    mWarn.setText("No city entered...");
                    mWarn.setVisibility(View.VISIBLE);
                }
                else{
                    mCity = mCity.toLowerCase().replace(" ", "");
                    mCityId = cityInfoMap.get(mCity.toLowerCase().replace(" ", "")).getId();
                    mProgress.clearFocus();
                    mWarn.setText("City Found!");
                    mWarn.setVisibility(View.VISIBLE);
                }
            }
        });


        mOpenWeatherMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWarn.setVisibility(View.INVISIBLE);
                Intent i = new Intent(MainActivity.this, OpenWeatherMap.class);
                i.putExtra(OpenWeatherMap.CITY_KEY, mCityId);
                startActivity(i);
            }
        });

        mAPIXU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWarn.setVisibility(View.INVISIBLE);
                Intent i = new Intent(MainActivity.this, APIXU.class);
                i.putExtra(APIXU.CITY_KEY, mCity);
                startActivity(i);
            }
        });


    }

    class CityInfo{
        long id;
        String cityName;
        String country;

        public CityInfo(long _id, String _cityName, String _country){
            this.id = _id;
            this.cityName = _cityName.toLowerCase().replace(" ", "");
            this.country = _country;
        }

        public long getId() {
            return id;
        }

        public String getCityName() {
            return cityName;
        }

        public String getCountry() {
            return country;
        }


    }

    private class GetCityID extends AsyncTask<String, Void, Map<String, CityInfo>> {

        @Override
        protected Map<String, CityInfo> doInBackground(String... strings) {
            try{
                InputStream is = MainActivity.this.getAssets().open("city.list.json");
                JsonReader jsonReader = new JsonReader(new InputStreamReader(is, "UTF-8"));
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    CityInfo c = readMessage(jsonReader);
                    if(!cityInfoMap.containsKey(c.getCityName())) {
                        cityInfoMap.put(c.getCityName(), c);
                    }
                }
                jsonReader.endArray();
                jsonReader.close();
            }catch (IOException ioe){
                ioe.printStackTrace();
            }
            return cityInfoMap;
        }

        @Override
        protected void onPostExecute(Map<String, CityInfo> cityInfo) {
            super.onPostExecute(cityInfo);
            mProgress.setVisibility(View.INVISIBLE);
            mOpenWeatherMap.setEnabled(true);
            mAPIXU.setEnabled(true);
            mGetCity.setEnabled(true);
            mCityId = cityInfo.get(mCity.toLowerCase().replace(" ", "")).getId();
            mWarn.setText("City Found!");
            mWarn.setVisibility(View.VISIBLE);
        }

        CityInfo readMessage(JsonReader reader) throws IOException {
            long id = -1;
            String cityName = null;
            String country = null;
            List<Double> geo = null;

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("id")) {
                    id = reader.nextLong();
                } else if (name.equals("name")) {
                    cityName = reader.nextString();
                } else if(name.equals("country")){
                    country = reader.nextString();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            return new CityInfo(id, cityName, country);
        }

    }

}

