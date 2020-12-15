package com.github.yangkangli.sample;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.github.yangkangli.ui.text_progress_bar.TextProgressBar;


public class MainActivity extends AppCompatActivity {

    private TextProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.text_progress_bar);

        progressBar.setOnProgressChangedListener(new TextProgressBar.OnProgressChangedListener() {
            @Override
            public void onProgressChange(int oldProgress, int newProgress) {
                String ss = "当前进度：" + newProgress + "%";
                if (newProgress == 65) {
                    ss = "当前进度：" + newProgress + "%";
                }
                progressBar.setProgressText(ss);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setProgress(0);
    }

    public void onViewClicked(View view) {
        int currentProgress = progressBar.getCurrentProgress();
        if (currentProgress <= 100) {
            currentProgress += 5;
        }
        if (currentProgress > 100) {
            currentProgress = 0;
        }
        progressBar.setProgress(currentProgress);
    }
}
