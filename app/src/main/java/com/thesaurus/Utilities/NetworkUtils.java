package com.thesaurus.Utilities;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by JHK on 2017. 6. 5..
 */

/*
 * 이 클래스는 다음 검색 api를 이용하여 apiKey 값과 쿼리 내용, 기타 파라미터를 합친 요청 주소를 만드는 클래스
 * 여기서 요청 주소를 만들어 요청하게 한 다음, OpenBookJsonUtils 클래스로 가서 Json 형태의 데이터들을
 * 보기 좋게 저장한다.
 */
public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    // api 키 값
    private static final String apiKey = "63e090aa874b7ab9946838734c81077b";

    // 다음 책 검색 api 요청 주소
    private static final String REQUEST_URL =
            "https://apis.daum.net/search/book";


    private static final String BOOK_BASE_URL = REQUEST_URL;

    // 기본 검색 조건
    // 20개 데이터, 판매량순, json 형태로 데이터 받아오기
    private static final int resultBooks = 20;
    private static final int startBooks = 1;
    private static final String sortBooks = "popular";
    private static final String outputBooks = "json";

    final static String APIKEY_PARAM = "apikey";

    // 다음 검색 api 요청 변수
    // 상세 설명은 다음 책 검색 api 사이트 참조
    final static String QUERY_PARAM = "q";
    final static String RESULT_PARAM = "result";
    final static String ADVANCE_PARAM = "advance";
    final static String PAGENO_PARAM = "pageno";
    final static String SORT_PARAM = "sort";
    final static String SEARCHTYPE_PARAM = "searchType";
    final static String CATE_ID_PARAM = "cate_id";
    final static String OUTPUT_PARAM = "output";

    /**
     * URL 주소를 다음 검색 api 요청 주소 형태에 맞게 만드는 작업. 찾는 검색어가 "q" 값에 집어놓고
     * 기본 검색 조건에 맞춰서 검색하여 필요한 데이터들을 받을 수 있게 한다
     *
     * @param searchQuery 찾고자 하는 검색어
     * @param searchType 전체, 제목 또는 저자에 따라 찾을지 결정하는 변수
     * @param sortBooks 판매량순, 정확도순, 발행일순에 따라 찾을지 정렬할지 결정하는 변수
     * @return 다음 검색 api 맞는 요청 주소
     */
    public static URL buildUrl(String searchQuery, String searchType, String sortBooks) {
        // 요청 변수를 url 요청 주소에 추가하는 과정
        Uri builtUri = Uri.parse(BOOK_BASE_URL).buildUpon()
                .appendQueryParameter(APIKEY_PARAM, apiKey)
                .appendQueryParameter(QUERY_PARAM, searchQuery)
                .appendQueryParameter(SEARCHTYPE_PARAM, searchType)
                .appendQueryParameter(RESULT_PARAM, Integer.toString(resultBooks))
                .appendQueryParameter(SORT_PARAM, sortBooks)
                .appendQueryParameter(OUTPUT_PARAM, outputBooks)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * URL 주소를 다음 검색 api 요청 주소 형태에 맞게 만드는 작업. 찾는 검색어가 "q" 값에 집어놓고
     * 기본 검색 조건에 맞춰서 검색하여 필요한 데이터들을 받을 수 있게 한다
     *
     * @param searchQuery 찾고자 하는 검색어
     * @return 다음 검색 api 맞는 요청 주소
     */
    public static URL buildUrl(String searchQuery) {
        // 요청 변수를 url 요청 주소에 추가하는 과정
        Uri builtUri = Uri.parse(BOOK_BASE_URL).buildUpon()
                .appendQueryParameter(APIKEY_PARAM, apiKey)
                .appendQueryParameter(QUERY_PARAM, searchQuery)
                .appendQueryParameter(RESULT_PARAM, Integer.toString(resultBooks))
                .appendQueryParameter(SORT_PARAM, sortBooks)
                .appendQueryParameter(OUTPUT_PARAM, outputBooks)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * 이 메서드는 HTTP 응답의 결과를 리턴한다
     *
     * @param url HTTP 응답을 받기 위한 URL 주소
     * @return HTTP 응답의 내용물
     * @throws IOException 네트워킹과 스트리밍에 관련 오류
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");

        try {
            int responseCode = urlConnection.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            return response.toString();
        } finally {
            urlConnection.disconnect();
        }
    }
}
