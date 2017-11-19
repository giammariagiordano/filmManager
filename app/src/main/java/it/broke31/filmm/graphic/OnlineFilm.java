package it.broke31.filmm.graphic;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import it.broke31.filmm.R;


public class OnlineFilm extends AppCompatActivity {
    private EditText title;
    private Button search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_film);
        title = (EditText) findViewById(R.id.titleOnlineFilm);
        search = (Button) findViewById(R.id.searchFilm);
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/America.ttf");
        title.setTypeface(myTypeface);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void searchFilm(View view) {
        final Intent i;
        if (title.getText().toString().equals(""))
            Snackbar.make(view, "title not valid", Snackbar.LENGTH_LONG).show();
        else {
            i = new Intent(getApplicationContext(), ShowFilm.class);
            i.putExtra("title", title.getText().toString() + ".est");
            startActivity(i);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.finish();
        return true;
    }
}
