package it.broke31.filmm.support;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import it.broke31.filmm.facade.Facade;


public class MyBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.compareToIgnoreCase("STOP_DOWNLOAD") == 0) {
            Facade facade = Facade.getFacade();
            Log.d("TIROCINIOlog", facade.getDownloader().toString());
            Boolean cancel = facade.getDownloader().cancel(false);
            Log.d("TIROCINIOlog", cancel.toString());
        }
    }
}






