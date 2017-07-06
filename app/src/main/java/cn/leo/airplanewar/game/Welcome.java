package cn.leo.airplanewar.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import android.widget.Toast;

import cn.leo.airplanewar.MainActivity;
import cn.leo.airplanewar.R;
import cn.leo.airplanewar.engine.Cell;
import cn.leo.airplanewar.engine.CellImage;
import cn.leo.airplanewar.engine.EngineControl;
import cn.leo.airplanewar.engine.LeoEngine;

/**
 * Created by Leo on 2017/7/5.
 */

public class Welcome {
    LeoEngine mLeoEngine;
    private final Context mContext;

    public Welcome(Context context, LeoEngine leoEngine) {
        mContext = context;
        mLeoEngine = leoEngine;
        initView();
    }

    //开始界面
    private void initView() {
        //界面元素资源
        Bitmap bg = BitmapFactory.decodeResource(mLeoEngine.getResources(), R.mipmap.background);
        Bitmap logo = BitmapFactory.decodeResource(mLeoEngine.getResources(), R.mipmap.shoot_copyright);
        Bitmap btn = BitmapFactory.decodeResource(mLeoEngine.getResources(), R.mipmap.game_start);

        int midX = mLeoEngine.getGameWidth() / 2;
        int midY = mLeoEngine.getHeight() / 2;
        //背景图
        CellImage bgCell = new CellImage(bg);
        mLeoEngine.addCell(bgCell);
        //logo
        CellImage logoCell = new CellImage(logo, midX - logo.getWidth() / 2,
                midY - logo.getHeight());
        mLeoEngine.addCell(logoCell);
        //按钮
        final CellImage btnCell = new CellImage(btn, midX - btn.getWidth() / 2, midY + btn.getHeight() * 3);
        mLeoEngine.addCell(btnCell);
        //mLeoEngine.loadSoundRsID(R.raw.game_music);
        mLeoEngine.playSound(R.raw.game_music, true); //播放背景音乐
        mLeoEngine.setControl(new EngineControl() {
            @Override
            public boolean onClick(Cell cell) {
                if (cell == btnCell) {
                    //点击开始游戏按钮
                    mLeoEngine.playSound(R.raw.button,false);
                    //Toast.makeText(mContext, "开始游戏", Toast.LENGTH_SHORT).show();
                    ((MainActivity) mContext).startGame();
                }
                return true;
            }

            @Override
            public void onTouch(Cell cell, MotionEvent motionEvent) {

            }
        });
        mLeoEngine.step();
    }


}
