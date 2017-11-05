package com.thesaurus.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thesaurus.Data.Book;
import com.thesaurus.R;

import java.util.List;

/**
 * Created by JHK on 2017. 5. 30..
 */

/** 
 * 이 BookAdapter는 검색하여 얻은 도서 데이터들을 리스트 형태로 나오도록 설정하게 도와주는 어댑터이다 
 * RecyclerView는 ListView 보다 더 효율적으로 메모리를 사용하면서 데이터들을 보기 편하게 보여주는 뷰 
 * 이 뷰를 사용하기 위해서 어댑터가 어떤 데이터들을 보여줘야하는지 도와주는 역할이다 
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookAdapterViewHolder> {
    private List<Book> mBookData;

    private Context context;

    private final BookAdapterOnClickHandler mClickHandler;

    public interface BookAdapterOnClickHandler {
        void onClick(Book dataOfBook);
    }

    // 한 도서가 선택하면 기능을 실행할 수 있게 적용한 것 
    // onClick 함수가 실행할 수 있게 도와준다고 생각하면 된다.
    public BookAdapter(BookAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * 화면에 어떻게 보여줘야 할지 book_list_item 레이아웃의 뷰들을 불러온다
     */
    public class BookAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mBookTitleTextView;
        public final TextView mBookAuthorPublisherDateTextView;
        public final ImageView mBookImageView;

        public BookAdapterViewHolder(View view) {
            super(view);
            mBookTitleTextView = (TextView) view.findViewById(R.id.tv_book_title);
            mBookAuthorPublisherDateTextView = (TextView) view.findViewById(R.id.tv_book_author_publisher_pubdate);
            mBookImageView = (ImageView) view.findViewById(R.id.iv_book_image);
            view.setOnClickListener(this);
        }
        // onClick을 통해 선택한 도서의 정보들을 가지고 메서드를 실행하도록 설정
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Book dataOfBook = mBookData.get(adapterPosition);
            mClickHandler.onClick(dataOfBook);
        }
    }

    /**
     * ViewHolder가 새로 만들 때마다 이 메서드가 실행된다. RecyclerView 가 데이터들을 뿌려질 때이다.
     * 화면이 넘어가면 데이터 목록들을 만들도록 하는 작업이다. 즉 화면에 가득 채운 데이터들을 먼저 만들어지고
     * 화면을 스크롤할 때 마다 그때 마다 새로운 데이터 목록들을 만드는 역할이다.
     *
     * @param viewGroup 뷰 홀더가 담겨져 있는 뷰그룹
     * @param viewType  지금 우리한테 필요 없는 매개변수. 혹시 모르니깐 지우지 말 것.
     * @return 각 데이터들이 담겨져 있는 뷰를 갖고 있는 새로운 BookCardAdapter
     */
    @Override
    public BookAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        // 어떤 레이아웃에 뿌려질지 결정한다
        int layoutIdForListItem = R.layout.book_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new BookAdapterViewHolder(view);
    }

    /**
     * RecyclerView 가 특정 위치에 데이터를 보여주고 싶을 때 이 메서드를 불러온다. 특정 위치의 값을 받아서 ViewHolder 의
     * 데이터가 그 위치에 나타나도록 설정한다.
     *
     * @param bookAdapterViewHolder 특정 위치에 있는 데이터가 업데이트 되도록 하는 ViewHolder
     * @param position              어댑터가 갖고 있는 데이터 리스트 중에 있는 특정 위치 값
     */
    @Override
    public void onBindViewHolder(BookAdapterViewHolder bookAdapterViewHolder, int position) {
        String publisher_date = "";
        String authorName = "";

        Book book = mBookData.get(position);

        // 책의 저자 값이 없으면 공백으로, 있으면 저자 다음에 "저" 글자 추가한다
        // 책의 저자 | 출판사 | 발행일    이런식으로 출력하기 위한 과정
        if(book.getAuthor() == null || book.getAuthor().equals("")) {
            authorName = "";
        } else {
            authorName = book.getAuthor() + " 저 | ";
        }

        if (book.getCoverSmall() == null || book.getCoverSmall().equals("")) {
            // 신간도서일 경우 내부 저장되어 있는 책 표지를 불러오도록 설정
            if (book.getCoverInt() != 0) {
                bookAdapterViewHolder.mBookImageView.setImageResource(book.getCoverInt());
            } else {
                // 책 표지가 없으면 no_image 그림 표시하도록 설정
                Glide.with(context)
                        .load(R.drawable.no_image)
                        .into(bookAdapterViewHolder.mBookImageView);
            }
        } else {
            // 검색하여 얻은 도서 정보들일 경우 책 표지의 작은 사이즈를 그림으로 설정
            Glide.with(context)
                    .load(book.getCoverSmall())
                    .into(bookAdapterViewHolder.mBookImageView);
        }

        // 발행일을 2017.06.05 형식으로 출력하게 설정
        String unformattedDate = book.getPubdate();
        String formatYear = unformattedDate.substring(0,4);
        String formatMonth = unformattedDate.substring(4,6);
        String formatDay = unformattedDate.substring(6,8);
        String formattedDate = formatYear + "." + formatMonth + "." + formatDay;

        // 저자명 저 | 출판사 | 발행일
        publisher_date = authorName + book.getPublisher() + " | " + formattedDate;

        bookAdapterViewHolder.mBookTitleTextView.setText(book.getTitle());
        bookAdapterViewHolder.mBookAuthorPublisherDateTextView.setText(publisher_date);
    }

    /**
     * 화면에 몇개의 데이터들을 보여줘야 하는지 값을 리턴하는 메서드.
     *
     * @return 도서 데이터들의 갯수
     */
    @Override
    public int getItemCount() {
        if (null == mBookData) return 0;
        return mBookData.size();
    }

    /**
     * 새로운 도서 데이터가 List 에 추가되면 변경되었다는 정보를 알려주도록 하는 메서드
     * 새로운 도서 데이터 list 를 받았지만 새로운 list 를 만들기 싫을 때 주로 쓴다
     *
     * @param bookData 화면에 뿌려질 새로운 도서 List
     */
    public void setBookData(List<Book> bookData) {
        mBookData = bookData;
        notifyDataSetChanged();
    }
}
