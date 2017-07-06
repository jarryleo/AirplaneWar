package cn.leo.airplanewar.engine;

import android.graphics.Canvas;

/**
 * Created by Leo on 2017/7/3.
 */

public class CellCustom extends Cell {
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    public int getCellType() {
        return Cell.TYPE_CUSTOM;
    }


}
