package com.example.lunarlandergame;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {
    SingletonDB singletonDB;
    //DBHelper helper;
    //SQLiteDatabase db;

    private static MediaPlayer music_player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        singletonDB = SingletonDB.getInstance(this);    // 처음 앱이 실행될 때 싱글톤 DB 인스턴스화
        singletonDB.refreshRowCount();  // 현재 행의 개수에 대한 제어변수
        //Log.d("jinjin",singletonDB.rowCount+"ccccccccc");

        music_player = MediaPlayer.create(this,R.raw.background_music);
        music_player.setLooping(true);
        music_player.start();

        //helper=new DBHelper(this);  // singletone으로 나중에 바꾸기
/*
        try{
            //db=helper.getWritableDatabase();
            singletonDB = SingletonDB.getInstance(this);
        }catch(SQLiteException ex){
            db=helper.getReadableDatabase();
        }
*/
        //id=(EditText) findViewById(R.id.id);
        //pw=(EditText) findViewById(R.id.pw);
    }

    /* 메인화면에서 play 버튼을 눌렀을 경우 */
    public void play(View v){
        Intent intent=new Intent(MainActivity.this,GameActivity.class);
        music_player.stop();
        music_player = MediaPlayer.create(this, R.raw.game_music);
        music_player.setLooping(true);
        music_player.start();

        startActivity(intent);
    }

    /* 메인화면에서 ranking 버튼을 눌렀을 경우 */
    public void ranking(View v){
        Intent intent = new Intent(MainActivity.this, rankingActivity.class);
        startActivity(intent);
    }

    /* 메인화면에서 instruction 버튼을 눌렀을 경우 */
    public void instruction(View v){
        Intent intent=new Intent(MainActivity.this, InfoActivity.class);
        startActivity(intent);
    }

/*
    @Override
    public void onPause(){
        super.onPause();
        music_player.stop();
    }
*/
    @Override
    public void onDestroy(){
        super.onDestroy();
        music_player.stop();
    }
}