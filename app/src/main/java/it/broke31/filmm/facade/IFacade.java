package it.broke31.filmm.facade;

import android.content.Context;
import android.widget.ListView;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import it.broke31.filmm.support.RowItem;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbException;


public interface IFacade {

    String getMyParent() throws MalformedURLException, SmbAuthException;

    boolean isFolder(String name) throws MalformedURLException, SmbException;

    void nextChild(String nameChild, NtlmPasswordAuthentication auth) throws MalformedURLException,
            SmbAuthException;

    ArrayList<String> readListString() throws SmbException;

    NtlmPasswordAuthentication getAuth();

    boolean isProtected(String name);

    String getPath();

    long getSize(String name) throws MalformedURLException, SmbException;

    void decodeJsoup(String title) throws IOException, JSONException;

    String getJsonElement(String jsonElement) throws JSONException;

    String getTrailer(String toYoutube) throws IOException, JSONException;

    void startDownload(String title,Context context);


    Facade.Downloader getDownloader();


    boolean isVideo(String name);


    ArrayList<RowItem> customRow(Context context, ArrayList<String> onlyTitle);

}


