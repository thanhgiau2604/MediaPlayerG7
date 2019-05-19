package group7.android.mediaplayerg7;

import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import group7.android.adapter.AddMusicAdapter;
import group7.android.adapter.PlaylistMusicAdapter;
import group7.android.model.Music;

import static group7.android.mediaplayerg7.MusicPlayer.PLAYER_PAUSE;
import static group7.android.mediaplayerg7.MusicPlayer.PLAYER_PLAY;

public class SonginplaylistActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener, MusicPlayer.OnCompletionListener{

    ListView lvBaiHat;
    public static ArrayList<Music> dsBaiHat;
    public static PlaylistMusicAdapter adapterBaiHat;
    public static String idplaylist;
    Button btnChinhSuaPl;
    Intent intent;

    public static boolean CHONTHEMBAIHATVAOPLAYLIST=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        idplaylist = intent.getStringExtra("idplaylist");
        setContentView(R.layout.activity_songinplaylist);
        AddControls();
        AddEvents();
        TiepTucBaiHat();
        ListplaylistActivity.DA_MO_PLAYLIST=false;
    }

    private  void TiepTucBaiHat()
    {
        if (MainActivity.musicPlayer.getState()==PLAYER_PLAY || MainActivity.musicPlayer.getState()==PLAYER_PAUSE)
        {
            MainActivity.tvArtist.setText(MainActivity.TEN_CA_SI);
            MainActivity.tvTitle.setText(MainActivity.TEN_BAI_HAT);
            MainActivity.tvTimeTotal.setText(MainActivity.TOTAL_TIME);
            MainActivity.isRunning = true;
            if (MainActivity.musicPlayer.getState()==PLAYER_PLAY)
                MainActivity.ivPlay.setImageResource(R.drawable.pause);
            else
                MainActivity.ivPlay.setImageResource(R.drawable.play);
        }
    }
    private void AddControls() {
        lvBaiHat = (ListView)findViewById(R.id.listsonginpl);
        dsBaiHat = new ArrayList<>();
        adapterBaiHat = new PlaylistMusicAdapter(SonginplaylistActivity.this,R.layout.itemsonginplaylist,dsBaiHat);
        lvBaiHat.setAdapter(adapterBaiHat);
        initViews();
        LayDanhSachBaiHatTrongPlaylist();
        adapterBaiHat.notifyDataSetChanged();

    }

    private void AddEvents() {
        initListeners();
        btnChinhSuaPl = (Button)findViewById(R.id.btnchinhsuapl);
        btnChinhSuaPl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CHONTHEMBAIHATVAOPLAYLIST=true;
                Intent intent = new Intent(SonginplaylistActivity.this,AddmusicActivity.class);
                intent.putExtra("idplaylist",idplaylist);
                startActivity(intent);
            }
        });

    }

    private void initListeners() {
        lvBaiHat.setOnItemClickListener(this);
        MainActivity.ivShuffle.setOnClickListener(this);
        MainActivity.ivPrevious.setOnClickListener(this);
        MainActivity.ivPlay.setOnClickListener(this);
        MainActivity.ivNext.setOnClickListener(this);
        MainActivity.ivRepeat.setOnClickListener(this);
        MainActivity.sbProcess.setOnSeekBarChangeListener(this);
        MainActivity.musicPlayer.setOnCompletionListener(this);
    }

    private void initViews() {
        //lvPlayList = (ListView) findViewById(R.id.lv_play_list);
        MainActivity.tvTitle = (TextView) findViewById(R.id.tv_title);
        MainActivity.tvArtist = (TextView) findViewById(R.id.tv_artist);
        MainActivity.tvTimeProcess = (TextView) findViewById(R.id.tv_time_process);
        MainActivity.sbProcess = (SeekBar) findViewById(R.id.sb_process);
        MainActivity.tvTimeTotal = (TextView) findViewById(R.id.tv_time_total);
        MainActivity.ivShuffle = (ImageView) findViewById(R.id.iv_shuffle);
        MainActivity.ivPrevious = (ImageView) findViewById(R.id.iv_previous);
        MainActivity.ivPlay = (ImageView) findViewById(R.id.iv_play);
        MainActivity.ivNext = (ImageView) findViewById(R.id.iv_next);
        MainActivity.ivRepeat = (ImageView) findViewById(R.id.iv_repeat);
    }

    public static void LayDanhSachBaiHatTrongPlaylist() {
        Cursor cursor = MainActivity.database.query("detailplaylist",null,"idplaylist=?",new String[]{idplaylist},null,null,null);
        dsBaiHat.clear();
        MainActivity.paths = new ArrayList<>();
        while (cursor.moveToNext())
        {
            Cursor cursor1 = MainActivity.database.query("music",null,"idsong=?",new String[]{cursor.getString(0)},null,null,null);
            while (cursor1.moveToNext())
            {
                Music music = new Music();
                music.setIdsong(cursor1.getString(0));
                music.setNamesong(cursor1.getString(1));
                music.setArtist(cursor1.getString(2));
                music.setAlbum(cursor1.getString(3));
                Boolean bool = cursor1.getInt(4)>0;
                music.setFavorite(bool);
                music.setPath(cursor1.getString(5));
                dsBaiHat.add(music);
                MainActivity.paths.add(music.getPath());
            }
            cursor1.close();
        }
        cursor.close();
        adapterBaiHat.notifyDataSetChanged();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MainActivity.UPDATE_TIME) {
                MainActivity.timeCurrent = MainActivity.musicPlayer.getTimeCurrent();
                MainActivity.tvTimeProcess.setText(getTimeFormat(MainActivity.timeCurrent));
                Double percentage = (double) 0;
                long currentSeconds = (int) (MainActivity.timeCurrent);
                long totalSeconds = (int) (MainActivity.musicPlayer.getTimeTotal());
                percentage =(((double)currentSeconds)/totalSeconds)*100;
                System.out.println(percentage);
                MainActivity.sbProcess.setProgress(percentage.intValue());
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MainActivity.position = position;
        String path = MainActivity.paths.get(position);
        playMusic(path);
    }

    private void playMusic(String path) {
        if (MainActivity.musicPlayer.getState() == PLAYER_PLAY) {
            MainActivity.musicPlayer.stop();
        }
        // process time // set up seekbar


        MainActivity.musicPlayer.setup(path);

        MainActivity.sbProcess.setProgress(0);


        MainActivity.musicPlayer.play();

        MainActivity.totaltime = MainActivity.musicPlayer.getTimeTotal();
        MainActivity.ivPlay.setImageResource(R.drawable.pause);
        // set up tên bài hát + ca sĩ
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(MainActivity.paths.get(MainActivity.position));
        String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        MainActivity.TEN_BAI_HAT= title;
        MainActivity.TEN_CA_SI=artist;
        MainActivity.tvArtist.setText(artist);
        MainActivity.tvTitle.setText(title);
        MainActivity.isRunning = true;
        // set up time
        // total time
        MainActivity.tvTimeTotal.setText(getTimeFormat(MainActivity.musicPlayer.getTimeTotal()));
        MainActivity.TOTAL_TIME = MainActivity.tvTimeTotal.getText().toString();


        showNotification(title,artist);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (MainActivity.isRunning) {
                    Message message = new Message();
                    message.what = MainActivity.UPDATE_TIME;
                    handler.sendMessage(message);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {

                    }
                }
            }
        }).start();
    }

    private String getTimeFormat(long time) {
        String tm = "";
        int s;
        int m;
        int h;
        //giây
        s = (int) (time % 60);
        m = (int) ((time - s) / 60);
        if (m >= 60) {
            h = m / 60;
            m = m % 60;
            if (h > 0) {
                if (h < 10)
                    tm += "0" + h + ":";
                else
                    tm += h + ":";
            }
        }
        if (m < 10)
            tm += "0" + m + ":";
        else
            tm += m + ":";
        if (s < 10)
            tm += "0" + s;
        else
            tm += s + "";
        return tm;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_next:
                nextMusic();
                break;

            case R.id.iv_play:
                if (MainActivity.musicPlayer.getState() == PLAYER_PLAY) {
                    MainActivity.ivPlay.setImageResource(R.drawable.play);
                    MainActivity.musicPlayer.pause();

                    MainActivity.notificationLayout.setViewVisibility(R.id.imgPlay,View.VISIBLE);
                    MainActivity.notificationLayout.setViewVisibility(R.id.imgPause,View.GONE);

                    MainActivity.notificationManager.notify(1, MainActivity.notification);

                } else {
                    MainActivity.ivPlay.setImageResource(R.drawable.pause);
                    MainActivity.musicPlayer.play();

                    MainActivity.notificationLayout.setViewVisibility(R.id.imgPlay,View.GONE);
                    MainActivity.notificationLayout.setViewVisibility(R.id.imgPause,View.VISIBLE);

                    MainActivity.notificationManager.notify(1, MainActivity.notification);
                }
                break;

            case R.id.iv_previous:
                previousMusic();
                break;

            default:
                break;
        }
    }

    private void previousMusic() {
        MainActivity.position--;
        if (MainActivity.position < 0) {
            MainActivity.position = MainActivity.paths.size() - 1;
        }
        String path = MainActivity.paths.get(MainActivity.position);
        playMusic(path);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Double percentage = (double) 0;
        long currentSeconds = (int) (MainActivity.musicPlayer.getTimeCurrent());
        long totalSeconds = (int) (MainActivity.musicPlayer.getTimeTotal());
        percentage =(((double)currentSeconds)/totalSeconds)*100;
        System.out.print("percentage="+percentage+" progress="+progress);
        if (percentage.intValue() != progress && MainActivity.timeCurrent != 0) {
            int currentDuration = 0;
            int totalDuration = MainActivity.musicPlayer.getTimeTotal();
            currentDuration = (int) ((((double)progress) / 100) * totalDuration);
            MainActivity.musicPlayer.seek(currentDuration * 1000);
        }
        if (progress==100)
        {
            nextMusic();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void OnEndMusic() {
    }

    private void nextMusic() {
        MainActivity.position++;
        if (MainActivity.position >= MainActivity.paths.size()) {
            MainActivity.position = 0;
        }
        String path = MainActivity.paths.get(MainActivity.position);
        playMusic(path);
    }

    private void showNotification(String tenbaihat, String tencasi) {
        MainActivity.notificationLayout.setTextViewText(R.id.tvnTenBaiHat, tenbaihat);
        MainActivity.notificationLayout.setTextViewText(R.id.tvnTenCaSi,tencasi);
        MainActivity.notificationLayout.setImageViewResource(R.id.imglargemusic,R.drawable.iclargemusic);
        MainActivity.notificationLayout.setImageViewResource(R.id.imgPlay,R.drawable.icplay);
        MainActivity.notificationLayout.setImageViewResource(R.id.imgPause,R.drawable.icpause);
        MainActivity.notificationLayout.setImageViewResource(R.id.imgNext,R.drawable.icnext);
        MainActivity.notificationLayout.setImageViewResource(R.id.imgPrevious,R.drawable.icprevious);

        MainActivity.notificationLayout.setViewVisibility(R.id.imgPlay,View.GONE);
        MainActivity.notificationLayout.setViewVisibility(R.id.imgPause,View.VISIBLE);

        MainActivity.notificationLayout.setOnClickPendingIntent(R.id.imgPause,
                onButtonNotificationClick(R.id.imgPause));
        MainActivity.notificationLayout.setOnClickPendingIntent(R.id.imgPlay,
                onButtonNotificationClick(R.id.imgPlay));
        MainActivity.notificationLayout.setOnClickPendingIntent(R.id.imgPrevious,
                onButtonNotificationClick(R.id.imgPrevious));
        MainActivity.notificationLayout.setOnClickPendingIntent(R.id.imgNext,
                onButtonNotificationClick(R.id.imgNext));

        MainActivity.notificationManager =
                (android.app.NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        MainActivity.notificationManager.notify(1, MainActivity.notification);
    }

    private PendingIntent onButtonNotificationClick(@IdRes int id) {
        Intent intent = new Intent(MainActivity.ACTION_NOTIFICATION_BUTTON_CLICK);
        intent.putExtra(MainActivity.EXTRA_BUTTON_CLICKED, id);
        return PendingIntent.getBroadcast(this, id, intent, 0);
    }
}
