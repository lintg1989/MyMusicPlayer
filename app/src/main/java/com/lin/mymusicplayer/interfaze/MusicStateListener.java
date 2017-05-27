package com.lin.mymusicplayer.interfaze;

/**
 * Created by Lin on 2017/5/26.
 */

public interface MusicStateListener {

    /**
     * 更新歌曲状态信息
     */
    void updateTrackInfo();

    void updateTime();

    void changeTheme();

    void reloadAdapter();

}
