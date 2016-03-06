package com.george.facebeautify;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;


public class compare extends Activity {
	private ImageView before,after;
	private Bitmap bmp1,bmp2;
	//int image = R.drawable.sy;//获取启动图片
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.compare);
		before = (ImageView) findViewById(R.id.compare_iView1);
		after = (ImageView) findViewById(R.id.compare_iView2);

		SharedPreferences sp = getSharedPreferences("photopath", 0);            //读取配置文件 96     
		String beforestr = sp.getString("afterdetect", null);
		String afterstr = sp.getString("after", null);

		if(beforestr!=null)
			bmp1 = BitmapFactory.decodeFile(beforestr);//decodematfailed???
		if(afterstr!=null)
			bmp2 = BitmapFactory.decodeFile(afterstr);//decodematfailed???

		before.setImageBitmap(bmp1);
		after.setImageBitmap(bmp2);

	}


}

