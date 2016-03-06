package com.george.facebeautify;

//import java.lang.String.Split;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
//import org.opencv.highgui.Highgui; //the functionality was split up into new videoio and imgcodecs
import org.opencv.imgcodecs.Imgcodecs; // imread, imwrite, etc
//import org.opencv.videoio;   // VideoCapture
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class after_beautify extends Activity {
	private Button bt1;//分享到新浪微博
	private Button bt2;//返回、对比
	//private Button bt3;//beautify
	private ImageView iView;
	private ColorMatrix colorMatrix;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.after_beautify);


		//用于退出程序
		enter.activityList.add(this);

		SharedPreferences sp = getSharedPreferences("photopath", 0);            //读取配置文件 96
		String filename = sp.getString("before", null);
		System.out.println("path2="+filename);


		Bitmap bitmap =null;
		try{
			if(filename!=null)
				bitmap = BitmapFactory.decodeFile(filename);//decodematfailed???
			iView = (ImageView) findViewById(R.id.after_be_iView);
			iView.setImageBitmap(bitmap);
		}
		catch (Exception e) {
			Toast.makeText(getApplicationContext(),"mat to bitmap  failed!", Toast.LENGTH_LONG).show();
		}

		bt1 = (Button) findViewById(R.id.af_be_bt1);//save
		bt1.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
            	/*sina weibo*/
				Intent intent = new Intent();
				intent.setClass(after_beautify.this, enter.class);
				startActivity(intent);
				//finish()???


			}
		});

		bt2 = (Button) findViewById(R.id.af_be_bt2);//back
		bt2.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent();
				intent.setClass(after_beautify.this, compare.class);
				// 转向添加页面
				startActivity(intent);
				//finish()???
			}
		});
	}



}
