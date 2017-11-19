package it.broke31.filmm.facade;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.broke31.filmm.R;
import it.broke31.filmm.graphic.ShowFilm;
import it.broke31.filmm.smb.ISamba;
import it.broke31.filmm.smb.MySmbFile;
import it.broke31.filmm.support.MJsonClass;
import it.broke31.filmm.support.RowItem;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;


/*Classe che comunica con le classi grafiche, e comunica con l'interfaccia SambaInterface
* questa classe interroga SambaInterface estrapola i dati e li restituisce alla classe grafica che
* l'ha richiamata*/
/*é STATO APPLICATO ANCHE IL SINGLETON PATTERN*/
public class Facade implements IFacade {
    private ISamba mySmbFile;
    private static Facade myFacade;
    private MJsonClass jsoupDecode;
    private JSONObject json;
    private Downloader downloader;

    public Facade(String path, final NtlmPasswordAuthentication authentication) throws MalformedURLException, SmbAuthException {
       /* final String myPath = "smb://" + path + "/";
        mySmbFile = new MySmbFile(myPath, authentication);
        //new Thread(mySmbFile).start();
        myFacade = new Facade();*/
        final String myPath = "smb://" + path + "/";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mySmbFile = new MySmbFile(myPath, authentication);
                } catch (MalformedURLException | SmbAuthException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        myFacade = new Facade();
        myFacade.mySmbFile = new MySmbFile(myPath, authentication);
        //mySmbFile = new MySmbFile(myPath, authentication);

    }

    private Facade() throws MalformedURLException {
        this.mySmbFile = new MySmbFile("smb://");
    }

    public static Facade getFacade() {
        if (myFacade == null)
            try {
                myFacade = new Facade();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        return myFacade;
    }

    public static void setPermission() {
        jcifs.Config.setProperty("resolveOrder", "DNS");

    }

    @Override
    public String getMyParent() throws MalformedURLException, SmbAuthException {
        String oldPath = myFacade.mySmbFile.getParent();
        myFacade.mySmbFile = new MySmbFile(oldPath, myFacade.mySmbFile.getAuth());
        return oldPath;
    }

    @Override
    public boolean isFolder(String path) throws MalformedURLException, SmbException {
        return myFacade.mySmbFile.isFolder(path);
    }

    @Override
    public void nextChild(String path, NtlmPasswordAuthentication auth) throws MalformedURLException, SmbAuthException {
        myFacade.mySmbFile = myFacade.mySmbFile.nextChildren(path, auth);
    }

    @Override
    public boolean isProtected(String path) {
        return myFacade.mySmbFile.isProtected(path);
    }

    @Override
    public ArrayList<String> readListString() throws SmbException {
        return myFacade.mySmbFile.readListString();
    }

    @Override
    public NtlmPasswordAuthentication getAuth() {
        return myFacade.mySmbFile.getAuth();
    }

    @Override
    public String getPath() {
        return myFacade.mySmbFile.getPath();
    }

    public long getSize(String title) throws MalformedURLException, SmbException {
        return myFacade.mySmbFile.lenghtOfFile(title);
    }

    @Override
    public void decodeJsoup(String title) throws IOException, JSONException {
        jsoupDecode = new MJsonClass();
        String jsonResurce = jsoupDecode.decodeString(title);
        JSONObject jsonObject = new JSONObject(jsonResurce);
        JSONArray jsonArray = (JSONArray) jsonObject.get("results");
        json = jsonArray.getJSONObject(0);
    }

    @Override
    public String getJsonElement(String jsonElement) throws JSONException {
        return String.valueOf(json.get(jsonElement));
    }

    @Override
    public String getTrailer(String toYoutube) throws IOException, JSONException {
        return jsoupDecode.getTrailer(toYoutube);
    }

    @Override
    public void startDownload(String title, Context context) {
        downloader = new Downloader(title, context);
        downloader.execute();
    }

    @Override
    public Downloader getDownloader() {
        return downloader;
    }

    @Override
    public boolean isVideo(String name) {
            String[] format = {".mkv", ".mp4", ".flv", ".avi", ".3gp",".divx"};
            int lastPoint = name.lastIndexOf(".");
            String support = name.substring(lastPoint);
            for (String x : format) {
                if (x.compareToIgnoreCase(support) == 0)
                    return true;
            }
            return false;
    }
    @Override
    public ArrayList<RowItem> customRow(Context context, ArrayList<String> onlyTitle) {
        Log.e("custom",onlyTitle.toString());
        ArrayList<RowItem> list = new ArrayList<>();
        ImageView imageView=new ImageView(context);
        try {
            RowItem singleRow;
            for (String s : onlyTitle) {
                if (this.isFolder(s)) {
                    imageView.setImageResource(R.drawable.folder_share);
                    imageView.setTag(R.drawable.folder_share);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    singleRow = new RowItem(s,imageView);
                    list.add(singleRow);
                } else if (this.isVideo(s)) {
                    ImageView imageFilm=new ImageView(context);
                    imageFilm.setImageResource(R.drawable.filmicon);
                    Facade facade= Facade.getFacade();
                    facade.decodeJsoup(s);
                    String secondPhotoS = facade.getJsonElement("poster_path");
                    URL url = new URL("http://image.tmdb.org/t/p/w342/" + secondPhotoS);
                    Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    imageFilm.setImageBitmap(image);
                    imageFilm.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    singleRow = new RowItem(s,imageFilm);
                    list.add(singleRow);
                }
            }
        } catch (SmbException | MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error params", Toast.LENGTH_LONG).show();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean validateIp(String ip){
        Pattern pattern;
        String IPADDRESS_PATTERN =
                "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        pattern=Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher=pattern.matcher(ip);
        return matcher.matches();
    }

    public class Downloader extends AsyncTask<Void, Integer, Integer> {
        SmbFileInputStream mFStream;
        int id = 42;
        NotificationCompat.Builder mBuilder;
        NotificationManager mNotifyManager;
        String title;
        Context context;
        FileOutputStream mFileOutputStream;

        Downloader(String title, Context context) {
            this.context = context;
            this.title = title;
            createNotify();
        }

        private void createNotify() {
            mNotifyManager = (NotificationManager) context.getSystemService(ShowFilm.NOTIFICATION_SERVICE);
            Intent stopIntent = new Intent();
            stopIntent.setAction("STOP_DOWNLOAD");
            PendingIntent pendingStopIntent = PendingIntent.getBroadcast(context, 12345, stopIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setContentTitle("Download")
                    .setContentText("Download in progress")
                    .addAction(R.id.icon_only, "stop", pendingStopIntent)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.icona).setAutoCancel(true)
                    .build();
            mNotifyManager.notify(id, mBuilder.build());
            mBuilder.setProgress(100, 0, false);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            createNotify();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // Update progress
            super.onProgressUpdate(values);
            mBuilder.setProgress(100, values[0], false);
            mNotifyManager.notify(id, mBuilder.build());
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                long total = 0;
                Log.d("title", title);
                Log.d("myFacade", myFacade.getPath());
                long size = getSize(myFacade.getPath());
                mFStream = new SmbFileInputStream((SmbFile) myFacade.mySmbFile);
                File mLocalFile = new File(Environment.getExternalStorageDirectory(),
                        myFacade.mySmbFile.getPath());
                File myFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(),
                                                                              mLocalFile.getName());
                mFileOutputStream = new FileOutputStream(
                        myFile);
                byte[] buffer = new byte[1024];
                int len1;
                int previousProgress = 0;
                int prog;
                publishProgress((int) (0));
                while ((len1 = mFStream.read(buffer)) > 0) {
                    if (isCancelled()) {
                        mNotifyManager.cancel(id);
                        break;
                    }
                    total += len1;
                    mFileOutputStream.write(buffer, 0, len1);
                    prog = (int) ((total * 100) / size);
                    if (prog > previousProgress) {
                        previousProgress = prog;
                        publishProgress((int) (prog));
                    }
                }
                mFileOutputStream.close();
                mFStream.close();
                mNotifyManager.cancel(id);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("MalformURL", e.getMessage());
            } catch (SmbException e) {
                e.printStackTrace();
                Log.e("SMBException", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            mBuilder.setContentText("Download complete");
            mBuilder.setProgress(0, 0, false);
            mNotifyManager.notify(id, mBuilder.build());
        }

        @Override
        protected void onCancelled() {
            createNotify();
            mBuilder.setProgress(0, 0, false);
            Log.d("TIROCINIOlog", "OnCancelledChiamato");
        }
    }
}