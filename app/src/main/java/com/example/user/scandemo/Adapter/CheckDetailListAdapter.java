package com.example.user.scandemo.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.user.scandemo.Bean.CheckDetailBean;
import com.example.user.scandemo.R;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by qiaozhili on 2018/10/11 16:48.
 */

public class CheckDetailListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<CheckDetailBean> checkDetailBeanList;
    private int type;   //0全部  1未盘点  2已盘点

    public CheckDetailListAdapter (int type) {
        this.type = type;
    }

    @Override
    public int getCount() {
        return checkDetailBeanList.size();
    }

    @Override
    public CheckDetailBean getItem(int position) {
        return checkDetailBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CheckDetailListAdapter.CheckListHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_checkdetail, null);
            holder = new CheckDetailListAdapter.CheckListHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (CheckDetailListAdapter.CheckListHolder) convertView.getTag();
        }
        final CheckDetailBean data = getItem(position);
        CheckDetailBean checkDetailBean = DataSupport.find(CheckDetailBean.class, position);
        String status = data.getStatus();
        if (type == 0) {
            holder.mName.setText(data.getDeviceName());
            holder.mStatus.setText("盘点状态：" + status);
            holder.mNumber.setText(data.getNumber() + "/");
            holder.mPerson.setText(data.getPerson() + "/");
            holder.mCFDD.setText(data.getCfdd());
        } else if (type == 1 && status.equals("否")) {
            holder.mName.setText(data.getDeviceName());
            holder.mStatus.setText("盘点状态：" + status);
            holder.mNumber.setText(data.getNumber() + "/");
            holder.mPerson.setText(data.getPerson() + "/");
            holder.mCFDD.setText(data.getCfdd());
        } else if (type == 2 && status.equals("是")) {
            holder.mName.setText(data.getDeviceName());
            holder.mStatus.setText("盘点状态：" + status);
            holder.mNumber.setText(data.getNumber() + "/");
            holder.mPerson.setText(data.getPerson() + "/");
            holder.mCFDD.setText(data.getCfdd());
        }
        return convertView;
    }


    class CheckListHolder extends RecyclerView.ViewHolder {
        TextView mName, mStatus, mNumber, mPerson, mCFDD;

        public CheckListHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.device_name);
            mStatus = (TextView) itemView.findViewById(R.id.device_status);
            mNumber = (TextView) itemView.findViewById(R.id.device_number);
            mPerson = (TextView) itemView.findViewById(R.id.device_person);
            mCFDD = (TextView) itemView.findViewById(R.id.device_cfdd);
        }
    }
}
