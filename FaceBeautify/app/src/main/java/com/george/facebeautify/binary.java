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
import org.opencv.imgcodecs.Imgcodecs; // imread, imwrite, etc
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

public class binary extends Activity {
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
	private Mat srcimgsub = null;											  //bug
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
		setContentView(R.layout.dilation);
		// myProgressBar = (ProgressBar) findViewById(R.id.be_progressBar1);
		//用于退出程序
		enter.activityList.add(this);


		new Handler().postDelayed(new Runnable() {
			public void run() {

				SharedPreferences sp = getSharedPreferences("photopath", 0);            //读取配置文件 96
				String photostr = sp.getString("afterdetect", null);
				try{
					srcimgsub = Imgcodecs.imread(photostr);//cannot read!!! why???
				}
				catch(Exception e){
					Toast.makeText(getApplicationContext(),"imread  failed!"+photostr, Toast.LENGTH_LONG).show();
				}

				Mat binarymat = null;
				String filename2=null;
				try{
					binarymat = BinaryFace(srcimgsub);//too much loops result long delay///
					filename2 = "/mnt/sdcard/DCIM/find your beauty/binary"+System.currentTimeMillis()+".jpg";
					//System.out.println(String.format("Writing %s", filename));
					if (srcimgsub != null)
						Imgcodecs.imwrite(filename2, binarymat);
					//	myProgressBar.incrementProgressBy(20);

					sp = getSharedPreferences("photopath", 0);            //写入配置文件
					Editor spEd = sp.edit();
					spEd.putString("afterbinary",filename2);
					//spEd.putString("binary", filename2);
					spEd.commit();



				}
				catch (Exception e){
					// TODO Auto-generated catch block
					Toast.makeText(getApplicationContext(),"binary  failed!"+photostr, Toast.LENGTH_LONG).show();
				}

				Bitmap bitmap =null;
				try{
					if(filename2!=null)
						bitmap = BitmapFactory.decodeFile(filename2);//decodematfailed???
					iView = (ImageView) findViewById(R.id.dilation_iView);
					iView.setImageBitmap(bitmap);
				}
				catch (Exception e) {
					Toast.makeText(getApplicationContext(),"mat to bitmap  failed!", Toast.LENGTH_LONG).show();
				}

				bt3 = (Button) findViewById(R.id.dilation_bt);
				bt3.setOnClickListener(new Button.OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub


						Intent intent = new Intent();
						intent.setClass(binary.this, dilation.class);
						//Uri uri = getIntent().getData();
						//intent.setData(uri);
						startActivity(intent);

					}
				});


			}
		}, 100); //ms for release

	}

	Mat  BinaryFace(Mat FaceImage)
	{
		int i,j,Rows = 0,Cols = 0;
		Mat Mark = null;
		Mat TrainFaceGray = null;

		int m,n;
		double Threshold=0;  //阈值
		double Mean =0;         //均值
		double SDeviation = 0;   //标准差


		try {
			Rows=FaceImage.rows();///
			Cols=FaceImage.cols();///
			Toast.makeText(getApplicationContext(),"FaceImage.rows()="+Rows+" FaceImage.cols()="+Cols, Toast.LENGTH_LONG).show();
			Mark = new Mat(Rows,Cols,CvType.CV_8UC1);///??? mark是变量名

			//下面是二值化...
			TrainFaceGray = new Mat(Rows,Cols,CvType.CV_8UC1);///???

			if(FaceImage.channels()==3)
				Imgproc.cvtColor( FaceImage, TrainFaceGray, CV_BGR2GRAY );  //opencv默认的通道是BGR
			else FaceImage.copyTo(TrainFaceGray);


		}
		catch (Exception e){
			Toast.makeText(getApplicationContext(),"binary init failed!", Toast.LENGTH_LONG).show();
			return null;
		}


		//try{
		for(i=0;i<Rows;i++)
			for(j=0;j<Cols;j++)
			{
				if((i>=0&&i<3)||(i>=(Rows-3)&&i<Rows)||(j>=0&&j<3)||(j>=(Cols-3)&&j<Cols))
				{
					//Mark.get(i, j)[0]=255;
					Mark.put(i, j, 255);
				}
				else {
					//Mark.put(i, j, 0);

					Mean=0;
					SDeviation=0;
					for(m=i-3;m<i+4;m++)
						for(n=j-3;n<j+4;n++)
						{
							//Mean=Mean+TrainFaceGray.get(m,n);         //为什么改为at<int>就会出错？？ char
							Mean = Mean + TrainFaceGray.get(m, n)[0];
							SDeviation = SDeviation + (TrainFaceGray.get(m,n)[0])*(TrainFaceGray.get(m,n)[0]);
						}
					Mean=Mean/49.0;
					// SDeviation=SDeviation/49.0;
					SDeviation=Math.sqrt(SDeviation/49.0);///

					Threshold=1.28*Mean-0.42*SDeviation;

					if(TrainFaceGray.get(i,j)[0] < Threshold)
					{
						//Mark.get(i,j)[0]=0;          //黑,受保护
						Mark.put(i, j, 0);
					}

					else
						//Mark.get(i,j)[0]=255;
						Mark.put(i, j, 255);
				}
			}

		return Mark;
	}



}
