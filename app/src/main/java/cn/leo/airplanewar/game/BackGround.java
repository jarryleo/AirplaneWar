package cn.leo.airplanewar.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import cn.leo.airplanewar.R;
import cn.leo.airplanewar.engine.CellImage;
import cn.leo.airplanewar.engine.CellString;
import cn.leo.airplanewar.engine.LeoEngine;

/**
 * Created by Leo on 2017/7/5.
 */

public class BackGround extends CellImage {
    private LeoEngine mLeoEngine;
    private int move;
    private Bitmap mBg;
    private Rect mSrcRect1;
    private Rect mSrcRect2;
    private Rect mDestRect1;
    private Rect mDestRect2;
    private float mDis;
    private boolean music = true;
    public CellImage mCellMusicOn;
    public CellImage mCellMusicOff;
    public CellImage mCellPauseGame;
    public CellImage mCellResumeGame;
    public CellImage mBombCell;
    private CellString mBombNum;

    public BackGround(LeoEngine leoEngine) {
        mLeoEngine = leoEngine;
        initView();
    }

    private void initView() {
        //加载背景图资源
        mBg = BitmapFactory.decodeResource(mLeoEngine.getResources(), R.mipmap.background);

        //游戏暂停资源
        Bitmap pauseGame = BitmapFactory.decodeResource(mLeoEngine.getResources(), R.mipmap.game_pause_pressed);
        Bitmap resumeGame = BitmapFactory.decodeResource(mLeoEngine.getResources(), R.mipmap.game_resume_pressed);
        mCellPauseGame = new CellImage(pauseGame,
                mLeoEngine.getGameWidth() - pauseGame.getWidth() - 30, 30);
        mCellResumeGame = new CellImage(resumeGame,
                mLeoEngine.getGameWidth() - resumeGame.getWidth() - 30, 30);
        mCellResumeGame.setVisable(false);
        mCellPauseGame.setZ(1000);
        mCellResumeGame.setZ(1000);
        mLeoEngine.addCell(mCellResumeGame);
        mLeoEngine.addCell(mCellPauseGame);


        //声音开关资源
        Bitmap musicOn = BitmapFactory.decodeResource(mLeoEngine.getResources(), R.mipmap.bkmusic_play);
        Bitmap musicOff = BitmapFactory.decodeResource(mLeoEngine.getResources(), R.mipmap.bkmusic_close);
        mCellMusicOn = new CellImage(musicOn, mLeoEngine.getGameWidth() - musicOn.getWidth() - 30,
                mCellPauseGame.getY() + mCellPauseGame.getHeight() + 30);
        mCellMusicOff = new CellImage(musicOff, mLeoEngine.getGameWidth() - musicOff.getWidth() - 30,
                mCellPauseGame.getY() + mCellPauseGame.getHeight() + 30);
        mCellMusicOff.setVisable(false);
        mCellMusicOff.setZ(1000);
        mCellMusicOn.setZ(1000);
        mLeoEngine.addCell(mCellMusicOn);
        mLeoEngine.addCell(mCellMusicOff);

        //炸弹按钮
        Bitmap bombBitmap = BitmapFactory.decodeResource(mLeoEngine.getResources(), R.mipmap.bomb);
        mBombCell = new CellImage(bombBitmap, 30, mLeoEngine.getGameHeight() - bombBitmap.getHeight() - 30);
        mBombCell.setZ(2000);
        mLeoEngine.addCell(mBombCell);

        //炸弹数字
        mBombNum = new CellString("X0", mBombCell.getX() + mBombCell.getWidth() + 20,
                mLeoEngine.getGameHeight() - bombBitmap.getHeight() - 30,
                (int) (20 * mLeoEngine.getDensity()), Color.GRAY);
        mBombNum.setTextAlign(Paint.Align.CENTER);
        mBombNum.setZ(2000);
        mLeoEngine.addCell(mBombNum);

        mDis = mLeoEngine.getGameHeight() * 1.0f / mBg.getHeight();
        move();
        setVisable(true);
    }

    //设置炸弹数显示
    public void setBombNum(int num) {
        mBombNum.setStr("X" + num);
    }

    //音乐开关
    public void musicToggle() {
        if (!mLeoEngine.isRunning()) return;
        mLeoEngine.playSound(R.raw.button, false);
        music = !music;
        if (music) {
            mLeoEngine.playSound(R.raw.game_music, true);
            mCellMusicOn.setVisable(true);
            mCellMusicOff.setVisable(false);
        } else {
            mLeoEngine.stopSound(R.raw.game_music);
            mCellMusicOn.setVisable(false);
            mCellMusicOff.setVisable(true);
        }
    }

    //游戏暂停开关
    public void gameToggle() {
        if (mLeoEngine.isRunning()) {
            mCellResumeGame.setVisable(true);
            mCellPauseGame.setVisable(false);
            mLeoEngine.pauseEngine();
        } else {
            mCellResumeGame.setVisable(false);
            mCellPauseGame.setVisable(true);
            mLeoEngine.reStart();
        }
        mLeoEngine.playSound(R.raw.button, false);
        mLeoEngine.step();
    }


    private void move() {
        //背景图移动
        mSrcRect1 = new Rect(0, 0, mBg.getWidth(), mBg.getHeight() - move);
        mDestRect1 = new Rect(0, (int) (move * mDis + 0.5), mLeoEngine.getGameWidth(), mLeoEngine.getGameHeight());

        mSrcRect2 = new Rect(0, mBg.getHeight() - move, mBg.getWidth(), mBg.getHeight());
        mDestRect2 = new Rect(0, 0, mLeoEngine.getGameWidth(), (int) (move * mDis + 0.5));
    }

    @Override
    public void event() {
        move += 2;
        if (move > mBg.getHeight()) {
            move = 0;
        }
        move();
    }

    @Override
    public void draw(Canvas canvas) {
        //绘制背景图
        canvas.drawBitmap(mBg, mSrcRect1, mDestRect1, paint);
        canvas.drawBitmap(mBg, mSrcRect2, mDestRect2, paint);
    }
}
