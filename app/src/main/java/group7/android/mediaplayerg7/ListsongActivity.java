package group7.android.mediaplayerg7;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import group7.android.adapter.MusicAdapter;
import group7.android.model.Music;

import static group7.android.mediaplayerg7.MusicPlayer.PLAYER_PLAY;
import static group7.android.mediaplayerg7.MusicPlayer.PLAYER_PAUSE;
public class ListsongActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener, MusicPlayer.OnCompletionListener{
    ListView lvBaiHatGoc;
    ArrayList<Music> dsBaiHatGoc;
    MusicAdapter adapterBaiHatGoc;
    TextView tvTieuDe;
    ImageView imgBack;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listsong);
        AddControls();
        AddEvents();
        initComponents();
        TiepTucBaiHat();

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
        lvBaiHatGoc = this.<ListView>findViewById(R.id.listSong);
        dsBaiHatGoc = new ArrayList<>();
        adapterBaiHatGoc = new MusicAdapter(ListsongActivity.this,R.layout.itemlistsong,dsBaiHatGoc);
        lvBaiHatGoc.setAdapter(adapterBaiHatGoc);

        tvTieuDe = (TextView)findViewById(R.id.tvTieuDe);
        tvTieuDe.setText("Danh sách bài hát");
        imgBack = (ImageView)findViewById(R.id.imageView4);

        initViews();

        if (KiemTraLanDauChayApp()) { //nếu lần đầu tiên chạy app thì mới quét điện thoại
            /*KhoiTaoList();
            DanhSachMusicQuetDuoc();*/
            getListSongs();
        }
        else //lấy từ database ra thôi
        {
            DanhSachMusicLayTuDatabase();
        }
        adapterBaiHatGoc.notifyDataSetChanged(); //cập nhật lại apdapter
    }

    private void initComponents() {
        /*MainActivity.musicPlayer.setOnCompletionListener(this);*/
    }

    private void AddEvents() {
        initListeners();
    }

    private void initListeners() {
        lvBaiHatGoc.setOnItemClickListener(this);
        MainActivity.ivShuffle.setOnClickListener(this);
        MainActivity.ivPrevious.setOnClickListener(this);
        MainActivity.ivPlay.setOnClickListener(this);
        MainActivity.ivNext.setOnClickListener(this);
        MainActivity.ivRepeat.setOnClickListener(this);
        MainActivity.sbProcess.setOnSeekBarChangeListener(this);
        MainActivity.musicPlayer.setOnCompletionListener(this);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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
    private void DanhSachMusicLayTuDatabase() {
        Cursor cursor = MainActivity.database.query("music",null,null,null,null,null,null);

        dsBaiHatGoc.clear();
        MainActivity.paths = new ArrayList<>();
        while (cursor.moveToNext())
        {
            Music music = new Music();
            music.setIdsong(cursor.getString(0));
            music.setNamesong(cursor.getString(1));
            music.setArtist(cursor.getString(2));
            music.setAlbum(cursor.getString(3));
            Boolean bool = cursor.getInt(4)>0;
            music.setFavorite(bool);
            music.setPath(cursor.getString(5));
            dsBaiHatGoc.add(music);
            MainActivity.paths.add(music.getPath());
        }
        cursor.close(); //Đóng kết nối
        adapterBaiHatGoc.notifyDataSetChanged();
    }

    private boolean KiemTraLanDauChayApp()
    {
        Cursor cursor = MainActivity.database.query("music",null,null,null,null,null,null);
        while (cursor.moveToNext())
        {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    void getListSongs(){
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(songUri, null, null, null, null);
        MainActivity.paths = new ArrayList<>();
        if(songUri != null && cursor.moveToFirst()){
            do{
                String s = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                if (s.endsWith(".mp3")) {
                    Music music = new Music();
                    music.setIdsong(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                    music.setNamesong(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                    music.setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                    music.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                    music.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                    music.setFavorite(false);

                    ContentValues row = new ContentValues();
                    row.put("idsong", music.getIdsong());
                    row.put("namesong", music.getNamesong());
                    row.put("artist", music.getArtist());
                    row.put("album", music.getAlbum());
                    row.put("favorite", music.getFavorite());
                    row.put("path", music.getPath());

                    long r = MainActivity.database.insert("music", null, row);
                    dsBaiHatGoc.add(music);
                    MainActivity.paths.add(music.getPath());
                }

            }while(cursor.moveToNext());
        }
        cursor.close();
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
            case R.id.iv_repeat:
                if (MainActivity.repeat==0)
                {
                    Toast.makeText(this, "Bạn đã chọn lặp lại bài hát", Toast.LENGTH_SHORT).show();
                    MainActivity.repeat=1;
                }
                else
                {
                    Toast.makeText(this, "Bạn bỏ chọn lặp lại bài hát", Toast.LENGTH_SHORT).show();
                    MainActivity.repeat=0;
                }
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
        if (MainActivity.repeat==1)
        {
            MainActivity.position--;
        }
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