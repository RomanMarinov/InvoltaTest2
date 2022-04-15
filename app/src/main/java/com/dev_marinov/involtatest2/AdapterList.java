package com.dev_marinov.involtatest2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<ObjectMessage> arrayList; // массив для хранения данных о сообщениях
    Context context;
    RecyclerView recyclerView;

    OnLoadMoreListener onLoadMoreListener;

    private final int SHOW_PROGRESS_BAR = 1; // отображаем прогресс бар-------
    private final int SHOW_RV_LIST = 0; // отображаем rv_list-------
    private int totalCountItem = 0; // сколько всего элементов-----------
    private int lastVisibleItem = 0; // сколько отображено(загружено)---------
    private int firstVisibleItem = 0; // сколько отображено(загружено)----------
    private boolean flagLoading;

    public AdapterList(ArrayList<ObjectMessage> arrayList, Context context, RecyclerView recyclerView)
    {
        this.arrayList = arrayList;
        this.context = context;
        //this.recyclerView = recyclerView;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            // totalCountItem переменная всегода равно размеру hashmap в который добавляется + 20
            totalCountItem = linearLayoutManager.getItemCount();
            // кол-во элементов которые сейчас видны на экране
            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            // переменная только для вывода счета, больше нигде не используется
            firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

            // передаем данные чтобы отображать пользователю
            MainActivity.interfaceNums.methodInterfaceNums(firstVisibleItem, lastVisibleItem);

            // метод должен срабатывать при достижении прокрутки
            // totalCountItem - общее, lastVisibleItem - последний видимый
            if(flagLoading == false && (totalCountItem - 5) == lastVisibleItem)
            {
                // тут я должен послать сообщение во fragmentList чтобы запустить progressBar
                // и запустить новый запрос даных на сервер с offset
                if(onLoadMoreListener != null)
                {
                    Log.e("333","-запустился interface в адаптере onLoadMore-");
                    onLoadMoreListener.onLoadMore();
                }
                flagLoading = true;
            }
            }
        });
    }

    // Если элемент является первым одним типом является TextView, а вторым типом является ImageView + Button + TextView,
    // тогда нам нужно использовать getItemViewType () и getTypeViewCount () для реализации такого сложного списка.
    @Override
    public int getItemViewType(int position) {
        if(arrayList.get(position) == null) // если был передан null из fragmentList
        {
           // Log.e("333","-hashMap.get(position)-" + arrayList.get(position));
            //Log.e("333","-SHOW_PROGRESS_BAR getItemViewType-position-" + position);
            return SHOW_PROGRESS_BAR; // 1
        }
        else
        {
           // Log.e("333","SHOW_RV_LIST -getItemViewType-position-" + position);
            return SHOW_RV_LIST; // 0
        }
        //return super.getItemViewType(position);
    }

    // viewType возвращает переопределенный метод адаптера getItemViewType().
    // В этом методе вы должны организовать логику определения типа айтема по позиции в списке (входной аргумент метода).
    // Если метод не переопределен в адаптере, то параметр возвращает 0 для всех позиций.
    // Параметр может быть любым целым числом типа int, конкретное значение вы указываете сами в логике метода.
    // Число это служит только для идентификации типа айтема.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SHOW_RV_LIST) // 0
        {
            View view = LayoutInflater.from(context).inflate(R.layout.rv_list, parent, false);
            return new HolderMessage(view);
        }
           else if(viewType == SHOW_PROGRESS_BAR)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.progress_bar, parent, false);
            return new HolderProgressBar(view);
        }
           return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HolderMessage)
        {
            ObjectMessage objectMessage = arrayList.get(position);
            HolderMessage holderMessage = (HolderMessage) holder;
            holderMessage.tvMessage.setText(objectMessage.message);
//            ((Holder) holder).tvMessage.setText(hashMap.get(position).message);
        }
        else if(holder instanceof HolderProgressBar)
        {
            HolderProgressBar holderProgressBar = (HolderProgressBar) holder;
            holderProgressBar.progressBar.setIndeterminate(true);
            //showProgressBar((HolderProgressBar) holder, position);
            // тут отображжется прогресс бар
        }
    }

    // getItemCount() возвращает общее количество элементов списка. Значения списка передаются с помощью конструктора
    @Override
    public int getItemCount() {
        return arrayList == null ? 0 : arrayList.size();
    }

   private class HolderMessage extends RecyclerView.ViewHolder{
        TextView tvMessage;
        HolderMessage(View view)
        {
            super(view);
            tvMessage = view.findViewById(R.id.tvMessage);
        }
    }

   private class HolderProgressBar extends RecyclerView.ViewHolder{
        ProgressBar progressBar;

        HolderProgressBar(View view)
        {
            super(view);
            progressBar = view.findViewById(R.id.progressBar);
        }
    }

    // интерфейс для запуска метода во fragmentList который запустит progressBar
    // и сделает новый запрос offset на сервер
    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    public void setLoading()
    {
        Log.e("333","-flagLoading записалось false-");
        flagLoading = false;
    }
}
