package com.example.lunarlandergame;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/*
* Listview에 Data를 출력하려면 아래 세 가지의 순서가 필요하다.

1. 리스트뷰에 표시할 data
2. data를 리스트뷰에 표시하는 Adapter
3. 화면에 리스트를 표시해주는 리스트뷰

즉, Adapter는 리스트뷰와 data 사이를 control 하는 존재라고 생각하면 된다. 리스트뷰에 내용을 나타내기 위해서는 꼭 필요하다.


출처: https://hyojjeong.tistory.com/entry/ListViewCursorAdapter-사용법 [씬나는 하루하루]
*
*/

public class rankingActivity extends AppCompatActivity {
    SingletonDB singletonDB;

    //DBHelper helper;
    //SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.ranking);
        singletonDB = SingletonDB.getInstance(this);
        //helper=new DBHelper(this);

        /*try{
            db=helper.getWritableDatabase();
        }catch(SQLiteException ex){
            db=helper.getReadableDatabase();
        }*/

        Cursor cursor = singletonDB.db.rawQuery("SELECT * FROM rankingtable order by score DESC",new String[]{});
        //startManagingCursor(cursor);
        if (cursor != null && cursor.getCount() != 0) {
            String[] from = {"_id","username", "score"};
            int[] to = {R.id.pk, R.id.eachname, R.id.eachscore};
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.listitem, cursor, from, to);
            ListView list = (ListView) findViewById(R.id.list);
            list.setAdapter(adapter);
        }
        else{
            Log.d("jinjin","랭킹 데이터 없음");
        }
    }
}
