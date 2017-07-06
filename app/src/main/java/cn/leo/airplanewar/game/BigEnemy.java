package cn.leo.airplanewar.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import cn.leo.airplanewar.R;
import cn.leo.airplanewar.engine.LeoEngine;

/**
 * Created by yjtx2 on 2017/7/5.
 */

public class BigEnemy extends Flyer {

    private static Bitmap[] mBitmaps;
    private static LeoAnim mAnim1;
    private static LeoAnim mAnim;

    public BigEnemy(LeoEngine leoEngine) {
        super(leoEngine);
        setZ(300); //飞行高度
    }

    @Override
    public void initView() {
        if (mBitmaps == null) {
            //加载飞机资源
            int[] ids = new int[]{R.mipmap.enemy3_n1, R.mipmap.enemy3_n2, R.mipmap.enemy3_down1,
                    R.mipmap.enemy3_down2, R.mipmap.enemy3_down3, R.mipmap.enemy3_down4,
                    R.mipmap.enemy3_down5, R.mipmap.enemy3_down6, R.mipmap.enemy3_hit};

            mBitmaps = new Bitmap[ids.length];
            for (int i = 0; i < ids.length; i++) {
                mBitmaps[i] = BitmapFactory.decodeResource(mLeoEngine.getResources(),
                        ids[i]);
            }
        }
        if (mAnim == null) {
            //飞机爆炸动画
            mAnim = new LeoAnim(new Bitmap[]{mBitmaps[2],
                    mBitmaps[3], mBitmaps[4], mBitmaps[5],
                    mBitmaps[6], mBitmaps[7]},
                    new int[]{100, 100, 100, 100, 100, 100}, false);
        }
        if (mAnim1 == null) {
            mAnim1 = new LeoAnim(new Bitmap[]{mBitmaps[0],
                    mBitmaps[1]},
                    new int[]{200, 200}, true);
        }
        setAnim(mAnim1);
        setVisable(true);
    }

    @Override
    public void getShoot() {
        if (life > 0) {
            life--;
        } else {
            return;
        }
        if (life < 3) {
            setAnim(mBitmaps[8]);
        }
        if (life <= 0) {
            dead();
        }
    }

    @Override
    public void dead() {
        life = 0;
        setAnim(mAnim);
        mLeoEngine.playSound(R.raw.enemy3_down, false);
        mLeoEngine.stopSound(R.raw.big_spaceship_flying);
    }

    @Override
    public void reset() {
        //飞机参数
        life = 5;
        speed = 6;
        score = 10;
        x = mRandom.nextInt((int) (mLeoEngine.getGameWidth() - width));
        y = -height;
        setAnim(mAnim1);
        setVisable(true);
        mLeoEngine.playSound(R.raw.big_spaceship_flying, true);
    }
}
