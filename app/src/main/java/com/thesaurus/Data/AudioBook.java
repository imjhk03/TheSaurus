package com.thesaurus.Data;

/**
 * Created by JHK on 2017. 6. 9..
 */

public class AudioBook {
    /**
     * 이 클래스는 테스트용으로 사용 안 하고 있음.
     *
     * 오디오북의 정보들을 하나의 클래스로 정의하여 정보를 담고 불러오기 편하게 하기 위한 클래스
     * 제목, 트랙 ID, 오디오북 커버아트
     */

    String title, trackID, cover;

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getTrackID() { return trackID; }

    public void setTrackID(String trackID) { this.trackID = trackID; }

    public String getCover() { return cover; }

    public void setCover(String cover) { this.cover = cover; }
}
