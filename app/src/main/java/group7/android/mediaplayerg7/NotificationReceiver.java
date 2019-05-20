package group7.android.mediaplayerg7;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import static group7.android.mediaplayerg7.MusicPlayer.PLAYER_PLAY;


public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        int id = intent.getIntExtra(MainActivity.EXTRA_BUTTON_CLICKED, -1);
        switch (id) {
            case R.id.imgPlay:
                MainActivity.notificationLayout.setViewVisibility(R.id.imgPlay,View.GONE);
                MainActivity.notificationLayout.setViewVisibility(R.id.imgPause,View.VISIBLE);
                MainActivity.notificationManager.notify(1,MainActivity.notification);
                MainActivity.ivPlay.setImageResource(R.drawable.pause);
                MainActivity.musicPlayer.play();
                break;
            case R.id.imgPause:
                MainActivity.notificationLayout.setViewVisibility(R.id.imgPause,View.GONE);
                MainActivity.notificationLayout.setViewVisibility(R.id.imgPlay,View.VISIBLE);
                MainActivity.notificationManager.notify(1,MainActivity.notification);
                MainActivity.ivPlay.setImageResource(R.drawable.play);
                MainActivity.musicPlayer.pause();
                break;
            case R.id.imgNext:
                nextMusic();
                break;
            case R.id.imgPrevious:
                previousMusic();
                break;
        }
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
        // process time // set up seekbar

        /*Toast.makeText(this, "Ban đầu" + String.valueOf(MainActivity.sbProcess.getMax()), Toast.LENGTH_SHORT).show();*/
        MainActivity.totaltime = MainActivity.musicPlayer.getTimeTotal();

        //Update notification
        MainActivity.notificationLayout.setTextViewText(R.id.tvnTenBaiHat,title);
        MainActivity.notificationLayout.setTextViewText(R.id.tvnTenCaSi,artist);
        MainActivity.notificationLayout.setViewVisibility(R.id.imgPlay,View.GONE);
        MainActivity.notificationLayout.setViewVisibility(R.id.imgPause,View.VISIBLE);
        MainActivity.notificationManager.notify(1,MainActivity.notification);
        //button play

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

    private void previousMusic() {
        MainActivity.position--;
        if (MainActivity.position < 0) {
            MainActivity.position = MainActivity.paths.size() - 1;
        }
        String path = MainActivity.paths.get(MainActivity.position);

        playMusic(path);
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
}
