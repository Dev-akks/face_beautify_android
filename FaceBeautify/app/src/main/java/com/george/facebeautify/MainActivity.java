package com.george.facebeautify;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
    private Button bt1;//photograph
    private Button bt2;//gallery
    private String fileName;
    private Uri uri;
    private String strImgPath;
    private String strImgPath2;
    private Intent imageCaptureIntent;
    public SharedPreferences sp ;
    //private boolean photoflag=false;
    //public static List<Activity> activityList = new ArrayList<Activity>();


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //exit the app
        enter.activityList.add(this);

        setContentView(R.layout.activity_topview);
        bt1 = (Button) findViewById(R.id.topview_bt1);
        bt1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                letCamera();
            }
        });

        bt2 = (Button) findViewById(R.id.topview_bt2);
        bt2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//调用android的图库
                startActivityForResult(i, 2);
            }
        });



    }

    protected void letCamera() {
        // TODO Auto-generated method stub
        imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(imageCaptureIntent, 1);//1: requestCode

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:// 拍照
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                    String strImgPath = Environment.getExternalStorageDirectory()
                            .toString() + "/DCIM/find your beauty/";// 存放照片的文件夹
                    fileName = ""+System.currentTimeMillis()+ ".png";// 照片命名
                    File out = new File(strImgPath);
                    if (!out.exists()) {
                        out.mkdirs();
                    }
                    out = new File(strImgPath, fileName);
                    strImgPath = strImgPath + fileName;// 该照片的绝对路径
                    strImgPath2 = "/mnt/sdcard/DCIM/find your beauty/"+fileName;
                    uri = Uri.fromFile(out);
                    try {
                        FileOutputStream b = new FileOutputStream(strImgPath);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, b);
                        b.flush();//???
                        b.close();
                        Toast.makeText(getApplicationContext(), "拍摄成功,照片已保存到：" + strImgPath2, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(getApplicationContext(), "拍摄成功,照片已保存到：" + strImgPath2, Toast.LENGTH_LONG).show();

                    sp = getSharedPreferences("photopath", 0);            //写入配置文件
                    Editor spEd = sp.edit();
                    spEd.putString("path",strImgPath2);
                    //spEd.putString("path2", strImgPath2);
                    spEd.commit();


                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, opening_ph.class);
                    //intent.setData(uri);
                    startActivity(intent);
                    //photoflag=true;
                }
                break;
            case 2:// gallery
                if (resultCode == RESULT_OK &&null!=data)
                {
                    Uri selectedImage = data.getData();//bug: can only read the img from gallery. cannot ...from /sdcard

                    Cursor cursor = getContentResolver().query(selectedImage,
                            null, null, null, null);
                    cursor.moveToFirst();

                    String picturePath = cursor.getString(1);
                    cursor.close();

                    sp = getSharedPreferences("photopath", 0);            //写入配置文件
                    Editor spEd = sp.edit();
                    spEd.putString("path",picturePath);//bug can't read img from sdcard
                    spEd.commit();
                    System.out.println("picturePath="+picturePath);

                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, opening_ph.class);
                    startActivity(intent);

                }
                break;

            default:
                break;
        }
    }



}
