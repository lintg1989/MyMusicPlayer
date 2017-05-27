package com.lin.mymusicplayer.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.session.MediaSession;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lin.mymusicplayer.MediaAidlInterface;
import com.lin.mymusicplayer.info.MusicInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

import static com.lin.mymusicplayer.service.MusicPlayer.duration;
import static com.lin.mymusicplayer.service.MusicPlayer.getAlbumName;
import static com.lin.mymusicplayer.service.MusicPlayer.getAlbumPath;
import static com.lin.mymusicplayer.service.MusicPlayer.getArtistName;
import static com.lin.mymusicplayer.service.MusicPlayer.getTrackName;
import static com.lin.mymusicplayer.service.MusicPlayer.isTrackLocal;
import static com.lin.mymusicplayer.service.MusicPlayer.position;

/**
 * Created by Lin on 2017/5/15.
 */

public class MediaService extends Service {

    public static final String PLAYSTATE_CHANGED = "com.lin.mymusicplayer.playstatechanged";
    public static final String POSITION_CHANGED = "com.lin.mymusicplayer.positionchanged";
    public static final String META_CHANGED = "com.lin.mymusicplayer.metachanged";
    public static final String PLAYLIST_ITEM_MOVED = "com.lin.mymusicplayer.mmoved";
    public static final String QUEUE_CHANGED = "com.lin.mymusicplayer.queuechanged";
    public static final String PLAYLIST_CHANGED = "com.lin.mymusicplayer.playlistchanged";
    public static final String REPEATMODE_CHANGED = "com.lin.mymusicplayer.repeatmodechanged";
    public static final String SHUFFLEMODE_CHANGED = "com.lin.mymusicplayer.shufflemodechanged";
    public static final String TRACK_ERROR = "com.lin.mymusicplayer.trackerror";
    public static final String TIMBER_PACKAGE_NAME = "com.lin.mymusicplayer";
    public static final String MUSIC_PACKAGE_NAME = "com.android.music";
    public static final String SERVICECMD = "com.lin.mymusicplayer.musicservicecommand";
    public static final String TOGGLEPAUSE_ACTION = "com.lin.mymusicplayer.togglepause";
    public static final String PAUSE_ACTION = "com.lin.mymusicplayer.pause";
    public static final String STOP_ACTION = "com.lin.mymusicplayer.stop";
    public static final String PREVIOUS_ACTION = "com.lin.mymusicplayer.previous";
    public static final String PREVIOUS_FORCE_ACTION = "com.lin.mymusicplayer.previous.force";
    public static final String NEXT_ACTION = "com.lin.mymusicplayer.next";
    public static final String MUSIC_CHANGED = "com.wm.remusi.change_music";
    public static final String REPEAT_ACTION = "com.lin.mymusicplayer.repeat";
    public static final String SHUFFLE_ACTION = "com.lin.mymusicplayer.shuffle";
    public static final String FROM_MEDIA_BUTTON = "frommediabutton";
    public static final String REFRESH = "com.lin.mymusicplayer.refresh";
    public static final String LRC_UPDATED = "com.lin.mymusicplayer.updatelrc";
    public static final String UPDATE_LOCKSCREEN = "com.lin.mymusicplayer.updatelockscreen";
    public static final String CMDNAME = "command";
    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDSTOP = "stop";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPLAY = "play";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";
    public static final String CMDNOTIF = "buttonId";
    public static final String TRACK_PREPARED = "com.lin.mymusicplayer.prepared";
    public static final String TRY_GET_TRACKINFO = "com.lin.mymusicplayer.gettrackinfo";
    public static final String BUFFER_UP = "com.lin.mymusicplayer.bufferup";
    public static final String LOCK_SCREEN = "com.lin.mymusicplayer.lock";
    public static final String SEND_PROGRESS = "com.lin.mymusicplayer.progress";
    public static final String MUSIC_LODING = "com.lin.mymusicplayer.loading";
    private static final String SHUTDOWN = "com.lin.mymusicplayer.shutdown";
    public static final String SETQUEUE = "com.lin.mymusicplayer.setqueue";
    public static final int NEXT = 2;
    public static final int LAST = 3;
    public static final int SHUFFLE_NONE = 0;
    public static final int SHUFFLE_NORMAL = 1;
    public static final int SHUFFLE_AUTO = 2;
    public static final int REPEAT_NONE = 2;
    public static final int REPEAT_CURRENT = 1;
    public static final int REPEAT_ALL = 2;
    public static final int MAX_HISTORY_SIZE = 1000;
    private static final String TAG = "MusicPlaybackService";
    private static final boolean D = true;
    private static final int LRC_DOWNLOADED = -10;
    private static final int IDCOLIDX = 0;
    private static final int TRACK_ENDED = 1;
    private static final int TRACK_WENT_TO_NEXT = 2;
    private static final int RELEASE_WAKELOCK = 3;
    private static final int SERVER_DIED = 4;
    private static final int FOCUSCHANGE = 5;
    private static final int FADEDOWN = 6;
    private static final int FADEUP = 7;
    private static final int IDLE_DELAY = 5 * 60 * 1000;
    private static final long REWIND_INSTEAD_PREVIOUS_THRESHOLD = 3000;

    private static final String[] PROJECTION = new String[]{
            "audio._id AS _id", MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID
    };
    private static final String[] ALBUM_PROJECTION = new String[]{
            MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.LAST_YEAR
    };
    private static final Shuffler mShuffler = new Shuffler();
    private static final int NOTIFY_MODE_NONE = 0;
    private static final int NOTIFY_MODE_FOREGROUND = 1;
    private static final int NOTIFY_MODE_BACKGROUND = 2;
    private static final String[] PROJECTION_MATRIX = new String[]{
            "_id", MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID
    };
    private static LinkedList<Integer> mHistory = new LinkedList<>();
    private final IBinder mBinder = new ServiceStub(this);
    private MultiPlayer mPlayer;
    private String mFileToPlay;
    private PowerManager.WakeLock mWakeLock;
    private AlarmManager mAlarmManager;
    private PendingIntent mShutdownIntent;
    private boolean mShutdownScheduled;
    private NotificationManager mNotificationManager;
    private Cursor mCursor;
    private Cursor mAlbumCursor;
    private AudioManager mAudioManager;
    private SharedPreferences mPreferences;
    private boolean mServiceInUse = false;
    private boolean mIsSupposedToBePlaying = false;
    private long mLastPlayedTime;
    private int mNotifyMode = NOTIFY_MODE_NONE;
    private long mNotificationPostTime = 0;
    private boolean mQueueIsSaveable = true;
    private boolean mPausedByTransientLossOfFocus = false;

    private MediaSession mSession;

    private ComponentName mMediaButtonReceiverComponent;

    private int mCardId;

    private int mPlayPos = -1;

    private int mNextPlayPos = -1;

    private int mOpenFailedCounter = 0;

    private int mMediaMountedCount = 0;

    private int mShuffleMode = SHUFFLE_NONE;

    private int mRepeatMode = REPEAT_ALL;

    private int mServiceStartId = -1;

    private ArrayList<MusicTrack> mPlaylist = new ArrayList<MusicTrack>(100);

    private HashMap<Long, MusicInfo> mPlaylistInfo = new HashMap<>();

    private long[] mAutoShuffleList = null;

    private MusicPlayerHandler mPlayerHandler;

    private HandlerThread mHandlerThread;
    private BroadcastReceiver mUnmountReceiver = null;
    private MusicPlaybackState mPlaybackStateStore;
    private boolean mShowAlbumArtOnLockscreen;
    private SongPlayCount mSongPlayCount;
    private RecentStore mRecentStore;
    private int mNotificationId = 1000;

    private ContentObserver mMediaStoreObserver;
    private static Handler mUrlHandler;
    private static Handler mLrcHandler;
    private MediaPlayerProxy mProxy;
    public static final String LRC_PATH = "/mymusicplayer/lrc/";
    private long mLastSeekPos = 0;
    private RequestPlayUrl mRequestUrl;
    private RequestLrc mRequestLrc;
    private boolean mIsSending = false;
    private boolean mIsLocked;
    private Bitmap mNoBit;
    private Notification mNotification;

    private Thread mLrcThread = new Thread(new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
            mLrcHandler = new Handler();
            Looper.loop();
        }
    });

    private Thread mGetUrlThread = new Thread(new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
            mUrlHandler = new Handler();
            Looper.loop();
        }
    });

    private final AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            mPlayerHandler.o
        }
    };



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static final class ServiceStub extends MediaAidlInterface.Stub {

        @Override
        public void openFile(String path) throws RemoteException {

        }

        @Override
        public void open(Map infos, long[] list, int position) throws RemoteException {

        }

        @Override
        public void stop() throws RemoteException {

        }

        @Override
        public void pause() throws RemoteException {

        }

        @Override
        public void play() throws RemoteException {

        }

        @Override
        public void prev(boolean forcePrevious) throws RemoteException {

        }

        @Override
        public void next() throws RemoteException {

        }

        @Override
        public void enqueue(long[] list, Map infos, int action) throws RemoteException {

        }

        @Override
        public Map getPlayinfos() throws RemoteException {
            return null;
        }

        @Override
        public void setQueuePosition(int index) throws RemoteException {

        }

        @Override
        public void setShuffleMode(int shufflemode) throws RemoteException {

        }

        @Override
        public void setRepeatMode(int repeatmode) throws RemoteException {

        }


        @Override
        public void moveQueueItem(int from, int to) throws RemoteException {

        }

        @Override
        public void refresh() throws RemoteException {

        }

        @Override
        public void playlistChanged() throws RemoteException {

        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return false;
        }

        @Override
        public long[] getQueue() throws RemoteException {
            return new long[0];
        }

        @Override
        public long getQueueItemAtPosition(int position) throws RemoteException {
            return 0;
        }

        @Override
        public int getQueueSize() throws RemoteException {
            return 0;
        }

        @Override
        public int getQueuePosition() throws RemoteException {
            return 0;
        }

        @Override
        public int getQueueHistoryPosition(int position) throws RemoteException {
            return 0;
        }

        @Override
        public int getQueueHistorySize() throws RemoteException {
            return 0;
        }

        @Override
        public int[] getQueueHistoryList() throws RemoteException {
            return new int[0];
        }

        @Override
        public long duration() throws RemoteException {
            return 0;
        }

        @Override
        public long position() throws RemoteException {
            return 0;
        }

        @Override
        public int secondPosition() throws RemoteException {
            return 0;
        }

        @Override
        public long seek(long pos) throws RemoteException {
            return 0;
        }

        @Override
        public void seekRelative(long deltaInMs) throws RemoteException {

        }

        @Override
        public long getAudioId() throws RemoteException {
            return 0;
        }

        @Override
        public MusicTrack getCurrentTrack() throws RemoteException {
            return null;
        }

        @Override
        public MusicTrack getTrack(int index) throws RemoteException {
            return null;
        }

        @Override
        public long getNextAudioId() throws RemoteException {
            return 0;
        }

        @Override
        public long getPreviousAudioId() throws RemoteException {
            return 0;
        }

        @Override
        public long getArtistId() throws RemoteException {
            return 0;
        }

        @Override
        public long getAlbumId() throws RemoteException {
            return 0;
        }

        @Override
        public String getArtistName() throws RemoteException {
            return null;
        }

        @Override
        public String getTrackName() throws RemoteException {
            return null;
        }

        @Override
        public boolean isTrackLocal() throws RemoteException {
            return false;
        }

        @Override
        public String getAlbumName() throws RemoteException {
            return null;
        }

        @Override
        public String getAlbumPath() throws RemoteException {
            return null;
        }

        @Override
        public String[] getAlbumPathtAll() throws RemoteException {
            return new String[0];
        }

        @Override
        public String getPath() throws RemoteException {
            return null;
        }

        @Override
        public int getShuffleMode() throws RemoteException {
            return 0;
        }

        @Override
        public int removeTracks(int first, int last) throws RemoteException {
            return 0;
        }

        @Override
        public int removeTrack(long id) throws RemoteException {
            return 0;
        }

        @Override
        public boolean removeTrackAtPosition(long id, int position) throws RemoteException {
            return false;
        }

        @Override
        public int getRepeatMode() throws RemoteException {
            return 0;
        }

        @Override
        public int getMediaMountedCount() throws RemoteException {
            return 0;
        }

        @Override
        public int getAudioSessionId() throws RemoteException {
            return 0;
        }

        @Override
        public void setLockscreenALbumArt(boolean enabled) throws RemoteException {

        }

        @Override
        public void exit() throws RemoteException {

        }

        @Override
        public void timing(int time) throws RemoteException {

        }
    }

    private static final class MusicPlayerHandler extends Handler {
        private final WeakReference<MediaService> mService;
        private float mCurrentVolume = 1.0f;

        private MusicPlayerHandler(final MediaService service, final Looper looper) {
            super(looper);
            this.mService = new WeakReference<MediaService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            final MediaService service = mService.get();
            if (service == null){
                return;
            }

            synchronized (service) {
                switch (msg.what) {
                    case FADEDOWN:
                        mCurrentVolume -= .05f;
                        if (mCurrentVolume > .2f){
                            sendEmptyMessageDelayed(FADEDOWN, 10);
                        } else {
                            mCurrentVolume = .2f;
                        }
                        service.mPlayer.setVolume(mCurrentVolume);
                        break;
                    case FADEUP:
                        mCurrentVolume += .01f;
                        if (mCurrentVolume < 1.0f){
                            sendEmptyMessageDelayed(FADEUP, 10);
                        } else {
                            mCurrentVolume = 1.0f;
                        }
                        service.mPlayer.setVolume(mCurrentVolume);
                        break;
                    case SERVER_DIED:
                        if (service.isPlaying()){
                            final TrackErrorInfo info = (TrackErrorInfo) msg.obj;
                            service.sendErrorMessage(info.mTrackName);

                            service.removeTrack(info.mId);
                        }
                }
            }

            super.handleMessage(msg);
        }
    }

    public int removeTrack(final long id) {
        int numremoved = 0;
        synchronized (this) {
            for (int i = 0; i < mPlaylist.size(); i++) {
                if (mPlaylist.get(i).mId == id) {
                    numremoved += removeTracksInternal(i, i);
                    i--;
                }
            }

            mPlaylistInfo.remove(id);
        }


        if (numremoved > 0) {
            notifyChange(QUEUE_CHANGED);
        }
        return numremoved;
    }

    private void notifyChange(final String what) {
        if (D) {
            Log.e(TAG, "notifyChange: what = " + what);
        }
        if (SEND_PROGRESS.equals(what)){
            final Intent intent = new Intent(what);
            intent.putExtra("position", position());
            intent.putExtra("duration", duration());

            sendStickyBroadcast(intent);
            return;
        }

        //Update the lockscreen controls
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            updateMediaSession(what);
        }

        if (what.equals(POSITION_CHANGED)) {
            return;
        }

        final Intent intent = new Intent(what);
        intent.putExtra("id", getAudioId());
        intent.putExtra("artist", getArtistName());
        intent.putExtra("album", getAlbumName());
        intent.putExtra("track", getTrackName());
        intent.putExtra("playing", isPlaying());
        intent.putExtra("albumuri",getAlbumPath());
        intent.putExtra("islocal",isTrackLocal());

        sendStickyBroadcast(intent);
        final Intent musicIntent = new Intent(intent);
        musicIntent.setAction(what.replace(TIMBER_PACKAGE_NAME, MUSIC_PACKAGE_NAME));
        sendStickyBroadcast(musicIntent);


        if (what.equals(META_CHANGED)) {
            mRecentStore.a
        }
    }

    private int removeTracksInternal(int first, int last) {
        synchronized (this) {
            if (last < first) {
                return 0;
            } else if (first < 0) {
                first = 0;
            } else if (last >= mPlaylist.size()) {
                last = mPlaylist.size() - 1;
            }

            boolean gotonext = false;
            if (first <= mPlayPos && mPlayPos <= last) {
                mPlayPos = first;
                gotonext = true;
            } else if (mPlayPos > last) {
                mPlayPos -= last - first + 1;
            }
            final int numToRemove = last - first + 1;

            if (first == 0 && last == mPlaylist.size() - 1) {
                mPlayPos = -1;
                mNextPlayPos = -1;
                mPlaylist.clear();
                mHistory.clear();
            } else {
                for (int i = 0; i < numToRemove; i++) {
                    mPlaylistInfo.remove(mPlaylist.get(first).mId);
                    mPlaylist.remove(first);

                }

                ListIterator<Integer> positionIterator = mHistory.listIterator();
                while (positionIterator.hasNext()) {
                    int pos = positionIterator.next();
                    if (pos >= first && pos <= last) {
                        positionIterator.remove();
                    } else if (pos > last) {
                        positionIterator.set(pos - numToRemove);
                    }
                }
            }
            if (gotonext) {
                if (mPlaylist.size() == 0) {
                    stop(true);
                    mPlayPos = -1;
                    closeCursor();
                } else {
                    if (mShuffleMode != SHUFFLE_NONE) {
                        mPlayPos = getNextPosition(true);
                    } else if (mPlayPos >= mPlaylist.size()) {
                        mPlayPos = 0;
                    }
                    final boolean wasPlaying = isPlaying();
                    stop(false);
                    openCurrentAndNext();
                    if (wasPlaying) {
                        play();
                    }
                }
                notifyChange(META_CHANGED);
            }
            return last - first + 1;
        }
    }

    public boolean isPlaying() {
        return mIsSupposedToBePlaying;
    }

    private static final class TrackErrorInfo {
        public long mId;
        public String mTrackName;

        public TrackErrorInfo(long id, String trackName) {
            mId = id;
            mTrackName = trackName;
        }
    }


    private void sendErrorMessage(final String trackName) {
        final Intent i = new Intent(TRACK_ERROR);
        i.putExtra(TrackErrorExtra.TRACK_NAME, trackName);
        sendBroadcast(i);
    }

    public interface TrackErrorExtra {
        String TRACK_NAME = "trackname";
    }
}
