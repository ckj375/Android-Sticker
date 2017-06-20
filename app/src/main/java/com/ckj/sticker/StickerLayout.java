package com.ckj.sticker;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.LinkedList;

/**
 * Created by Administrator on 2017/6/20.
 */

public class StickerLayout extends FrameLayout {
    private LinkedList<StickerView> mStickerViewList = new LinkedList<>();
    private StickerView mSelectedStickerView;

    public StickerLayout(@NonNull Context context) {
        super(context);
    }

    public StickerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StickerLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        if (child instanceof StickerView) {
            mStickerViewList.add((StickerView) child);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                for (StickerView stickerView : mStickerViewList) {
                    if (stickerView.isOnPic(x, y)) {
                        mSelectedStickerView = stickerView;
                        //使得被点击到的StickerView放到最顶层
                        mStickerViewList.remove(mSelectedStickerView);
                        mStickerViewList.addLast(mSelectedStickerView);
                        break;
                    }
                }
                removeAllViews();
                int count = mStickerViewList.size();
                for (int i = 0; i < count; i++) {
                    StickerView stickerView = mStickerViewList.get(i);
                    stickerView.setSelectState(false);
                    super.addView(stickerView);
                }
                break;
        }


//        ListIterator<StickerView> iterator = mStickerViewList.listIterator();
//        while (iterator.hasNext()){
//            StickerView stickerView = iterator.next();
//            if (stickerView.isOnPic(x, y)) {
//                mSelectedStickerView = stickerView;
//                //使得被点击到的StickerView放到最顶层
//                iterator.remove();
//                break;
//            }
//        }





//        for (StickerView view : mStickerViewList) {
//            addView(view);
//        }
        return super.onInterceptTouchEvent(event);
    }
}
