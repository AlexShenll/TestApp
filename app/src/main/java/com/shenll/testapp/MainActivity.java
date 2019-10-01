package com.shenll.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView env = findViewById(R.id.env);
        //Display current environment
        env.setText(BuildConfig.environment);
        //Try to start foreground service when ever activity is created
        startForeGroundService();
    }

    private void startForeGroundService() {
        //Start service when the service is not running
        if (!CustomService.IS_SERVICE_RUNNING) {
            //To avoid restarting the service
            CustomService.IS_SERVICE_RUNNING = true;
            Toast.makeText(this, "The Service is starting", Toast.LENGTH_SHORT).show();
            Intent service = new Intent(this, CustomService.class);
            service.setAction(Constants.ACTION.START);
            startService(service);
        } else {
            Toast.makeText(this, "The Service already running", Toast.LENGTH_SHORT).show();
        }
    }
}
