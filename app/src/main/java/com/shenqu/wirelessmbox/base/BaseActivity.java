package com.shenqu.wirelessmbox.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.AbsListView.LayoutParams;
import android.widget.TextView;

import com.shenqu.wirelessmbox.R;

/**
 * 继承于Activity用于以后方便管理
 *
 * @author coder
 */
public class BaseActivity extends Activity {

    private View statusBar;
    private View titleView;
    private TextView tv_title;
    private ImageView btn_left, btn_right;

    private LinearLayout mBaseLayout;
    // 内容区域的布局
    private View contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        statusBar = findViewById(R.id.statue_bar);
        if (!setTranslucentStatus()) {
            statusBar.setVisibility(View.GONE);
        }
        
        titleView = findViewById(R.id.baseTitleView);
        tv_title = (TextView) titleView.findViewById(R.id.tv_title);
        btn_left = (ImageView) titleView.findViewById(R.id.btn_left);
        btn_right = (ImageView) titleView.findViewById(R.id.btn_right);

        mBaseLayout = (LinearLayout) findViewById(R.id.baseLayout);
        BaseApplication.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.removeActivity(this);
    }

    /**
     * 设置状态栏背景状态
     */
    private boolean setTranslucentStatus() {
        //判断当前SDK版本号，如果是4.4以上，就是支持沉浸式状态栏的
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            return true;
        }
        return false;
    }

    /***
     * 设置内容区域
     *
     * @param resId 资源文件ID
     */
    public void setContentLayout(int resId) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(resId, null);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        contentView.setLayoutParams(layoutParams);
        if (null != mBaseLayout) {
            mBaseLayout.addView(contentView);
        }
    }

    /***
     * 设置内容区域
     *
     * @param view View对象
     */
    @Override
    public void setContentView(View view) {
        if (null != mBaseLayout) {
            mBaseLayout.addView(view);
        }
    }

    /**
     * 得到内容的View
     */
    public View getLyContentView() {
        return contentView;
    }

    /**
     * 得到左边的按钮
     */
    public ImageView getbtn_left() {
        return btn_left;
    }

    /**
     * 得到右边的按钮
     */
    public ImageView getbtn_right() {
        return btn_right;
    }


    @TargetApi(VERSION_CODES.JELLY_BEAN)
    public void setBackground(Bitmap bmp){
        mBaseLayout.setBackground(new BitmapDrawable(getResources(), bmp));
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN)
    public void setBackground(Resources rs, Bitmap bmp){
        mBaseLayout.setBackground(new BitmapDrawable(rs, bmp));
    }

    /**
     * 设置标题栏背景图片
     * */
    public void setTitleViewBackground(Drawable drawable) {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            titleView.setBackground(drawable);
        }
    }

    public void setTitleViewBackground(int color) {
        titleView.setBackgroundColor(color);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        if (null != tv_title) {
            tv_title.setText(title);
        }
    }

    /**
     * 设置标题
     *
     * @param resId
     */
    public void setTitle(int resId) {
        tv_title.setText(getString(resId));
    }

    /**
     * 设置左边按钮的图片资源
     *
     * @param resId
     */
    public void setbtn_leftRes(int resId) {
        if (null != btn_left) {
            btn_left.setBackgroundResource(resId);
        }
    }

    /**
     * 设置左边按钮的图片资源
     *
     * @param drawable
     */
    public void setbtn_leftRes(Drawable drawable) {
        if (null != btn_left) {
            btn_left.setBackgroundDrawable(drawable);
        }
    }

    /**
     * 设置右边按钮的图片资源
     *
     * @param resId
     */
    public void setbtn_rightRes(int resId) {
        if (null != btn_right) {
            btn_right.setBackgroundResource(resId);
        }
    }

    /**
     * 设置右边按钮的图片资源
     *
     * @param drawable
     */
    public void setbtn_rightRes(Drawable drawable) {
        if (null != btn_right) {
            btn_right.setBackgroundDrawable(drawable);
        }
    }

    /**
     * 隐藏上方的标题栏
     */
    public void hideTitleView() {
        if (null != titleView) {
            titleView.setVisibility(View.GONE);
        }
    }

    /**
     * 隐藏左边的按钮
     */
    public void hidebtn_left() {
        if (null != btn_left) {
            btn_left.setVisibility(View.GONE);
        }
    }

    /***
     * 隐藏右边的按钮
     */
    public void hidebtn_right() {
        if (null != btn_right) {
            btn_right.setVisibility(View.GONE);
        }
    }

}