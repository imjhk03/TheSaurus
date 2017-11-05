package com.thesaurus.Utilities;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

/**
 * Created by JHK on 2017. 6. 8..
 */

/*
 * 오디오북 관련 자료 클래스
 * 사용하고 있지 않음. 테스트용
 */
public class AudioPlay {
    public static MediaPlayer mediaPlayer;
    private static SoundPool soundPool;
    public static boolean isplayingAudio=false;

    public static void playAudio(Context c, int id){
        mediaPlayer = MediaPlayer.create(c,id);
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        if(!mediaPlayer.isPlaying())
        {
            isplayingAudio = true;
            mediaPlayer.start();
        }
    }

    public static void stopAudio(){
        isplayingAudio = false;
        mediaPlayer.stop();
    }
    /*
    public static MediaPlayer mediaPlayer;
    private static MediaPlayer newMediaPlayer;
    private static SoundPool soundPool;
    public static boolean isPlayingAudio = false;
    private static int previousSong;

    public static void setMediaPlayer(Context context, int id) {
        mediaPlayer = MediaPlayer.create(context, id);
        previousSong = id;
    }

    public static void playAudio(Context context, int id){
        if(mediaPlayer.isPlaying())
        {
            System.out.println("@@@@@@@@@@@@@@@@@@ media player is playing - " + mediaPlayer.getAudioSessionId());
            if (previousSong == id) {
                System.out.println("@@@@@@@@@@@@@@@@@@ media player is playing current audio - " + mediaPlayer.getAudioSessionId());
                mediaPlayer.start();
                isPlayingAudio = true;
            } else {
                System.out.println("@@@@@@@@@@@@@@@@@@ media player is trying to play another song -  " + mediaPlayer.getAudioSessionId());
                isPlayingAudio = false;
                mediaPlayer.stop();
                System.out.println("@@@@@@@@@@@@@@@@@@ media player stopped - " + mediaPlayer.getAudioSessionId());
                mediaPlayer.release();
                mediaPlayer = null;
                System.out.println("@@@@@@@@@@@@@@@@@@ media player released and null ");

                setMediaPlayer(context, id);

                newMediaPlayer = mediaPlayer;

                isPlayingAudio = true;
                newMediaPlayer.start();
            }
        } else {
            System.out.println("@@@@@@@@@@@@@@@@@@ media player is not playing - " + mediaPlayer.getAudioSessionId());
            isPlayingAudio = true;
            mediaPlayer.start();
        }
    }

    public static void stopAudio(){
        isPlayingAudio = false;
        mediaPlayer.stop();
    }
    */
}
