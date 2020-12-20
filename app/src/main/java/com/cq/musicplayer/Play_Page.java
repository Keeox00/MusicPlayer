package com.cq.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

public class Play_Page extends AppCompatActivity {

    private static final String TAG = "Play_Page";
    private ImageButton  button_Pause;
    private ImageButton  button_Start;
    private ImageButton button_last;
    private ImageButton button_next;
    private static SeekBar seekBar;
    private boolean index = false;
    private int i = 0;
    private Bundle bundle;
    private String[] strings = new String[]{
                "Alan Walker",
                "影子习惯",
                "勇气",
                "从你的全世界路过",
                "小幸运",
                "米津玄師 (よねづ けんし)",
                "孤单心事",};
    private static player_Service.MyBinder iBinder;
    private String name;

    // 接收更新后的进度条，用来更新音乐进度。
    public static Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle bundle = msg.getData();
            //获取发送过来的数据
            int currentPosition = bundle.getInt("currentPosition");
            int duration = bundle.getInt("duration");

            //为进度条设置当前进度和总进度
            seekBar.setMax(duration);
            seekBar.setProgress(currentPosition);

            //为精度条设置更改事件
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int progress = seekBar.getProgress();
                    iBinder.callseekTo(progress);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play__page);

        button_Pause = findViewById(R.id.pause);
        button_Start = findViewById(R.id.start);
        button_last = findViewById(R.id.last);
        button_next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekbar);

        //获取上个活动传递过来的数据
        Intent intent = getIntent();
        bundle = intent.getBundleExtra("bundle");
        name = bundle.getString("name");

        Intent intent1 = new Intent(this,player_Service.class);  //首先通过startService的方法开启服务，保证该服务在后台长期运行
        startService(intent1);
        bindService(intent1,new MyConnection(),BIND_ABOVE_CLIENT);
    }

    class MyConnection implements ServiceConnection{

        //绑定成功
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onServiceConnected(ComponentName name1, IBinder service) {
            iBinder = (player_Service.MyBinder) service;
            iBinder.callpalyMusic(name);
        }

        //绑定失败
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void myOnclick(View view){
        switch (view.getId()){
            //音乐开始键
            case R.id.start:
                      //调用继续播放方法
                iBinder.callcontinuePlay();
                      //让开始键消失
                button_Start.setVisibility(View.GONE);
                     //让暂停键显示出来
                button_Pause.setVisibility(View.VISIBLE);
                break;

            //音乐暂停键（刚开始是显示的暂停键）
            case R.id.pause:
                     //调用服务里的暂停音乐方法
                iBinder.callpauseMusic();
                     //让暂停键消失
                button_Pause.setVisibility(View.GONE);
                     //让开始键显示出来
                button_Start.setVisibility(View.VISIBLE);
                break;

            //上一首
            case R.id.last:
                //让开始键消失
                button_Start.setVisibility(View.GONE);
                //让暂停键显示出来
                button_Pause.setVisibility(View.VISIBLE);
                if (!index){
                    A: for (int j = 0; j < strings.length;j++){
                        if (strings[j].equals(name)){
                            i = j;
                            break A;
                        }
                    }
                    index = true;
                }
                i = i-1 >= 0 ? --i : strings.length - 1;
                iBinder.callpalyMusic(strings[i]);
                Log.d(TAG, "myOnclick:------- " + i);
                Log.d(TAG, "myOnclick: " + strings[i]);
                break;
            //下一首
            case R.id.next:
                //让开始键消失
                button_Start.setVisibility(View.GONE);
                //让暂停键显示出来
                button_Pause.setVisibility(View.VISIBLE);
                if (!index){
                    A: for (int j = 0; j < strings.length;j++){
                        if (strings[j].equals(name)){
                            i = j;
                            break A;
                        }
                    }
                    index = true;
                }
                i = i+1 < strings.length ? ++i : 0;
                Log.d(TAG, "myOnclick:------- " + i);
                iBinder.callpalyMusic(strings[i]);
                Log.d(TAG, "myOnclick: " + strings[i]);
                break;
        }
    }

}