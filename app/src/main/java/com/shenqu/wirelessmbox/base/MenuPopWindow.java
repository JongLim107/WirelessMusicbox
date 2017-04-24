package com.shenqu.wirelessmbox.base;

import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.PopupWindow;

/**
 * Created by JongLim on 2016/9/27.
 */
public abstract class MenuPopWindow extends PopupWindow implements AdapterView.OnItemClickListener {

    private View mView;

    public MenuPopWindow() {
        mView = findView();
        setContentView(mView);
        initView();
    }

    protected abstract View findView();

    private void initView() {
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);

        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener
        this.setBackgroundDrawable(new ColorDrawable(0x88444444));

        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(android.R.style.Animation_Activity);
        // 刷新状态
        this.update();
    }

    /**
     * 显示popupWindow
     *
     * @param anchor
     */
    public void showPopupWindow(View anchor, int xoff, int yoff) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(anchor, xoff, yoff);
        } else {
            this.dismiss();
        }
    }


    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent, int gravity, int x, int y) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAtLocation(parent, gravity, x, y);
        } else {
            this.dismiss();
        }
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showAboveParent(View parent, int gravity, int heightWeight) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAtLocation(parent, gravity, 0, parent.getHeight() / heightWeight);
        } else {
            this.dismiss();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MenuPopWindow.this.dismiss();
        onPopupClickListener(position);
    }

    protected abstract void onPopupClickListener(int index);
}
