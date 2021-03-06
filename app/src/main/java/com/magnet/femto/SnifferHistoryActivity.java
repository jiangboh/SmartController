package com.magnet.femto;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.magnet.FemtoController.BaseActivity;
import com.magnet.FemtoController.ProxyApplication;
import com.magnet.R;
import com.magnet.adapters.AdapeterSnifferList;
import com.magnet.custom_view.RecordOnClick;

public class SnifferHistoryActivity extends BaseActivity {
    private ListView TargetListView;
    private AdapeterSnifferList adapeterSnifferList;
    private ImageView btnRight;
    @Override
    protected void initView() {
        setContentView(R.layout.activity_history);
         //back button
        findViewById(R.id.iv_activity_back).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                onBackPressed();
                super.recordOnClick(v, "Back Event " + TAG);
            }
        });
        ((TextView) findViewById(R.id.tv_activity_title)).setText("Sniffer History");
        //target list
        TargetListView = (ListView) findViewById(R.id.target_list);
        adapeterSnifferList = new AdapeterSnifferList(mContext);
        TargetListView.setAdapter(adapeterSnifferList);

        setStatusBarsColor(R.color.colorPrimaryDark);
        initStatusView();
        btnRight = (ImageView) findViewById(R.id.iv_activity_right);
        btnRight.setVisibility(View.VISIBLE);
        btnRight.setImageResource(R.drawable.btn_clear_selector);
        btnRight.setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                adapeterSnifferList.deleteAll();
                super.recordOnClick(v, "Clear sniffer history List Event");
            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
    }

}
