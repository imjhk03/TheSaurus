package com.thesaurus.Data;

import java.io.Serializable;

/**
 * Created by JHK on 2017. 5. 30..
 */

public class Book implements Serializable {
    /**
     * 책의 정보들을 하나의 클래스로 정의하여 정보를 담고 불러오기 편하게 하기 위한 클래스
     * 제목, 표지 작은 사이즈, 표지 큰 사이즈, 작가, 출판사, isbn 13자리, 설명, 출판일, 카테고리 값
     */

    String title, coverSmall, coverLarge, author, publisher, isbn13, description, pubdate, category;
    int coverInt = 0;

    public Book() { }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getCoverSmall() { return coverSmall; }

    public void setCoverSmall(String coverSmall) { this.coverSmall = coverSmall; }

    public String getCoverLarge() { return coverLarge; }

    public void setCoverLarge(String coverLarge) { this.coverLarge = coverLarge; }

    public String getAuthor() { return author; }

    public void setAuthor(String author) { this.author = author; }

    public String getPublisher() { return publisher; }

    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getIsbn13() { return isbn13; }

    public void setIsbn13(String isbn) { this.isbn13 = isbn; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getPubdate() { return pubdate; }

    public void setPubdate(String pubdate) { this.pubdate = pubdate; }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public int getCoverInt() { return coverInt; }

    public void setCoverInt(int coverInt) { this.coverInt = coverInt; }
}
