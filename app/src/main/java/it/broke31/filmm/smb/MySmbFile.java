package it.broke31.filmm.smb;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import java.net.MalformedURLException;
import java.util.ArrayList;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;


/*la classe si occupa della gestione dei dati a basso livello crea e gestisce ogni singola cartella/file
* all'interno delle cartelle condivise*/
public class MySmbFile extends SmbFile implements ISamba, Cloneable {
    private NtlmPasswordAuthentication auth;
    private static ArrayList<String>current;

    public MySmbFile(String path, NtlmPasswordAuthentication auth) throws MalformedURLException, SmbAuthException {
        super(path, auth);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        this.auth = auth;
        current=new ArrayList<>();

    }

    public MySmbFile(String path) throws MalformedURLException {
        super(path, new NtlmPasswordAuthentication(null, null, null));
        current=new ArrayList<>();
    }

    @Override
    public String getPath() {
        return this.getCanonicalPath();
    }

    /*hidden=true show all, hidden =false only file not hidden*/
    @Override
    public ArrayList<String> readListString() throws SmbException {

        for (SmbFile file : this.listFiles())
            if (!file.isHidden())
                current.add(file.getName());
        Log.e("toR",current.toString());

        return current;
    }

    @Override
    public String getParent() {
        return super.getParent();
    }

    @Override
    public MySmbFile nextChildren(String children, NtlmPasswordAuthentication auth) throws MalformedURLException, SmbAuthException {
        return new MySmbFile(this.getPath() + children, auth);
    }

    @Override
    public boolean isFolder(String path) throws MalformedURLException, SmbException {
        MySmbFile app = new MySmbFile(this.getPath() + path + "/", auth);
        return app.isDirectory();
    }

    @Override
    public NtlmPasswordAuthentication getAuth() {
        return this.auth;
    }

    @Override
    protected ISamba clone() throws CloneNotSupportedException {

        try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        try {
            return new MySmbFile(this.getPath(), this.getAuth());
        } catch (MalformedURLException | SmbAuthException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isProtected(String folder) {
        ISamba sambaInterface;
        if (!folder.endsWith("/")){
            folder=folder+"/";
        }
        try {
            sambaInterface = new MySmbFile(getCanonicalPath() + folder, auth);
            sambaInterface.readListString();
        } catch (MalformedURLException | SmbAuthException e) {
            e.printStackTrace();
            return true;
        } catch (SmbException e) {
            e.printStackTrace();
            return true;

        }
        return false;
    }

    public long lenghtOfFile(String path) throws MalformedURLException, SmbException {
        return new SmbFile(path, getAuth()).length();
    }
}