package com.dev_marinov.involtatest2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import java.util.List;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide(); // скрыть бар

        // фулскрин
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        startFragmentList(); // переход во fragmentList
    }

    @Override
    public void onBackPressed() {
        // как только будет ноль (последний экран) выполниться else
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            super.onBackPressed();
            Log.e("MAIN_ACT","getFragmentManager().getBackStackEntryCount()== " + getSupportFragmentManager().getBackStackEntryCount() );
        }
        else {
            getSupportFragmentManager().popBackStack();
            myAlertDialog();
        }
    }
    // метод реализации диалога с пользователем закрыть приложение или нет
    public void myAlertDialog()
    {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(MainActivity.this);
        alertbox.setTitle("Ты хочешь выйти ?");
        alertbox.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // уничтожение деятельности
                finish();
            }
        });

        alertbox.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                startFragmentList(); // переход во fragmentList
            }
        });
        alertbox.show();
    }

    public void startFragmentList()  // метод для перехода во fragmentList
    {
        FragmentList fragmentList = new FragmentList();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.llFragList, fragmentList, "llFragList");
        //fragmentTransaction.addToBackStack("llFragList");
        fragmentTransaction.commit();
    }
}