package cn.leo.airplanewar.engine;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Leo on 2017/7/3.
 */

public abstract class Cell implements Comparable<Cell> {
    public static final int TYPE_IMAGE = 0; //图片元素
    public static final int TYPE_STRING = 1; //字符串元素
    public static final int TYPE_ANIMATION = 2; //动画元素
    public static final int TYPE_CUSTOM = 3; //自绘元素
    protected Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG); //元素默认画笔
    protected int cellType; //元素类型
    protected boolean visable = true; //元素可见状态
    protected boolean distroy = false; //元素销毁状态
    protected float x; //元素 x 坐标
    protected float y; //元素 y 坐标
    protected float z; //元素图层高度，值越大越显示到前面，值相同随机；
    protected float width;//元素宽
    protected float height;//元素高
    protected int id; //元素id
    protected Object tag; //元素标记，附加数据

    /**
     * 元素在每帧画面执行的动作，不能有耗时操作和睡眠操作，否则会导致画面卡顿
     */
    public void event() {

    }


    public void draw(Canvas canvas) {
        if (canvas == null) {
            return;
        }
    }

    ; //引擎调用绘制方法

    /**
     * 引擎获取元素类型
     *
     * @return
     */
    public abstract int getCellType();

    @Override
    public int compareTo(Cell o) {
        return Float.compare(z, o.getZ());
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public boolean isVisable() {
        return visable;
    }

    public void setVisable(boolean visable) {
        this.visable = visable;
    }

    public boolean isDistroy() {
        return distroy;
    }

    public void setDistroy(boolean distroy) {
        this.distroy = distroy;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }
}
