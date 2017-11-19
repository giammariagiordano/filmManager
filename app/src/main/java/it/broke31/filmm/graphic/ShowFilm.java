package it.broke31.filmm.graphic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import it.broke31.filmm.facade.Facade;
import it.broke31.filmm.facade.IFacade;
import it.broke31.filmm.R;
import jcifs.smb.SmbAuthException;

public class ShowFilm extends AppCompatActivity {
   private TextView titleTextView;
    private String title;
    private Boolean pc_film;
    private String path;
    private Facade facade;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_file);
        facade = Facade.getFacade();
        ImageView imageView = (ImageView) findViewById(R.id.imageId);
        ImageView backImageView = (ImageView) findViewById(R.id.secondPhoto);
        titleTextView = (TextView) findViewById(R.id.title);
        TextView dataRilascioTextView = (TextView) findViewById(R.id.rilasciato);
        TextView languageTextView = (TextView) findViewById(R.id.language);
        TextView descriptionTextView = (TextView) findViewById(R.id.descrizione);
        TextView imdbRating = (TextView) findViewById(R.id.imdbRatingId);
        ImageView playVideo = (ImageView) findViewById(R.id.play);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        Intent i = getIntent();
        title = i.getExtras().getString("title");
        pc_film = i.getExtras().getBoolean("pc_film", false);

        //disabilita il tasto play se la intent non è stata chiamata dalla intent pc
        if (!pc_film) {
            playVideo.setVisibility(View.INVISIBLE);
        }
        path = facade.getPath();
        try {
            // questo blocco usa la facade, ma non è stato inserito direttamente nella facade poiché
            //recupera le informazioni dalla facede e li inseriesce direttamente in oggetti grafici
            //senza fare particolari operazioni
            facade.nextChild(title,facade.getAuth());
            facade.decodeJsoup(title);
            String poster = facade.getJsonElement("poster_path");
            URL url = new URL("http://image.tmdb.org/t/p/original/" + poster);
            formatTextView(facade.getJsonElement("overview"), descriptionTextView);
            formatTextView(facade.getJsonElement("original_title"), titleTextView);
            formatTextView(facade.getJsonElement("release_date"), dataRilascioTextView);
            formatTextView(facade.getJsonElement("vote_average"), imdbRating);
            formatTextView(facade.getJsonElement("original_language"), languageTextView);
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            imageView.setImageBitmap(image);
            String secondPhotoS = facade.getJsonElement("backdrop_path");
            url = new URL("http://image.tmdb.org/t/p/original/" + secondPhotoS);
            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            backImageView.setImageBitmap(image);
            descriptionTextView.setTextColor(Color.WHITE);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Film not found!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void formatTextView(String text, TextView textView) {
        textView.setText(String.format("%s %s ", textView.getText(), text));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.finish();
            try {
                facade.getMyParent();
            } catch (MalformedURLException | SmbAuthException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        if (!pc_film) {
            menu.getItem(0).setVisible(false);//tolgo la funzione download
        }
        return true;
    }

    public void trailerOnclick(MenuItem item) {
        try {
            String toYoutube = facade.getJsonElement("id");
            toYoutube = facade.getTrailer(toYoutube);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(toYoutube)));
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Trailer not found!", Toast.LENGTH_LONG).show();
        }
    }

    public void playFilm(View view) {
        Uri uri = Uri.parse(path+title);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        Toast.makeText(getApplicationContext(),uri.toString(),Toast.LENGTH_LONG).show();
        String app=uri.toString();
        app=app.replaceAll(" ","%20");
        intent.setDataAndType(Uri.parse(app), "video/mp4");
        startActivity(intent);
    }

    public void download(MenuItem item) {
        IFacade facade = Facade.getFacade();
        facade.startDownload(title, getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        try {
            facade.getMyParent();
        } catch (MalformedURLException | SmbAuthException e) {
            e.printStackTrace();
        }
        super.onBackPressed();

    }

    public void shareInfo(MenuItem item) {
        String[] separateTitle = titleTextView.getText().toString().split("Title:");
        String text="Hey, I suggest you watch this movie:"+ separateTitle[1]+" it's great!\n" +
                "Message sent by film manager!";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,text);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}