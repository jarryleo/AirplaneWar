package cn.leo.airplanewar.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import cn.leo.airplanewar.R;
import cn.leo.airplanewar.engine.LeoEngine;

/**
 * Created by yjtx2 on 2017/7/5.
 */

public class MiddleEnemy extends Flyer {

    private static Bitmap[] mBitmaps;
    private static LeoAnim mAnim;

    public MiddleEnemy(LeoEngine leoEngine) {
        super(leoEngine);
        setZ(400);//飞行高度
    }

    @Override
    public void initView() {
        if (mBitmaps == null) {
            //加载飞机资源
            int[] ids = new int[]{R.mipmap.enemy2, R.mipmap.enemy2_down1,
                    R.mipmap.enemy2_down2, R.mipmap.enemy2_down3, R.mipmap.enemy2_down4,
                    R.mipmap.enemy2_hit};

            mBitmaps = new Bitmap[ids.length];
            for (int i = 0; i < ids.length; i++) {
                mBitmaps[i] = BitmapFactory.decodeResource(mLeoEngine.getResources(),
                        ids[i]);
            }
        }
        if (mAnim == null) {
            //飞机爆炸动画
            mAnim = new LeoAnim(new Bitmap[]{mBitmaps[1],
                    mBitmaps[2], mBitmaps[3], mBitmaps[4]},
                    new int[]{100, 100, 100, 100}, false);
        }
        setAnim(mBitmaps[0]);
        setVisable(true);
    }

    @Override
    public void getShoot() {
        if (life > 0) {
            life--;
        } else {
            return;
        }
        if (life < 2) {
            setAnim(mBitmaps[5]);
        }
        if (life <= 0) {
            dead();
        }
    }

    @Override
    public void dead() {
        life = 0;
        setAnim(mAnim);
        mLeoEngine.playSound(R.raw.enemy2_down, false);
    }

    @Override
    public void reset() {
        //飞机参数
        life = 2;
        speed = 8;
        score = 5;
        x = mRandom.nextInt((int) (mLeoEngine.getGameWidth() - width));
        y = -height;
        setAnim(mBitmaps[0]);
        setVisable(true);
    }
}
