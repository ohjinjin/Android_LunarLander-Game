package com.example.lunarlandergame;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public class GameActivity extends AppCompatActivity implements SensorEventListener {
    public static Activity activity = null; // 좋지 않은 방법같다.
    SensorManager mSensorManager;
    //SensorEventListener gyroListener;
    Sensor mGyroSensor;
    //CompassView compass;
    //Roll and Pitch
    private double pitch;
    private double roll;
    private double yaw;

    //timestamp and dt
    private double timestamp;
    private double dt;
    public static SingletonDB singletonDB;
    // for radian -> dgree
    private double RAD2DGR = 180 / Math.PI;
    private static final float NS2S = 1.0f/1000000000.0f;

    private LunarGame game;
    private LunarGame.LunarThread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        singletonDB = SingletonDB.getInstance(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mGyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        game=(LunarGame)findViewById(R.id.lunarGame);
        thread=game.getThread();
        thread.setRunning(true);
        activity = this;

        thread.doStart();
        /*try{
            thread.join();  // 작업스레드의 종료를 대기
        } catch (InterruptedException e){
            e.printStackTrace();
        } finally {
            finish();
        }*/
    }

    @Override
    protected void onResume(){
        super.onResume();
        mSensorManager.registerListener(this, mGyroSensor, SensorManager.SENSOR_DELAY_UI);
        if (game.getThread() != null){
            if (game.getThread().getState() == Thread.State.TERMINATED){
                finish();
            }
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        game.getThread().pause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onStop(){
        super.onStop();
        game.getThread().setRunning(false);
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        dt = (event.timestamp - timestamp) * NS2S;
        timestamp = event.timestamp;
        if (dt - timestamp*NS2S != 0) {

            /* 각속도 성분을 적분 -> 회전각(pitch, roll)으로 변환.
             * 여기까지의 pitch, roll의 단위는 '라디안'이다.
             * SO 아래 로그 출력부분에서 멤버변수 'RAD2DGR'를 곱해주어 degree로 변환해줌.  */
            roll = roll + event.values[0]*dt;
            game.setRoll((float)(roll*RAD2DGR));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){
    }
}

