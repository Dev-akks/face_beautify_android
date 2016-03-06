package com.george.facebeautify;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
//import org.opencv.highgui.Highgui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class dilation extends Activity {
	//private Button bt1;//save
	//private Button bt2;//home
	private Button bt3;//beautify
	private ImageView iView;//beautified img
	//======================================================
	private final int FILTER_LENGTH=5;      //迭代多级非线性滤波器的长度
	private final int ITERATION=6;			 ///number of iteration
	private CascadeClassifier face_cascade; ///???
	private final int CV_BGR2GRAY = 6;
	//private final int CV_HAAR_FIND_BIGGEST_OBJECT = 4;
	//private final int CV_HAAR_DO_ROUGH_SEARCH = 8;
	private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255); //
	private Mat binaryimg = null;											  //bug
	private Rect faceRect ;
	private File mCascadeFile;
	static {
		if (!OpenCVLoader.initDebug()) {
			// Handle initialization error
		}
	}//!!!!!important

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filter);
		//用于退出程序
		enter.activityList.add(this);


		new Handler().postDelayed(new Runnable() {
			public void run() {


				SharedPreferences sp = getSharedPreferences("photopath", 0);            //读取配置文件 96
				String photostr = sp.getString("afterbinary", null);
				System.out.println("path2="+photostr);
				try{
					binaryimg = Imgcodecs.imread(photostr);//cannot read!!! why???
					//Toast.makeText(getApplicationContext(),"binary channel="+binaryimg.channels(), Toast.LENGTH_LONG).show();

				}
				catch(Exception e){
					Toast.makeText(getApplicationContext(),"imread  failed!"+photostr, Toast.LENGTH_LONG).show();
				}

				Mat dilationimg = null;
				String filename2=null;
				Mat binarymat1 = new Mat();
				try{
					if(binaryimg.channels()==3)
						Imgproc.cvtColor( binaryimg, binarymat1, CV_BGR2GRAY );  //opencv默认的通道是BGR
					else binaryimg.copyTo(binarymat1);

					dilationimg = Dilation(binarymat1);//too much loops result long delay///
					filename2 = "/mnt/sdcard/DCIM/find your beauty/dilation"+System.currentTimeMillis()+".jpg";
					//System.out.println(String.format("Writing %s", filename));
					if (dilationimg != null)
						Imgcodecs.imwrite(filename2, dilationimg);

					sp = getSharedPreferences("photopath", 0);            //写入配置文件
					Editor spEd = sp.edit();
					spEd.putString("afterdilation",filename2);
					//spEd.putString("binary", filename2);
					spEd.commit();

				}
				catch (Exception e){
					// TODO Auto-generated catch block
					Toast.makeText(getApplicationContext(),"BinaryFace  failed!"+photostr, Toast.LENGTH_LONG).show();
				}


				Bitmap bitmap =null;
				try{
					if(filename2!=null)
						bitmap = BitmapFactory.decodeFile(filename2);//decodematfailed???
					iView = (ImageView) findViewById(R.id.filter_iView);
					iView.setImageBitmap(bitmap);
				}
				catch (Exception e) {
					Toast.makeText(getApplicationContext(),"mat to bitmap  failed!", Toast.LENGTH_LONG).show();
				}

				bt3 = (Button) findViewById(R.id.filter_bt);
				bt3.setOnClickListener(new Button.OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub

						Intent intent = new Intent();
						intent.setClass(dilation.this, filter.class);
						//Uri uri = getIntent().getData();
						//intent.setData(uri);
						startActivity(intent);

					}
				});



			}
		}, 100); //ms for release

	}

	Mat  Dilation(Mat Mark)
	{
		int i,j,Rows = 0,Cols = 0;
		Rows=Mark.rows();///
		Cols=Mark.cols();///
		//Toast.makeText(getApplicationContext(),"FaceImage.rows()="+Rows+" FaceImage.cols()="+Cols, Toast.LENGTH_LONG).show();
		int m,n;

		Mat FinalMark =null;
		int Num0 = 0;

		FinalMark=Mark.clone();

		for(i=3;i<Rows-3;i++)
			for(j=3;j<Cols-3;j++)
			{
				if((int)Mark.get(i,j)[0]==0)
				{
					Num0=0;
					for(m=i-3;m<i+4;m++)
					{
						for(n=j-3;n<j+4;n++)
						{
							if(Num0<2 && (int)Mark.get(m,n)[0]==0)
								Num0++;
							if(Num0>=2)
							{
								break;
							}
						}
						if(Num0>=2)
						{
							break;
						}
					}
					if(Num0>=2)
					{
						for(m=i-3;m<i+4;m++)
							for(n=j-3;n<j+4;n++)
							{
								//FinalMark.get(m,n)[0]=0;
								FinalMark.put(m, n, 0);
							}
					}
				}
			}

		return FinalMark;
	}


}
