package com.farhaninet.alerts;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private EditText etKeywords;
    private Button btnSave;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, 100);
        }

        etKeywords = findViewById(R.id.etKeywords);
        btnSave = findViewById(R.id.btnSave);
        sharedPreferences = getSharedPreferences("BotSettings", MODE_PRIVATE);

        String savedKeywords = sharedPreferences.getString("keywords", "حوالة,تم ايداع,ايداع");
        etKeywords.setText(savedKeywords);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keywordsToSave = etKeywords.getText().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("keywords", keywordsToSave);
                editor.apply();
                Toast.makeText(MainActivity.this, "تم الحفظ بنجاح!", Toast.LENGTH_LONG).show();
            }
        });
    }
}

