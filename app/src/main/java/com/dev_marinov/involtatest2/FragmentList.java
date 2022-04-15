package com.dev_marinov.involtatest2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class FragmentList extends Fragment{

    View frag;
    String url = "https://dev-marinov.ru/server/involtaServer/involtalist.php";
    RecyclerView recyclerView;
    AdapterList adapterList;
    ArrayList<ObjectMessage> arrayList;
    ProgressBar progressBar, progressBarCheckNet;
    Handler handler; // для задержки работы progressBar
    Button btTryAgain; // кн повторить запрос
    TextView tvInfo;
    CardView cardView;
    int z = 0; // переменная для увеличения значения для метода getData
    int numForTry; // временная переменная для копии увеличенного значения для метода getData

    TextView tvVisibleRangeBefore, tvVisibleRangeAfter, tvAdd, tvTotalVisibleElementsStart,
            tvTotalVisibleElementsEnd, tvTotalElements;
    int count = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        frag = inflater.inflate(R.layout.fragment_list, container, false);

        arrayList = new ArrayList<>(); // массив для записи сообщений
        handler = new Handler(Looper.getMainLooper()); // handler для главного потока

        tvVisibleRangeBefore = frag.findViewById(R.id.tvVisibleRangeBefore);
        tvVisibleRangeAfter = frag.findViewById(R.id.tvVisibleRangeAfter);
        tvAdd = frag.findViewById(R.id.tvAdd);
        tvTotalVisibleElementsStart = frag.findViewById(R.id.tvTotalVisibleElementsStart);
        tvTotalVisibleElementsEnd = frag.findViewById(R.id.tvTotalVisibleElementsEnd);


        progressBar = frag.findViewById(R.id.progressBar);
        progressBarCheckNet = frag.findViewById(R.id.progressBarCheckNet);
        recyclerView = frag.findViewById(R.id.recyclerView);
        btTryAgain = frag.findViewById(R.id.btTryAgain);
        tvInfo = frag.findViewById(R.id.tvInfo);
        cardView = frag.findViewById(R.id.cardView);

        // передача данных счета для пользователя
        ((MainActivity)getActivity()).setInterfaceNums(new MainActivity.InterfaceNums() {
            @Override
            public void methodInterfaceNums(int first, int last) {
                tvVisibleRangeBefore.setText("" + first);
                tvVisibleRangeAfter.setText("" + last);

                tvTotalVisibleElementsStart.setText("0");
                if(last > count)
                {
                    count = last;
                    tvTotalVisibleElementsEnd.setText("" + count);
                }
            }
        });

        getData(0); // сетевой запрос на полуение данных

        // установка менеджера макетов для recyclerView вертикал
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        // инициализация adapterList и передача в него параметров
        adapterList = new AdapterList(arrayList, getContext(), recyclerView);
        recyclerView.setAdapter(adapterList); // установка adapterList

        // слушатель adapterList для добавления и удлаения Progress bar
        adapterList.setOnLoadMoreListener(new AdapterList.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("3333","-зашел в setOnLoadMoreListener-");
                // сначала должен появиться Progress bar show
                //hashMap.put(hashMap.size()+1, null);
                arrayList.add(null); // с помощью null мы добавляем progressBar

                // затем отобразить список с учетом минус 1, чтобы не учитывать null(progressBar) помещенный ранее
                adapterList.notifyItemInserted(arrayList.size()-1);

                // задержка чтобы в адаптере записутить метод и изменить flagLoading на false
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                            arrayList.remove(arrayList.size() - 1); // удаляем progressBar

                            adapterList.notifyDataSetChanged(); //???????????????????????
                        // и запустить новый запрос с offset
                        //getData(hashMap.size() + 1);/// + 20;
                        z = z + 20; // переменная для увеличения значения offset
                        getData(z);/// + 20;
                        Log.e("3333","-зашел в setOnLoadMoreListener добавил + 20-");
                    }
                },100);
            }
        });

        return frag;
    }

    public void getData(int num) // сетевой запрос на полуение данных
    {
        // проверяем наличие интернета при каждом сетевом запросе
        if(checkNetworkMethod())
        {

        numForTry = num;
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("offset", num);
        Log.e("333","-url + params-" + url + requestParams);
        asyncHttpClient.get(url, requestParams, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
               // Log.e("333", "onFailure" + responseString);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cardView.setVisibility(View.VISIBLE); // показать view с сообщением пользователю
                        tvInfo.setText("ошибка работы сервера (onFailure)");
                    }
                });
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //Log.e("333","-onSuccess-responseString-" + responseString);

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(responseString);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    // jsonArray.length() на случай если сообщения закончились, чтобы не долбить сервер
                    if(jsonArray.length() != 0)
                    {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String messageStr = jsonArray.getString(i);
                            arrayList.add(new ObjectMessage(messageStr));
                            Log.e("333fr", "arrayList " + arrayList.get(i).message.toString());
                        }
                         Log.e("333fr", "arrayList.size" + arrayList.size());
                        adapterList.notifyDataSetChanged(); // обновили все данные в recyclerview
                        adapterList.setLoading(); // присваиваем flagLoading = false в методе setLoading()

                        // 3
                        tvAdd.setText("+ " + arrayList.size());
                    }

                } catch (JSONException e) {
                    Log.e("3333","-try catch-" + e);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cardView.setVisibility(View.VISIBLE); // показать view с сообщением пользователю
                            tvInfo.setText("ошибка работы сервера Exception");

                            //tvInfo.animate().rotation(90).start();
                        }
                    });
                    btTryAgain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            buttonClick();
                        }
                    });
                }
            }
        });

        }
    }

    public boolean checkNetworkMethod() // метод проверки наличия интернета при каждом сетевом запросе
    {
        // проверка интернера при отправке запроса на сервер
        if (CheckNetwork.isInternetAvailable(getContext())) //возвращает true, если интернет доступен
        {
            cardView.setVisibility(View.GONE);  // скрыть view с сообщением пользователю
            tvInfo.setText("");
            return true;

        } else { // возвращает false, если интернет недоступен
            getActivity().runOnUiThread(new Runnable() { // в главном потоке
                @Override
                public void run() {
                    cardView.setVisibility(View.VISIBLE); // показать cardView с кн.повторной попытки
                    tvInfo.setText("интернет соединение отсутствует");
                    btTryAgain.setOnClickListener(new View.OnClickListener() { // кн.повторной попытки
                        @Override
                        public void onClick(View view) {
                            buttonClick();
                        }
                    });
                }
            });
            return false;
        }
    }


    public void buttonClick()
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBarCheckNet.setVisibility(View.VISIBLE); // запуск PG на 0,5сек
            }
        });

        Runnable runnable = new Runnable() { // главный поток
            @Override
            public void run() {
                //getData(numForTry); // сделать сетевой запрос с текущим num
                progressBarCheckNet.setVisibility(View.GONE); // скрыть PG
                tvInfo.setText("");
                cardView.setVisibility(View.GONE); // скрыть cardView с кн.повторной попытки

                Runnable runnable1 = new Runnable() {
                    @Override
                    public void run() {
                        getData(numForTry);
                    }
                };
                handler.postDelayed(runnable1, 3000);
            }
        };
        handler.postDelayed(runnable, 500); // задержка
    }

}