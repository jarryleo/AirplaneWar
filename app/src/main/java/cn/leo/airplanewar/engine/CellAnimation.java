package cn.leo.airplanewar.engine;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Leo on 2017/7/3.
 */

public class CellAnimation extends Cell {

    public static final int CORNER_TOP_LEFT = 0; //动画固定角左上
    public static final int CORNER_TOP_RIGHT = 1;//动画固定角右上
    public static final int CORNER_BOTTOM_LEFT = 2;//动画固定角左下
    public static final int CORNER_BOTTOM_RIGHT = 3;//动画固定角右下
    private LeoAnim mAnim; //动画组
    private Bitmap staticImage; //静态画面
    private int corner;//左上0,右上1,左下2,右下3,动画坐标原点(固定角)
    private int currentFrame = 0; //当前帧
    private long sumTime; //动画总时长
    private long startTime; //动画开始播放时间
    private long mPathTime;//动画播放进度
    private boolean isPlaying; //动画是否在播放状态
    private boolean loop; //动画是否循环播放
    private boolean complete; //动画是否播放完毕
    private boolean staticMode;// 是否静态画面
    private boolean fillAfter;//播放完是否保持最后一张画面
    private boolean autoStart = true;//是否自动开始播放动画
    private AnimListener mAnimListener;


    /**
     * 允许空参，但不可见
     */
    public CellAnimation() {
        visable = false;
    }

    /**
     * 构造默认固定角左上
     *
     * @param anim
     */
    public CellAnimation(LeoAnim anim) {
        this(anim, 0);
    }

    /**
     * 构造自定义固定角(图片和时间数组长度要相等)
     *
     * @param anim
     * @param corner
     */
    public CellAnimation(LeoAnim anim, int corner) {
        this.corner = corner;
        setAnim(anim);
    }

    /**
     * 初始静态画面构造
     *
     * @param staticImage
     */
    public CellAnimation(Bitmap staticImage) {
        this.staticImage = staticImage;
        width = staticImage.getWidth();
        height = staticImage.getHeight();
        staticMode = true;
    }

    /**
     * 设置动画组，即时修改动画
     *
     * @param anim
     */
    public CellAnimation setAnim(LeoAnim anim) {
        if (anim.bitmaps == null || anim.times == null ||
                anim.bitmaps.length != anim.times.length || anim.bitmaps.length < 1) {
            throw new NullPointerException("动画帧数和持续时间不等");
        }
        sumTime = 0;
        mAnim = anim;
        loop = anim.loop;
        staticMode = false;
        this.width = anim.bitmaps[0].getWidth();
        this.height = anim.bitmaps[0].getHeight();
        for (int i = 0; i < anim.times.length; i++) {
            sumTime += anim.times[i];
        }
        if (autoStart) {
            start(); //设置完后自动播放
        }
        return this;
    }

    /**
     * 设置静态画面
     *
     * @param bitmap
     */
    public CellAnimation setAnim(Bitmap bitmap) {
        staticImage = bitmap;
        staticMode = true;
        complete = false;
        width = staticImage.getWidth();
        height = staticImage.getHeight();
        return this;
    }

    /**
     * 获取动画是否循环播放
     *
     * @return
     */
    public boolean isLoop() {
        return loop;
    }

    /**
     * 设置固定角
     *
     * @param corner
     */
    public CellAnimation setCorner(int corner) {
        this.corner = corner;
        return this;
    }

    /**
     * 设置是否循环播放
     *
     * @param loop
     */
    public CellAnimation setLoop(boolean loop) {
        this.loop = loop;
        return this;
    }

    /**
     * 获取动画是否播放完毕
     *
     * @return
     */
    public boolean isComplete() {
        return complete;
    }


    /**
     * 设置动画播放完毕
     *
     * @param complete
     */
    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    /**
     * 设置是否自动开始播放
     *
     * @param autoStart
     */
    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public boolean isFillAfter() {
        return fillAfter;
    }

    /**
     * 设置播放完是否保持最后一帧画面
     *
     * @param fillAfter
     */
    public CellAnimation setFillAfter(boolean fillAfter) {
        this.fillAfter = fillAfter;
        return this;
    }

    /**
     * 播放状态
     *
     * @return
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * 设置播放状态
     *
     * @param playing
     */
    public CellAnimation setPlaying(boolean playing) {
        isPlaying = playing;
        mPathTime = System.currentTimeMillis() - startTime;
        return this;
    }

    /**
     * 引擎获取动画当前帧
     *
     * @param passTime
     * @return
     */
    public Bitmap getBitmap(long passTime) {
        if (staticMode) {
            return staticImage;
        }
        if (passTime > sumTime && !loop) {
            isPlaying = false;
            complete = true;
            if (mAnimListener != null) {
                mAnimListener.onComplete(this);
            }
        }
        if (sumTime <= 0) {
            return staticImage;
        }
        passTime = passTime % sumTime;
        if (!isPlaying) { //动画暂停，返回静止画面
            startTime = System.currentTimeMillis() - mPathTime;
            return mAnim.bitmaps[currentFrame];
        }
        long sum = 0;
        for (int i = 0; i < mAnim.times.length; i++) {
            sum += mAnim.times[i];
            if (sum > passTime) {
                currentFrame = i;
                return mAnim.bitmaps[i];
            }
        }
        return mAnim.bitmaps[0];
    }

    /**
     * 开始播放动画
     */
    public void start() {
        startTime = System.currentTimeMillis();
        isPlaying = true;
        complete = false;
        currentFrame = 0;
        setVisable(true);
    }

    /**
     * 引擎获取固定角
     *
     * @return
     */
    public int getCorner() {
        return corner;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (!staticMode && complete && !fillAfter) {
            setVisable(false);
            return;
        }
        Bitmap animBitmap = getBitmap(System.currentTimeMillis() - startTime);
        switch (corner) {
            case CORNER_TOP_LEFT:
                canvas.drawBitmap(animBitmap, x, y, paint);
                break;
            case CORNER_TOP_RIGHT:
                canvas.drawBitmap(animBitmap, x +
                        (mAnim.bitmaps[0].getWidth() - animBitmap.getWidth()), y, paint);
                break;
            case CORNER_BOTTOM_LEFT:
                canvas.drawBitmap(animBitmap, x,
                        y + (mAnim.bitmaps[0].getHeight() - animBitmap.getHeight()), paint);
                break;
            case CORNER_BOTTOM_RIGHT:
                canvas.drawBitmap(animBitmap,
                        x + (mAnim.bitmaps[0].getWidth() - animBitmap.getWidth()),
                        y + (mAnim.bitmaps[0].getHeight() - animBitmap.getHeight()), paint);
                break;
        }

    }

    @Override
    public int getCellType() {
        return Cell.TYPE_ANIMATION;
    }

    @Override
    public void setDistroy(boolean distroy) {
        super.setDistroy(distroy);
        for (int i = 0; i < mAnim.bitmaps.length; i++) {
            mAnim.bitmaps[i].recycle();
        }
        mAnim = null;
    }

    /**
     * 动画组单元
     */
    public static class LeoAnim {
        public Bitmap[] bitmaps; //帧动画,每帧的bitmap图像
        public int[] times; //每帧动画持续的时间
        public boolean loop; //动画是否重复播放

        public LeoAnim(Bitmap[] bitmaps, int[] times, boolean loop) {
            this.bitmaps = bitmaps;
            this.times = times;
            this.loop = loop;
        }
    }

    public AnimListener getAnimListener() {
        return mAnimListener;
    }

    public void setAnimListener(AnimListener animListener) {
        mAnimListener = animListener;
    }

    public interface AnimListener {
        void onComplete(CellAnimation cellAnimation);
    }
}
