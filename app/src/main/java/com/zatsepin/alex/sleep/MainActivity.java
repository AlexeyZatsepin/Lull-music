package com.zatsepin.alex.sleep;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eralp.circleprogressview.CircleProgressView;
import com.eralp.circleprogressview.ProgressAnimationListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView timer;
    private EditText et_h;
    private EditText et_m;
    private EditText et_s;
    private AudioManager mAudioManager;
    private TimerTask task;
    private CircleProgressView mCircleProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.add);
        Button stop = (Button) findViewById(R.id.stop);
        button.setOnClickListener(this);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task.cancel(true);
                timer.setText("0");
                mCircleProgressView.setProgressWithAnimation(0,100);
            }
        });
        et_h = (EditText) findViewById(R.id.time_hours);
        et_m = (EditText) findViewById(R.id.time_min);
        et_s = (EditText) findViewById(R.id.time_sec);
        timer = (TextView) findViewById(R.id.timer);
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        mCircleProgressView = (CircleProgressView) findViewById(R.id.circle_progress_view);
        mCircleProgressView.setTextEnabled(true);
        mCircleProgressView.setInterpolator(new AccelerateDecelerateInterpolator());
        mCircleProgressView.addAnimationListener(new ProgressAnimationListener() {
            @Override
            public void onValueChanged(float value) {
//                Toast.makeText(MainActivity.this, "Value of view changed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationEnd() {
//                Toast.makeText(MainActivity.this, "Animation of view done", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onClick(View view) {
        String min = et_m.getText().toString();
        String hours = et_h.getText().toString();
        String sec = et_s.getText().toString();
        task = new TimerTask();
        int time = Integer.valueOf(sec) + 60 * Integer.valueOf(min) + 60 * 60 * Integer.valueOf(hours);
        task.execute(time);
    }

    private class TimerTask extends AsyncTask<Integer, Void, Void> {
        private NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        @Override
        protected Void doInBackground(Integer... args) {
            int counter = args[0];
            NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(MainActivity.this)
                    .setContentTitle("Sleeper")
                    .setContentText("Music shut down in " + counter)
                    .setSmallIcon(R.drawable.image);
            int point = 100/counter;
            int current = 0;
            notifyTimer(counter,current);
            while(counter > 0){
                builder.setContentText("Music shut down in:" + counter);
                manager.notify(1,builder.build());
                try {
                    Thread.sleep(900);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                counter--;
                current+=point;
                notifyTimer(counter,current);
            }
            if (mAudioManager.isMusicActive()) {
                Intent intent = new Intent("com.android.music.musicservicecommand");
                intent.putExtra("command", "pause");
                MainActivity.this.sendBroadcast(intent);
            }
            return null;
        }
    }

    private void notifyTimer(final int counter,final int point){
        runOnUiThread(new Thread(){
            @Override
            public void run() {
                timer.setText(String.valueOf(counter));
                mCircleProgressView.setProgressWithAnimation(point,100);
            }
        });
    }
}
