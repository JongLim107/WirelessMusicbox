package com.shenqu.wirelessmbox.ximalaya.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shenqu.wirelessmbox.R;
import com.shenqu.wirelessmbox.base.BaseApplication;

/**
 * Created by JongLim on 2017/1/9.
 */

public class BaseFragmentActivity extends FragmentActivity implements Handler.Callback{

    private View statusBar;
    private View titleView;
    private TextView tv_title;
    private ImageView btn_left, btn_right;

    //后期增加的搜索栏
    private EditText et_search;
    private View vLine;

    private LinearLayout mBaseLayout;
    // 内容区域的布局
    private View contentView;

    public Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        statusBar = findViewById(R.id.statue_bar);
        if (!setTranslucentStatus()) {
            statusBar.setVisibility(View.GONE);
        }

        titleView = findViewById(R.id.baseTitleView);
        tv_title = (TextView) titleView.findViewById(R.id.tv_title);
        btn_left = (ImageView) titleView.findViewById(R.id.btn_left);
        btn_right = (ImageView) titleView.findViewById(R.id.btn_right);

        et_search = (EditText) titleView.findViewById(R.id.searchEt);
        vLine = titleView.findViewById(R.id.line);

        mBaseLayout = (LinearLayout) findViewById(R.id.baseLayout);
        BaseApplication.addActivity(this);
        mHandler = new Handler(this);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
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
        LayoutParams layoutParams = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
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

    public ImageView getbtn_left() {
        return btn_left;
    }

    public ImageView getbtn_right() {
        return btn_right;
    }

    public TextView getTv_title(){
        return tv_title;
    }

    public EditText getEt_search(){
        return et_search;
    }

    public View getvLine(){
        return vLine;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setBackground(Bitmap bmp) {
        mBaseLayout.setBackground(new BitmapDrawable(getResources(), bmp));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setBackground(Resources rs, Bitmap bmp) {
        mBaseLayout.setBackground(new BitmapDrawable(rs, bmp));
    }

    /**
     * 设置标题栏背景图片
     */
    public void setTitleViewBackground(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            titleView.setBackground(drawable);
        }
    }

    public void setTitleViewBackground(int color) {
        titleView.setBackgroundColor(color);
        statusBar.setBackgroundColor(color);
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

    public void setTitleColor(int color){
        if (null != tv_title) {
            tv_title.setTextColor(color);
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

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }
}
