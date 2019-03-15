package com.example.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    private Button b1;
    private Button b3;
    private FrameLayout but;
    private RelativeLayout GameButtons;
    private RelativeLayout GameButtons1;
    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private GameView gameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new GameView(this);
        b1 = new Button(this);
        b1.setText("back");
        //b1.setX(300);
        //b1.setY(800);
        //b1.setWidth(10);
        //b1.setHeight(10);
        GameButtons = new RelativeLayout(this);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameView.updateposition();
            }
        });

        GameButtons1 = new RelativeLayout(this);
        b3 = new Button(this);
        b3.setText("front");
        //b3.setX(300);
        //b3.setY(800);
        //b1.setWidth(10);
        //b1.setHeight(10);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameView.updatepositionfront();
            }
        });

        RelativeLayout.LayoutParams b2 = new RelativeLayout.LayoutParams(
                                     RelativeLayout.LayoutParams.WRAP_CONTENT,
                              RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams b4 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                   RelativeLayout.LayoutParams.FILL_PARENT,
                                    RelativeLayout.LayoutParams.FILL_PARENT);

        GameButtons.addView(b1);
        GameButtons1.addView(b3);
        b2.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        b2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        b1.setLayoutParams(b2);

        b4.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        b4.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        b3.setLayoutParams(b4);

        but = new FrameLayout(this);
        but.addView(gameView);
        but.addView(GameButtons);
        but.addView(GameButtons1);

        setContentView(but);
        //setContentView(but);


    }
    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }
    class GameView extends SurfaceView implements Runnable {
        private Thread gameThread;
        private SurfaceHolder ourHolder;
        private volatile boolean playing;
        private Canvas canvas;
        private Bitmap bitmapRunningMan;
        private Bitmap bitmapRunningMon;
        private Bitmap bitmapRunningMan1;

        private boolean isMoving;
        private boolean back = false;
        private boolean front = true;
        private float runSpeedPerSecond = 100;
        private float manXPos = 10, manYPos = 600;
        private float manXPos1 = 600, manYPos1 = 600;
        private float manXPos2 = 600, manYPos2 = 600;
        private int frameWidth = 200, frameHeight = 265;
        private int frameWidth1 = 200, frameHeight1 = 265;
        private int frameCount = 8;
        private int frameCount1 = 20;
        private int currentFrame = 0;
        private int currentFrame1 = 0;
        private long fps;
        private long timeThisFrame;
        private long lastFrameChangeTime = 0;
        private int frameLengthInMillisecond = 100;
        private Rect frameToDraw = new Rect(0, 0, frameWidth, frameHeight);
        private RectF whereToDraw = new RectF(manXPos, manYPos, manXPos + frameWidth, frameHeight);
        private Rect frameToDraw2 = new Rect(0, 0, frameWidth, frameHeight);
        private RectF whereToDraw2 = new RectF(manXPos, manYPos, manXPos + frameWidth, frameHeight);
        private Rect frameToDraw1 = new Rect(0, 0, frameWidth1, frameHeight1);
        private RectF whereToDraw1 = new RectF(manXPos1, manYPos1, manXPos1 + frameWidth1, frameHeight1);

        public GameView(Context context) {
            super(context);
            ourHolder = getHolder();
            bitmapRunningMan = BitmapFactory.decodeResource(getResources(), R.drawable.spritewalk1);
            bitmapRunningMan = Bitmap.createScaledBitmap(bitmapRunningMan, frameWidth * frameCount, frameHeight, false);
            bitmapRunningMon = BitmapFactory.decodeResource(getResources(), R.drawable.finalmon);
            bitmapRunningMon = Bitmap.createScaledBitmap(bitmapRunningMon, frameWidth1 * frameCount1, frameHeight1, false);
            bitmapRunningMan1 = BitmapFactory.decodeResource(getResources(), R.drawable.spritewalkrev);
            bitmapRunningMan1 = Bitmap.createScaledBitmap(bitmapRunningMan1, frameWidth * frameCount, frameHeight, false);

        }
        @Override
        public void run() {
            while (playing) {
                long startFrameTime = System.currentTimeMillis();
                update();
                draw();
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = 1000 / timeThisFrame;
                }
            }
        }
        public void update() {
            if (isMoving) {
                if(front){
                manXPos = manXPos + runSpeedPerSecond / fps;
                //manXPos1 = manXPos1 - runSpeedPerSecond / fps;
                if (manXPos > getWidth()) {
                    //manYPos += (int) frameHeight;
                    manXPos = getWidth();
                }
                if (manYPos + frameHeight > getHeight()) {
                   // manYPos = 10;
                }

                if (manXPos1 < getWidth()/3) {
                    //manYPos += (int) frameHeight;
                    manXPos1 = 600;
                }
                if (manYPos1 + frameHeight1 > getHeight()) {
                    // manYPos = 10;
                }}
                if(back){
                    manXPos = manXPos - runSpeedPerSecond / fps;
                    if (manXPos < 0) {
                        //manYPos += (int) frameHeight;
                        manXPos = 0;
                    }



                    if (manXPos1 < getWidth()/3) {
                        //manYPos += (int) frameHeight;
                        manXPos1 = 600;
                    }

                }
            }
        }

        public void updateposition(){
            //manXPos = 10;
            back = true;
            front = false;
            //canvas.drawBitmap(bitmapRunningMan1, frameToDraw, whereToDraw, null);
        }

        public void updatepositionfront(){
            //manXPos = 10;
            back = false;
            front = true;
            //canvas.drawBitmap(bitmapRunningMan1, frameToDraw, whereToDraw, null);
        }


        public void manageCurrentFrame() {
            long time = System.currentTimeMillis();
            if (isMoving) {
                if (time > lastFrameChangeTime + frameLengthInMillisecond) {
                    lastFrameChangeTime = time;
                    currentFrame++;
                    if (currentFrame >= frameCount) {
                        currentFrame = 0;
                    }
                    currentFrame1++;
                    if (currentFrame1 >= frameCount1) {
                        currentFrame1 = 0;
                    }
                }

            }
            frameToDraw.left = currentFrame * frameWidth;
            frameToDraw.right = frameToDraw.left + frameWidth;
            frameToDraw1.left = currentFrame1 * frameWidth1;
            frameToDraw1.right = frameToDraw1.left + frameWidth1;
        }
        public void draw() {
            if (ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();
                canvas.drawColor(Color.WHITE);
                whereToDraw.set((int) manXPos, (int) manYPos, (int) manXPos + frameWidth, (int) manYPos + frameHeight);
                whereToDraw1.set((int) manXPos1, (int) manYPos1, (int) manXPos1 + frameWidth1, (int) manYPos1 + frameHeight1);
                manageCurrentFrame();
                if(front) {
                    canvas.drawBitmap(bitmapRunningMan, frameToDraw, whereToDraw, null);
                    canvas.drawBitmap(bitmapRunningMon, frameToDraw1, whereToDraw1, null);
                }

                if(back){
                    canvas.drawBitmap(bitmapRunningMan1, frameToDraw, whereToDraw, null);
                    canvas.drawBitmap(bitmapRunningMon, frameToDraw1, whereToDraw1, null);
                }
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }
        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch(InterruptedException e) {
                Log.e("ERR", "Joining Thread");
            }
        }
        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN :
                    isMoving = !isMoving;
                    break;
            }
            return true;
        }


    }
}
