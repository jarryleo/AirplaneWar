package cn.leo.airplanewar.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.leo.airplanewar.engine.LeoEngine;

/**
 * Created by yjtx2 on 2017/7/5.
 */

public class EnemyCenter {
    private LeoEngine mLeoEngine;
    private List<Flyer> mFlyerList = new ArrayList<>();
    private Random mRandom;
    private int enemyNum;

    public EnemyCenter(LeoEngine leoEngine) {
        mLeoEngine = leoEngine;
        mRandom = new Random();
        enemyNum = 30; //同屏敌人总数

    }

    public int getEnemyNum() {
        return enemyNum;
    }

    public void setEnemyNum(int enemyNum) {
        this.enemyNum = enemyNum;
    }

    public List<Flyer> getFlyerList() {
        return mFlyerList;
    }


    //获取存活的敌人数
    private int getLifeEnemy() {
        int lifers = 0;
        for (Flyer flyer : mFlyerList) {
            if (flyer.isVisable()) {
                lifers++;
            }
        }
        return lifers;
    }

    //重置敌人
    public void reset() {
        for (Flyer flyer : mFlyerList) {
            flyer.setVisable(false);
        }
    }

    //产生敌人
    public void makeEnemy() {
        int r = mRandom.nextInt(100);
        if (getLifeEnemy() > enemyNum) { //总敌人
            return;
        }
        if (r > 30) { //7成小飞机
            boolean flag = true;
            for (Flyer flyer : mFlyerList) { //复用飞机
                if (flyer instanceof LittleEnemy) {
                    if (!flyer.isVisable()) {
                        flyer.reset();
                        flag = false;
                        break;
                    }
                }
            }
            if (flag) { //创建新的小飞机
                Flyer flyer = new LittleEnemy(mLeoEngine);
                mLeoEngine.addCell(flyer);
                mFlyerList.add(flyer);
            }
        } else if (r < 10) { //1成大飞机
            boolean flag = true;
            for (Flyer flyer : mFlyerList) { //复用飞机
                if (flyer instanceof BigEnemy) {
                    if (!flyer.isVisable()) {
                        flyer.reset();
                        flag = false;
                        break;
                    }
                }
            }
            if (flag) { //创建新的大飞机
                Flyer flyer = new BigEnemy(mLeoEngine);
                mLeoEngine.addCell(flyer);
                mFlyerList.add(flyer);
            }
        } else {//2成中等飞机
            boolean flag = true;
            for (Flyer flyer : mFlyerList) { //复用飞机
                if (flyer instanceof MiddleEnemy) {
                    if (!flyer.isVisable()) {
                        flyer.reset();
                        flag = false;
                        break;
                    }
                }
            }
            if (flag) { //创建新的中飞机
                Flyer flyer = new MiddleEnemy(mLeoEngine);
                mLeoEngine.addCell(flyer);
                mFlyerList.add(flyer);
            }
        }

        if (r % 20 == 0) { //产生双子弹
            boolean flag = true;
            for (Flyer flyer : mFlyerList) { //复用
                if (flyer instanceof AmmunitionSupport) {
                    if (!flyer.isVisable()) {
                        flyer.reset();
                        flag = false;
                        break;
                    }
                }
            }
            if (flag) { //创建
                Flyer flyer = new AmmunitionSupport(mLeoEngine);
                mLeoEngine.addCell(flyer);
                mFlyerList.add(flyer);
            }
        }

        if (r % 20 == 1) { //产生炸弹补给
            boolean flag = true;
            for (Flyer flyer : mFlyerList) { //复用
                if (flyer instanceof BombSupport) {
                    if (!flyer.isVisable()) {
                        flyer.reset();
                        flag = false;
                        break;
                    }
                }
            }
            if (flag) { //创建
                Flyer flyer = new BombSupport(mLeoEngine);
                mLeoEngine.addCell(flyer);
                mFlyerList.add(flyer);
            }
        }
    }

}
