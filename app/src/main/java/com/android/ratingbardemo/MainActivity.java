package com.android.ratingbardemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MyRatingBar rbStart = (MyRatingBar) findViewById(R.id.rb_start);
        rbStart.setItem(5, 3);

        findViewById(R.id.tv_get_point).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int point = rbStart.getPointNumber();
                Toast.makeText(MainActivity.this, String.valueOf(point), Toast.LENGTH_LONG).show();
            }
        });

    }
}
