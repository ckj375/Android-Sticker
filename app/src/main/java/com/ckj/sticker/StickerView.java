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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


/**
 * 使用矩阵控制图片移动、缩放、旋转
 *
 * @author ckj375
 */
public class StickerView extends View {

    private static final String TAG = "StickerView";
    private Context context;
    private String imgPath;
    private Bitmap mainBmp, deleteBmp, controlBmp;
    private int mainBmpWidth, mainBmpHeight, deleteBmpWidth, deleteBmpHeight, controlBmpWidth, controlBmpHeight;
    private float[] srcPs, dstPs;
    private Matrix matrix;
    private Paint paint, paintFrame;
    private Point lastPoint;                        // 记录最后一次Touch事件触摸点
    private float defaultDegree, lastDegree;
    private boolean isSelected = true;              // 默认选中状态
    private boolean isRemoved = false;              // 是否被移除

    /**
     * 图片控制点
     * 0--------1
     * |        |
     * |    4   |
     * |        |
     * 3--------2
     */
    private static final int CP_NONE = -1;
    private static final int CP_REMOVE = 0;
    private static final int CP_ROTATE_SCALE = 2;
    private int current_cp = CP_NONE;

    /**
     * 图片操作类型
     */
    public static final int OPER_SELECTED = 1;      // 选择
    public static final int OPER_TRANSLATE = 2;     // 移动
    public static final int OPER_ROTATE_SCALE = 3;  // 旋转缩放


    /**
     * 素材选中监听
     */
    public interface OnSelectedListener {
        void onSelected();
    }

    public OnSelectedListener mOnSelectedListener = null;

    public void setOnSelectedListener(OnSelectedListener listener) {
        this.mOnSelectedListener = listener;
    }

    /**
     * 素材移除监听
     */
    public interface OnRemovedListener {
        void onRemoved();
    }

    public OnRemovedListener mOnRemovedListener = null;

    public void setOnRemovedListener(OnRemovedListener listener) {
        this.mOnRemovedListener = listener;
    }

    public StickerView(Context context, String imgPath) {
        super(context);
        this.context = context;
        this.imgPath = imgPath;
        initData();
    }

    private void initData() {
        mainBmp = BitmapFactory.decodeFile(imgPath);
        deleteBmp = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.ic_f_delete_normal);
        controlBmp = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.ic_f_rotate_normal);
        mainBmpWidth = mainBmp.getWidth();
        mainBmpHeight = mainBmp.getHeight();
        deleteBmpWidth = deleteBmp.getWidth();
        deleteBmpHeight = deleteBmp.getHeight();
        controlBmpWidth = controlBmp.getWidth();
        controlBmpHeight = controlBmp.getHeight();

        srcPs = new float[]{
                0, 0,
                mainBmpWidth, 0,
                mainBmpWidth, mainBmpHeight,
                0, mainBmpHeight,
                mainBmpWidth / 2, mainBmpHeight / 2
        };
        dstPs = srcPs.clone();
        matrix = new Matrix();

        paint = new Paint();
        paint.setAntiAlias(true);
        paintFrame = new Paint();
        paintFrame.setColor(Color.WHITE);
        paintFrame.setStrokeWidth(getResources().getDimension(R.dimen.stickerview_frame_width));
        paintFrame.setAntiAlias(true);

        lastPoint = new Point(0, 0);
        defaultDegree = computeDegree(new Point(mainBmpWidth, mainBmpHeight), new Point(mainBmpWidth / 2, mainBmpHeight / 2));
        lastDegree = defaultDegree;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float evX = event.getX();
        float evY = event.getY();

        if (!isOnStickerView((int) evX, (int) evY) && getControlPoint((int) evX, (int) evY) == CP_NONE) {
            if (isSelected) {
                isSelected = false;
                invalidate();
            }

            return false;
        }

        if (getControlPoint((int) evX, (int) evY) == CP_REMOVE) {
            if (isSelected) {
                mOnRemovedListener.onRemoved();
                isRemoved = true;
                invalidate();
                return true;
            }

            return false;
        }

        if (!isSelected) {
            isSelected = true;
            mOnSelectedListener.onSelected();
            bringToFront();
            requestLayout();
        }

        int operType = getOperationType(event);
        switch (operType) {
            case OPER_TRANSLATE:
                translate(evX, evY);
                break;
            case OPER_ROTATE_SCALE:
                rotate(evX, evY);
                scale(evX, evY);
                break;
        }
        matrix.mapPoints(dstPs, srcPs);
        invalidate();

        lastPoint.x = (int) evX;
        lastPoint.y = (int) evY;

        return true;
    }

    /**
     * 获取控制点类型
     */
    private int getControlPoint(int evx, int evy) {
        Rect rect = new Rect(evx - controlBmpWidth / 2, evy - controlBmpHeight / 2, evx + controlBmpWidth / 2, evy + controlBmpHeight / 2);
        int type = CP_NONE;
        if (rect.contains((int) dstPs[0], (int) dstPs[1])) {
            type = CP_REMOVE;
        }
        if (rect.contains((int) dstPs[4], (int) dstPs[5])) {
            type = CP_ROTATE_SCALE;
        }
        return type;
    }

    /**
     * 判断触摸点是否在贴图上
     */
    private boolean isOnStickerView(int x, int y) {
        Matrix inMatrix = new Matrix();
        matrix.invert(inMatrix);

        float[] tempPs = new float[]{0, 0};
        inMatrix.mapPoints(tempPs, new float[]{x, y});
        if (tempPs[0] > 0 && tempPs[0] < mainBmp.getWidth() && tempPs[1] > 0 && tempPs[1] < mainBmp.getHeight()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断操作类型：移动或旋转缩放
     */
    private int getOperationType(MotionEvent event) {
        float evX = event.getX();
        float evY = event.getY();

        int curOper = 0;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                curOper = OPER_SELECTED;
                current_cp = getControlPoint((int) evX, (int) evY);
                break;
            case MotionEvent.ACTION_MOVE:
                if (current_cp == CP_ROTATE_SCALE) {
                    curOper = OPER_ROTATE_SCALE;
                } else {
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

    /**
     * 移动
     */
    private void translate(float evX, float evY) {
        float deltaX = evX - lastPoint.x;
        float deltaY = evY - lastPoint.y;

        matrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 缩放
     */
    private void scale(float evX, float evY) {
        float centerPointX = dstPs[4];
        float centerPointY = dstPs[5];

        float centerPointX_new = centerPointX + (evX - lastPoint.x);
        float centerPointY_new = centerPointY + (evY - lastPoint.y);

        float preDistance = getDistanceOfTwoPoints(new Point((int) centerPointX, (int) centerPointY), new Point((int) dstPs[8], (int) dstPs[9]));
        float lastDistance = getDistanceOfTwoPoints(new Point((int) centerPointX_new, (int) centerPointY_new), new Point((int) dstPs[8], (int) dstPs[9]));

        float scaleValue = lastDistance / preDistance;// 贴图素材缩放值
        Log.d("img", "scaleValue is " + scaleValue);
        if (getScaleValue() < (float) 0.3 && scaleValue < (float) 1) {
            // 限定最小缩放比为0.3
        } else {
            matrix.postScale(scaleValue, scaleValue, dstPs[8], dstPs[9]);
        }
    }

    /**
     * 旋转
     */
    private void rotate(float evX, float evY) {
        float centerPointX = dstPs[4];
        float centerPointY = dstPs[5];

        float centerPointX_new = centerPointX + (evX - lastPoint.x);
        float centerPointY_new = centerPointY + (evY - lastPoint.y);

        float preDegree = computeDegree(new Point((int) centerPointX_new, (int) centerPointY_new), new Point((int) dstPs[8], (int) dstPs[9]));
        matrix.postRotate(preDegree - lastDegree, dstPs[8], dstPs[9]);
        lastDegree = preDegree;
    }

    /**
     * 计算两个点之间的距离
     */
    private float getDistanceOfTwoPoints(Point p1, Point p2) {
        return (float) (Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y)));
    }

    /**
     * 计算两点与垂直方向夹角
     */
    private float computeDegree(Point p1, Point p2) {
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isRemoved) {
            canvas.drawBitmap(mainBmp, matrix, paint);
            if (isSelected) {
                drawFrame(canvas);
                drawControlPoints(canvas);
            }
        }
    }

    /**
     * 绘制边框
     */
    private void drawFrame(Canvas canvas) {
        canvas.drawLine(dstPs[0], dstPs[1], dstPs[2], dstPs[3], paintFrame);
        canvas.drawLine(dstPs[2], dstPs[3], dstPs[4], dstPs[5], paintFrame);
        canvas.drawLine(dstPs[4], dstPs[5], dstPs[6], dstPs[7], paintFrame);
        canvas.drawLine(dstPs[0], dstPs[1], dstPs[6], dstPs[7], paintFrame);
    }

    /**
     * 绘制控制按钮
     */
    private void drawControlPoints(Canvas canvas) {
        canvas.drawBitmap(deleteBmp, dstPs[0] - deleteBmpWidth / 2, dstPs[1] - deleteBmpHeight / 2, paint);
        canvas.drawBitmap(controlBmp, dstPs[4] - controlBmpWidth / 2, dstPs[5] - controlBmpHeight / 2, paint);
    }

    /**
     * 获取饰品旋转角度
     */
    public float getDegree() {
        Log.d(TAG, "饰品旋转角度:" + (lastDegree - defaultDegree));
        return lastDegree - defaultDegree;
    }

    /**
     * 获取饰品中心点坐标
     */
    public float[] getCenterPoint() {
        float[] centerPoint = new float[2];
        centerPoint[0] = dstPs[8];
        centerPoint[1] = dstPs[9];
        Log.d(TAG, "饰品中心点坐标:(" + (int) centerPoint[0] + "," + (int) centerPoint[1] + ")");
        return centerPoint;
    }

    /**
     * 获取饰品缩放比例(与原图相比)
     */
    public float getScaleValue() {
        float preDistance = (srcPs[8] - srcPs[0]) * (srcPs[8] - srcPs[0]) + (srcPs[9] - srcPs[1]) * (srcPs[9] - srcPs[1]);
        float lastDistance = (dstPs[8] - dstPs[0]) * (dstPs[8] - dstPs[0]) + (dstPs[9] - dstPs[1]) * (dstPs[9] - dstPs[1]);
        float scaleValue = (float) Math.sqrt(lastDistance / preDistance);
        Log.d(TAG, "饰品缩放比例:" + scaleValue);
        return scaleValue;
    }

    /**
     * 获取素材图片路径
     */
    public String getImgPath() {
        return imgPath;
    }

}  
