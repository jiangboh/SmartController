package com.magnet.FemtoController;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.magnet.R;
import com.magnet.adapters.AdapterFemtoList;
import com.magnet.adapters.ErrorMsgListAdapter;
import com.magnet.custom_view.CustomProgressDialog;
import com.magnet.custom_view.CustomToast;
import com.magnet.custom_view.DeleteOrIgnoreErrorDialog;
import com.magnet.custom_view.OneBtnHintDialog;
import com.magnet.custom_view.RecordOnClick;
import com.magnet.custom_view.RecordOnItemClick;
import com.magnet.custom_view.RecordOnItemLongClick;
import com.magnet.data_ben.FemtoDataStruct;
import com.magnet.database.FemtoList;
import com.magnet.database.FemtoListDao;
import com.magnet.dialog.DialogConfig;
import com.magnet.listview.UserDefineListView;
import com.magnet.parse_generate_xml.ErrorNotif;
import com.magnet.parse_generate_xml.Status;
import com.magnet.socket_service.EventBusMsgConstant;
import com.magnet.socket_service.EventBusMsgSendTCPMsg;
import com.magnet.utils.Logs;
import com.magnet.utils.SharePreferenceUtils;
import com.magnet.utils.Utils;
import com.magnet.wifi.WifiAP;
import com.magnet.wifi.WifiAdmin;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.magnet.femto.BcastCommonApi.saveBcastInfo;
import static com.magnet.femto.BcastCommonApi.updateBcastInfoEndTime;


public class ErrorMsgActivity extends BaseActivity{
    private ExpandableListView listView;
    private ErrorMsgListAdapter adapter;
    private DeleteOrIgnoreErrorDialog dialog;
    @Override
    protected void initView() {
        setContentView(R.layout.activity_error_msg);
        findViewById(R.id.iv_activity_back).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                onBackPressed();
                super.recordOnClick(v, "Back Event " + TAG);
            }
        });
        ((TextView) findViewById(R.id.tv_activity_title)).setText(R.string.error_msg);
        listView = (ExpandableListView) findViewById(R.id.error_msg_list);
        adapter = new ErrorMsgListAdapter();
        listView.setAdapter(adapter);
        listView.setGroupIndicator(null);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = view.getTag();
                dialog = new DeleteOrIgnoreErrorDialog(mContext,R.style.dialog_style);
                dialog.setOnClickListener(new DeleteOrIgnoreErrorDialog.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ErrorNotif en = (ErrorNotif) view.getTag();
                        if(view.getId() == R.id.delete_or_ignore_error_delete){
                            if(dialog.getType() == DeleteOrIgnoreErrorDialog.GROUP){
                                Utils.deleteErrorsByIp(en.getIpAddress());
                            }else if(dialog.getType() == DeleteOrIgnoreErrorDialog.ITEM){
                                Utils.deleteError(en);
                            }
                        }else if(view.getId() == R.id.delete_or_ignore_error_ignore){
                            if(dialog.getType() == DeleteOrIgnoreErrorDialog.GROUP){
                                ArrayList<ErrorNotif> errors = Utils.errors.get(en.getIpAddress());
                                if(Utils.isAllIgnores(errors)){
                                    for(ErrorNotif temEn : errors){
                                        Utils.deleteIgnoreError(temEn);
                                    }
                                }else{
                                    for(ErrorNotif temEn : errors){
                                        Utils.addIgnoreError(temEn);
                                    }
                                }

                            }else if(dialog.getType() == DeleteOrIgnoreErrorDialog.ITEM){
                                if(Utils.isIgnoreError(en)){
                                    Utils.deleteIgnoreError(en);
                                }else{
                                    Utils.addIgnoreError(en);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
                if(obj instanceof ErrorMsgListAdapter.ParentViewHolder){
                    ErrorNotif en = (ErrorNotif)((ErrorMsgListAdapter.ParentViewHolder)obj).ipAddress.getTag();
                    dialog.setErrorNotif(en);
                    dialog.setType(DeleteOrIgnoreErrorDialog.GROUP);
                    dialog.show();
                    ArrayList<ErrorNotif> errors = Utils.errors.get(en.getIpAddress());
                    if(Utils.isAllIgnores(errors)){
                        dialog.setIgnoreText("Attention All");
                    }else{
                        dialog.setIgnoreText("Ignore All");
                    }
                }else if(obj instanceof ErrorMsgListAdapter.ChildViewHolder){
                    ErrorNotif en = (ErrorNotif)((ErrorMsgListAdapter.ChildViewHolder)obj).time.getTag();
                    dialog.setErrorNotif(en);
                    dialog.setType(DeleteOrIgnoreErrorDialog.ITEM);
                    dialog.show();
                    if(Utils.isIgnoreError(en)){
                        dialog.setIgnoreText("Attention");
                    }else{
                        dialog.setIgnoreText("Ignore");
                    }
                }
                return true;
            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        registeEventBus();
    }

    @Override
    public void errorNotif(ErrorNotif en) {
        super.errorNotif(en);
        adapter.notifyDataSetChanged();
    }
}
