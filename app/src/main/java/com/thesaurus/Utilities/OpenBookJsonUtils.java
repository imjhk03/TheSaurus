package com.thesaurus.Utilities;

import android.content.Context;

import com.thesaurus.Data.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JHK on 2017. 6. 5..
 */

public class OpenBookJsonUtils {
    /**
     * 이 메서드는 요청 주소에서 응답 받은 JSON 값을 List<Books> 형태에 저장하도록 파싱하는 작업이다.
     *
     * @param bookJsonStr 요청 주소에서 응답 받은 JSON
     *
     * @return 도서의 정보들이 저장되어 있는 List
     *
     * @throws JSONException JSON 데이터들이 제대로 파싱이 못 될 때
     */
    public static List<Book> getSimpleBookStringsFromJson(Context context, String bookJsonStr)
            throws JSONException {

        final String OBM_CHANNEL = "channel";

        // 다음 책 검색 JSON 안에 있는 item 배열에 책의 정보들이 담겨져 있다
        final String OBM_ITEM = "item";

        // 아래 변수는 출력 결과와 관련되어 있는 변수
        // 자세한 정보는 다음 책 검색 api 참조
        final String OBM_TITLE = "title";
        final String OBM_AUTHOR = "author";
        final String OBM_CATEGORY = "category";
        final String OBM_COVER_SMALL = "cover_s_url";
        final String OBM_COVER_LARGE = "cover_l_url";
        final String OBM_PUBLISHER = "pub_nm";
        final String OBM_PUBDATE = "pub_date";
        final String OBM_ISBN13 = "isbn13";
        final String OBM_DESCRIPTION = "description";;

        // 데이터들을 저장하도록 List 변수 선언
        List<Book> bookList = new ArrayList<>();

        JSONObject resultBookJson = new JSONObject(bookJsonStr);
        // channel 이름의 JSON 파싱
        JSONObject bookJson = resultBookJson.getJSONObject(OBM_CHANNEL);
        // channel JSON 안에 있는 item 배열 파싱
        JSONArray bookArray = bookJson.getJSONArray(OBM_ITEM);

        for (int i = 0; i < bookArray.length(); i++) {
            /* 수집할 정보들 */
            String title;
            String author;
            String category;
            String coverLarge;
            String coverSmall;
            String publisher;
            String pubdate;
            String isbn13;
            String description;

            /* item 배열 안에 있는 책 데이터 JSON */
            JSONObject oneBook = bookArray.getJSONObject(i);

            /* 수집할 정보들을 변수에 담는다 */
            title = oneBook.getString(OBM_TITLE);
            author = oneBook.getString(OBM_AUTHOR);
            category = oneBook.getString(OBM_CATEGORY);
            coverLarge = oneBook.getString(OBM_COVER_LARGE);
            coverSmall = oneBook.getString(OBM_COVER_SMALL);
            publisher = oneBook.getString(OBM_PUBLISHER);
            isbn13 = oneBook.getString(OBM_ISBN13);
            description = oneBook.getString(OBM_DESCRIPTION);
            pubdate = oneBook.getString(OBM_PUBDATE);

            /* List<Book> 에 담기 위해서 Book 클래스 형태에 맞춰서 정보들을 담는다 */
            Book newBook = new Book();
            newBook.setTitle(title);
            newBook.setAuthor(author);
            newBook.setCategory(category);
            newBook.setCoverLarge(coverLarge);
            newBook.setCoverSmall(coverSmall);
            newBook.setPublisher(publisher);
            newBook.setIsbn13(isbn13);
            newBook.setDescription(description);
            newBook.setPubdate(pubdate);

            /* List<Book> 에 저장한 Book 데이터들을 추가한다 */
            bookList.add(i, newBook);

            // parsedBookData[i] = title + " - " + image + " - " + author + " - " + publisher + " - " + isbn + " - " + description + " - " + pubdate;
        }

        return bookList;
    }
}
