package com.example.user.scandemo.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.scandemo.Adapter.CheckDetailListAdapter;
import com.example.user.scandemo.Base.BaseFragment;
import com.example.user.scandemo.Bean.CheckDetailBean;
import com.example.user.scandemo.R;
import com.example.user.scandemo.barcodeservice.SerialPortService;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import static com.example.user.scandemo.activity.CheckDetailActivity.readDb;
import static com.example.user.scandemo.activity.CheckDetailActivity.updateDb;

/**
 * Created by qiaozhili on 2018/10/11 16:50.
 */

@SuppressLint("ValidFragment")
public class FragmentChecked extends BaseFragment implements PullToRefreshBase.OnRefreshListener2<ListView>{
    private CheckDetailListAdapter checkDetailListAdapter;
    private List<CheckDetailBean> checkDetailBeanList = new ArrayList<CheckDetailBean>();
    private PullToRefreshListView checkdetail_list;
    private String checkID;
    Context context;

    public FragmentChecked(List<CheckDetailBean> checkDetailBeanList, String checkID) {
        this.checkDetailBeanList = checkDetailBeanList;
        this.checkID = checkID;
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_checkdetail_list;
    }

    @Override
    protected void init() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SerialPortService.BARCODEPORT_RECEIVEDDATA_ACTION);
        getActivity().registerReceiver(receiver, intentFilter);
        checkDetailBeanList.clear();
        initData();
        checkdetail_list = (PullToRefreshListView) getRootView().findViewById(R.id.checkdetail_list);
        checkdetail_list.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        checkdetail_list.setOnRefreshListener(this);
        checkDetailListAdapter = new CheckDetailListAdapter(2);
        checkdetail_list.setAdapter(checkDetailListAdapter);
        checkDetailListAdapter.notifyDataSetChanged();
    }

    private void initData() {
        checkDetailBeanList = DataSupport.where("checkID=? and status=?", checkID, "是").find(CheckDetailBean.class);
//        checkDetailListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void viewClick(View view) {

    }

    @Override
    protected void lazyLoad() {

    }
    @Override
    public void onResume() {
        initData();
        super.onResume();
    }

    @Override
    public void onPullDownToRefresh(final PullToRefreshBase<ListView> refreshView) {
        refreshView.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshView.onRefreshComplete();

            }
        }, 1000);
        onResume();
//        if (readDb()) {
//            checkDetailBeanList.clear();
//            checkDetailBeanList = DataSupport.where("checkID=? and status=?", checkID, "是").find(CheckDetailBean.class);
//            checkDetailListAdapter.notifyDataSetChanged();
//            showToast("刷新成功");
//        } else {
//            showToast("刷新失败");
//        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    /**
     * 广播接收器
     * */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if(action!=null){
                if(action.equals(SerialPortService.BARCODEPORT_RECEIVEDDATA_ACTION)){
                    String data=intent.getStringExtra(SerialPortService.BARCODEPORT_RECEIVEDDATA_EXTRA_DATA);
                    if(data!=null){
//                        if (data.length() >= 16) {
//                            updateDb(data.substring(0, 16), checkID);
//                        } else {
//                            showToast("请检查二维码！");
//                        }
                        initData();
                        checkDetailListAdapter.notifyDataSetChanged();
//                        smResult.append(data);
                        Log.v(TAG, "receiver data:"+data);
                    }
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    private class CheckDetailListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
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
        public View getView(int position, View convertView, ViewGroup parent) { CheckDetailListAdapter.CheckListHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
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


        public final class CheckListHolder extends RecyclerView.ViewHolder {
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
}
