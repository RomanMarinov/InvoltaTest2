package com.dev_marinov.involtatest2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class CheckNetwork { // класс проверки наличия интернета

    Context context;

    public CheckNetwork(Context context) {
        this.context = context;
    }

    private static final String TAG = CheckNetwork.class.getSimpleName();

    public static boolean isInternetAvailable(Context context)
    {
        NetworkInfo info = (NetworkInfo) ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info == null)
        {
           // Toast.makeText(context.getApplicationContext(), "no internet connection checkNetwor", Toast.LENGTH_SHORT).show();
            Log.e(TAG,"no 333internet connection");
            return false;
        }
        else
        {
            if(info.isConnected())
            {
               // Toast.makeText(context.getApplicationContext(), " internet connection available...", Toast.LENGTH_SHORT).show();
                Log.e(TAG," 333internet connection available...");
                return true;
            }
            else
            {
                //Toast.makeText(context.getApplicationContext(), " internet connection checkNetwork", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "333internet connection");
                return true;
            }
        }
    }

}
