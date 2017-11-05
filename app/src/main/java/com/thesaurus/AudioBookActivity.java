package com.thesaurus;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 외주업체가 만들어준 오디오북 액티비티
 * 한 액티비티에 바코드, 다음 책 검색, 오디오 재생 기능들이 있음
 * JHK 가 만든게 아니기 때문에 주석 달기 힘들어 주석이 없음
 *
 * 오디오북 버튼을 눌렀을 때 이 액티비티가 실행되지 않고
 * AudioBookAlterActivity 가 실행된다
 */
public class AudioBookActivity extends AppCompatActivity {
    RelativeLayout layoutSearch;
    RelativeLayout layoutPlay;
    ListViewAdapter _adapter;
    @BindView(R.id.etSearch) EditText _etSearch;
    @BindView(R.id.btPlay) Button _btPlay;
    @BindView(R.id.btNext10) Button _btNext10;
    @BindView(R.id.btPre10) Button _btPre10;
    @BindView(R.id.progressBar) ProgressBar _progressBar;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if((player != null) && (player.isPlaying()))
            {
                int n = (int)((float)player.getCurrentPosition() / player.getDuration() * _progressBar.getMax());
                _progressBar.setProgress(n);
            }
            mHandler.sendEmptyMessageDelayed(0, 1000);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);

        mHandler.sendEmptyMessageDelayed(0, 1000);
        layoutSearch = (RelativeLayout)findViewById(R.id.layout_search);
        layoutPlay = (RelativeLayout)findViewById(R.id.layout_play);
        layoutPlay.setVisibility(View.GONE);
        {
            //ArrayAdapter ad = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, g_arr_list);
            //ListView lv = (ListView)getView().findViewById(R.id.listView);
            EditText et = (EditText)findViewById(R.id.etSearch);
            et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    search();
                    return false;
                }
            });
        }
        _adapter = new ListViewAdapter(this);
        ListView lv = (ListView)findViewById(R.id.listView);
        lv.setAdapter(_adapter);

        Button bt = (Button)findViewById(R.id.btBarcode);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator i = new IntentIntegrator(AudioBookActivity.this);
                i.setOrientationLocked(false);
                i.initiateScan();
            }
        });

        bt = (Button)findViewById(R.id.btSearch);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(_etSearch.getWindowToken(), 0);
                search();
            }
        });

        _btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(player != null)
                {
                    if(player.isPlaying() == true)
                    {
                        player.pause();
                        _btPlay.setText("Play");
                    }
                    else
                    {
                        player.start();
                        _btPlay.setText("Stop");
                    }
                }
            }
        });
        _btPre10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(player != null)
                {
                    int currentPosition = player.getCurrentPosition();
                    if (currentPosition - 10000 > 0) {
                        player.seekTo(currentPosition - 10000);
                    } else {
                        player.seekTo(0);
                    }
                }
            }
        });
        _btNext10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(player != null)
                {
                    int currentPosition = player.getCurrentPosition();
                    if (currentPosition + 10000 < player.getDuration()) {
                        player.seekTo(currentPosition + 10000);
                    } else {
                        player.seekTo(player.getDuration());
                    }
                }
            }
        });
    }

    class BookInfo
    {
        public String name;
        public String link;
        public String description;
        public String author;
        public String cover_l_url;
        public String cover_s_url;
        public String pub_nm;
        public String isbn13;
        public String toString()
        {
            return name;
        }
    }
    String[] g_arr_list = new String[10];

    public class ListViewAdapter extends BaseAdapter {
        private ArrayList<BookInfo> listItem = new ArrayList<>();
        private Context _context;
        public ListViewAdapter(Context context)
        {
            _context = context;
        }

        public void clear()
        {
            listItem.clear();
        }

        public void add(BookInfo info)
        {
            listItem.add(info);
        }
        @Override
        public int getCount() {
            return listItem.size() ;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final Context context = parent.getContext();

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_item, parent, false);
            }

            ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView) ;
            TextView titleTextView = (TextView) convertView.findViewById(R.id.tvTitle) ;
            TextView tvAuthor = (TextView)convertView.findViewById(R.id.tvAuthor);
            TextView tvPub = (TextView)convertView.findViewById(R.id.tvPub);
            //TextView descTextView = (TextView) convertView.findViewById(R.id.textView2) ;

            // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
            BookInfo info = listItem.get(position);

            // 아이템 내 각 위젯에 데이터 반영
            //iconImageView.setImageDrawable(listViewItem.getIcon());
            //titleTextView.setText(listViewItem.getTitle());
            //descTextView.setText(listViewItem.getDesc());
            titleTextView.setText(info.name);
            tvAuthor.setText("저자 " + info.author);
            tvPub.setText("출판사 " + info.pub_nm);
            Glide.with(_context).load(info.cover_s_url).into(iconImageView);
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position ;
        }

        @Override
        public Object getItem(int position) {
            return listItem.get(position) ;
        }
    }

    MediaPlayer player = null;

    private void search()
    {
        EditText et = (EditText)findViewById(R.id.etSearch);


        RequestQueue queue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        String strUrl = "https://apis.daum.net/search/book?apikey=5aa28d23611f5d9659222761b65a4ed1&sort=accu&q=" + URLEncoder.encode(et.getText().toString())+  "&output=json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,  strUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray arr = response.getJSONObject("channel").getJSONArray("item");
                    ArrayList<BookInfo> items = new ArrayList<BookInfo>();
                    _adapter.clear();
                    for(int i = 0; i < arr.length(); i++)
                    {
                        JSONObject obj = arr.getJSONObject(i);
                        Log.d("test1", obj.getString("title"));
                        BookInfo info = new BookInfo();
                        info.name = obj.getString("title");
                        info.author = obj.getString("author");
                        info.link = obj.getString("link");
                        info.cover_l_url = obj.getString("cover_l_url");
                        info.cover_s_url = obj.getString("cover_s_url");
                        info.pub_nm = obj.getString("pub_nm");
                        info.description = obj.getString("description");
                        info.isbn13 = obj.getString("isbn13");
                        //items.add(info);
                        _adapter.add(info);
                    }
                    ListView lv = (ListView)findViewById(R.id.listView);

                    _adapter.notifyDataSetChanged();
                    //ArrayAdapter ad = new ArrayAdapter(Main2Activity.this, android.R.layout.simple_list_item_1, items);
                    //lv.setAdapter(ad);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            BookInfo info = (BookInfo)adapterView.getItemAtPosition(i);
                            Log.d("test1", info.name + " " + info.description);
                            layoutPlay.setVisibility(View.VISIBLE);
                            ImageView v = (ImageView)findViewById(R.id.imageCover);
                            Glide.with(AudioBookActivity.this).load(info.cover_l_url).into(v);
                            layoutSearch.setVisibility(View.GONE);
                            if(player != null)
                            {
                                player.stop();
                                player.release();
                            }
                            try {
                                player = MediaPlayer.create(AudioBookActivity.this, AudioBookActivity.this.getResources().getIdentifier("i" + info.isbn13, "raw", getPackageName()));
                            } catch(Resources.NotFoundException e)
                            {
                                player = MediaPlayer.create(AudioBookActivity.this, R.raw.audiobook);
                            }

                            player.start();
                        }
                    });
                } catch(JSONException e)
                {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(request);
    }

    @Override
    public void onBackPressed() {
        if(layoutPlay.getVisibility() == View.VISIBLE)
        {
            layoutPlay.setVisibility(View.GONE);
            layoutSearch.setVisibility(View.VISIBLE);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if (result.getContents() != null) {
                EditText et = (EditText) findViewById(R.id.etSearch);
                et.setText(result.getContents());
                search();
            }
            //Log.d("test1", result.getContents());
            //Log.d("test1", tv.toString());
        /*if(result != null) {
            if(result.getContents() == null) {
                //Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                tv.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }*/
        }
    }
}