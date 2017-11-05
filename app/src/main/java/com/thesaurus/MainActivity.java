package com.thesaurus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesaurus.Adapter.BookCardAdapter;
import com.thesaurus.Data.Book;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * 이 액티비티는 메인 액티비티로, 앱의 첫 화면과 오디오북, 도서리스트 버튼과 신간도서의 목록을 보여주는 화면을 보여주는 액티비티
 * 신간도서의 자료들은 구글 firebase 데이터베이스에 있는 데이터를 불러와서 보여준다.
 */
public class MainActivity extends AppCompatActivity implements BookCardAdapter.BookCardAdapterOnClickHandler {
    static final String TAG = "MainActivity";

    // 임시로 프로젝트 내부에 있는 책 사진을 배열로 정리
    private int[] bookCoverImage = {
            R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5,
            R.drawable.image6, R.drawable.image7, R.drawable.image8, R.drawable.image9, R.drawable.image10,
            R.drawable.image11, R.drawable.image12, R.drawable.image13, R.drawable.image14, R.drawable.image15
    };

    // 필요한 뷰 선언
    private RecyclerView mRecyclerView;
    private BookCardAdapter mBookCardAdapter;
    private List<Book> bookList = new ArrayList<>();

    private TextView mMoreBookTextView;
    private Button mBookActivityButton;
    private Button mAudioBookActivityButton;

    // 구글 파이어베이스 데이터베이스 연동
    // 메인 화면에서 신간 도서 관련 데이터베이스 연동
    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mBooksDatabaseReference;

    // 메인 액티비티가 만들어지면서 하는 일
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 파이어베이스 데이터베이스 초기화
        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mBooksDatabaseReference = mFirebaseDatabase.getReference();

        // 도서 리스트 버튼 받아오고 버튼 눌렀을 때 해야하는 활동 선언
        mBookActivityButton = (Button) findViewById(R.id.btn_bookActivity);
        mBookActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBookActivityButtonClicked(v);
            }
        });

        // 오디오북 버튼 받아오고 버튼 눌렀을 때 해야하는 활동 선언
        mAudioBookActivityButton = (Button) findViewById(R.id.btn_audioBookActivity);
        mAudioBookActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAudioBookActivityButtonClicked(v);
            }
        });

        // 신간도서의 더보기 텍스트를 누르면 해야하는 활동 선언
        mMoreBookTextView = (TextView) findViewById(R.id.tv_more_book);
        mMoreBookTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onMoreBookButtonClicked(v);
            }
        });

        // RecyclerView 선언하면서 리스트를 어떻게 보여줘야하는지 작업
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_book);

        // 그리드 형식으로 자료들을 보여주기 위한 LayoutManager 설정
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
//        LinearLayoutManager layoutManager
//                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mBookCardAdapter = new BookCardAdapter(this);

        // 파이어베이스 데이터베이스 자료들을 받아오는 함수 시작
        mBooksDatabaseReference.limitToFirst(9).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;

                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    bookList.add(child.getValue(Book.class));
                    bookList.get(i).setCoverInt(bookCoverImage[i]);
                    i++;
                }
                // List에 데이터가 추가되었기 때문에 데이터 변동이 있다고 알려주는 함수
                mBookCardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

        mBookCardAdapter.setBookData(bookList);
        mRecyclerView.setAdapter(mBookCardAdapter);

        //mRecyclerView.setAdapter(mBookCardAdapter);
    }

    // 신간도서 목록에 있는 한 도서를 선택했을 때 하는 작업
    // 책이 갖고 있는 정보를 BookDetailActivity 에 보내서 책의 상세 페이지 화면을 띄운다
    @Override
    public void onClick(Book dataOfBook) {
        Context context = this;
        Class destinationClass = BookDetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("BookData", (Serializable) dataOfBook);
        startActivity(intentToStartDetailActivity);
    }

    // 오디오북 버튼을 누르면 AudioBookAlterActivity 실행하여 오디오북 화면으로 전환하기
    private void onAudioBookActivityButtonClicked(View view) {
        Context context = this;
        Class destinationClass = AudioBookAlterActivity.class;
        Intent intentToStartAudioBookActivity = new Intent(context, destinationClass);
        startActivity(intentToStartAudioBookActivity);
    }

    // 도서리스트 버튼을 누르면 BookActivity 실행하여 도서리스트 화면으로 전환하기
    private void onBookActivityButtonClicked(View view) {
        Context context = this;
        Class destinationClass = BookActivity.class;
        Intent intentToStartBookActivity = new Intent(context, destinationClass);
        startActivity(intentToStartBookActivity);
    }

    // 신간도서의 더보기 텍스트를 누르면 신간도서 리스트 목록 화면으로 전환하기(BookListActivity)
    private void onMoreBookButtonClicked(View view) {
        Context context = this;
        Class destinationClass = BookListActivity.class;
        Intent intentToStartBookListActivity = new Intent(context, destinationClass);
        startActivity(intentToStartBookListActivity);
    }

    // 오른쪽 상단 메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.menu_main, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }
}
