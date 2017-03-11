package com.alleoindong.cabtap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alleoindong.cabtap.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/courier.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
