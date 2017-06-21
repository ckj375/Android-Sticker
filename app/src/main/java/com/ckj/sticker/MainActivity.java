package com.ckj.sticker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity {

    private StickerLayout container;
    private Button saveBtn;
    private ImageView img;
    private Bitmap src;
    private TextView decorateType;
    private static final int REQUEST_FOR_PICTURE = 1;
    // 定义贴图素材集合
    private ArrayList<StickerView> materialList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 贴图容器
        container = (StickerLayout) findViewById(R.id.layout_sticker);
      //  img = (ImageView) findViewById(R.id.src);
        container.setBackgroundResource(R.drawable.bg);
        src = BitmapFactory.decodeResource(getResources(),R.drawable.bg);
     //   img.setImageBitmap(src);

        // 素材集合
        materialList = new ArrayList<>();

        // 跳转至素材界面
        decorateType = (TextView) findViewById(R.id.type_decorate);
        decorateType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setClass(MainActivity.this, MaterialActivity.class);
                startActivityForResult(i, REQUEST_FOR_PICTURE);
                overridePendingTransition(R.anim.push_up, 0);

                /*
                for(CommonImgEffectView effectView:materialList){
                    if(!effectView.getIsActive()){
                        materialList.remove(effectView);
                    }
                }
                */

            }
        });

//        // 跳转至素材界面
//        modeType = (TextView) findViewById(R.id.type_mode);
//        modeType.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent();
//                i.setClass(MainActivity.this,MaterialActivity.class);
//                startActivityForResult(i,REQUEST_FOR_PICTURE);
//            }
//        });

        // 保存图片
        saveBtn = (Button) findViewById(R.id.save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap tempBmp = Bitmap.createBitmap(src);
                for (StickerView effectView : materialList) {
                    if (effectView.getIsActive()) {
                        tempBmp = createBitmap(tempBmp,
                                BitmapFactory.decodeFile(effectView.getImgPath()),
                                effectView.getCenterPoint(), effectView.getDegree(), effectView.getScaleValue());
                    }
                }

                saveMyBitmap(tempBmp);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //requestCode标示请求的标示   resultCode表示有数据
        if (requestCode == REQUEST_FOR_PICTURE && resultCode == RESULT_OK) {
            String imgPath = data.getStringExtra(MaterialActivity.MATERIAL_PATH);
            // 贴图容器中心点
//            int centerX = (img.getLeft() + img.getRight()) / 2;
//            int centerY = (img.getTop() + img.getBottom()) / 2;
//            Log.v("ckjc", "centerX=" + centerX + "  centerY=" + centerY);

            StickerView view = new StickerView(MainActivity.this, imgPath);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            view.setLayoutParams(params);
            container.addView(view);
            // 添加至素材集合
            materialList.add(view);
        }
    }

    // 图片合成
    private Bitmap createBitmap(Bitmap src, Bitmap dst, float[] centerPoint, float degree, float scaleValue) {
        if (src == null) {
            return null;
        }
        int w = src.getWidth();
        int h = src.getHeight();
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        float scale = (float) w / (float) width;

        int ww = dst.getWidth();
        int wh = dst.getHeight();

        float Ltx = centerPoint[0] - img.getLeft() - ww * scaleValue / 2;
        float Lty = centerPoint[1] - img.getTop() - wh * scaleValue / 2;

        Bitmap newb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);//创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(newb);
        cv.drawBitmap(src, 0, 0, null);//在 0，0坐标开始画入src

        // 定义矩阵对象
        Matrix matrix = new Matrix();
        matrix.postScale(scaleValue, scaleValue);
//        matrix.postRotate(degree);
        cv.save();
        cv.rotate(degree, centerPoint[0], centerPoint[1]);
        Bitmap dstbmp = Bitmap.createBitmap(dst, 0, 0, dst.getWidth(), dst.getHeight(),
                matrix, true);
        cv.drawBitmap(dstbmp, Ltx * scale, Lty * scale, null);//在src画贴图
        //cv.save( Canvas.ALL_SAVE_FLAG );//保存
        cv.restore();//存储
        return newb;
    }

    // 保存图片到手机
    public void saveMyBitmap(Bitmap mBitmap) {
        //获取当前时间，进一步转化为字符串
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy" + "_" + "MM" + "_" + "dd" + "_" + "HH" + "_" + "mm" + "_" + "ss");
        String bmpName = format.format(date);

        File f = new File(Environment.getExternalStorageDirectory().getPath() + "/" + bmpName + ".jpg");
        if (f.exists()) {
            f.delete();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(MainActivity.this, "已保存:"+f.getAbsolutePath(), Toast.LENGTH_SHORT).show();
    }

}
