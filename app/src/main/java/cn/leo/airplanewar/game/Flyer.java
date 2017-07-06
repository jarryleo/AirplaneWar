package cn.leo.airplanewar.game;

import java.util.Random;

import cn.leo.airplanewar.engine.CellAnimation;
import cn.leo.airplanewar.engine.LeoEngine;

/**
 * Created by yjtx2 on 2017/7/5.
 */

public abstract class Flyer extends CellAnimation {
    protected int life; //血量
    protected int score;//击杀得到分数
    protected int speed;//移动速度
    protected Random mRandom = new Random();

    public LeoEngine mLeoEngine;

    public Flyer(LeoEngine leoEngine) {
        mLeoEngine = leoEngine;
        initView();
        reset();
    }

    public abstract void initView();

    public abstract void getShoot();

    public abstract void dead();

    public abstract void reset();

    @Override
    public void event() { //动作
        y += speed;
        if (y > mLeoEngine.getGameHeight()) {
            setVisable(false);
        }
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
