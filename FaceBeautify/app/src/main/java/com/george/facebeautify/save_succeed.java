package com.george.facebeautify;



import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class save_succeed extends Activity {
    private Button bt1;//back
    private Button bt2;//home

    //private String fileName;
    //Intent imageCaptureIntent;
    //private Uri photoUri;
    //private static final int Exit_ID = Menu.FIRST+1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //exit the app
        enter.activityList.add(this);

        setContentView(R.layout.activity_save);
        bt1 = (Button) findViewById(R.id.save_bt1);
        bt1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent intent = new Intent();
                intent.setClass(save_succeed.this, after_beautify.class);
                // 转向添加页面
                startActivity(intent);
                //finish()???
            }
        });

        bt2 = (Button) findViewById(R.id.save_bt2);
        bt2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent intent = new Intent();
                intent.setClass(save_succeed.this, enter.class);
                // 转向添加页面
                startActivity(intent);
                //finish()???
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        return true;
    }


}
