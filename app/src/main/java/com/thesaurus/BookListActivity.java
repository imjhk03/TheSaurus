package com.thesaurus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesaurus.Adapter.BookAdapter;
import com.thesaurus.Data.Book;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * 이 액티비티는 신간도서의 도서 목록을 리스트 형식으로 보여주기 위한 액티비티
 */
public class BookListActivity extends AppCompatActivity implements BookAdapter.BookAdapterOnClickHandler {
    static final String TAG = "BookListActivity";

    // 임시로 프로젝트 내부에 있는 책 사진을 배열로 정리
    private int[] bookCoverImage = {
            R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5,
            R.drawable.image6, R.drawable.image7, R.drawable.image8, R.drawable.image9, R.drawable.image10,
            R.drawable.image11, R.drawable.image12, R.drawable.image13, R.drawable.image14, R.drawable.image15
    };

    // 필요한 뷰와 변수 선언
    private RecyclerView mRecyclerView;
    private BookAdapter mBookAdapter;
    private List<Book> bookList = new ArrayList<>();

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mBooksDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 화면 제목 설정
        setTitle("신간도서");

        // 구글 파이어베이스 데이터베이스 초기화
        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mBooksDatabaseReference = mFirebaseDatabase.getReference();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_book);

//        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        // 불러온 데이터들을 리스트 형식으로 보여지게 하는 layoutManager 설정
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mBookAdapter = new BookAdapter(this);

        // Google Firebase 데이터베이스에 있는 데이터들을 불러오기 위한 함수
        mBooksDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;

                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    bookList.add(child.getValue(Book.class));
                    bookList.get(i).setCoverInt(bookCoverImage[i]);
//                    System.out.println(bookList.get(i).getTitle() + " - " + bookList.get(i).getAuthor());
                    i++;
                }
                mBookAdapter.setBookData(bookList);
                mBookAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mBookAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    // 각 도서를 선택했을 때 책 상세 페이지(BookDetailActivity)로 전환하도록 onClick함수 설정
    @Override
    public void onClick(Book dataOfBook) {
        Context context = this;
        Class destinationClass = BookDetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("BookData", (Serializable) dataOfBook);
        startActivity(intentToStartDetailActivity);
    }
}
