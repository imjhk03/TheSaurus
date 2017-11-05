package com.thesaurus;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;

/**
 * 음악 재생화면을 구성하는 액티비티
 * 외주업체의 소스를 받아와서 소스를 조금 수정함
 */
public class AudioBookListenActivity extends AppCompatActivity {
    @BindView(R.id.btPlay)
    Button _btPlay;
    @BindView(R.id.btNext10) Button _btNext10;
    @BindView(R.id.btPre10) Button _btPre10;
    @BindView(R.id.progressBar)
    ProgressBar _progressBar;

    String bookTitle, isbn13Audio, audioCoverImage;
    int position = 0;

    // raw 폴더에 있는 오디오북 음악 파일들을 담고 있는 배열
    private int[] bookAudio = {
            R.raw.i9788916047210, R.raw.i9788916047241, R.raw.i9788916047258, R.raw.i9788916047265, R.raw.i9788916047289
    };

    private MediaPlayer mediaPlayer;
    private TextView songName, duration;
    private ImageView mBookCoverImageView;
    private ImageButton mPlayButton;
    private double timeElapsed = 0, finalTime = 0;
    private int forwardTime = 5 * 1000, backwardTime = 5 * 1000; // 뒤로감기, 앞으로감기 기본 5초로 설정
    private Handler durationHandler = new Handler();
    private SeekBar seekBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_book_listen);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("오디오북 듣기");

        // 오디오북에 대한 정보가 들어가 있는 Intent 받아오기
        Intent intentThatStartedThisActivity = getIntent();

        // 화면에 보여줄 정보 표시하기
        if (intentThatStartedThisActivity != null) {
            bookTitle = intentThatStartedThisActivity.getStringExtra("BOOK_TITLE");
            isbn13Audio = intentThatStartedThisActivity.getStringExtra("BOOK_AUDIO");
            audioCoverImage = intentThatStartedThisActivity.getStringExtra("BOOK_COVER");
        }

        // isbn 값이 오디오북 음악파일과 같으면 배열에 있는 값을 지정한다
        if (isbn13Audio.equals("i9788916047210")) {
            position = 0;
        } else if (isbn13Audio.equals("i9788916047241")) {
            position = 1;
        } else if (isbn13Audio.equals("i9788916047258")) {
            position = 2;
        } else if (isbn13Audio.equals("i9788916047265")) {
            position = 3;
        } else if (isbn13Audio.equals("i9788916047289")) {
            position = 4;
        }

        // 뷰들을 초기화한다
        initilizeViews(bookTitle, position);
    }

    /**
     * 뷰들을 초기화한다.(텍스트뷰, 버튼 등등)
     * 여기서 음악을 재생하는 MediaPlayer 객체도 생성한다
     *
     * @param title 오디오북의 제목
     * @param position 오디오북 음악파일이 있는 배열의 위치 값
     */
    public void initilizeViews(String title, int position) {
        songName = (TextView) findViewById(R.id.tv_book_title);
        songName.setText(title);

        mediaPlayer = MediaPlayer.create(this, bookAudio[position]);
        finalTime = mediaPlayer.getDuration();

        mPlayButton = (ImageButton) findViewById(R.id.ibtn_play);

        // 남는 시간 보여주는 텍스트
        duration = (TextView) findViewById(R.id.songDuration);
        // 진행 상황을 보여주는 바
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        // 진행 상황을 보여주는 바의 버튼을 움직여서 원하는 곳에 놓으면 거기서부터 재생하도록 설정하는 작업
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int seek_to;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seek_to = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seek_to);
            }
        });

        mBookCoverImageView = (ImageView) findViewById(R.id.image_cover);

        Glide.with(this)
                .load(audioCoverImage)
                .into(mBookCoverImageView);

        seekBar.setMax((int) finalTime);
        seekBar.setClickable(false);
    }

    // 다른 작업을 하면서도 음악이 재생되도록 쓰레드에 남도록(?) 하는 작업
    // 계속 남는 시간을 보여주도록 설정
    private Runnable updateSeekBarTime = new Runnable() {
        @Override
        public void run() {
            timeElapsed = mediaPlayer.getCurrentPosition();
            seekBar.setProgress((int) timeElapsed);
            double timeRemaining = finalTime - timeElapsed;
            duration.setText(String.format("%d 분 %d 초", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining),
                    TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));
            durationHandler.postDelayed(this, 100);
        }
    };

    // 음악 재생 버튼을 누르면 재생을, 재생 중에 누르면 일시정지 하도록 하는 버튼
    public void play(View view) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mPlayButton.setImageResource(R.drawable.ic_play_arrow_black_48dp);
        } else {
            mPlayButton.setImageResource(R.drawable.ic_pause_black_48dp);
            mediaPlayer.start();
            timeElapsed = mediaPlayer.getCurrentPosition();
            seekBar.setProgress((int) timeElapsed);
            durationHandler.postDelayed(updateSeekBarTime, 100);
        }
        /*
        mediaPlayer.start();
        timeElapsed = mediaPlayer.getCurrentPosition();
        seekBar.setProgress((int) timeElapsed);
        durationHandler.postDelayed(updateSeekBarTime, 100);
        */

        /*
        if(mediaPlayer.isPlaying()){
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ media player is playing - " + mediaPlayer.getAudioSessionId());
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;

            mediaPlayer = MediaPlayer.create(this, bookAudio[position]);
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ media player is newly setted - " + mediaPlayer.getAudioSessionId());
        } else {
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ media player is not playing");
        }
        mediaPlayer.start();
        timeElapsed = mediaPlayer.getCurrentPosition();
        seekBar.setProgress((int) timeElapsed);
        durationHandler.postDelayed(updateSeekBarTime, 100);
        */
    }

    // 앞으로 감기 버튼, 5초 앞으로 간다
    public void forward(View view) {
        if (mediaPlayer != null) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            if (currentPosition + forwardTime <= mediaPlayer.getDuration()) {
                mediaPlayer.seekTo(currentPosition + forwardTime);
            } else {
                mediaPlayer.seekTo(mediaPlayer.getDuration());
            }
        }
    }

    // 뒤로 감기 버튼, 5초 뒤로 간다
    public void rewind(View view) {
        if (mediaPlayer != null) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            if (currentPosition - backwardTime >= 0) {
                mediaPlayer.seekTo(currentPosition - backwardTime);
            } else {
                mediaPlayer.seekTo(0);
            }
        }
    }

    // 뒤로 가기 버튼
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
