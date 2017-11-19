package it.broke31.filmm.graphic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import it.broke31.filmm.R;

public class Choose extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        ActivityCompat.requestPermissions(Choose.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    public void searchLocalFoder(View view) {
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
    }

    public void searchOnlineFilm(View view) {
        Intent i = new Intent(getApplicationContext(), OnlineFilm.class);
        startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED || requestCode != 1) {
            Toast.makeText(getApplicationContext(), "The download function will be disabled", Toast.LENGTH_LONG).show();
        }


    }
}
