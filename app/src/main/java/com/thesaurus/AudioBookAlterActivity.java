package com.thesaurus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.thesaurus.Adapter.BookAdapter;
import com.thesaurus.Data.Book;
import com.thesaurus.Utilities.NetworkUtils;
import com.thesaurus.Utilities.OpenBookJsonUtils;

import java.io.Serializable;
import java.net.URL;
import java.util.List;

/**
 * Created by JHK on 2017. 6. 7..
 */

/**
 * 메인 화면에서 오디오북 버튼을 누르면 이 액티비티가 실행된다.(AudioBookActivity 가 아니다)
 * 형태는 도서 리스트 화면과 똑같다.(BookActivity)
 */
public class AudioBookAlterActivity extends AppCompatActivity implements BookAdapter.BookAdapterOnClickHandler {
    static final String TAG = "AudioBookAlterActivity";

    private RecyclerView mRecyclerView;
    private BookAdapter mBookAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    private Button mHaveBookButton;
    private Button mWantReadBookButton;
    private Button mReadBookButton;
    private ImageView mSearchButton;
    private ImageView mBarcodeButton;

    private RadioGroup mSearchRadioGroup;
    private RadioGroup mSortRadioGroup;

    // 책 검색하는 기본 조건들. 쿼리 값과 검색타입(전체, 제목, 저자), 정렬순(판매량순, 정확도순, 발행일순)
    private String queryText = "";
    private String searchTypeText = "all";
    private String sortText = "popular";

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_book_alter);

        setTitle("오디오북");

        // 검색 버튼을 눌렀을 때, 입력한 텍스트를 가지고 다음 책 검색하도록 설정하는 것
        mSearchButton = (ImageView) findViewById(R.id.image_search);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchButtonClicked(v);
            }
        });

        // 바코드 버튼을 눌렀을 때, 스캔하는 화면이 나타나도록 설정하는 것
        // 바코드 검색한 다음에 꼭 onActivityResult 으로 돌아가야함
        mBarcodeButton = (ImageView) findViewById(R.id.image_barcode);
        mBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBarcodeButtonClicked(v);
            }
        });

        // 태그 영역 중, 소장중 버튼을 눌렀을 때, 색깔 반전하게 만들어주는 것
        // 아직 사용자 데이터베이스가 안 되어 있기 때문에 기능은 구현되어 있지 않음.
        mHaveBookButton = (Button) findViewById(R.id.btn_have_book);
        mHaveBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { changeColor(mHaveBookButton);
            }
        });

        // 태그 영역 중, 읽고싶어요 버튼을 눌렀을 때, 색깔 반전하게 만들어주는 것
        // 아직 사용자 데이터베이스가 안 되어 있기 때문에 기능은 구현되어 있지 않음.
        mWantReadBookButton = (Button) findViewById(R.id.btn_want_read);
        mWantReadBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { changeColor(mWantReadBookButton);
            }
        });

        // 태그 영역 중, 읽었어요 버튼을 눌렀을 때, 색깔 반전하게 만들어주는 것
        // 아직 사용자 데이터베이스가 안 되어 있기 때문에 기능은 구현되어 있지 않음.
        mReadBookButton = (Button) findViewById(R.id.btn_read);
        mReadBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { changeColor(mReadBookButton);
            }
        });

        // 전체, 제목, 저자 탭 영역의 선택 탭 영역
        // 각 택 영역을 선택했을 때, 다음 검색 api 에 맞는 쿼리 설정
        mSearchRadioGroup = (RadioGroup) findViewById(R.id.rbtng_search);
        mSearchRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch (checkedId)
                {
                    case R.id.rbtn_all:
                        searchTypeText = "all";
                        // 다음 검색 api json 데이터를 불러오게 하는 함수
                        reloadBookData(searchTypeText, sortText);
                        break;
                    case R.id.rbtn_title:
                        searchTypeText = "title";
                        reloadBookData(searchTypeText, sortText);
                        break;
                    case R.id.rbtn_author:
                        searchTypeText = "writer";
                        reloadBookData(searchTypeText, sortText);
                        break;
                    default:
                        break;
                }
            }
        });

        // 판매량순, 정확도순, 발행일순 탭 영역의 선택 탭 영역
        // 각 택 영역을 선택했을 때, 다음 검색 api 에 맞는 쿼리 설정
        mSortRadioGroup = (RadioGroup) findViewById(R.id.rbtng_sort);
        mSortRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch (checkedId)
                {
                    case R.id.rbtn_popular:
                        sortText = "popular";
                        reloadBookData(searchTypeText, sortText);
                        break;
                    case R.id.rbtn_accu:
                        sortText = "accu";
                        reloadBookData(searchTypeText, sortText);
                        break;
                    case R.id.rbtn_date:
                        sortText = "date";
                        reloadBookData(searchTypeText, sortText);
                        break;
                    default:
                        break;
                }
            }
        });

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_book);

//        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mBookAdapter = new BookAdapter(this);

        mRecyclerView.setAdapter(mBookAdapter);
    }

    // 키보드가 나와있을때, 다른 화면 선택하면 키보드가 없어지게 하는 함수
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();
        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    // 키보드 사라지게 하는 함수
    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    // 태그 버튼들을 선택했을 때, 색깔 반전되게 하는 함수
    public void changeColor(Button button) {
        int textColor;
        ColorDrawable colorDrawable;
        int colorId;

        colorDrawable = (ColorDrawable) button.getBackground();
        colorId = colorDrawable.getColor();

        textColor = button.getCurrentTextColor();

        button.setBackgroundColor(textColor);
        button.setTextColor(colorId);
    }

    // 바코드 버튼을 눌렀을때, 스캔하는 화면으로 넘어가게 하는 함수
    // 스캔한 뒤에 onActivityResult 함수로 넘아감.
    private void onBarcodeButtonClicked(View view) {
        IntentIntegrator i = new IntentIntegrator(AudioBookAlterActivity.this);
        i.setOrientationLocked(false);
        i.initiateScan();
    }

    // 검색 버튼을 눌렀을 때, 텍스트에 있는 글자를 받아서 검색하게 만듬
    // EditText에 텍스트가 없을 때, 검색어를 입력해 주세요 문구 나오게 설정
    private void onSearchButtonClicked(View view) {
        EditText searchText = (EditText) findViewById(R.id.edit_search);
        if (searchText.getText().toString().equals("")) {
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(this, "검색어를 입력해 주세요", Toast.LENGTH_LONG);

            mToast.show();
        } else {
            queryText = searchText.getText().toString();
            loadBookData(queryText, searchTypeText, sortText);
        }
    }

    // 데이터들을 불러오게 하는 함수
    // loadBookData 와 비슷한 함수
    private void reloadBookData(String searchTypeText, String sortText) {
        EditText searchText = (EditText) findViewById(R.id.edit_search);
        if (searchText.getText().toString().equals("")) {
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(this, "검색어를 입력해 주세요", Toast.LENGTH_LONG);

            mToast.show();
        } else {
            queryText = searchText.getText().toString();
            loadBookData(queryText, searchTypeText, sortText);
        }
    }

    // 도서 목록에서 한 도서를 선택했을 때, 책의 상세 페이지(BookDetailActivity)로 넘어가게 하는 함수
    @Override
    public void onClick(Book dataOfBook) {
        Context context = this;
        Class destinationClass = AudioBookDetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("BookData", (Serializable) dataOfBook);
        startActivity(intentToStartDetailActivity);
    }

    // 책 목록을 보여주고 에러 메세지는 사라지게 하는 함수
    private void showBookDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    // 에러 메세지를 보여주고 책 목록을 사라지게 하는 함수
    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    // 검색어에 대한 결과가 없으면 검색 결과가 없습니다 문구 나오게 설정하는 함수
    private void showNoItemMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        EditText searchText = (EditText) findViewById(R.id.edit_search);
        mErrorMessageDisplay.setText(searchText.getText().toString() + "에 대한 검색 결과가 없습니다");
    }

    // AsyncTask 을 이용해서 뒷 작업에서 json 데이터를 받아오게 하는 함수
    private void loadBookData(String query, String searchType, String sortText) {
        new FetchBookTask().execute(query, searchType, sortText);
    }

    // AsyncTask 함수를 이용해서 다음 검색 api에서 데이터들을 받아온다.
    public class FetchBookTask extends AsyncTask<String, Void, List<Book>> {
        // 작업하기 전 작업
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        // 뒷작업 시작
        @Override
        protected List<Book> doInBackground(String... params) {

            // 쿼리 검색어
            String queryText = params[0];
            // 검색하는 타입
            String searchTypeText = params[1];
            // 정렬하는 순
            String sortText = params[2];
            URL bookRequestUrl = null;
            // NetworkUtils 클래스를 이용해서 인터넷 연결한 다음에 json 데이터 불러오기
            // Utilities 폴더에 있는 NetworkUtils 클래스 참고
            bookRequestUrl = NetworkUtils.buildUrl(queryText, searchTypeText, sortText);

            try {
                // 요청해아하는 다음 검색 api 인터넷 주소를 문자열로 저장
                // NetworkUtils 클래스 참고
                String jsonBookResponse = NetworkUtils
                        .getResponseFromHttpUrl(bookRequestUrl);
                // json 데이터에서 받아온 정보들을 List 형식으로 데이터들을 저장
                // OpenBookJsonUtils 클래스 참고
                List<Book> simpleJsonBookData = OpenBookJsonUtils
                        .getSimpleBookStringsFromJson(AudioBookAlterActivity.this, jsonBookResponse);

                return simpleJsonBookData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        // 뒷 작업 다 완료되어 있으면 하는 작업
        @Override
        protected void onPostExecute(List<Book> bookData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            // 데이터에 값이 들어가 있으면
            if (!bookData.isEmpty()) {
                showBookDataView();
                mBookAdapter.setBookData(bookData);
            } else {
                showNoItemMessage();
                Log.w(TAG,"error after fetch data");
            }
        }
    }

    // 왼쪽 상단 뒤로 가기 버튼
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    // 바코드 스캔 화면 끝난 뒤, 이 함수로 결과 값을 이용하여 설정하는 함수
    // isbn 값을 검색어로 설정하여 다음 검색 api 검색함
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            if (scanResult.getContents() != null) {
                EditText searchText = (EditText) findViewById(R.id.edit_search);
                searchText.setText(scanResult.getContents());
                queryText = scanResult.getContents();
                loadBookData(queryText, searchTypeText, sortText);
            }
        }
        // else continue with any other code you need in the method
    }
}
