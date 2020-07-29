package com.tdevelopments.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.BreakIterator;

public class MainActivity extends AppCompatActivity {


    EditText cityText;
    TextView infoText;
    TextView cityNameText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        
        cityText = findViewById(R.id.editTextCity);
        infoText = findViewById(R.id.textViewInfo);
        cityNameText  = findViewById(R.id.cityText);


       
    }

    public void getWeather(View view) {


        

        DownloadTask task = new DownloadTask();

        try {
            String encoddedCityName = URLEncoder.encode(cityText.getText().toString(), "UTF-8");

            task.execute("https://openweathermap.org/data/2.5/weather?q="+ encoddedCityName +"&appid=439d4b804bc8187953eb36d2a8c26a02");
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(cityText.getWindowToken(),0);

        } catch (UnsupportedEncodingException e) {
            Toast.makeText(this, "cannot find weather", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }






    }


    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;


            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }


                return result;

            }   catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject  = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                Log.i("weather content", weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);
                 String message = "";
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = jsonPart.getString("main") + " ☁ ";
                    String description = jsonPart.getString("description");

                    if (!main.equals("") && !description.equals("")) {
                        message += main + ": " + description + "\r\n";
                    }
                }

                if (!message.equals("")) {

                    cityNameText.setText(cityText.getText().toString() + "☁:" + "forecast");

                    infoText.setText(message);
                }   else {
                    Toast.makeText(MainActivity.this, "can not find the weather info", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "can not find the weather info", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }
    }
}