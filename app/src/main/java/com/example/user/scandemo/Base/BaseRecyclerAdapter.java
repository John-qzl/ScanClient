package com.example.user.scandemo.Base;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiaozhili on 2018/9/17.
 */

public abstract class BaseRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private List<T> dataList;

    public BaseRecyclerAdapter() {
        dataList = new ArrayList<>();
    }

    public void setDataList(List<T> dataList) {
        if (dataList == null || dataList.size() <= 0) {
            this.dataList.clear();
        } else {
            if (this.dataList != null || this.dataList.size() > 0) {
                this.dataList.clear();
            }
            this.dataList.addAll(dataList);
        }
        notifyDataSetChanged();
    }

    public List<T> getDataList() {
        return dataList;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getLayoutId(), parent, false);

        return getViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        onBindViewHolder(holder, dataList.get(position), position);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public abstract int getLayoutId();

    public abstract VH getViewHolder(View view);

    public abstract void onBindViewHolder(VH holder, T data, int position);

}
