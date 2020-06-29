package com.example.user.scandemo.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.user.scandemo.Bean.CheckBean;
import com.example.user.scandemo.R;
import com.example.user.scandemo.Base.BaseRecyclerAdapter;
import com.example.user.scandemo.application.ScanApplicaton;
import com.example.user.scandemo.event.OnRecyclerItemClickListener;

import java.util.List;

/**
 * Created by qiaozhili on 2018/9/26 22:56.
 */

public class CheckListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<CheckBean> checkBeanList;
    private OnCheckListListener onCheckListListener;

    public CheckListAdapter (Context context, List<CheckBean> checkBeanList) {
        inflater = LayoutInflater.from(context);
        this.checkBeanList = checkBeanList;
    }

    public void setOnCheckListListener(OnCheckListListener onCheckListListener) {
        this.onCheckListListener = onCheckListListener;
    }

    @Override
    public int getCount() {
        return checkBeanList.size();
    }

    @Override
    public CheckBean getItem(int position) {
        return checkBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CheckListHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_main_check, null);
            holder = new CheckListHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (CheckListHolder) convertView.getTag();
        }
        final CheckBean data = getItem(position);
        holder.tvCheckTitle.setText(data.getTitle());
        holder.mPerson.setText("负责人：" +data.getName());
        holder.mYPD.setText(String.valueOf(data.getYpd()));
        holder.mPDZS.setText("/" +String.valueOf(data.getPdzs()));
        holder.mDepartment.setText("部门：" + data.getDepartment());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanApplicaton.getApplication().setChechID(data.getCheckID());
                onCheckListListener.itemClick(data.getCheckID());
            }
        });
        return convertView;
    }


    class CheckListHolder extends RecyclerView.ViewHolder {
        TextView tvCheckTitle, mYPD, mPDZS, mDepartment, mPerson;
        LinearLayout llCheck;

        public CheckListHolder(View itemView) {
            super(itemView);
            tvCheckTitle = (TextView) itemView.findViewById(R.id.check_list_title);
            llCheck = (LinearLayout) itemView.findViewById(R.id.ll_check);
            mYPD = (TextView) itemView.findViewById(R.id.check_list_ypd);
            mPDZS = (TextView) itemView.findViewById(R.id.check_list_pdzs);
            mDepartment = (TextView) itemView.findViewById(R.id.check_list_department);
            mPerson = (TextView) itemView.findViewById(R.id.check_list_person);
        }
    }

    public interface OnCheckListListener {
        void itemClick(String checkID);
    }


}
