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

public class filter extends Activity {
	//private Button bt1;//save
	//private Button bt2;//home
	private Button bt1;//分享到新浪微博
	private Button bt2;//返回、对比
	private Button bt3;//beautify
	private ImageView iView;//beautified img
	//======================================================
	private final int FILTER_LENGTH=5;      //迭代多级非线性滤波器的长度
	private final int ITERATION=8;			 ///number of iteration
	private CascadeClassifier face_cascade; ///???
	private final int CV_BGR2GRAY = 6;
	//private final int CV_HAAR_FIND_BIGGEST_OBJECT = 4;
	//private final int CV_HAAR_DO_ROUGH_SEARCH = 8;
	private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255); //
	private Mat dilationimg = null;
	private Mat srcimgsub = null;//bug
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
		setContentView(R.layout.after_beautify);

		//用于退出程序
		enter.activityList.add(this);

		new Handler().postDelayed(new Runnable() {
			public void run() {


				SharedPreferences sp = getSharedPreferences("photopath", 0);            //读取配置文件 96
				String photostr = sp.getString("afterdilation", null);
				String srcimgsubstr = sp.getString("afterdetect", null);
				try{
					dilationimg = Imgcodecs.imread(photostr);//cannot read!!! why???
					srcimgsub = Imgcodecs.imread(srcimgsubstr);
				}
				catch(Exception e){
					Toast.makeText(getApplicationContext(),"imread  failed!"+photostr, Toast.LENGTH_LONG).show();
				}

				Mat DstImg = null;
				try{

					for(int timeNum=0;timeNum<ITERATION;timeNum++)
					{
						DstImg = Iterative_MedFilter(srcimgsub, dilationimg , FILTER_LENGTH);///
					}

				}
				catch (Exception e){
					// TODO Auto-generated catch block
					Toast.makeText(getApplicationContext(),"BinaryFace  failed!"+photostr, Toast.LENGTH_LONG).show();
				}

				String filename = null;
				try {
					filename = "/mnt/sdcard/DCIM/find your beauty/beautified"+System.currentTimeMillis()+".png";
					//System.out.println(String.format("Writing %s", filename));
					if (DstImg != null)
						Imgcodecs.imwrite(filename, DstImg);

					//
					sp = getSharedPreferences("photopath", 0);            //写入配置文件
					Editor spEd = sp.edit();
					spEd.putString("after",filename);
					//spEd.putString("path2", strImgPath2);
					spEd.commit();


				}
				catch (Exception e) {
					Toast.makeText(getApplicationContext(),"imwrite  failed!"+photostr, Toast.LENGTH_LONG).show();
				}

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
						intent.setClass(filter.this, enter.class);
						startActivity(intent);
						//finish()???


					}
				});

				bt2 = (Button) findViewById(R.id.af_be_bt2);//back
				bt2.setOnClickListener(new Button.OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub

						Intent intent = new Intent();
						intent.setClass(filter.this, compare.class);
						// 转向添加页面
						startActivity(intent);
						//finish()???
					}
				});
			}


		}, 100); //ms for release

	}


	Mat Iterative_MedFilter(Mat srcFace,Mat mark,final int L)  //迭代处理
	{

		List<Mat> SingleImg = new ArrayList<Mat>();///
		SingleImg.add(new Mat());
		SingleImg.add(new Mat());
		SingleImg.add(new Mat());
		int i,j,k;
		int dim;
		int channel;
		int N=(L-1)/2;
		double[] Candidate = new double[L-1];///
		double[] Z = new double[4];///
		double[] maxmin = new double[2];
		maxmin[0]=maxmin[1]=0.0;
		Core.split(srcFace, SingleImg);///???opencv  get RGB

		for(channel=0;channel<3;channel++)
		{
			for(i=N;i<srcFace.rows()-N;i++)
				for(j=N;j<srcFace.cols()-N;j++)
				{
					if((int)mark.get(i,j)[0]>128)
					{
						dim=0;
						for(k=j-N;k<=j+N;k++)
						{
							if(k!=j)
							{
								Candidate[dim]=SingleImg.get(channel).get(i,k)[0];
								dim++;
							}
						}
						Z[0]=Median(Candidate,L-1);

						dim=0;
						for(k=i-N;k<=i+N;k++)
						{
							if(k!=i)
							{
								Candidate[dim]=SingleImg.get(channel).get(k,j)[0];
								dim++;
							}
						}
						Z[1]=Median(Candidate,L-1);


						dim=0;
						for(k=-N;k<=N;k++)
						{
							if(k!=0)
							{
								Candidate[dim]=SingleImg.get(channel).get(i+k,j-k)[0];
								dim++;
							}
						}
						Z[2]=Median(Candidate,L-1);

						dim=0;
						for(k=-N;k<=N;k++)
						{
							if(k!=0)
							{
								Candidate[dim]=SingleImg.get(channel).get(i+k,j+k)[0];
								dim++;
							}
						}
						Z[3]=Median(Candidate,L-1);

						Order(Z,maxmin,4);
						if(SingleImg.get(channel).get(i,j)[0]>maxmin[1])
							//SingleImg.get(channel).get(i,j)[0]=Umax;
							SingleImg.get(channel).put(i, j, maxmin[1]);
						else
						if(SingleImg.get(channel).get(i,j)[0]<maxmin[0])
							//SingleImg.get(channel).get(i,j)[0]=Umin;
							SingleImg.get(channel).put(i, j, maxmin[0]);
					}

				}
		}

		Mat DstImg = new Mat();
		Core.merge(SingleImg, DstImg);//opencv merge RGB

		return DstImg;
	}


	void Order(double[] ZZ,double[] maxmin,int length)
	{

		int j,k,kk;
		//char tmp;
		double tmp;
		k=length-1;

		while(k>0)
		{
			kk=0;
			for(j=0;j<k;j++)
				if(ZZ[j]>ZZ[j+1])
				{
					tmp=ZZ[j];
					ZZ[j]=ZZ[j+1];
					ZZ[j+1]=tmp;
					kk=j;
				}
			k=kk;
		}

		maxmin[0]=ZZ[0];
		maxmin[1]=ZZ[length-1];


	}


	double Median(double[] Candidate,int length )  //length=2*N为偶数
	{
		int j,k,kk;
		double tmp;;
		k=length-1;



		while(k>0)
		{
			kk=0;
			for(j=0;j<k;j++)
				if(Candidate[j]>Candidate[j+1])
				{
					tmp=Candidate[j];
					Candidate[j]=Candidate[j+1];
					Candidate[j+1]=tmp;
					kk=j;
				}
			k=kk;
		}
		//char Mid=(char)cvRound((Candidate[length/2-1]+Candidate[length/2])/2);
		double Mid=(double)Math.round((Candidate[length/2-1]+Candidate[length/2])/2);
		// cout<<"Mid= "<<(int)Mid<<endl<<endl;

		return Mid;
	}






}
