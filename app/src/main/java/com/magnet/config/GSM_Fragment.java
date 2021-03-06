package com.magnet.config;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.magnet.FemtoController.ProxyApplication;
import com.magnet.FemtoController.RevealAnimationActivity;
import com.magnet.R;
import com.magnet.custom_view.CustomToast;
import com.magnet.custom_view.RecordOnClick;
import com.magnet.femto.IPEdit;
import com.magnet.fragments.RevealAnimationBaseFragment;
import com.magnet.parse_generate_xml.Status;
import com.magnet.parse_generate_xml.ex_config.ConfigGsm;
import com.magnet.parse_generate_xml.ex_config.ConfigReq;
import com.magnet.parse_generate_xml.ex_config.ConfigRsp;
import com.magnet.utils.SharePreferenceUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.magnet.femto.BcastCommonApi.sendUdpMsg;

/**
 * A simple {@link Fragment} subclass.
 */
public class GSM_Fragment extends RevealAnimationBaseFragment {
    private String strControlIP;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_gsm);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        strControlIP = SharePreferenceUtils.getInstance(context).getString("status_notif_controller_ip" +
                ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() +
                ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        LoadData();
    }
    @Override
    public void onResume() {
        super.onResume();
        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.VISIBLE);
        ((RevealAnimationActivity)context).getSettingBtn().setImageResource(R.drawable.btn_config_selector);
        ((RevealAnimationActivity)context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                if (CheckObserverMode(strControlIP)){
                    SetGSM();
                }
                InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0); //强制隐藏键盘
                super.recordOnClick(v, "Set Config GSM Event");
            }
        });
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        SaveData();
    }

    private void SetGSM() {
        ConfigGsm configGsm = new ConfigGsm();
        configGsm.setMonitorIp(((IPEdit)contentView.findViewById(R.id.monitor_ip)).getIPAddtrss());
        ConfigReq configReq = new ConfigReq();
        configReq.setGsm(configGsm);
        sendUdpMsg(context, ConfigReq.toXml(configReq));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ExStatusNotifi(ConfigRsp configRsp){
        if (configRsp.getStatus().equals("SUCCESS")) {
            CustomToast.showToast(context, "Set Config Success");
        } else {
            CustomToast.showToast(context, "Set Config Failure");
        }
    }

    private void SaveData() {
        SharedPreferences preferences = context.getSharedPreferences("gsm_config",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("monitor_ip", ((IPEdit)contentView.findViewById(R.id.monitor_ip)).getIPAddtrss());
        editor.commit();
    }

    private void LoadData() {
        SharedPreferences sp = context.getSharedPreferences("gsm_config", MODE_PRIVATE);
        ((IPEdit)contentView.findViewById(R.id.monitor_ip)).initIpAddress(sp.getString("monitor_ip", ""));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void StatusNotif(Status s) {
        strControlIP = s.getControllerClient();
    }
}
