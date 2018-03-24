package com.shenll.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView env = findViewById(R.id.env);
        env.setText(BuildConfig.environment);
        startForeGroundService();
    }

    private void startForeGroundService() {
        Intent service = new Intent(this, CustomService.class);
        if (!CustomService.IS_SERVICE_RUNNING && !CustomService.IS_SERVICE_FINISHED) {
            service.setAction(Constants.ACTION.START);
            CustomService.IS_SERVICE_RUNNING = true;
            startService(service);
        }
        Toast.makeText(this, "The Service is " + (CustomService.IS_SERVICE_RUNNING ? "Running" : "Finished"), Toast.LENGTH_SHORT).show();
    }
}
