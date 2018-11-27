package com.example.lenovo.weatherapp;

import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private ConstraintLayout constraintLayout;

    EditText editTextCity;
    TextView txtResult, txtTemp, txtPressure;
    String cityToFind;
    Button btnCrrWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        constraintLayout=(ConstraintLayout) findViewById(R.id.layout);

        AnimationDrawable animationDrawable= (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        editTextCity = (EditText) findViewById(R.id.editTextCity);
        txtResult = (TextView) findViewById(R.id.txtResult);
        txtTemp = (TextView) findViewById(R.id.txtTemp);
        txtPressure = (TextView) findViewById(R.id.txtPressure);
        btnCrrWeather = (Button) findViewById(R.id.btnCrrWeather);

        btnCrrWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Loading weather", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

               FindWeather(view);
            }
        });
    }

    public void FindWeather(View v) {

            cityToFind = editTextCity.getText().toString();
            if (cityToFind == null) {
                Toast.makeText(MainActivity.this, "Plz enter City Name", Toast.LENGTH_SHORT).show();
                editTextCity.setFocusable(true);

            } else {
                try {

                    ExecuteTask task = new ExecuteTask();
                    task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + cityToFind + "&units=metric&APPID=f86cea3b46cccd4b34b48f46dd47dc3f");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

    }


    public class ExecuteTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {

            String result = "";
            URL url;
            HttpURLConnection myconnection = null;


            try {

                url = new URL(strings[0]);
                myconnection = (HttpURLConnection) url.openConnection();
                InputStream in = myconnection.getInputStream();
                InputStreamReader myStreamReader = new InputStreamReader(in);
                int data = myStreamReader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = myStreamReader.read();
                }
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {

                String message = "";
                JSONObject jsonObject = new JSONObject(s);

                String infoWeatherToday = jsonObject.getString("weather");
                JSONArray array = new JSONArray(infoWeatherToday);

                JSONObject main1 = new JSONObject(jsonObject.getString("main"));
                String temperature = main1.getString("temp");
                String pressure = main1.getString("pressure");
                txtPressure.setText(pressure);
                txtTemp.setText(temperature);


                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonSecondary = array.getJSONObject(i);
                    String main = "";
                    String description = "";
                    main = jsonSecondary.getString("main");
                    description = jsonSecondary.getString("description");

                    if (main != "" && description != "") {

                        message += main + " : " + description + "\r\n";
                    }
                    if (message != "") {
                        txtResult.setText(message);
                    } else {
                        Toast.makeText(MainActivity.this, "An Error Occurred", Toast.LENGTH_SHORT).show();
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}



