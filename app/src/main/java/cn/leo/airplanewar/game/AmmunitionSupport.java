package cn.leo.airplanewar.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import cn.leo.airplanewar.R;
import cn.leo.airplanewar.engine.LeoEngine;

/**
 * Created by yjtx2 on 2017/7/5.
 */

public class AmmunitionSupport extends Flyer {

    public AmmunitionSupport(LeoEngine leoEngine) {
        super(leoEngine);
        setZ(500);//飞行高度
    }

    @Override
    public void initView() {
        Bitmap bomb = BitmapFactory.decodeResource(mLeoEngine.getResources(), R.mipmap.ufo1);
        setAnim(bomb);
        setVisable(true);
    }

    @Override
    public void getShoot() {

    }

    @Override
    public void dead() {
        setVisable(false);
    }

    @Override
    public void reset() {
        //小飞机参数
        life = 200;
        speed = 5;
        score = 200;
        x = mRandom.nextInt((int) (mLeoEngine.getGameWidth() - width));
        y = -height;
        setVisable(true);
    }
}
