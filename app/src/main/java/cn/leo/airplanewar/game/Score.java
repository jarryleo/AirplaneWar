package cn.leo.airplanewar.game;

import android.graphics.Color;

import cn.leo.airplanewar.engine.CellString;
import cn.leo.airplanewar.engine.LeoEngine;

/**
 * Created by Leo on 2017/7/6.
 */

public class Score extends CellString {

    private LeoEngine mLeoEngine;

    public Score(LeoEngine leoEngine) {
        mLeoEngine = leoEngine;
        initView();
    }

    private void initView() {
        x = 30;
        y = 30;
        z = 2000;
        setTextSize((int) (30 * mLeoEngine.getDensity()));
        setTextColor(Color.GRAY);
    }


}
