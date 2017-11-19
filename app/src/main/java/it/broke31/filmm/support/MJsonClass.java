package it.broke31.filmm.support;

import android.os.StrictMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;

public class MJsonClass {
   private String key="e5ae1cec06ae360226f0cbc76620ed0f";
    public MJsonClass() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    /* decodifica una stringa contentente il titolo di un film nel formato nomefile.est ed estrapola le info del film*/

    public String decodeString(String film) throws IOException {
        film = cutString(film);
        return Jsoup.connect("http://api.themoviedb.org/3/search/movie?api_key="+key+"&query="+film)
                .timeout(10 * 1000).ignoreContentType(true).execute().body();

    }

    private String cutString(String s) {
        return s.substring(0, s.indexOf("."));
    }

    public String getTrailer(String idFilm) throws IOException, JSONException {
        String jsonResurce = Jsoup.connect("http://api.themoviedb.org/3/movie/" + idFilm
                + "/videos?api_key="+key)
                .timeout(10 * 1000).ignoreContentType(true).execute().body();
        JSONObject jsonObject = new JSONObject(jsonResurce);
        JSONArray jsonArray = (JSONArray) jsonObject.get("results");
        JSONObject json = jsonArray.getJSONObject(0);
        return "https://www.youtube.com/watch?v=" + json.get("key").toString();
    }
}