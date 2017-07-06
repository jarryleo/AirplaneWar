package cn.leo.airplanewar.engine;

import android.view.MotionEvent;

/**
 * 元素触控事件接口
 * Created by JarryLeo on 2017/1/8.
 */

public interface EngineControl {
    //点击事件
    boolean onClick(Cell cell);

    //触摸事件
    void onTouch(Cell cell, MotionEvent event);
}
