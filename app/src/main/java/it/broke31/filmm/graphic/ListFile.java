package it.broke31.filmm.graphic;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.util.ArrayList;

import it.broke31.filmm.facade.Facade;
import it.broke31.filmm.facade.IFacade;
import it.broke31.filmm.support.Adapter;
import it.broke31.filmm.support.RowItem;
import it.broke31.filmm.R;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbException;

/*la classe si ocupa della gestione delle cartelle e dei file visualizza il contenuto
* delle cartelle e mostra i file presenti*/
public class ListFile extends AppCompatActivity  {
    private ListView listView;
    private Adapter adapter;
    private IFacade facade;
    private ArrayList<String> onlyTitle;
    private ArrayList<RowItem> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //tasto back
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent i = getIntent();
        String username = i.getStringExtra("username");
        String domain = i.getStringExtra("domain");
        String passowrd = i.getStringExtra("password");
        String ip = i.getStringExtra("ip");
        username = checkParam(username);
        domain = checkParam(domain);
        passowrd = checkParam(passowrd);

        /*viene creato l'oggetto Facade sfruttando l'interfaccia FacadeInterface*/
        try {
            Facade.setPermission();
            facade = new Facade(ip, new NtlmPasswordAuthentication(domain, username, passowrd));
            onlyTitle= facade.readListString();
        } catch (MalformedURLException | SmbAuthException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
            finish();
        } catch (SmbException e) {
            e.printStackTrace();
        }
        listView = (ListView) findViewById(R.id.listViewFolderId);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    final RowItem s = (RowItem) parent.getAdapter().getItem(position);
                    if (facade.isFolder(s.getTitle())) {
                        if (!facade.isProtected(s.getTitle())) {
                            facade.nextChild(s.getTitle(), facade.getAuth());
                            onlyTitle= facade.readListString();
                            Log.e("onlyTitle",onlyTitle.toString());
                            list = customRow(onlyTitle);
                            adapter = new Adapter(getApplicationContext(), R.layout.adapter, list);
                            listView.setAdapter(adapter);
                        } else
                            Snackbar.make(view, "you do not have the necessary privileges", Snackbar.LENGTH_LONG).show();
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(getApplication(), ShowFilm.class);
                                i.putExtra("title", s.getTitle());
                                i.putExtra("pc_film", true);
                                i.putExtra("path", facade.getPath() + s.getTitle());
                                i.putExtra("auth", facade.getAuth());
                                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                overridePendingTransition(0, 0);
                            }
                        }).start();
                    }
                } catch (MalformedURLException | SmbException e) {
                    e.printStackTrace();
                }
            }
        });
        manageGraphics();
    }

    /*controlla se i parametri inseriti nella LoginActivity sono validi, se viene lasciato un campo vuoto
    * quel campo assumerà valore null*/
    private String checkParam(String param) {
        if (param.equalsIgnoreCase(""))
            return null;
        return param;
    }

    /*metodo invocato quando si preme il tasto back della app, se ci troviamo nella directory root
    * viene riproposta la schermata di login*/
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home) {
            try {
                if (!facade.getMyParent().equalsIgnoreCase("smb://")) {
                    onlyTitle=facade.readListString();
                    list = customRow(onlyTitle);
                    adapter.clearAll();
                    adapter = new Adapter(getApplication().getApplicationContext(), R.layout.adapter, list);
                    listView.setAdapter(adapter);
                } else {
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                }
            } catch (SmbException | MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /*metodo che permette la gestione della grafica, setta la listview custom per visualizzare le varie
     * cartelle/file */
    public void manageGraphics() {
        if(onlyTitle!=null) {
            list = customRow(onlyTitle);
            adapter = new Adapter(this, R.layout.adapter, list);
            listView.setAdapter(adapter);
        }
    }

    /*metodo che mi serve per la gestione di ogni singola riga della listview*/
    private ArrayList<RowItem> customRow(ArrayList<String>onlyTitle) {
      return facade.customRow(getApplicationContext(),onlyTitle);
    }

    public void upList(MenuItem item) {

        adapter =new Adapter(this,R.layout.adapter,list);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_file, menu);
        return true;
    }
}

