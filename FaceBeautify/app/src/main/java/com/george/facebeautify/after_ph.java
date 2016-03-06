package com.george.facebeautify;



import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class after_ph extends Activity {
    //private Button bt1;//save
    //private Button bt2;//home
    private ImageView iView;
    private Button bt3;//beautify
    //private Uri myuri;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //exit the app
        enter.activityList.add(this);

        setContentView(R.layout.after_photograph);

        SharedPreferences sp = getSharedPreferences("photopath", 0);            //读取配置文件 96
        String photostr = sp.getString("path", null);
        System.out.println("path1="+photostr);//bug can't read img from sdcard/bluetooth
        Bitmap bitmap = BitmapFactory.decodeFile(photostr);
        iView = (ImageView) findViewById(R.id.after_ph_iView);
        iView.setImageBitmap(bitmap);
        //iView.setImageURI(myuri);

        bt3 = (Button) findViewById(R.id.af_ph_bt3);
        bt3.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(after_ph.this, beautifying.class);
                //Uri uri = getIntent().getData();
                //intent.setData(uri);
                startActivity(intent);
                //after_ph.this.finish();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        return true;
    }


}
