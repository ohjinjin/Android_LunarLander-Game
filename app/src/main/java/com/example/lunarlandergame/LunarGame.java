package com.example.lunarlandergame;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;

public class LunarGame extends SurfaceView implements SurfaceHolder.Callback {
    float roll = 0;
    //DBHelper helper;
    //SQLiteDatabase db;
    private int currSpriteImg = R.drawable.space;
    private int space_state=1;

    public Handler lunarHandler;
    public SurfaceHolder lunarSurfaceHoler;
    public Context lunarContext;

    private int mCanvasHeight = 1;
    private int mCanvasWidth = 1;

    private Paint paint;
    //private Paint linePaint;

    private int startX;
    private int stopX;
    private int startY;

    private Bitmap lunarBackground;

    private int fuel;
    private int score;
    private int time;
    private double mx=0;
    private double my=0;
    private double dx,dy;
    private LunarGame mview;
    private Context mcontext;
    private boolean lunarRun = false;

    public LunarGame(Context context) {
        super(context);
    }

    public LunarGame(final Context context, AttributeSet attrs) {
        super(context, attrs);
        final SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        mcontext = context;
        thread = new LunarThread(this, holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
            }
        });
        setFocusable(true);
    }

    class LunarThread extends Thread {

        public LunarThread(LunarGame view, SurfaceHolder surfaceHolder, Context context,Handler handler){
            lunarSurfaceHoler=surfaceHolder;
            lunarHandler=handler;
            lunarContext=context;
            mview = view;

            final Resources res=context.getResources();
            lunarBackground=BitmapFactory.decodeResource(res,R.drawable.bgimg);

            //아래 linePaint는 테스트를 위한 용도
            //linePaint=new Paint();
            //linePaint.setAntiAlias(true);
            //linePaint.setStrokeWidth(10);
            //linePaint.setARGB(255,58,144,158);

            paint=new Paint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(25);
        }

        public void doStart(){
            synchronized (lunarSurfaceHoler) {
                fuel = 500;
                space_state = 1;
                mx = 0;
                my = 2;
                time = 0;
                dx = 0;
                dy = 0;
            }
        }

        @Override
        public void run() {
            while (lunarRun) {
                Canvas c = null;
                try {
                    c = lunarSurfaceHoler.lockCanvas();
                    synchronized (lunarSurfaceHoler) {
                        onDraw(c);
                    }
                } catch(Exception e){

                }
                finally {
                    if (c != null) {
                        lunarSurfaceHoler.unlockCanvasAndPost(c);

                    }
                }
            }
            handler.sendEmptyMessage(0);    // 작업스레드에서 핸들러를 이용하여 처리기로 메세지를 전달하여 기본 UI에 연결하는 것
        }

        public void setRunning(boolean x){
            lunarRun=x;
        }

        public void setSurfaceSize(int width, int height) {
            synchronized (lunarSurfaceHoler) {
                mCanvasWidth = width;
                mCanvasHeight = height;

                startX = (mCanvasWidth / 10) * 7;
                stopX = (mCanvasWidth / 10) * 9;

                startY=(mCanvasHeight/10)*8;

                lunarBackground = Bitmap.createScaledBitmap(lunarBackground, width, height, true);
            }
        }

        public void pause(){
            thread.interrupt();
        }

        protected void onDraw(Canvas canvas){
            canvas.drawBitmap(lunarBackground, 0, 0, null);

            canvas.drawText("SCORE: "+score,10,60,paint);
            canvas.drawText("FUEL: "+fuel,10,90,paint);
            canvas.drawText("TIME: "+time,10,120,paint);

            //canvas.drawLine(startX, startY, stopX, startY, linePaint);

            move();
            check();

            // 현재 우주선의 이미지를 비트맵으로 얻어와 회전 변환행렬을 적용시킨 후 캔버스에 그리기
            Bitmap b = rotateImage(BitmapFactory.decodeResource(getResources(),currSpriteImg),roll);
            Bitmap resize_bitmap = Bitmap.createScaledBitmap(b, 150, 150, true);
            canvas.drawBitmap(resize_bitmap,(int)mx,(int)my,null);

        }


        public void check(){
            if(fuel<=0){
                currSpriteImg = R.drawable.fire;
                space_state = 3;
                score=0;
                //Log.d("dd", "실패"); // LOSE
                this.setRunning(false);
            }
            else {
                if (my < 0 || my > startY - 120 || mx < 0 || mx > mCanvasWidth) {
                    currSpriteImg = R.drawable.fire; // 우주선 폭발
                    space_state = 3;
                    score=0;
                    //Log.d("dd", "실패"); // LOSE
                    this.setRunning(false);
                }
                else {
                    if (my >= startY - 150 && my <= startY - 130) {
                        if (mx >= startX-50 && mx <= stopX - 100) {
                            if ((space_state == 1) && (roll > -0.4 && roll < 0.4)) {    // 착륙 시 우주선의 기울기가 절댓값 0.4보다 작을경우에만 성공
                                space_state = 3;
                                currSpriteImg = R.drawable.space;
                                //Log.d("dd", "성공"); // WIN
                                this.setRunning(false);
                            }
                        }
                        else {
                            currSpriteImg = R.drawable.fire;    // 우주선 폭발
                            space_state = 3;
                            score=0;
                            //Log.d("dd", "실패"); // LOSE
                            this.setRunning(false);
                        }
                    }
                }
            }
        }

        public void move(){
            // 시간을 계속 재고 있음
            if(space_state==3)
                time+=0;
            else
                time++;

            // 추후 점수 계산을 위한 용도
            score=fuel-(int)(time/100);

            switch(space_state){
                // default == 1
                case 1:
                    dx = 10;
                    dy = 10;
                    break;
                // 부스터 쓴 경우 방향에 움직임이 종속되도록 함
                case 2:
                    if (roll<0){
                        dx = -10;
                    }
                    else{
                        dx = 10;
                    }
                    dy = -11;
                    fuel-=5;
                    break;
                // 끝났을 때
                case 3:
                    dx = 0;
                    dy =0;
                    fuel-=0;
                    break;
            }

            // 본격적인 move
            mx += dx;
            my += dy;

            // JVM이 시간이 날 때 화면을 다시 그리도록
            invalidate();
        }
    }

    private LunarThread thread;

    public LunarThread getThread() {
        return thread;
    }

    public void surfaceCreated(SurfaceHolder holder){
        //Log.d("jinjin","surface created!!!!");
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        thread.setSurfaceSize(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        //Log.d("jinjin","surface destoryed!!!!");
        boolean retry=true;
        thread.setRunning(false);

        /*while (retry){
            try{
                thread.join();
                retry=false;
            }catch (InterruptedException e){

            }
        }*/
    }

    // 수신처에 핸들러 객체가 있어야 함
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==0) {
                AlertDialog.Builder adb = new AlertDialog.Builder(mcontext);
                adb.setTitle("점수 기록하기");
                adb.setMessage("사용자 이름을 입력하세요");

                final EditText editText = new EditText(mcontext);
                adb.setView(editText);

                adb.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    @Override
                    public  void onClick(DialogInterface dialog, int which){
                        //Log.d("jinjin","Yes btn clicked");
                        String username = editText.getText().toString();
                        //Log.d("jinjin", (SingletonDB.rowCount+1)+"");
                        GameActivity.singletonDB.db.execSQL("INSERT INTO rankingtable VALUES ('"+(SingletonDB.rowCount+1)+"','"+username+"','"+score+"');");

                        dialog.dismiss();
                        GameActivity.singletonDB.refreshRowCount();
                        GameActivity.activity.finish();
                    }
                });

                // 취소 버튼 설정
                adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Log.v("jinjin","No Btn Click");
                        dialog.dismiss();     //닫기
                        // Event
                        GameActivity.activity.finish();
                    }
                });

                AlertDialog ad = adb.create();
                ad.show();
            }
            }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int keyAction = event.getAction();

        switch (keyAction){
            case MotionEvent.ACTION_DOWN:
                currSpriteImg = R.drawable.space_burn;
                space_state = 2;
                break;
            case MotionEvent.ACTION_UP:
                currSpriteImg = R.drawable.space;
                space_state = 1;
                break;
        }
        return true;
    }

    public void setRoll(float roll){
        this.roll = roll;
    }

    public Bitmap rotateImage(Bitmap src, float degree) {

        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }
}
