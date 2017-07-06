package cn.leo.airplanewar.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;

import cn.leo.airplanewar.R;
import cn.leo.airplanewar.engine.CellImage;
import cn.leo.airplanewar.engine.CellString;
import cn.leo.airplanewar.engine.LeoEngine;

/**
 * Created by Leo on 2017/7/6.
 */

public class GameOver {
    private LeoEngine mLeoEngine;
    private CellImage mBgGameOver;
    public CellImage mAgain;
    public CellImage mOver;
    private CellString mScore;
    private CellString mHighScore;

    public GameOver(LeoEngine leoEngine) {
        mLeoEngine = leoEngine;
        initView();
    }

    private void initView() {
        Bitmap bg = BitmapFactory.decodeResource(mLeoEngine.getResources(), R.mipmap.gameover);
        Bitmap again = BitmapFactory.decodeResource(mLeoEngine.getResources(), R.mipmap.game_again);
        Bitmap over = BitmapFactory.decodeResource(mLeoEngine.getResources(), R.mipmap.game_over);
        mBgGameOver = new CellImage(bg);
        mAgain = new CellImage(again, mLeoEngine.getGameWidth() / 2 - again.getWidth() / 2,
                mLeoEngine.getGameHeight() / 2 + again.getHeight() * 2);
        mOver = new CellImage(over, mLeoEngine.getGameWidth() / 2 - again.getWidth() / 2,
                mLeoEngine.getGameHeight() / 2 + over.getHeight() * 4);
        mBgGameOver.setZ(10000);
        mAgain.setZ(11000);
        mOver.setZ(11000);
        mBgGameOver.setVisable(false);
        mAgain.setVisable(false);
        mOver.setVisable(false);
        mLeoEngine.addCell(mBgGameOver);
        mLeoEngine.addCell(mAgain);
        mLeoEngine.addCell(mOver);

        mScore = new CellString("0", mLeoEngine.getGameWidth() / 2,
                mLeoEngine.getGameHeight() / 2 - 50*mLeoEngine.getDensity(),
                (int) (40 * mLeoEngine.getDensity()), Color.GRAY);
        mScore.setTextAlign(Paint.Align.CENTER);
        mScore.setVisable(false);
        mScore.setZ(12000);
        mLeoEngine.addCell(mScore);

        mHighScore = new CellString("0", mLeoEngine.getGameWidth() / 3,
                45 * mLeoEngine.getDensity(),
                (int) (30 * mLeoEngine.getDensity()), Color.GRAY);
        mHighScore.setTextAlign(Paint.Align.CENTER);
        mHighScore.setVisable(false);
        mHighScore.setZ(12000);
        mLeoEngine.addCell(mHighScore);

    }

    public void show(int score) {
        Context context = mLeoEngine.getContext();
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        int highScore = sp.getInt("score", 0);
        mHighScore.setStr("" + highScore);
        mScore.setStr("" + score);
        if (score > highScore) { //保存最高纪录
            mLeoEngine.playSound(R.raw.achievement, false);
            SharedPreferences.Editor edit = sp.edit();
            edit.putInt("score", score);
            edit.commit();
        }

        mBgGameOver.setVisable(true);
        mAgain.setVisable(true);
        mOver.setVisable(true);
        mScore.setVisable(true);
        mHighScore.setVisable(true);
    }

    public void hide() {
        mBgGameOver.setVisable(false);
        mAgain.setVisable(false);
        mOver.setVisable(false);
        mScore.setVisable(false);
        mHighScore.setVisable(false);
    }

}
