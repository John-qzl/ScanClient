package com.example.user.scandemo.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.scandemo.Adapter.CheckListAdapter;
import com.example.user.scandemo.Base.App;
import com.example.user.scandemo.Base.BaseActivity;
import com.example.user.scandemo.Bean.CheckBean;
import com.example.user.scandemo.Bean.CheckDetailBean;
import com.example.user.scandemo.R;
import com.example.user.scandemo.db.SQLdb;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by qiaozhili on 2018/9/17.
 */

public class MainActivity extends BaseActivity implements CheckListAdapter.OnCheckListListener, PullToRefreshBase.OnRefreshListener2<ListView>{
    private PullToRefreshListView checkDetails;
    private CheckListAdapter checkListAdapter;
    private List<CheckBean> checkBeanList = checkBeanList = new ArrayList<>();;
    private ImageView mRefresh;
    private TextView smResult;
    public static List<CheckBean> headMap = new ArrayList<CheckBean>();
    public static List<String> checkIDList = new ArrayList<>();
    private SQLdb sqLdb;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        sqLdb = SQLdb.getInstance();
        initData();
        checkDetails = (PullToRefreshListView) findViewById(R.id.check_list);
        checkDetails.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        checkDetails.setOnRefreshListener(this);
        checkListAdapter = new CheckListAdapter(getContext(), checkBeanList);
        checkListAdapter.setOnCheckListListener(this);
        checkDetails.setAdapter(checkListAdapter);
        checkListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void viewClick(View view) {
        switch (view.getId()) {
//            case R.id.iv_refresh:
//                if (initData()) {
//                    checkListAdapter.notifyDataSetChanged();
//                    Toast.makeText(this, "刷新成功", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "刷新失败", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            default:
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /**
     * @Description: 初始化读取读取W_ZCPDJHB表数据(主表)
     * @author qiaozhili
     * @date 2018/10/18 8:31
     * @param
     * @return
     */
    private boolean initData() {
        checkIDList.clear();
        checkBeanList.clear();
        DataSupport.deleteAll(CheckDetailBean.class);
        DataSupport.deleteAll(CheckBean.class);
        boolean result = false;
        sqLdb = new SQLdb();
        SQLiteDatabase db = sqLdb.openDatabase(App.getApp().getApplicationContext());

        String sqlqurey = "select * from W_ZCPDJHB where ID=?";
//        String sqlqurey1 = "select * from W_PDSBB where REFID=?";
        String sqlqurey1 = "select REFID from W_PDSBB ";
        if (db != null) {
            Cursor cursor = db.query("W_ZCPDJHB", null, null, null, null, null, null, null);
            int id = cursor.getColumnIndex("ID");
            for (cursor.moveToFirst();!(cursor.isAfterLast());cursor.moveToNext()) {
                checkIDList.add(cursor.getString(id));
            }
            cursor.close();
        } else {
            showToast("请检查数据库!");
        }
        if (checkIDList.size() > 0 && checkIDList != null) {
            for (int i = 0; i < checkIDList.size(); i++) {
                Cursor cursor = db.rawQuery(sqlqurey, new String[]{checkIDList.get(i)});
                if (cursor.moveToNext()) {
                    CheckBean checkBean = new CheckBean();
                    checkBean.setCheckID(checkIDList.get(i));
                    checkBean.setDepartment(cursor.getString(cursor.getColumnIndex("F_PDBM")));
                    checkBean.setPdzs(Integer.parseInt(cursor.getString(cursor.getColumnIndex("F_PDSBZS"))));
                    checkBean.setYpd(Integer.parseInt(cursor.getString(cursor.getColumnIndex("F_YPD"))));
                    checkBean.setName(cursor.getString(cursor.getColumnIndex("F_PDFZR")));
                    checkBean.setTitle(cursor.getString(cursor.getColumnIndex("F_JHMC")));
                    checkBean.save();
                    checkBeanList.add(checkBean);
                }
                cursor.close();
            }
            result = true;
        } else {
            result = false;
        }

        return result;
    }

    @Override
    public void itemClick(String checkID) {
        if (checkListAdapter != null) {
            Intent intent = new Intent(this, CheckDetailActivity.class);
            intent.putExtra("checkID", checkID);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        checkBeanList.clear();
        finish();
//        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        initData();
        checkListAdapter.notifyDataSetChanged();
        super.onResume();
    }


//    /**
//     * 广播接收器
//     * */
//    private BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // TODO Auto-generated method stub
//            String action = intent.getAction();
//            if(action!=null){
//                if(action.equals(SerialPortService.BARCODEPORT_RECEIVEDDATA_ACTION)){
//                    String data=intent.getStringExtra(SerialPortService.BARCODEPORT_RECEIVEDDATA_EXTRA_DATA);
//                    if(data!=null){
//                        smResult.append(data);
//                        Log.v(TAG, "receiver data:"+data);
//                    }
//                }
//            }
//        }
//    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {  //表示按返回键 时的操作
                exitBy2Click();
                DataSupport.deleteAll(CheckBean.class);
                DataSupport.deleteAll(CheckDetailBean.class);
                return false;    //已处理
            }
        }
        return false;
    }

    private static Boolean isExit = false;
    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            showToast("再按一次退出程序");
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    public void onPullDownToRefresh(final PullToRefreshBase<ListView> refreshView) {
        refreshView.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshView.onRefreshComplete();

            }
        }, 1000);
        if (initData()) {
            checkListAdapter.notifyDataSetChanged();
            showToast("刷新成功");
        } else {
            showToast("刷新失败");
        }
    }

    @Override
    public void onPullUpToRefresh(final PullToRefreshBase<ListView> refreshView) {
        refreshView.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshView.onRefreshComplete();
            }
        }, 500);
        if (initData()) {
            checkListAdapter.notifyDataSetChanged();
        } else {
            showToast("没有更多数据");
        }
    }
}
