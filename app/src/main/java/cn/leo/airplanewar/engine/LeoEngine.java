package cn.leo.airplanewar.engine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by JarryLeo on 2017/2/24.
 * 本引擎使用方法,在布局上创建一个本引擎控件即可
 */

public class LeoEngine extends SurfaceView implements Runnable {

    /**
     * 主holder
     */
    private SurfaceHolder mHolder;
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 屏幕宽
     */
    private int screenWidth;
    /**
     * 屏幕高
     */
    private int screenHeight;
    /**
     * 游戏区域宽
     */
    private int gameWidth;
    /**
     * 游戏区域高
     */
    private int gameHeight;
    /**
     * 游戏帧率,每秒画面刷新次数 ,默认30帧
     */
    private int fps = 30;
    /**
     * 引擎所有图像单元集合
     */
    private List<Cell> cells = new ArrayList<>();
    /**
     * 元素缓存
     */
    private List<Cell> cellsCache;
    /**
     * 引擎所有子线程单元集合
     */
    private List<LeoEngineThread> threads = new ArrayList<>();

    /**
     * 引擎所有声音单元集合
     */
    private ConcurrentHashMap<Integer, Integer> sounds = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, Integer> soundsStop = new ConcurrentHashMap<>();
    /**
     * 引擎声音池
     */
    private SoundPool sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
    /**
     * 引擎触控事件控制器
     */
    private EngineControl mControl;
    /**
     * 引擎状态监听
     */
    private EngineListener mEngineListener;
    /**
     * 引擎运行状态/暂停游戏用
     */
    private boolean isRunning = true;
    /**
     * 引擎可见状态,游戏最小化用
     */
    private boolean isVisiable = false;
    /**
     * 初始化完成状态
     */
    private boolean ready;
    /**
     * 引擎存活状态,销毁引擎用
     */
    private boolean live = true;
    /**
     * 声音状态
     */
    private boolean hasSound = true;
    /**
     * 引擎主线程
     */
    private Thread engineThread;
    private boolean mSort;


    public LeoEngine(Context context) {
        this(context, null);
    }

    public LeoEngine(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LeoEngine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initEngine();
    }

    /**
     * 初始化引擎
     */
    private void initEngine() {
        //拿取父类holder
        mHolder = getHolder();
        //本类上下文
        mContext = getContext();
        //本引擎view置顶显示
        setZOrderOnTop(true);
        //设置本类回调
        mHolder.addCallback(mCallBack);
        //屏幕宽度
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        //屏幕高度
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        //元素缓存
        cellsCache = Collections.synchronizedList(new ArrayList<Cell>());
    }

    /**
     * 本类holderBack回调
     */
    final SurfaceHolder.Callback mCallBack = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            //窗口可见
            isVisiable = true;
            soundResume();
            if (!ready) {
                ready = true;
                if (mEngineListener != null) {
                    mEngineListener.onEngineReady(LeoEngine.this);
                }
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            //窗口改变状态
            //游戏可见区域宽
            gameWidth = width;
            //游戏可见区域高
            gameHeight = height;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            //窗口不可见
            isVisiable = false;
            soundPause();
        }
    };

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //游戏可见区域宽
        gameWidth = getMeasuredWidth();
        //游戏可见区域高
        gameHeight = getMeasuredHeight();
    }


    /**
     * 启动游戏引擎,不能直接调用run方法
     */
    public void startEngine() {
        if (engineThread != null) {
            return;
        }
        engineThread = new Thread(this);
        engineThread.start();
    }

    public void run() { //引擎主进程
        while (live) {
            if (isRunning && (cells.size() > 0 ||
                    cellsCache.size() > 0) && isVisiable) {
                core();
            }
            if (isVisiable) {
                SystemClock.sleep(1000 / fps); //线程休息时间
            } else {
                SystemClock.sleep(100); //暂停的时候10fps
            }
        }
    }

    /**
     * 引擎工作主方法,绘制所有单元图像
     */
    public void step() {
        core();
    }

    /**
     * 引擎核心
     *
     * @return
     */
    private void core() {
        Canvas canvas = mHolder.lockCanvas();
        if (canvas == null) {
            return;
        }
        //从缓冲拿取元素
        if (cellsCache.size() > 0) {
            cells.addAll(cellsCache);
            cellsCache.clear();
        }
        //元素层级排序
        if (mSort) {
            Collections.sort(cells);
            mSort = false;
        }
        //引擎每帧事件
        if (mEngineListener != null) {
            mEngineListener.onFrameFresh(this);
        }
        //清空画布
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        Iterator<Cell> iterator = cells.iterator();
        while (iterator.hasNext()) {
            Cell lec = iterator.next();
            //移除被销毁的单元
            if (lec.isDistroy()) {
                iterator.remove();
                continue;
            }
            lec.event();//调用单元事件
            //单元不可见
            if (!lec.isVisable()) {
                continue;
            }

            lec.draw(canvas); //绘制单元
        }
        mHolder.unlockCanvasAndPost(canvas);
    }

    /**
     * 加载子线程 ，如果想在引擎加载自己的线程，调用此方法,
     * 子线程内不需要加死循环，引擎会自动循环调用，
     * 也不需要加休眠间隔，引擎按照帧率频率访问
     *
     * @param thread
     */
    public void addThread(EngineThread thread) {
        LeoEngineThread et = new LeoEngineThread(thread);
        et.start();
        threads.add(et);
    }

    /**
     * 子线程类
     */
    private class LeoEngineThread extends Thread {
        private EngineThread let;

        public LeoEngineThread(EngineThread thread) {
            this.let = thread;
        }

        public void run() {
            while (live) {
                if (isRunning && isVisiable) {
                    let.run(); //调用子线程的方法
                    SystemClock.sleep(1000 / fps);
                } else {
                    SystemClock.sleep(100); //暂停时候10fps，休眠时间，减少资源消耗
                }
            }
        }
    }

    /**
     * 如果你动态改变元素的层级，请手动调用刷新
     */
    public void sortCell() {
        mSort = true;
    }

    /**
     * 播放声音无参 （若无提前加载声音资源，第一次播放会延迟）
     *
     * @param rsID 资源ID
     */
    public void playSound(int rsID, boolean loop) {
        int l = loop ? -1 : 0;
        playSound(rsID, 1.0f, 1.0f, 0, l, 1.0f);
    }

    /**
     * 带参数的声音播放方法
     *
     * @param rsID        资源id
     * @param leftVolume  左声道音量大小 0.0f - 1.0f
     * @param rightVolume 右声道音量大小 0.0f - 1.0f
     * @param priority    优先级 默认0
     * @param loop        循环播放-1 不循环0
     * @param rate        声音倍速 0.5f - 2.0f ,正常倍速 1.0f
     */
    public void playSound(final int rsID, final float leftVolume, final float rightVolume,
                          final int priority, final int loop, final float rate) {
        if (!hasSound) return;
        if (sounds.containsKey(rsID)) {
            int playId = sp.play(sounds.get(rsID), leftVolume, rightVolume, priority, loop, rate);
            soundsStop.put(rsID, playId);
        } else {
            final int id = sp.load(mContext, rsID, 1);
            sounds.put(rsID, id);
            sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    int playId = soundPool.play(id, leftVolume, rightVolume, priority, loop, rate);
                    soundsStop.put(rsID, playId);
                    sp.setOnLoadCompleteListener(null);
                }
            });
        }
    }

    /**
     * 全部静音
     */
    public void soundOff() {
        hasSound = false;
        soundPause();
    }

    /**
     * 开启声音
     */
    public void soundOn() {
        hasSound = true;
        soundResume();
    }

    /**
     * 停止播放音乐
     *
     * @param rsID
     */
    public void stopSound(int rsID) {
        sp.stop(soundsStop.remove(rsID));
    }

    /**
     * 暂停指定的音乐
     *
     * @param rsID
     */
    public void pauseSound(int rsID) {
        sp.pause(sounds.get(rsID));
    }

    /**
     * 恢复暂停的声音
     *
     * @param rsID
     */
    public void resumeSound(int rsID) {
        sp.resume(sounds.get(rsID));
    }

    /**
     * 获取引擎状态
     *
     * @return
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * 提前加载声音资源，在第一次播放的时候就不会延迟
     *
     * @param rsID
     */
    public void loadSoundRsID(int rsID) {
        int id = sp.load(mContext, rsID, 1);
        sounds.put(rsID, id);
    }

    /**
     * 提前批量加载声音资源
     *
     * @param rsId
     */
    public void loadSounds(int... rsId) {
        for (int i = 0; i < rsId.length; i++) {
            loadSoundRsID(rsId[i]);
        }
    }

    /**
     * 添加显示单元
     *
     * @param lec
     */
    public void addCell(Cell lec) { //添加显示的图片元素
        if (cells.contains(lec) || cellsCache.contains(lec)) return;
        cellsCache.add(lec);
        if (lec.getZ() == 0) {
            lec.setZ(cells.size() * 10 + 1);
        }
        sortCell();
    }

    /**
     * 移除显示单元
     *
     * @param lec
     */
    public void removeCell(Cell lec) { //移除显示的图片元素
        lec.setDistroy(true);
    }

    /**
     * 清空所有显示单元
     */
    public void clearCell() {
        for (Cell cell : cells) {
            cell.setDistroy(true); //所有显示元素清空
        }
    }

    /**
     * 清空所有声音单元
     */
    public void clearSounds() {
        for (int sd : sounds.keySet()) {
            sp.unload(sounds.get(sd));
        }
        sounds.clear();
    }

    /**
     * 设置游戏的fps
     *
     * @param fps
     */
    public void setFps(int fps) {
        if (fps < 60 && fps > 1) {
            this.fps = fps;
        }
    }

    /**
     * 暂停引擎，暂停游戏用
     */
    public void pauseEngine() {
        isRunning = false;
        if (mEngineListener != null) {
            mEngineListener.onEnginePause(this);
        }
        //声音暂停
        soundPause();

    }

    private void soundPause() {
        Set<Integer> ids = sounds.keySet();
        for (Integer id : ids) {
            sp.pause(sounds.get(id));
        }
        Set<Integer> idss = soundsStop.keySet();
        for (Integer id : idss) {
            sp.pause(soundsStop.get(id));
        }
    }

    /**
     * 从暂停恢复引擎
     */
    public void reStart() {
        isRunning = true;
        if (mEngineListener != null) {
            mEngineListener.onEngineRestart(this);
        }
        //声音继续
        soundResume();
    }

    private void soundResume() {
        Set<Integer> ids = sounds.keySet();
        for (Integer id : ids) {
            sp.resume(sounds.get(id));
        }
        Set<Integer> idss = soundsStop.keySet();
        for (Integer id : idss) {
            sp.resume(soundsStop.get(id));
        }
    }

    /**
     * 销毁引擎。清空所有图片单元,和线程单元,写在活动页的销毁事件里
     * 销毁过后不能重新启用
     */
    public void destroyEngine() {
        isRunning = false;
        live = false;
        clearCell();
        clearSounds();
        threads.clear();
        sp.release();
        engineThread = null;
    }

    /**
     * 为引擎设置控制响应
     *
     * @param control
     */
    public void setControl(EngineControl control) {
        this.mControl = control;
    }

    /**
     * 设置引擎状态监听
     *
     * @param engineListener
     */
    public void setEngineListener(EngineListener engineListener) {
        mEngineListener = engineListener;
    }

    /**
     * 引擎触控事件,元素点击事件只支持标准图片和裁剪图片
     *
     * @param event
     * @return
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mControl == null) return false;
        Cell cell = null;
        int x = (int) event.getX();
        int y = (int) event.getY();
        for (int i = cells.size() - 1; i >= 0; i--) {
            Cell a = cells.get(i);
            if (!a.isVisable()) continue;
            Rect rect1 = new Rect((int) a.getX(),
                    (int) a.getY(),
                    (int) (a.getX() + a.getWidth()),
                    (int) (a.getY() + a.getHeight()));
            if (rect1 != null && rect1.contains(x, y)) {
                cell = a;
                break;
            }
        }
        boolean onClick = false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            onClick = mControl.onClick(cell);
        }
        if (!onClick) {
            mControl.onTouch(cell, event);
        }
        return true;
    }

    /**
     * 碰撞检测，传入2个单元
     *
     * @param a
     * @param b
     * @param overlap //重叠部分，修正误差
     * @return
     */
    public boolean hit(Cell a, Cell b, int overlap) {
        Rect rect1 = new Rect((int) a.getX() - overlap,
                (int) a.getY() - overlap,
                (int) (a.getX() + a.getWidth() + overlap),
                (int) (a.getY() + a.getHeight() + overlap));
        Rect rect2 = new Rect((int) b.getX() - overlap,
                (int) b.getY() - overlap,
                (int) (b.getX() + b.getWidth() + overlap),
                (int) (b.getY() + b.getHeight() + overlap));
        if (rect1 != null && rect2 != null) {
            return rect1.intersect(rect2);
        }
        return false;
    }

    /**
     * 获取屏幕宽
     *
     * @return
     */
    public int getScreenWidth() {
        return screenWidth;
    }

    /**
     * 获取屏幕高
     *
     * @return
     */
    public int getScreenHeight() {
        return screenHeight;
    }

    /**
     * 获取游戏区域宽 ,在引擎准备完后才能获取到正确的值
     *
     * @return
     */
    public int getGameWidth() {
        return gameWidth;
    }

    /**
     * 获取游戏区域高,在引擎准备完后才能获取到正确的值
     *
     * @return
     */
    public int getGameHeight() {
        return gameHeight;
    }

    /**
     * 获取游戏fps
     *
     * @return
     */
    public int getFps() {
        return fps;
    }

    /**
     * 获取dpi跟像素比值
     *
     * @return
     */
    public float getDensity() {
        return getResources().getDisplayMetrics().density;
    }
}


