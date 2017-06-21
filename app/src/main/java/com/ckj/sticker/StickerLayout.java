//package com.ckj.sticker;
//
//import android.content.Context;
//import android.support.annotation.AttrRes;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.FrameLayout;
//
//import java.util.LinkedList;
//
///**
// * Created by Administrator on 2017/6/20.
// * 专门放StickerView的容器，使得StickerView可以同一界面操作多个
// * 可以添加其他的View作为StickerView的背景View
// */
//
//public class StickerLayout extends FrameLayout {
//    private LinkedList<StickerView> mStickerViewList = new LinkedList<>();
//
//    public StickerLayout(@NonNull Context context) {
//        super(context);
//    }
//
//    public StickerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public StickerLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    @Override
//    public void addView(View child) {
//        super.addView(child);
//        if (child instanceof StickerView) {
//            refreshChildState();
//            mStickerViewList.add((StickerView) child);
//        }
//    }
//
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent event) {
//        int x = (int) event.getX();
//        int y = (int) event.getY();
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                for (StickerView stickerView : mStickerViewList) {
//                    if (stickerView.isOnPic(x, y)) {
//                        removeView(stickerView);
//                        super.addView(stickerView);
//                        break;
//                    }
//                }
//                refreshChildState();
//                break;
//        }
//        return super.onInterceptTouchEvent(event);
//    }
//
//    /**
//     * 刷新所有StickerView的状态，主要是边框的显示刷新
//     */
//    private void refreshChildState() {
//        int count = mStickerViewList.size();
//        for (int i = 0; i < count; i++) {
//            StickerView stickerView = mStickerViewList.get(i);
//            stickerView.setSelectState(false);
//        }
//    }
//}
