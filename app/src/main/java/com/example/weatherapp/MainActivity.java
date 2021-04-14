package com.example.weatherapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private Boolean cityWasFound = false;
    private String snowDescription;
    private EditText editTextNameOrIndex;
    private TextView textViewInfo;
    private ConstraintLayout layout;
    private final String weatherUrl = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=0106b55e19a857839c7996f62c8d30ae&units=metric&lang=ru";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        editTextNameOrIndex = findViewById(R.id.editTextNameOrIndex);
        textViewInfo = findViewById(R.id.textViewInfo);
        layout = (ConstraintLayout) findViewById(R.id.layout);
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public void onClickGetWeather(View view) {
        String msg = editTextNameOrIndex.getText().toString().trim();
        if (!msg.isEmpty()) {
            DownloadJSONTask task = new DownloadJSONTask();
            String url = String.format(weatherUrl, msg);
            cityWasFound = false;
            task.execute(url);
        } else {
            Toast.makeText(this, R.string.isEmpty, Toast.LENGTH_SHORT).show();
        }
    }

    private class DownloadJSONTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader streamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(streamReader);
                String line = reader.readLine();
                while (line != null) {
                    result.append(line);
                    line = reader.readLine();
                    return result.toString();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                if (s == null) {
                    Toast.makeText(getApplicationContext(), R.string.notFind, Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject jsonObject = new JSONObject(s);
                    String city = jsonObject.getString("name");
                    String id = jsonObject.getString("id");
                    String newTempC = jsonObject.getJSONObject("main").getString("temp");
                    String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                    String weather = String.format("%s\nТемпература: %s\nНа улице: %s", city, newTempC, description);
                    textViewInfo.setText(weather);
                    if (weather.contains("снег")) {
                        layout.setBackgroundResource(R.drawable.snow);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}