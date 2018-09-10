package com.magnet.status;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.magnet.R;
import com.magnet.fragments.RevealAnimationBaseFragment;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class Kpi_Fragment extends RevealAnimationBaseFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_kpi);
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

}
