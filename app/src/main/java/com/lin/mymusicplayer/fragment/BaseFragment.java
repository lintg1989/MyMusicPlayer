package com.lin.mymusicplayer.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.lin.mymusicplayer.activity.BasicActivity;
import com.lin.mymusicplayer.interfaze.MusicStateListener;

/**
 * Created by Lin on 2017/5/26.
 */

public class BaseFragment extends Fragment implements MusicStateListener {

    public Context mContext;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BasicActivity) getActivity()).removeMusicStateListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void updateTrackInfo() {

    }

    @Override
    public void updateTime() {

    }

    @Override
    public void changeTheme() {

    }

    @Override
    public void reloadAdapter() {

    }
}
