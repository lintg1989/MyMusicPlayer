package com.lin.mymusicplayer.activity;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.media.session.PlaybackState;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.lin.mymusicplayer.fragment.QuickControlsFragment;
import com.lin.mymusicplayer.interfaze.MusicStateListener;
import com.lin.mymusicplayer.service.MusicPlayer;

import java.util.ArrayList;

/**
 * activity 基类
 * Created by Lin on 2017/5/11.
 */

public class BasicActivity extends AppCompatActivity implements ServiceConnection {

    private MusicPlayer.ServiceToken mToken;
    private PlaybackState mPlaybackStatus;//receiver 接受播放状态变化等
    private QuickControlsFragment fragment;//底部播放控制栏
    private String TAG = "BaseActivity";
    private ArrayList<MusicStateListener> mMusicListener = new ArrayList<>();

    /**
     * 更新播放列表
     */
    public void updateQueue() {
//        for ()
    }

    /**
     * 更新歌曲状态信息
     */
    public void updateTrackInfo(){

    }


    public void removeMusicStateListener(final MusicStateListener status){
        if (status != null) {
            mMusicListener.remove(status);
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }
}
