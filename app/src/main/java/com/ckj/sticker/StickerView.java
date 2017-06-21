package com.ckj.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;


/**
 * 使用矩阵控制图片移动、缩放、旋转
 */
public class StickerView extends View {

    private Context context;
    private String imgPath;

//    int resultWidth = mainBmpWidth + deleteBmpWidth / 2 + controlBmpWidth / 2;;
//    int resultHeight = mainBmpHeight + deleteBmpHeight / 2 + controlBmpHeight / 2;;

    //   private float[] srcPs, dstPs;

    private Paint paint, paintFrame;


    /**
     * 图片操作类型
     */
    public static final int OPER_DEFAULT = -1;      //默认  
    public static final int OPER_TRANSLATE = 0;     //移动  
    public static final int OPER_SCALE = 1;         //缩放  
    public static final int OPER_ROTATE = 2;        //旋转  
    public static final int OPER_SELECTED = 3;      //选择  


    /* 图片控制点 
     * 0--------1
     * |        |
      *|    4   |
     * |        |
     * 3--------2
     */
    public static final int CTR_NONE = -1;
    public static final int CTR_LEFT_TOP = 0;
    public static final int CTR_RIGHT_BOTTOM = 2;
    public static final int CTR_MID_MID = 4;
    public int current_ctr = CTR_NONE;

    private ArrayList<StickerModel> mStickerViewArrayList = new ArrayList<>();

    public void setStickerViewArrayList(ArrayList<StickerModel> stickerViewArrayList) {
        mStickerViewArrayList = stickerViewArrayList;
    }

//    public StickerView(Context context, String imgPath) {
//        super(context);
//        this.context = context;
//        this.imgPath = imgPath;
//        initData(imgPath);
//    }

    public StickerView(Context context) {
        super(context);
        this.context = context;
        initData();
    }

    public StickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initData();
    }

    public StickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initData();
    }

    public void addStickerView(StickerModel stickerModel) {
        stickerModel.setSrcPs(new float[]{
                0, 0,
                stickerModel.getMainBmpWidth(), 0,
                stickerModel.getMainBmpWidth(), stickerModel.getMainBmpHeight(),
                0, stickerModel.getMainBmpHeight(),
                stickerModel.getMainBmpWidth() / 2, stickerModel.getMainBmpHeight() / 2
        });


        // 平移后中心点位置
        stickerModel.setPrePivot(new Point(stickerModel.getMainBmpWidth() / 2, stickerModel.getMainBmpHeight() / 2));
        // 平移前中心点位置
        stickerModel.setLastPivot(new Point(stickerModel.getMainBmpWidth() / 2, stickerModel.getMainBmpHeight() / 2));

        // 上一次触摸点位置
        stickerModel.setLastPoint(new Point(0, 0));

        stickerModel.setDefaultDegree(computeDegree(new Point(stickerModel.getMainBmpWidth(), stickerModel.getMainBmpHeight())
                , new Point(stickerModel.getMainBmpWidth() / 2, stickerModel.getMainBmpHeight() / 2)));
        stickerModel.setLastDegree(computeDegree(new Point(stickerModel.getMainBmpWidth(), stickerModel.getMainBmpHeight())
                , new Point(stickerModel.getMainBmpWidth() / 2, stickerModel.getMainBmpHeight() / 2)));

        setMatrix(OPER_DEFAULT, stickerModel);
        mStickerViewArrayList.add(stickerModel);
        invalidate();
    }


    /**
     * 初始化数据
     */
    private void initData() {

        paint = new Paint();
        paint.setAntiAlias(true);

        paintFrame = new Paint();
        paintFrame.setColor(Color.WHITE);
        paintFrame.setAntiAlias(true);



//        mainBmp = BitmapFactory.decodeFile(imgPath);
//        deleteBmp = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.ic_f_delete_normal);
//        controlBmp = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.ic_f_rotate_normal);
//        mainBmpWidth = mainBmp.getWidth();
//        mainBmpHeight = mainBmp.getHeight();
//        deleteBmpWidth = deleteBmp.getWidth();
//        deleteBmpHeight = deleteBmp.getHeight();
//        controlBmpWidth = controlBmp.getWidth();
//        controlBmpHeight = controlBmp.getHeight();


    }

    /**
     * 矩阵变换，达到图形平移的目的
     */
    private void setMatrix(int operationType, StickerModel stickerModel) {
        float[] dstPs = stickerModel.getDstPs();
        switch (operationType) {
            case OPER_TRANSLATE:
                stickerModel.getMatrix().postTranslate(stickerModel.getDeltaX(), stickerModel.getDeltaY());
                break;
            case OPER_SCALE:
                stickerModel.getMatrix().postScale(stickerModel.getScaleValue(), stickerModel.getScaleValue()
                        , dstPs[CTR_MID_MID * 2], dstPs[CTR_MID_MID * 2 + 1]);
                break;
            case OPER_ROTATE:
                stickerModel.getMatrix().postRotate(stickerModel.getPreDegree() - stickerModel.getLastDegree()
                        , dstPs[CTR_MID_MID * 2], dstPs[CTR_MID_MID * 2 + 1]);
                break;
        }
        stickerModel.getMatrix().mapPoints(dstPs, stickerModel.getSrcPs());
    }

    // 判断触摸点是否在贴图上
    public boolean isOnPic(int x, int y, StickerModel stickerModel) {
        // 获取逆向矩阵
        Matrix inMatrix = new Matrix();
        stickerModel.getMatrix().invert(inMatrix);

        float[] tempPs = new float[]{0, 0};
        inMatrix.mapPoints(tempPs, new float[]{x, y});
        if (tempPs[0] > 0 && tempPs[0] < stickerModel.getMainBmp().getWidth() && tempPs[1] > 0 && tempPs[1] < stickerModel.getMainBmp().getHeight()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断点所在的控制点
     *
     * @param
     * @param
     * @return
     */
    private int isOnCP(int evx, int evy, StickerModel stickerModel) {
        Rect rect = new Rect(evx - stickerModel.getControlBmpWidth() / 2, evy - stickerModel.getControlBmpHeight() / 2
                , evx + stickerModel.getControlBmpWidth() / 2, evy + stickerModel.getControlBmpHeight() / 2);
        int res = 0;
        float[] dstPs = stickerModel.getDstPs();
        for (int i = 0; i < dstPs.length; i += 2) {
            if (rect.contains((int) dstPs[i], (int) dstPs[i + 1])) {
                return res;
            }
            ++res;
        }
        return CTR_NONE;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int evX = (int) event.getX();
        int evY = (int) event.getY();

        for (StickerModel stickerModel : mStickerViewArrayList) {
            if (!isOnPic(evX, evY, stickerModel) && isOnCP(evX, evY, stickerModel) == CTR_NONE) {
                stickerModel.setSelected(false);
                //    requestLayout();
            } else if (isOnCP(evX, evY, stickerModel) == CTR_LEFT_TOP) {
                stickerModel.setActive(false);
                //    requestLayout();
            } else {
                int operType = OPER_DEFAULT;
                operType = getOperationType(event, stickerModel);

                switch (operType) {
                    case OPER_TRANSLATE:
                        if (isOnPic(evX, evY, stickerModel)) {
                            translate(evX, evY, stickerModel);
                        }
                        break;
                    case OPER_ROTATE:
                        rotate(event, stickerModel);
                        scale(event, stickerModel);
                        break;
                }

                stickerModel.getLastPoint().x = evX;
                stickerModel.getLastPoint().y = evY;

                stickerModel.setLastOper(operType);
                stickerModel.setSelected(true);
                //   requestLayout();
            }
        }
        invalidate();//重绘
        return true;
    }


    private int getOperationType(MotionEvent event, StickerModel stickerModel) {
        int evX = (int) event.getX();
        int evY = (int) event.getY();
        int curOper = stickerModel.getLastOper();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                current_ctr = isOnCP(evX, evY, stickerModel);
                if (current_ctr != CTR_NONE || isOnPic(evX, evY, stickerModel)) {
                    curOper = OPER_SELECTED;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (current_ctr == CTR_LEFT_TOP) {
                    // 删除饰品
                } else if (current_ctr == CTR_RIGHT_BOTTOM) {
                    curOper = OPER_ROTATE;
                } else if (stickerModel.getLastOper() == OPER_SELECTED) {
                    curOper = OPER_TRANSLATE;
                }
                break;
            case MotionEvent.ACTION_UP:
                curOper = OPER_SELECTED;
                break;
            default:
                break;
        }
        return curOper;

    }


//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int desireWidth = MeasureSpec.getSize(widthMeasureSpec);
//        int desireHeight = MeasureSpec.getSize(heightMeasureSpec);
//        switch (MeasureSpec.getMode(widthMeasureSpec)) {
//            case MeasureSpec.AT_MOST:
//            case MeasureSpec.UNSPECIFIED:
//                resultWidth = (mainBmp.getWidth() + deleteBmpWidth/2 + controlBmpWidth/2);
//                break;
//            case MeasureSpec.EXACTLY:
//                resultWidth = desireWidth;
//                break;
//        }
//
//        switch (MeasureSpec.getMode(heightMeasureSpec)) {
//            case MeasureSpec.AT_MOST:
//            case MeasureSpec.UNSPECIFIED:
//                resultHeight =  (mainBmp.getHeight() + deleteBmpHeight/2 + controlBmpHeight/2);
//                break;
//            case MeasureSpec.EXACTLY:
//                resultHeight = desireHeight;
//                break;
//        }
//        setMeasuredDimension(resultWidth > desireWidth?desireWidth:resultWidth,resultHeight > desireHeight?desireHeight:resultHeight);
//    }

    /**
     * 移动
     *
     * @param evx
     * @param evy
     */
    private void translate(int evx, int evy, StickerModel stickerModel) {
        stickerModel.setDeltaX(evx - stickerModel.getLastPoint().x);
        stickerModel.setDeltaY(evy - stickerModel.getLastPoint().y);
        stickerModel.getPrePivot().x += stickerModel.getDeltaX();
        stickerModel.getPrePivot().y += stickerModel.getDeltaY();
        stickerModel.getLastPivot().x = stickerModel.getPrePivot().x;
        stickerModel.getLastPivot().y = stickerModel.getPrePivot().y;

        setMatrix(OPER_TRANSLATE, stickerModel); //设置矩阵
    }

    /**
     * 缩放
     * 0---1---2
     * |       |
     * 7   8   3
     * |       |
     * 6---5---4
     *
     * @param
     * @param
     */
    private void scale(MotionEvent event, StickerModel stickerModel) {
        float[] dstPs = stickerModel.getDstPs();
        int pointIndex = current_ctr * 2;

        float px = dstPs[pointIndex];
        float py = dstPs[pointIndex + 1];

        float evx = event.getX();
        float evy = event.getY();

        float oppositeX = 0;
        float oppositeY = 0;

        oppositeX = dstPs[pointIndex - 4];
        oppositeY = dstPs[pointIndex - 3];

        float temp1 = getDistanceOfTwoPoints(px, py, oppositeX, oppositeY);
        float temp2 = getDistanceOfTwoPoints(evx, evy, oppositeX, oppositeY);

        stickerModel.setScaleValue(temp2 / temp1);
        stickerModel.getSymmetricPoint().x = (int) oppositeX;
        stickerModel.getSymmetricPoint().y = (int) oppositeY;
        stickerModel.getCenterPoint().x = (int) (stickerModel.getSymmetricPoint().x + px) / 2;
        stickerModel.getCenterPoint().y = (int) (stickerModel.getSymmetricPoint().y + py) / 2;
        stickerModel.getRightBottomPoint().x = (int) dstPs[8];
        stickerModel.getRightBottomPoint().y = (int) dstPs[9];
        Log.i("img", "scaleValue is " + stickerModel.getScaleValue());
        //  if (getScaleValue() < (float) 0.3 && stickerModel.getScaleValue() < (float) 1) {
        // 限定最小缩放比为0.3
        //   } else {
        setMatrix(OPER_SCALE, stickerModel);
        //  }
    }

    /**
     * 旋转图片
     * 0---1---2
     * |       |
     * 7   8   3
     * |       |
     * 6---5---4
     *
     * @param
     * @param
     */
    private void rotate(MotionEvent event, StickerModel stickerModel) {
        float[] dstPs = stickerModel.getDstPs();
        if (event.getPointerCount() == 2) {
            stickerModel.setPreDegree(computeDegree(new Point((int) event.getX(0), (int) event.getY(0))
                    , new Point((int) event.getX(1), (int) event.getY(1))));
        } else {
            stickerModel.setPreDegree(computeDegree(new Point((int) event.getX(), (int) event.getY())
                    , new Point((int) dstPs[8], (int) dstPs[9])));
        }
        setMatrix(OPER_ROTATE, stickerModel);
        stickerModel.setLastDegree(stickerModel.getPreDegree());
    }


    /**
     * 计算两点与垂直方向夹角
     *
     * @param p1
     * @param p2
     * @return
     */
    public float computeDegree(Point p1, Point p2) {
        float tran_x = p1.x - p2.x;
        float tran_y = p1.y - p2.y;
        float degree = 0.0f;
        float angle = (float) (Math.asin(tran_x / Math.sqrt(tran_x * tran_x + tran_y * tran_y)) * 180 / Math.PI);
        if (!Float.isNaN(angle)) {
            if (tran_x >= 0 && tran_y <= 0) {//第一象限
                degree = angle;
            } else if (tran_x <= 0 && tran_y <= 0) {//第二象限
                degree = angle;
            } else if (tran_x <= 0 && tran_y >= 0) {//第三象限
                degree = -180 - angle;
            } else if (tran_x >= 0 && tran_y >= 0) {//第四象限
                degree = 180 - angle;
            }
        }
        return degree;
    }

    /**
     * 计算两个点之间的距离
     *
     * @param p1
     * @param p2
     * @return
     */
    private float getDistanceOfTwoPoints(Point p1, Point p2) {
        return (float) (Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y)));
    }

    private float getDistanceOfTwoPoints(float x1, float y1, float x2, float y2) {
        return (float) (Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
    }


    @Override
    public void onDraw(Canvas canvas) {
        for (StickerModel stickerModel : mStickerViewArrayList) {
            if (!stickerModel.isActive()) {
                return;
            }
            canvas.drawBitmap(stickerModel.getMainBmp(), stickerModel.getMatrix(), paint);//绘制主图片
            if (stickerModel.isSelected()) {
                drawFrame(canvas, stickerModel);//绘制边框,以便测试点的映射
                drawControlPoints(canvas, stickerModel);//绘制控制点图片
            }
        }
    }

    private void drawFrame(Canvas canvas, StickerModel stickerModel) {
        float[] dstPs = stickerModel.getDstPs();
        canvas.drawLine(dstPs[0], dstPs[1], dstPs[2], dstPs[3], paintFrame);
        canvas.drawLine(dstPs[2], dstPs[3], dstPs[4], dstPs[5], paintFrame);
        canvas.drawLine(dstPs[4], dstPs[5], dstPs[6], dstPs[7], paintFrame);
        canvas.drawLine(dstPs[0], dstPs[1], dstPs[6], dstPs[7], paintFrame);
    }

    private void drawControlPoints(Canvas canvas, StickerModel stickerModel) {
        float[] dstPs = stickerModel.getDstPs();
        canvas.drawBitmap(stickerModel.getDeleteBmp(), dstPs[0] - stickerModel.getDeleteBmpWidth() / 2
                , dstPs[1] - stickerModel.getDeleteBmpHeight() / 2, paint);
        canvas.drawBitmap(stickerModel.getControlBmp(), dstPs[4] - stickerModel.getControlBmpWidth() / 2
                , dstPs[5] - stickerModel.getControlBmpHeight() / 2, paint);
    }

//    // 获取饰品旋转角度
//    public float getDegree() {
//        return lastDegree - defaultDegree;
//    }

    // 获取饰品中心点坐标
//    public float[] getCenterPoint() {
//        float[] centerPoint = new float[2];
//        centerPoint[0] = dstPs[8];
//        centerPoint[1] = dstPs[9];
//        return centerPoint;
//    }
//
//    // 获取饰品缩放比例(与原图相比)
//    public float getScaleValue() {
//        float preDistance = (srcPs[8] - srcPs[0]) * (srcPs[8] - srcPs[0]) + (srcPs[9] - srcPs[1]) * (srcPs[9] - srcPs[1]);
//        float lastDistance = (dstPs[8] - dstPs[0]) * (dstPs[8] - dstPs[0]) + (dstPs[9] - dstPs[1]) * (dstPs[9] - dstPs[1]);
//        float scaleValue = (float) Math.sqrt(lastDistance / preDistance);
//        return scaleValue;
//    }

//    // 判断饰品是否已被移除
//    public boolean getIsActive() {
//        return isActive;
//    }


    // 获取素材图片路径
    public String getImgPath() {
        return imgPath;
    }

//    public void setSelectState(boolean unselect){
//        this.isSelected = unselect;
//        invalidate();
//    }
}  
