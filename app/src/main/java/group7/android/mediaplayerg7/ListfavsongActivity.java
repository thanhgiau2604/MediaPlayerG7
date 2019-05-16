package group7.android.mediaplayerg7;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import static group7.android.mediaplayerg7.MusicPlayer.PLAYER_PAUSE;
import static group7.android.mediaplayerg7.MusicPlayer.PLAYER_PLAY;

public class ListfavsongActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener, MusicPlayer.OnCompletionListener{

    ListView lvBaiHatYeuThich;
    public static ArrayList<Music> dsBaiHatYeuThich;
    public static MusicAdapter adapterBaiHatYeuThich;
    public static ArrayList<String> paths;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listfavsong);

        AddControls();
        AddEvents();
        TiepTucBaiHat();
    }

    private  void TiepTucBaiHat()
    {
        if (MainActivity.musicPlayer.getState()==PLAYER_PLAY || MainActivity.musicPlayer.getState()==PLAYER_PAUSE)
        {
            MainActivity.tvArtist.setText(MainActivity.TEN_BAI_HAT);
            MainActivity.tvTitle.setText(MainActivity.TEN_CA_SI);
            MainActivity.tvTimeTotal.setText(MainActivity.TOTAL_TIME);
            if (MainActivity.musicPlayer.getState()==PLAYER_PLAY)
                MainActivity.ivPlay.setImageResource(R.drawable.pause);
            else
                MainActivity.ivPlay.setImageResource(R.drawable.play);
        }
    }

    private void AddEvents() {
        initListeners();
    }

    private void AddControls() {
        lvBaiHatYeuThich = this.<ListView>findViewById(R.id.listfavsong);
        dsBaiHatYeuThich = new ArrayList<>();
        adapterBaiHatYeuThich = new MusicAdapter(ListfavsongActivity.this,R.layout.itemlistsong,dsBaiHatYeuThich);
        lvBaiHatYeuThich.setAdapter(adapterBaiHatYeuThich);

        initViews();

        LayDuLieuBaiHatYeuThichTuCSDL();
        adapterBaiHatYeuThich.notifyDataSetChanged();
    }

    private void initListeners() {
        lvBaiHatYeuThich.setOnItemClickListener(this);
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

    public static void LayDuLieuBaiHatYeuThichTuCSDL()
    {

        Cursor cursor = MainActivity.database.query("music",null,"favorite=?",new String[] {"1"},null,null,null);
        dsBaiHatYeuThich.clear();
        paths = new ArrayList<>();
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
            dsBaiHatYeuThich.add(music);
            paths.add(music.getPath());
        }
        cursor.close(); //Đóng kết nối
        adapterBaiHatYeuThich.notifyDataSetChanged();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MainActivity.UPDATE_TIME) {
                MainActivity.timeCurrent = MainActivity.musicPlayer.getTimeCurrent();
                MainActivity.tvTimeProcess.setText(getTimeFormat(MainActivity.timeCurrent));
                MainActivity.sbProcess.setProgress(MainActivity.timeCurrent);
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MainActivity.position = position;
        String path = paths.get(position);
        playMusic(path);
    }

    private void playMusic(String path) {
        if (MainActivity.musicPlayer.getState() == PLAYER_PLAY) {
            MainActivity.musicPlayer.stop();
        }
        MainActivity.musicPlayer.setup(path);
        MainActivity.musicPlayer.play();
        MainActivity.ivPlay.setImageResource(R.drawable.pause);
        // set up tên bài hát + ca sĩ
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(paths.get(MainActivity.position));
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
        // process time // set up seekbar
        MainActivity.sbProcess.setMax(MainActivity.musicPlayer.getTimeTotal());
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
                } else {
                    MainActivity.ivPlay.setImageResource(R.drawable.pause);
                    MainActivity.musicPlayer.play();
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
            MainActivity.position = paths.size() - 1;
        }
        String path = paths.get(MainActivity.position);
        playMusic(path);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (MainActivity.timeCurrent != progress && MainActivity.timeCurrent != 0)
            MainActivity.musicPlayer.seek(MainActivity.sbProcess.getProgress() * 1000);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void OnEndMusic() {
        // khi kết thúc bài hát nó sẽ vào đây
        nextMusic();
        Log.d("chanh", "vào đây");
        // như vậy khi kết thúc bài hát nó có thể next bài tiếp theo
        // nếu hết danh sách bài hát nó sẽ quay lại từ bài đầu tiên
    }

    private void nextMusic() {
        MainActivity.position++;
        if (MainActivity.position >= paths.size()) {
            MainActivity.position = 0;
        }
        String path = paths.get(MainActivity.position);
        playMusic(path);
    }
}
