package com.thesaurus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thesaurus.Data.Book;

/**
 * Created by JHK on 2017. 6. 7..
 */

/*
 * 이 액티비티는 오디오북 상세 페이지를 구성하는 액티비티
 * 도서 목록 화면에서 선택한 도서의 정보들을 받아와서 화면에 뿌려지게 한다
 */
public class AudioBookDetailActivity extends AppCompatActivity {
    private TextView mBookTitleTextView, mBookAuthorTextView, mBookPublisherTextView, mBookGenreTextView, mBookDateTextView, mBookDescriptionTextView;
    private ImageView mBookCoverImageView;
    private Button mAudioPlayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_book_detail);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("오디오 도서");

        mBookTitleTextView = (TextView) findViewById(R.id.tv_book_title);
        mBookAuthorTextView = (TextView) findViewById(R.id.tv_book_author);
        mBookPublisherTextView = (TextView) findViewById(R.id.tv_book_publisher);
        mBookCoverImageView = (ImageView) findViewById(R.id.iv_book_cover);
        mBookGenreTextView = (TextView) findViewById(R.id.tv_book_genre);
        mBookDateTextView = (TextView) findViewById(R.id.tv_book_date);
        mBookDescriptionTextView = (TextView) findViewById(R.id.tv_book_description);

        // 도서 목록에서 받아온 정보들을 받게 하는 함수
        // Intent는 중요하므로 따로 공부하도록
        Intent intentThatStartedThisActivity = getIntent();

        // 받아온 정보가 있다면 책 상세 페이지에 정보들 보여주게 하는 것
        if (intentThatStartedThisActivity != null) {
            final Book mDataOfBook = (Book) intentThatStartedThisActivity.getSerializableExtra("BookData");

            mBookTitleTextView.setText(mDataOfBook.getTitle());
            mBookAuthorTextView.setText(mDataOfBook.getAuthor());
            mBookPublisherTextView.setText(mDataOfBook.getPublisher());

            String imageData = mDataOfBook.getCoverLarge();
            // 만약 다음 검색에서 받아온 정보가 아니라면
            // 구글 파이어베이스 데이터베이스에 있는 책을 불러오기 때문에 내부에 있는 책 그림들을 불러와 설정하게 함
            if(imageData == null) {
                mBookCoverImageView.setImageResource(mDataOfBook.getCoverInt());
            } else if (mDataOfBook.getCoverLarge() == null || mDataOfBook.getCoverLarge().equals("")) {
                // 다음 검색 중에 책 표지 그림이 없으면 no_image 그림으로 설정하기
                Glide.with(this)
                        .load(R.drawable.no_image)
                        .into(mBookCoverImageView);
            } else {
                // 다음 검색 중에 책 표지가 있다면 그림 설정하기
                Glide.with(this)
                        .load(mDataOfBook.getCoverLarge())
                        .into(mBookCoverImageView);
            }
            mBookGenreTextView.setText(mDataOfBook.getCategory());
            mBookDateTextView.setText(mDataOfBook.getPubdate());
            mBookDescriptionTextView.setText(mDataOfBook.getDescription());

            // isbn 13 자리 값에 i 추가한것
            // raw 폴더에 있는 오디오북 파일과 일치하게 만드는 변수
            final String isbn13 = "i" + mDataOfBook.getIsbn13();

            mAudioPlayButton = (Button) findViewById(R.id.btn_listen_audio);
            mAudioPlayButton.setVisibility(View.VISIBLE);
            // 오디오북 듣기 버튼을 누르면 책의 제목, 표지, 오디오북 파일 정보를
            // AudioBookListenActivity 로 보내어 재생하도록 만든다
            mAudioPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = getApplicationContext();
                    Class destinationClass = AudioBookListenActivity.class;
                    Intent intentToStartDetailActivity = new Intent(context, destinationClass);
                    intentToStartDetailActivity.putExtra("BOOK_AUDIO", isbn13);
                    intentToStartDetailActivity.putExtra("BOOK_COVER", mDataOfBook.getCoverLarge());
                    intentToStartDetailActivity.putExtra("BOOK_TITLE", mDataOfBook.getTitle());
                    startActivity(intentToStartDetailActivity);
                }
            });
        }
    }

    // 뒤로 가기 버튼 설정
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
