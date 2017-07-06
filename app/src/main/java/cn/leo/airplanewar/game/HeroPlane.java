package cn.leo.airplanewar.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.leo.airplanewar.R;
import cn.leo.airplanewar.engine.CellAnimation;
import cn.leo.airplanewar.engine.LeoEngine;

/**
 * Created by Leo on 2017/7/5.
 */

public class HeroPlane extends CellAnimation {
    private int bomb = 1; //炸弹数
    private int life = 1;
    private int score = 0; //积分

    private LeoEngine mLeoEngine;
    private LeoAnim mHeroNormal;
    private LeoAnim mHeroDead;
    private float mMx;
    private float mMy;
    private int shootInterval; //射击间隔
    //子弹集合
    private List<Bullet> mBullets = new ArrayList<>();
    private List<Bullet> mBulletCache;
    //敌人集合
    private List<Flyer> mFlyerCache;
    private EnemyCenter mEnemyCenter;
    private boolean mBombFire;
    private boolean mdoubleBullet;
    private int mdoubleBulletTime;//双倍子弹时间

    public HeroPlane(LeoEngine leoEngine) {
        mLeoEngine = leoEngine;
        //缓存目的。敌人和子弹都是变化的。遍历的时候容易并发修改异常
        mFlyerCache = Collections.synchronizedList(new ArrayList<Flyer>());
        mBulletCache = Collections.synchronizedList(new ArrayList<Bullet>());
        initView();
    }

    private void initView() {
        //加载英雄飞机图片资源
        int[] id = new int[]{R.mipmap.hero1, R.mipmap.hero2,
                R.mipmap.hero_blowup_n1, R.mipmap.hero_blowup_n2,
                R.mipmap.hero_blowup_n3, R.mipmap.hero_blowup_n4};
        Bitmap[] heroBitmap = new Bitmap[id.length];
        for (int i = 0; i < id.length; i++) {
            heroBitmap[i] = BitmapFactory.decodeResource(mLeoEngine.getResources(), id[i]);
        }
        //飞机飞行动画
        mHeroNormal = new LeoAnim(
                new Bitmap[]{heroBitmap[0], heroBitmap[1]}, new int[]{100, 100}, true);
        //飞机挂掉动画
        mHeroDead = new LeoAnim(new Bitmap[]{heroBitmap[2], heroBitmap[3],
                heroBitmap[4], heroBitmap[5]},
                new int[]{100, 100, 100, 100}, false);

        int midX = mLeoEngine.getGameWidth() / 2;
        setAnim(mHeroNormal);
        setX(midX - width / 2);
        setY(mLeoEngine.getGameHeight() - height * 1.5f);
        setVisable(true);
    }

    //飞机挂掉
    public void dead() {
        if (life > 0) {
            life = 0;
            setAnim(mHeroDead);
            mLeoEngine.playSound(R.raw.game_over, false);
        }
    }

    public void move(float x, float y) {
        mMx = x;
        mMy = y;
    }

    @Override
    public void event() { //飞机移动
        if (life <= 0) return;
        shoot();
        if (mMx < 0 || mMy < 0) {
            return;
        }

        float cx = x + width / 2;
        float cy = y + height / 2;

        float offsetX = mMx - cx;
        float offsetY = mMy - cy;
        if (Math.abs(offsetX) < 10 && Math.abs(offsetY) < 10) {
            return;
        } else if (offsetX == 0) {
            y += (offsetY > 0 ? 1 : -1) * 10;
        } else if (offsetY == 0) {
            x += (offsetX > 0 ? 1 : -1) * 10;
        } else {
            float hypotenuse = (float) Math.sqrt(offsetX * offsetX + offsetY * offsetY);
            x += 20 * offsetX / hypotenuse;
            y += 20 * offsetY / hypotenuse;
        }

    }

    //重置玩家
    public void reset() {
        score = 0;
        life = 1;
        bomb = 1;
        mdoubleBullet = false;
        int midX = mLeoEngine.getGameWidth() / 2;
        setAnim(mHeroNormal);
        setX(midX - width / 2);
        setY(mLeoEngine.getGameHeight() - height * 1.5f);
        setVisable(true);
    }


    //发射子弹
    public void shoot() {
        shootInterval--;
        mdoubleBulletTime--;//双倍子弹时间递减
        if (mdoubleBulletTime <= 0) {
            mdoubleBullet = false;
        }
        if (shootInterval < 0) {
            mLeoEngine.playSound(R.raw.bullet, false);
            shootInterval = mLeoEngine.getFps() / 10; //子弹发射间隔。1秒10发
            boolean flag = true;
            int bulletNum = 0;
            //遍历已存在的子弹，如果有空闲的就复用，没有就新建
            for (int i = mBullets.size() - 1; i >= 0; i--) {
                Bullet bullet = mBullets.get(i);
                if (!bullet.isVisable()) {
                    if (mdoubleBullet) { //复用双发
                        int by = (int) (y + (height / 3));
                        if (bulletNum > 0) { //复用
                            int bx = (int) (x + (width / 9) * 7);
                            bullet.setX(bx);
                            bullet.setY(by);
                            bullet.setBulletType(Bullet.TYPE_DOUBLE);
                            bullet.setVisable(true);
                            flag = true;
                            bulletNum++;
                            break;
                        } else if (bulletNum == 0) {
                            int bx = (int) (x + (width / 13) * 2);
                            bullet.setX(bx);
                            bullet.setY(by);
                            bullet.setBulletType(Bullet.TYPE_DOUBLE);
                            bullet.setVisable(true);
                            bulletNum++;
                        }
                    } else { //复用单发
                        bullet.setX(x + width / 2);
                        bullet.setY(y);
                        bullet.setVisable(true);
                        flag = false;
                        break;
                    }
                }
            }
            if (flag) {  //新建子弹
                if (mdoubleBullet) {
                    if (bulletNum < 1) { //新建双发子弹
                        int bx1 = (int) (x + (width / 13) * 2);
                        int by1 = (int) (y + (height / 3));
                        Bullet bullet1 = new Bullet(mLeoEngine, bx1, by1, Bullet.TYPE_DOUBLE);
                        mBullets.add(bullet1);
                        mLeoEngine.addCell(bullet1);
                        int bx2 = (int) (x + (width / 9) * 7);
                        int by2 = (int) (y + (height / 3));
                        Bullet bullet2 = new Bullet(mLeoEngine, bx2, by2, Bullet.TYPE_DOUBLE);
                        mBullets.add(bullet2);
                        mLeoEngine.addCell(bullet2);
                    } else if (bulletNum == 1) { //新建双发剩余的
                        int bx = (int) (x + (width / 9) * 7);
                        int by = (int) (y + (height / 3));
                        Bullet bullet = new Bullet(mLeoEngine, bx, by, Bullet.TYPE_DOUBLE);
                        mBullets.add(bullet);
                        mLeoEngine.addCell(bullet);
                    }

                } else { //新建单发
                    Bullet bullet = new Bullet(mLeoEngine, x + width / 2, y, Bullet.TYPE_NORMAL);
                    mBullets.add(bullet);
                    mLeoEngine.addCell(bullet);
                }
            }

        }

    }

    //检查子弹射击情况
    public void checkHit() {
        if (mEnemyCenter == null) return;
        //扫描周围敌人
        mFlyerCache.clear();
        mFlyerCache.addAll(mEnemyCenter.getFlyerList());
        //子弹获知
        mBulletCache.clear();
        mBulletCache.addAll(mBullets);

        for (Flyer flyer : mFlyerCache) {
            for (Bullet bullet : mBulletCache) {
                if (flyer.getLife() > 0 && flyer.life < 100
                        && bullet.isVisable()
                        && mLeoEngine.hit(flyer, bullet, 1)) {
                    flyer.getShoot();//击中敌人
                    bullet.setVisable(false);//子弹消失
                    if (flyer.getLife() <= 0) { //敌人死亡
                        score += flyer.score; //积分增加
                    }
                }
            }
            if (flyer.getLife() > 0 && flyer.isVisable() &&
                    visable && mLeoEngine.hit(this, flyer, 1)) { //碰撞敌机
                flyer.dead();//敌机也要死
                score += flyer.score; //积分增加
                if (flyer.life == 100) {  //吃到炸弹补给
                    bomb++;
                    mLeoEngine.playSound(R.raw.get_bomb, false);
                } else if (flyer.life == 200) {//吃到双倍子弹补给
                    mdoubleBullet = true;
                    mLeoEngine.playSound(R.raw.get_double_laser, false);
                    mdoubleBulletTime = mLeoEngine.getFps() * 60; //双倍时间1分钟
                } else {
                    dead();//撞到敌机
                    mLeoEngine.playSound(R.raw.out_porp, false);
                }
            }
        }
        if (mBombFire) { //点击炸弹
            for (Flyer flyer : mFlyerCache) {
                if (mBombFire && flyer.getLife() > 0) { //炸弹
                    if (flyer.life < 100) { //不炸补给
                        flyer.dead();
                        score += flyer.score; //积分增加
                    }
                    continue;
                }
            }
            mBombFire = false;
        }
    }

    //炸弹攻击
    public void bomb() {
        if (bomb > 0) {
            mLeoEngine.playSound(R.raw.use_bomb, false);
            mBombFire = true;
            bomb--;
        }
    }

    //获取敌人中心
    public void setEnemyCenter(EnemyCenter enemyCenter) {
        mEnemyCenter = enemyCenter;
    }

    public int getScore() {
        return score;
    }

    public int getLife() {
        return life;
    }

    public int getBomb() {
        return bomb;
    }
}
