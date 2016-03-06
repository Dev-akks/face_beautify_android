package com.george.facebeautify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


public class startup extends Activity {

	//int image = R.drawable.sy;//获取启动图片
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startup);

		new Handler().postDelayed(new Runnable() {
			public void run() {
                /* Create an Intent that will start the Main WordPress Activity. */
				Intent intent = new Intent();
				intent.setClass(startup.this, enter.class);
				startActivity(intent);
				startup.this.finish();
			}
		}, 3000); //ms for release


		//turn to login

		// 转向添加页面
	}


}

