package com.example.oliverng.stormy2.UI;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oliverng.stormy2.OnSwipeTouchListener;
import com.example.oliverng.stormy2.R;
import com.example.oliverng.stormy2.adapters.DayAdapter;
import com.example.oliverng.stormy2.weather.Day;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DailyForecastActivity extends ActionBarActivity {

    int mWidth;
    private Day[] mDays;

    @InjectView(R.id.backgroundImageView) ImageView mImageView;
    @InjectView(android.R.id.empty) TextView mTextView;
    @InjectView(android.R.id.list) ListView mListView;



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
        setContentView(R.layout.activity_daily_forecast);
        ButterKnife.inject(this);

        mImageView.setOnTouchListener(new OnSwipeTouchListener(this, mWidth){
            @Override
            public void onSwipeRight(){
                finish();
            }
        });

        mListView.setOnTouchListener(new OnSwipeTouchListener(this, mWidth){
            @Override
            public void onSwipeRight() {
                finish();
            }
        });

        mListView.setEmptyView(mTextView);


        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.DAILY_FORECAST);
        mDays = Arrays.copyOf(parcelables, parcelables.length, Day[].class);

        DayAdapter adapter = new DayAdapter(this, mDays);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(mOnItemClickListener);
    }

    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String dayOfTheWeek = mDays[i].getDayOfTheWeek();
            String conditions = mDays[i].getSummary();
            String highTemp = mDays[i].getCTemperatureMax() + "";
            String message = String.format("On %s the high will be %s and it will be %s ",
                    dayOfTheWeek,
                    highTemp,
                    conditions);
            Toast.makeText(view.getContext(), message, Toast.LENGTH_LONG).show();
        }
    };




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_daily_forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
            finish();
            return true;
//        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
