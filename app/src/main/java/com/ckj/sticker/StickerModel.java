package com.ckj.sticker;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;

import static com.ckj.sticker.StickerView.OPER_SELECTED;

/**
 * Created by Administrator on 2017/6/21.
 * 贴纸的模型
 */

public class StickerModel {
    private Bitmap mainBmp, deleteBmp, controlBmp;
    private int mainBmpWidth, mainBmpHeight, deleteBmpWidth, deleteBmpHeight, controlBmpWidth, controlBmpHeight;

    float[] srcPs;
    float[] dstPs;

    private Point lastPoint;
    private Point prePivot, lastPivot;
    private float defaultDegree, preDegree, lastDegree;
    private Point symmetricPoint = new Point();    //当前操作点对称点
    private Point centerPoint = new Point();       //中心点
    private Point rightBottomPoint = new Point();  //旋转缩放点

    private Matrix matrix = new Matrix();

    private float deltaX = 0, deltaY = 0;   // 位移值
    private float scaleValue = 1;           // 贴图素材缩放值

    private boolean isSelected = true;              // 是否选中
    private boolean isActive = true;               // 是否删除

    public int lastOper = OPER_SELECTED;

    public int getLastOper() {
        return lastOper;
    }

    public void setLastOper(int lastOper) {
        this.lastOper = lastOper;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public float getDeltaX() {
        return deltaX;
    }

    public void setDeltaX(float deltaX) {
        this.deltaX = deltaX;
    }

    public float getDeltaY() {
        return deltaY;
    }

    public void setDeltaY(float deltaY) {
        this.deltaY = deltaY;
    }

    public float[] getDstPs() {
        return dstPs;
    }

    public float getScaleValue() {

        return scaleValue;
    }

    public void setScaleValue(float scaleValue) {
        this.scaleValue = scaleValue;
    }

    public StickerModel(Bitmap mainBmp, Bitmap deleteBmp, Bitmap controlBmp) {
        this.mainBmp = mainBmp;
        this.deleteBmp = deleteBmp;
        this.controlBmp = controlBmp;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public Point getLastPoint() {
        return lastPoint;
    }

    public void setLastPoint(Point lastPoint) {
        this.lastPoint = lastPoint;
    }

    public Point getPrePivot() {
        return prePivot;
    }

    public void setPrePivot(Point prePivot) {
        this.prePivot = prePivot;
    }

    public Point getLastPivot() {
        return lastPivot;
    }

    public void setLastPivot(Point lastPivot) {
        this.lastPivot = lastPivot;
    }

    public float getDefaultDegree() {
        return defaultDegree;
    }

    public void setDefaultDegree(float defaultDegree) {
        this.defaultDegree = defaultDegree;
    }

    public float getPreDegree() {
        return preDegree;
    }

    public void setPreDegree(float preDegree) {
        this.preDegree = preDegree;
    }

    public float getLastDegree() {
        return lastDegree;
    }

    public void setLastDegree(float lastDegree) {
        this.lastDegree = lastDegree;
    }

    public Point getSymmetricPoint() {
        return symmetricPoint;
    }

    public void setSymmetricPoint(Point symmetricPoint) {
        this.symmetricPoint = symmetricPoint;
    }

    public Point getCenterPoint() {
        return centerPoint;
    }

    public void setCenterPoint(Point centerPoint) {
        this.centerPoint = centerPoint;
    }

    public Point getRightBottomPoint() {
        return rightBottomPoint;
    }

    public void setRightBottomPoint(Point rightBottomPoint) {
        this.rightBottomPoint = rightBottomPoint;
    }

    public void setSrcPs(float[] srcPs) {
        this.srcPs = srcPs;
        dstPs = srcPs.clone();
    }

    public float[] getSrcPs() {
        return srcPs;
    }

    public Bitmap getMainBmp() {
        return mainBmp;
    }

    public void setMainBmp(Bitmap mainBmp) {
        this.mainBmp = mainBmp;
    }

    public Bitmap getDeleteBmp() {
        return deleteBmp;
    }

    public void setDeleteBmp(Bitmap deleteBmp) {
        this.deleteBmp = deleteBmp;
    }

    public Bitmap getControlBmp() {
        return controlBmp;
    }

    public void setControlBmp(Bitmap controlBmp) {
        this.controlBmp = controlBmp;
    }

    public int getMainBmpWidth() {
        return  mainBmp.getWidth();
    }


    public int getMainBmpHeight() {
        return  mainBmp.getHeight();
    }


    public int getDeleteBmpWidth() {
        return deleteBmp.getWidth();
    }


    public int getDeleteBmpHeight() {
        return deleteBmp.getHeight();
    }


    public int getControlBmpWidth() {
        return controlBmp.getWidth();
    }


    public int getControlBmpHeight() {
        return controlBmp.getHeight();
    }




}
