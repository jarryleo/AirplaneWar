package cn.leo.airplanewar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import cn.leo.airplanewar.engine.Cell;
import cn.leo.airplanewar.engine.EngineControl;
import cn.leo.airplanewar.engine.EngineListener;
import cn.leo.airplanewar.engine.EngineThread;
import cn.leo.airplanewar.engine.LeoEngine;
import cn.leo.airplanewar.game.BackGround;
import cn.leo.airplanewar.game.EnemyCenter;
import cn.leo.airplanewar.game.GameOver;
import cn.leo.airplanewar.game.HeroPlane;
import cn.leo.airplanewar.game.Score;
import cn.leo.airplanewar.game.Welcome;

public class MainActivity extends AppCompatActivity {

    private LeoEngine mEngine;
    private HeroPlane mHeroPlane;
    private BackGround mBackGround;
    private EnemyCenter mEnemyCenter;
    private Score mScore;
    private GameOver mGameOver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }


    private void initView() {
        mEngine = (LeoEngine) findViewById(R.id.engine);
        //mEngine.startEngine();
        mEngine.setEngineListener(new EngineListener() {
            @Override
            public void onEngineReady(LeoEngine engine) {
                new Welcome(MainActivity.this, mEngine);
            }
        });
    }

    public void startGame() {
        mEngine.startEngine();//引擎开启

        mEngine.clearCell();//清空开始界面
        //加载声音资源
        mEngine.loadSounds(R.raw.achievement, R.raw.big_spaceship_flying, R.raw.bullet,
                R.raw.button, R.raw.enemy1_down, R.raw.enemy2_down, R.raw.enemy3_down,
                R.raw.game_over, R.raw.get_bomb, R.raw.get_double_laser,
                R.raw.out_porp, R.raw.use_bomb);
        //加载动态背景
        mBackGround = new BackGround(mEngine);
        mEngine.addCell(mBackGround);
        //加载英雄飞机
        mHeroPlane = new HeroPlane(mEngine);
        mEngine.addCell(mHeroPlane);
        //加载敌人
        mEnemyCenter = new EnemyCenter(mEngine);
        //玩家飞机获知敌人控制中心
        mHeroPlane.setEnemyCenter(mEnemyCenter);
        //积分
        mScore = new Score(mEngine);
        mScore.setStr("积分：" + mHeroPlane.getScore());
        mEngine.addCell(mScore);
        //游戏结束面板
        mGameOver = new GameOver(mEngine);
        //操作监听
        initEvent();
        initGame();
    }

    //重新开始局
    private void restartGame() {
        mEnemyCenter.reset();
        mHeroPlane.reset();
        mGameOver.hide();
        mEngine.reStart();
        initEvent();
    }

    private int interval;

    private void initGame() {
        mEngine.addThread(new EngineThread() {
            @Override
            public void run() {
                //子弹击中敌人检测
                mHeroPlane.checkHit();
                mScore.setStr("积分：" + mHeroPlane.getScore());
                mBackGround.setBombNum(mHeroPlane.getBomb());
                interval--;
                if (interval < 0) {
                    interval = 5;
                    mEnemyCenter.makeEnemy();
                }
                if (!mHeroPlane.isVisable()) {
                    mGameOver.show(mHeroPlane.getScore());
                    mEngine.pauseEngine();
                    mEngine.step();
                }
            }
        });
    }


    private void initEvent() {
        mEngine.setControl(new EngineControl() {
            @Override
            public boolean onClick(Cell cell) { //点击事件
                if (cell == mBackGround.mCellMusicOn || cell == mBackGround.mCellMusicOff) {
                    mBackGround.musicToggle();
                    return true;
                }
                if (cell == mBackGround.mCellPauseGame || cell == mBackGround.mCellResumeGame) {
                    mBackGround.gameToggle();
                    return true;
                }
                if (cell == mBackGround.mBombCell) {
                    mHeroPlane.bomb();
                    return true;
                }

                if (cell == mGameOver.mAgain) {
                    restartGame();
                    return true;
                }

                if (cell == mGameOver.mOver) {
                    finish();
                    return true;
                }
                return false;
            }

            @Override
            public void onTouch(Cell cell, MotionEvent motionEvent) { //触摸事件
                //飞机移动
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        mHeroPlane.move(motionEvent.getX(), motionEvent.getY());
                        break;
                    case MotionEvent.ACTION_UP:
                        mHeroPlane.move(-1, -1);
                        break;
                }
            }
        });
    }
}
