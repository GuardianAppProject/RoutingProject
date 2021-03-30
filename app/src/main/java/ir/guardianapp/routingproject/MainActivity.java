package ir.guardianapp.routingproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void gotoMap(View view) {
        Intent gotoMap = new Intent(this, SelectNavigationActivity.class);
        startActivity(gotoMap);
        finish();
    }
}