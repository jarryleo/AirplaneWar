package cn.leo.airplanewar.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import cn.leo.airplanewar.R;
import cn.leo.airplanewar.engine.CellImage;
import cn.leo.airplanewar.engine.LeoEngine;

/**
 * Created by Leo on 2017/7/5.
 */

public class Bullet extends CellImage {
    private LeoEngine mEngine;
    private static Bitmap mBullet1Bitmap;
    private static Bitmap mBullet2Bitmap;
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_DOUBLE = 2;
    private int bulletType = TYPE_NORMAL;

    public Bullet(LeoEngine engine, float x, float y, int bulletType) {
        mEngine = engine;
        this.x = x;
        this.y = y;
        this.bulletType = bulletType;
        initView();
    }

    private void initView() {
        //加载子弹资源
        if (mBullet1Bitmap == null)
            mBullet1Bitmap = BitmapFactory.decodeResource(mEngine.getResources(), R.mipmap.bullet1);
        if (mBullet2Bitmap == null)
            mBullet2Bitmap = BitmapFactory.decodeResource(mEngine.getResources(), R.mipmap.bullet2);
        if (bulletType == TYPE_NORMAL) {
            setBitmap(mBullet2Bitmap);
        } else if (bulletType == TYPE_DOUBLE) {
            setBitmap(mBullet1Bitmap);
        }
        setType(TYPE_BITMAP_STANDARD);
        setVisable(true);
    }

    //设置子弹类型
    public void setBulletType(int bulletType) {
        this.bulletType = bulletType;
        if (bulletType == TYPE_NORMAL) {
            setBitmap(mBullet2Bitmap);
        } else if (bulletType == TYPE_DOUBLE) {
            setBitmap(mBullet1Bitmap);
        }
    }

    @Override
    public void event() {
        if (y < 0) {
            setVisable(false);
        } else {
            y -= 15;
        }
    }
}
