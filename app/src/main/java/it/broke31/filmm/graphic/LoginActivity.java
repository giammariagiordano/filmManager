package it.broke31.filmm.graphic;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import it.broke31.filmm.facade.Facade;
import it.broke31.filmm.R;


/*Class LoginActivity, questa classe viene invocata appena parte l'applicazione e chiede all'utente
* le credenziali di accesso*/
public class LoginActivity extends AppCompatActivity {

   private EditText username, password, ip, domain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/America.ttf");
        domain = (EditText) findViewById(R.id.domainEditText);
        username = (EditText) findViewById(R.id.usernameEditText);
        password = (EditText) findViewById(R.id.passwordEditText);
        ip = (EditText) findViewById(R.id.ipEditText);
        domain.setTypeface(myTypeface);
        username.setTypeface(myTypeface);
        password.setTypeface(myTypeface);
        ip.setTypeface(myTypeface);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /*metodo che viene invocato quando si preme il bottone ok
    * controlla se l'ip inserito è un ip valido tramite una espressione regolare*/
    public void OkclickListener(View view) {
        if (Facade.validateIp(ip.getText().toString())) {
            Intent i = new Intent(getApplicationContext(), ListFile.class);
            i.putExtra("domain", domain.getText().toString());
            i.putExtra("username", username.getText().toString());
            i.putExtra("password", password.getText().toString());
            i.putExtra("ip", ip.getText().toString());
            startActivity(i);
        } else
            Snackbar.make(view, "ip not valid", Snackbar.LENGTH_LONG).show();

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(getApplication(), Choose.class);
        finish();
        startActivity(i);
        return true;
    }
}
