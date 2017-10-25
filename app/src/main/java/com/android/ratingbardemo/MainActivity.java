package com.android.ratingbardemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private MyRatingBar rbStar;
    private TextView tvPoint;
    private TextView tvGetPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rbStar = (MyRatingBar) findViewById(R.id.rb_start);
        tvPoint = (TextView) findViewById(R.id.tv_point);
        tvGetPoint = (TextView) findViewById(R.id.tv_get_point);

        rbStar.setItem(5, 3);
        rbStar.setPointChangeListener(new MyRatingBar.OnPointChangeListener() {
            @Override
            public void onPointChangeListener(int point) {
                tvPoint.setText("当前评分：" + point);
            }
        });

        tvGetPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int point = rbStar.getPointNumber();
                Toast.makeText(MainActivity.this, String.valueOf(point), Toast.LENGTH_LONG).show();
            }
        });

        tvPoint.setText("当前评分：-");
    }
}
