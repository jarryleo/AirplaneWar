package cn.leo.airplanewar.engine;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

/**
 * 引擎图形元素
 * Created by JarryLeo on 2017/1/8.
 */

public class CellImage extends Cell {
    public static final int TYPE_BITMAP_STANDARD = 1; //元素类型图片标准格式
    public static final int TYPE_BITMAP_MATRIX = 2; //元素类型图片矩阵格式
    public static final int TYPE_BITMAP_RECT = 3; //元素类型图片裁剪格式
    public static final int TYPE_BITMAP_FULL = 4; //元素类型图片填充格式
    private Bitmap bitmap; //bitmap图像
    private Matrix matrix; //元素矩阵
    private Rect src, dst; //元素裁剪粘贴区域
    private int type = TYPE_BITMAP_STANDARD; //元素图片绘制类型

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        switch (type) {
            case TYPE_BITMAP_FULL: //绘制填充图片
                canvas.drawBitmap(bitmap, null, new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), paint);
                break;
            case TYPE_BITMAP_STANDARD: //绘制标准图片
                canvas.drawBitmap(bitmap, x, y, paint);
                break;
            case TYPE_BITMAP_MATRIX: //绘制矩阵图片
                canvas.drawBitmap(bitmap, matrix, paint);
                break;
            case TYPE_BITMAP_RECT: //绘制裁剪图片
                canvas.drawBitmap(bitmap, src, dst, paint);
                break;
        }
    }

    @Override
    public int getCellType() {
        return Cell.TYPE_IMAGE;
    }

    /**
     * 允许空参构造但会隐藏
     */
    public CellImage() {
        visable = false;
    }

    /**
     * 设置铺满背景的图片,会自动拉伸
     *
     * @param bitmap
     */
    public CellImage(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        this.type = TYPE_BITMAP_FULL;
    }

    /**
     * 设置标准图片
     *
     * @param bitmap
     * @param x      图片位置x
     * @param y      图片位置y
     */
    public CellImage(Bitmap bitmap, float x, float y) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.type = TYPE_BITMAP_STANDARD;
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
    }

    /**
     * 设置矩阵图片,矩阵可以调整图片的很多属性,会用的人用
     *
     * @param bitmap
     * @param matrix
     */
    public CellImage(Bitmap bitmap, Matrix matrix) {
        this.bitmap = bitmap;
        this.matrix = matrix;
        this.type = TYPE_BITMAP_MATRIX;
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
    }

    /**
     * 设置伸缩图片,传入原始矩形区域和目标矩形区域,让图片产生拉伸效果或者区域效果
     *
     * @param bitmap
     * @param src
     * @param dst
     */
    public CellImage(Bitmap bitmap, Rect src, Rect dst) {
        this.bitmap = bitmap;
        this.src = src;
        this.dst = dst;
        this.type = TYPE_BITMAP_RECT;
        this.width = dst.width();
        this.height = dst.height();
    }

    @Override
    public void setDistroy(boolean distroy) {
        super.setDistroy(distroy);
        bitmap.recycle();
    }

    /**
     * 获取元素图案
     *
     * @return
     */
    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * 设置图像
     *
     * @param bitmap
     */

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * 获取元素类型
     *
     * @return
     */
    public int getType() {
        return type;
    }

    /**
     * 更改元素状态
     *
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * 获取元素绘制区域大小
     *
     * @return
     */
    public Rect getDst() {
        return dst;
    }

    /**
     * 修改元素绘制区域
     *
     * @param dst
     */
    public void setDst(Rect dst) {
        this.dst = dst;
    }

    /**
     * 获取元素裁剪区域大小
     *
     * @return
     */
    public Rect getSrc() {
        return src;
    }

    /**
     * 设置元素裁剪区域
     *
     * @param src
     */
    public void setSrc(Rect src) {
        this.src = src;
    }

    /**
     * 获取元素矩阵
     *
     * @return
     */
    public Matrix getMatrix() {
        return matrix;
    }

    /**
     * 设置元素矩阵
     *
     * @param matrix
     */
    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

}
