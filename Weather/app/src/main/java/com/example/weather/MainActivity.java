/**
 * MainActivity.java:- A class which helps to create weather Application using oneweathermap api
 *
 * @author :- Deep Shah (B00796368)
 * @version :-1.0
 * Created on 20 March 2019
 */
package com.example.weather;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    // Declaration of all the element that are used to store different parameters of weather
    EditText city;
    Button getWeather;
    TextView getTemperature, cityName, minTemperature, maxTemperature, weather, desc, humidity, cloud;
    AutoCompleteTextView cityAutocomplete;

    //Delcaration of final variable
    private static final String URL_address = "https://api.openweathermap.org/data/2.5/weather?q=Halifax&appid=c7efb41660082306a3b0fe9ee27f770d";
    private static final double kelvin = 273.15;
    private static final String key = "c7efb41660082306a3b0fe9ee27f770d";
    private static final String Request_method = "GET";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //checking for the internet connection
        if (isNetworkStatusAvialable(getApplicationContext())) {
            // To  display the weather of Halifac city when the application opens
            new getWeather().execute(URL_address);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.internet_na), Toast.LENGTH_SHORT).show();

        }

        // Connecting the variable with their XML id's
        city = (EditText) findViewById(R.id.l_city);
        getWeather = (Button) findViewById(R.id.bt_weather);
        getTemperature = (TextView) findViewById(R.id.l_temperature);
        cityName = (TextView) findViewById(R.id.l_cityname);
        minTemperature = (TextView) findViewById(R.id.minimum_tempereature);
        maxTemperature = (TextView) findViewById(R.id.maximun_temperature);
        weather = (TextView) findViewById(R.id.main_weather);
        desc = (TextView) findViewById(R.id.description);
        humidity = (TextView) findViewById(R.id.humidity);
        cloud = (TextView) findViewById(R.id.clouds);
        String[] array = getResources().getStringArray(R.array.cities);
        cityAutocomplete = (AutoCompleteTextView) findViewById(R.id.l_city);

        // Arrayadapter which is used to display the the static list of the city name
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, array);
        cityAutocomplete.setThreshold(1);
        cityAutocomplete.setAdapter(adapter);

        // Actions when user click on the getweather button
        getWeather.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //when user click on the button, keyboard should hide automatically
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(cityAutocomplete.getWindowToken(), 0);

                //To check if user is connected with internet or not
                if (isNetworkStatusAvialable(getApplicationContext())) {

                    //if user does not provide any value for the city it displays the toast
                    if (city.getText().toString().equals("")) {
                        Toast.makeText(MainActivity.this, getString(R.string.blank_city), Toast.LENGTH_SHORT).show();

                        //Return all the parameters of weather for the given cityname
                    } else if (city.getText().toString().matches("[0-9]+")) {
                        Toast.makeText(MainActivity.this, getString(R.string.cityname_digit), Toast.LENGTH_SHORT).show();
                    } else if (city.getText().toString().matches("^[a-zA-Z ]*$")){
                        String cityname = city.getText().toString();
                        new getWeather().execute(getString(R.string.url) + cityname + getString(R.string.appid) + key + "");
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, getString(R.string.invalid_city), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.internet_na), Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    //Method which returns true or false based on the user conectivity with internet
    public static boolean isNetworkStatusAvialable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if (netInfos != null)
                if (netInfos.isConnected())
                    return true;
        }
        return false;
    }

    //This class allows you to perform background operations and publish results on the UI thread
    // without having to manipulate threads and/or handlers.
    public class getWeather extends AsyncTask<String, String, String> {

        //This method is used to perform background computation that can take a long time.
        // The parameters of the asynchronous task are passed to this step.
        @Override
        protected String doInBackground(String... strings) {


            StringBuilder builder = new StringBuilder();

            try {

                //Initializtion of http URL Connection
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
                urlConnect.setRequestMethod(Request_method);

                // InputStreamReader and BufferedReader useful to store all the values that are
                //fetched  from the weather API in JSON format
                InputStreamReader streamReader = new InputStreamReader(urlConnect.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(streamReader);

                String input = "";

                // Read every line using BufferedReader
                while ((input = bufferedReader.readLine()) != null) {

                    builder.append(input);
                }


                urlConnect.disconnect();
                streamReader.close();

            } catch (Exception e) {

                e.printStackTrace();
            }

            // Storing values from builder as a string in builderStr and returns Str string
            String Str = builder.toString();
            return Str;
        }

        //invoked on the UI thread after the background computation finishes.
        // The result of the background computation is passed to this step as a parameter.
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            try {

                // Creation of different JSONObject to fetched different weather parameter
                // from the given result.
                JSONObject one = new JSONObject(result);
                JSONObject mainJson = one.getJSONObject(getString(R.string.Main));
                JSONObject sysJson = one.getJSONObject(getString(R.string.Sys));
                JSONArray weatherarray = one.getJSONArray(getString(R.string.Weather));
                JSONObject cloudJson = one.getJSONObject(getString(R.string.Cloud));
                JSONObject w = weatherarray.getJSONObject(0);
                String main_weather = w.getString(getString(R.string.Main));
                String description = w.getString(getString(R.string.Description)).toLowerCase();

                //Assign value from the JSON object to display the result

                int temp = (int) (mainJson.getDouble(getString(R.string.Temperature)) - kelvin);
                String st = String.valueOf(temp);
                getTemperature.setText(st + " " + "°" + 'C');
                cityName.setText(one.getString(getString(R.string.name)) + ", " + sysJson.getString(getString(R.string.country)));
                minTemperature.setText(getString(R.string.Minimum) + (int) (mainJson.getDouble(getString(R.string.temp_min)) - kelvin) + " " + "°" + 'C');
                maxTemperature.setText(getString(R.string.Maximum) + (int) (mainJson.getDouble(getString(R.string.temp_max)) - kelvin) + " " + "°" + 'C');
                weather.setText(main_weather);
                desc.setText(description);
                humidity.setText(mainJson.getString(getString(R.string.humidity)) + "%" + " " + getString(R.string.humidity_text));
                cloud.setText(cloudJson.getString(getString(R.string.All)) + "%" + " " + getString(R.string.cloud_text));

                // View  which is used to set the image in the background
                View v = (LinearLayout) findViewById(R.id.image_weather);

                // Different image will display according to the value of description of the weather


                if (description.contains("snow")) {

                    Drawable draw = ResourcesCompat.getDrawable(getResources(), R.drawable.snow_image, null);
                    v.setBackground(draw);

                } else if (description.contains("clear")) {

                    Drawable draw = ResourcesCompat.getDrawable(getResources(), R.drawable.clean, null);
                    v.setBackground(draw);

                } else if (description.contains("sunny")) {

                    Drawable draw = ResourcesCompat.getDrawable(getResources(), R.drawable.sunny_sky, null);
                    v.setBackground(draw);

                } else if (description.contains("thunderstorm")) {

                    Drawable draw = ResourcesCompat.getDrawable(getResources(), R.drawable.thunder_storm, null);
                    v.setBackground(draw);

                } else if (description.contains("rain")) {

                    Drawable draw = ResourcesCompat.getDrawable(getResources(), R.drawable.rainy_image, null);
                    v.setBackground(draw);

                } else if (description.contains("clouds")) {

                    Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.cloud, null);
                    v.setBackground(drawable);

                }

            } catch (JSONException e) {

                // If user has not select any city it will lead to the exception and show this toast
                Toast.makeText(MainActivity.this, getString(R.string.enter_valid_city), Toast.LENGTH_SHORT).show();
                e.printStackTrace();


            }
        }
    }

}