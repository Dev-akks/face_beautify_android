package com.george.facebeautify;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class enter extends Activity {
    private Button bt1 ;//enter
    public static List<Activity> activityList = new ArrayList<Activity>();
    private static final int Exit_ID = Menu.FIRST+1;

    //int image = R.drawable.sy;//获取启动图片
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter);
        //exit the app
        enter.activityList.add(this);
        bt1 = (Button) findViewById(R.id.enter_bt1);
        bt1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(enter.this, MainActivity.class);
                startActivity(intent);
            }
        });


        //turn to login

        // 转向添加页面
    }

    public static void exitClient()
    {
        //Log.d("sdfas", "----- exitClient -----");
        // 关闭所有Activity
        for (int i = 0; i < activityList.size(); i++)
        {
            if (null != activityList.get(i))
            {
                activityList.get(i).finish();
            }
        }
        //ActivityManager activityMgr= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE );
        //activityMgr.restartPackage(context.getPackageName());
        System.exit(0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //退出程序
        menu.add(0, Exit_ID, 0, R.string.exit) //center
                .setShortcut('4', 'd');
        //.setIcon(R.drawable.exit);
        return true;
    }
    //handle the menu operation
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId()==Exit_ID) {
            //exit
            enter.exitClient();
            //android.os.Process.killProcess(Process.myPid());  

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

