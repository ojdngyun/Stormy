package com.example.oliverng.stormy2.UI;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.oliverng.stormy2.OnSwipeTouchListener;
import com.example.oliverng.stormy2.R;
import com.example.oliverng.stormy2.weather.Current;
import com.example.oliverng.stormy2.weather.Day;
import com.example.oliverng.stormy2.weather.Forecast;
import com.example.oliverng.stormy2.weather.Hour;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String DAILY_FORECAST = "DAILY_FORECAST";
    public static final String HOURLY_FORECAST = "HOULRY_FORECAST";

    private Forecast mForecast;
    private double latitude;
    private double longitude;
    private int mWidth;

    @InjectView(R.id.timeLabel) TextView mTimeLabel;
    @InjectView(R.id.temperatureLabel) TextView mTemperatureLabel;
    @InjectView(R.id.humidityValue) TextView mHumidityValue;
    @InjectView(R.id.precipValue) TextView mPrecipValue;
    @InjectView(R.id.summaryLabel) TextView mSummaryLabel;
    @InjectView(R.id.iconImageView) ImageView mIconImageView;
    @InjectView(R.id.refreshButton) ImageView mRefreshButton;
    @InjectView(R.id.progressBar) ProgressBar mProgressBar;
    @InjectView(R.id.backgroundImageView) ImageView mImageView;


    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mWidth = size.x;
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mProgressBar.setVisibility(View.INVISIBLE);

        latitude = 43.6667;
        longitude = -79.4167;

        getForecast(latitude, longitude);

        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForecast(latitude, longitude);
            }
        });
        mImageView.setOnTouchListener(new OnSwipeTouchListener(this, mWidth){
            @Override
            public void onSwipeLeft(){
                Intent subAcitivity = new Intent(MainActivity.this, DailyForecastActivity.class);
                subAcitivity.putExtra(DAILY_FORECAST, mForecast.getDailyForecast());
                Bundle translateBundle = ActivityOptions.makeCustomAnimation(MainActivity.this,
                        R.anim.slide_in_left, R.anim.slide_out_left).toBundle();
                startActivity(subAcitivity, translateBundle);
            }

            @Override
            public void onSwipeRight(){
                Intent subActivity = new Intent(MainActivity.this, HourlyForecastActivity.class);
                subActivity.putExtra(HOURLY_FORECAST, mForecast.getHourlyForecast());
                Bundle translateBundle = ActivityOptions.makeCustomAnimation(MainActivity.this,
                        R.anim.slide_in_right, R.anim.slide_out_right).toBundle();
                startActivity(subActivity, translateBundle);
            }

            @Override
            public void onSwipeBottom() {
                getForecast(latitude, longitude);
            }
        });

    }

    private void getForecast(double latitude, double longitude) {
        String apiKey = "9fae88c03e66b95357f31f76650ffbcb";

        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey + "/" + latitude + "," + longitude;


        if(isNetworkAvailable()) {
            toggleRefresh();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(forecastUrl).build();

            Call call = client.newCall(request);

            //asynchronous process occurring in background
            call.enqueue(new Callback() {

                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUser(getString(R.string.error_callback_message));
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toggleRefresh();
                            }
                        });
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mForecast = parseForecastDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e){
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        }else{
            alertUser(getString(R.string.error_network_message));
        }
    }

    private void alertUser(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.error_title))
                .setMessage(message)
                .setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void toggleRefresh(){
        if(mProgressBar.getVisibility() == View.INVISIBLE) {
            mRefreshButton.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }else{
            mRefreshButton.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void updateDisplay() {
        Current current = mForecast.getCurrent();

        mTemperatureLabel.setText(current.getCTemperature() + "");
        mTimeLabel.setText("At " + current.getFormattedTime() + " it will be");
        mHumidityValue.setText(current.getHumidity() + "");
        mPrecipValue.setText(current.getPrecipitation() + "%");
        mSummaryLabel.setText(current.getSummary());

        Drawable drawable =  getResources().getDrawable(current.getIconId());
        mIconImageView.setImageDrawable(drawable);
    }

    private Forecast parseForecastDetails(String jsonData) throws JSONException {
        Forecast forecast = new Forecast();
        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setDailyForecast(getDailyForecast(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));
        return forecast;
    }

    private Hour[] getHourlyForecast(String jsonData)  throws JSONException{
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");

        Hour[] hourlyForecast = new Hour[data.length()];
        for(int i = 0; i < data.length(); i++){

            Hour hour = new Hour();
            hour.setTimezone(timezone);
            hour.setTime(data.getJSONObject(i).getLong("time"));
            hour.setIcon(data.getJSONObject(i).getString("icon"));
            hour.setSummary(data.getJSONObject(i).getString("summary"));
            hour.setTemperature(data.getJSONObject(i).getDouble("temperature"));

            hourlyForecast[i] = hour;
        }
        return hourlyForecast;
    }

    private Day[] getDailyForecast(String jsonData) throws JSONException{
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        Day[] dailyForecast = new Day[data.length()];
        for(int i = 0; i < data.length(); i++){

            Day day = new Day();
            day.setTimezone(timezone);
            day.setTime(data.getJSONObject(i).getLong("time"));
            day.setTemperatureMax(data.getJSONObject(i).getDouble("temperatureMax"));
            day.setIcon(data.getJSONObject(i).getString("icon"));
            day.setSummary(data.getJSONObject(i).getString("summary"));

            dailyForecast[i] = day;
        }
        return dailyForecast;
    }

    private Current getCurrentDetails(String jsonData) throws JSONException{
        JSONObject forecast = new JSONObject(jsonData);

        String timezone = forecast.getString("timezone");

        JSONObject currently = forecast.getJSONObject("currently");

        Current current = new Current();
        current.setHumidity(currently.getDouble("humidity"));
        current.setTime(currently.getLong("time"));
        current.setIcon(currently.getString("icon"));
        current.setPrecipitation(currently.getDouble("precipProbability"));
        current.setSummary(currently.getString("summary"));
        current.setTemperature(currently.getDouble("temperature"));
        current.setTimezone(timezone);

        return current;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
    }

    private void alertUserAboutError() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.error_title))
                .setMessage(getString(R.string.error_message))
                .setPositiveButton(getString(R.string.error_ok_button_text), null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
