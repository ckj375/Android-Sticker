package com.ckj.sticker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import java.util.LinkedList;

/**
 * Created by chenkaijian on 17-10-19.
 */

public class StickerViewLayout extends FrameLayout {

    private LinkedList<StickerView> mStickerViewList = new LinkedList<>();

    public StickerViewLayout(@NonNull Context context) {
        super(context);
    }

    public StickerViewLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        if (child instanceof StickerView) {
            mStickerViewList.add((StickerView) child);
            ((StickerView) child).setOnSelectedListener(new StickerView.OnSelectedListener() {
                @Override
                public void onselected() {
                    for (StickerView v : mStickerViewList) {
                        if (v.getIsSelected()) {
                            mStickerViewList.remove(v);
                            mStickerViewList.add(v);
                            break;
                        }
                    }
                }
            });
        }
    }

    public LinkedList getStickerViewList() {
        return mStickerViewList;
    }
}
