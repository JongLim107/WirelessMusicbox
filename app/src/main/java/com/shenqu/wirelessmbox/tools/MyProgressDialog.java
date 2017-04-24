package com.shenqu.wirelessmbox.tools;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shenqu.wirelessmbox.R;

/**
 * Created by JongLim on 2016/12/23.
 */

public class MyProgressDialog extends Dialog{
    private static final String TAG = "MyProDlog";

    private Context mContext;
    private TextView tipTextView;

    public MyProgressDialog(Context context) {
        super(context, R.style.loading_dialog);// 创建自定义样式dialog
        mContext = context;
    }

    public void initDialog(boolean cancelable, String msg) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.dialog_loading, null);
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);
        // 设置布局
        this.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        // 不可以用“返回键”取消
        this.setCancelable(cancelable);

        // 使用ImageView显示动画
        Animation animation= AnimationUtils.loadAnimation(mContext, R.anim.load_animation);
        ImageView imageView = (ImageView) v.findViewById(R.id.img);
        imageView.startAnimation(animation);

        // 提示文字
        tipTextView = (TextView) v.findViewById(R.id.tipTextView);
        tipTextView.setText(msg);// 设置加载信息
    }

    public void setMessage(String msg){
        tipTextView.setText(msg);// 设置加载信息
    }
}
