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

public class beautifying extends Activity {
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
	private Mat SrcImage = null;											  //bug
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
		setContentView(R.layout.binary);
		// myProgressBar = (ProgressBar) findViewById(R.id.be_progressBar1);

		//用于退出程序
		enter.activityList.add(this);

		new Handler().postDelayed(new Runnable() {
			public void run() {

				try{
					loadcascadefile();//!!!
				}
				catch(OutOfMemoryError e){
					Toast.makeText(getApplicationContext(),"load .xml failed!", Toast.LENGTH_LONG).show();
				}


				SharedPreferences sp = getSharedPreferences("photopath", 0);            //读取配置文件 96
				String photostr = sp.getString("path", null);
				System.out.println("path2="+photostr);
				try{
					SrcImage = Imgcodecs.imread(photostr);//cannot read!!! why???
				}
				catch(Exception e){
					Toast.makeText(getApplicationContext(),"imread  failed!"+photostr, Toast.LENGTH_LONG).show();
				}

				Mat srcimgsub = null;
				String filename1=null;
				try{
					faceRect = cFaceDetect(SrcImage);
					srcimgsub = SrcImage.submat(faceRect);
					filename1 = "/mnt/sdcard/DCIM/find your beauty/facedetect"+System.currentTimeMillis()+".jpg";
					//System.out.println(String.format("Writing %s", filename));
					if (srcimgsub != null)
						Imgcodecs.imwrite(filename1, srcimgsub);

					sp = getSharedPreferences("photopath", 0);            //写入配置文件
					Editor spEd = sp.edit();
					spEd.putString("afterdetect",filename1);
					//spEd.putString("binary", filename2);
					spEd.commit();

				}
				catch (Exception e){
					// TODO Auto-generated catch block
					Toast.makeText(getApplicationContext(),"facedetect  failed!"+photostr, Toast.LENGTH_LONG).show();
				}

				Bitmap bitmap =null;
				try{
					if(filename1!=null)
						bitmap = BitmapFactory.decodeFile(filename1);//decodematfailed???
					iView = (ImageView) findViewById(R.id.binary_iView);
					iView.setImageBitmap(bitmap);
				}
				catch (Exception e) {
					Toast.makeText(getApplicationContext(),"mat to bitmap  failed!", Toast.LENGTH_LONG).show();
				}

				bt3 = (Button) findViewById(R.id.binary_bt);
				bt3.setOnClickListener(new Button.OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub

						Intent intent = new Intent();
						intent.setClass(beautifying.this, binary.class);
						//Uri uri = getIntent().getData();
						//intent.setData(uri);
						startActivity(intent);

					}
				});
			}

		}, 100); //ms for release

	}


	public Rect cFaceDetect(Mat SrcImage) {

		Mat ImageGray = new Mat();//gray srcimg
		try{
			if(SrcImage.channels()==3) {
				Imgproc.cvtColor( SrcImage, ImageGray, CV_BGR2GRAY );///???
				//Toast.makeText(getApplicationContext()," ", Toast.LENGTH_LONG).show();
			}
			else
				SrcImage.copyTo(ImageGray);
		}
		catch (Exception e){
			Toast.makeText(getApplicationContext(),"rgb to gray failed ", Toast.LENGTH_LONG).show();
		}
		MatOfRect faces =null;
		try{
			faces = new MatOfRect();
			if (face_cascade != null)
				face_cascade.detectMultiScale(ImageGray, faces, 1.1, 2, 4|8, new Size(30, 30), new Size());
			//face_cascade.detectMultiScale(SrcImage, faces );
		}
		catch (Exception e){
			Toast.makeText(getApplicationContext(),"detectmultiscale failed ", Toast.LENGTH_LONG).show();
		}
		try {
			Rect[] facesArray = faces.toArray();
			if(facesArray.length > 0)
			{

				facesArray[0].y = (facesArray[0].y-10)>=0?(facesArray[0].y-10):0;
				//facesArray[0].width += 20;
				facesArray[0].height += 20;

				return facesArray[0];
			}
			else
				return new Rect(0,0,0,0);
		}
		catch (Exception e){
			Toast.makeText(getApplicationContext(),"rectangle failed ", Toast.LENGTH_LONG).show();
		}
		return new Rect(0,0,0,0);
	}




	public void loadcascadefile()
	{
		try {
			// load cascade file from application resources
			InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
			File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
			mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
			FileOutputStream os = new FileOutputStream(mCascadeFile);

			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			is.close();
			os.close();

			face_cascade = new CascadeClassifier(mCascadeFile.getAbsolutePath());
			if (face_cascade.empty()) {
				// Log.e(TAG, "Failed to load cascade classifier");
				face_cascade = null;
			}
			else
			{
				//Toast.makeText(getApplicationContext(),"cascade succeed!", Toast.LENGTH_LONG).show();
			}
			// Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

			cascadeDir.delete();///!!!

		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(),"cascade failed!", Toast.LENGTH_LONG).show();
		}
	}


}
