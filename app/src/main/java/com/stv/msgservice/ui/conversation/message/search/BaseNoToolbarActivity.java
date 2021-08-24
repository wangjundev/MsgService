package com.stv.msgservice.ui.conversation.message.search;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.stv.msgservice.R;
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import butterknife.ButterKnife;

public abstract class BaseNoToolbarActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        beforeViews();
        setContentView(contentLayout());
        ButterKnife.bind(this);
        UltimateBarX.with(this)
                .fitWindow(true)
                .light(true)
                .lvLightColor(Color.WHITE/*Color.parseColor("#F7F7F7")*/)
                .applyStatusBar();
        afterViews();
//        setStatusBar();
    }

    protected void hideInputMethod() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * @return 布局文件
     */
    protected abstract @LayoutRes
    int contentLayout();

    /**
     * {@link AppCompatActivity#setContentView(int)}之前调用
     */
    protected void beforeViews() {

    }

    /**
     * {@link AppCompatActivity#setContentView(int)}之后调用
     * <p>
     * 此时已经调用了{@link ButterKnife#bind(Activity)}, 子类里面不需要再次调用
     */
    protected void afterViews() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        hideInputMethod();
    }

    public boolean checkPermission(String permission) {
        return checkPermission(new String[]{permission});
    }

    public boolean checkPermission(String[] permissions) {
        boolean granted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                granted = checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
                if (!granted) {
                    break;
                }
            }
        }
        return granted;
    }

    /**
     * Changes the System Bar Theme.
     */
    public static void setStatusBarTheme(final Activity pActivity, final boolean pIsDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Fetch the current flags.
            final int lFlags = pActivity.getWindow().getDecorView().getSystemUiVisibility();
            // Update the SystemUiVisibility dependening on whether we want a Light or Dark theme.
            pActivity.getWindow().getDecorView().setSystemUiVisibility(pIsDark ? (lFlags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) : (lFlags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
        }
    }

    /**
     * 设置状态栏的颜色
     *
     * @param resId 颜色资源id
     */
    protected void setStatusBarColor(int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, resId));
        }
    }

    protected boolean useThemestatusBarColor = true;//是否使用特殊的标题栏背景颜色，android5.0以上可以设置状态栏背景色，如果不使用则使用透明色值
    protected boolean useStatusBarColor = true;//是否使用状态栏文字和图标为暗色，如果状态栏采用了白色系，则需要使状态栏和图标为暗色，android6.0以上可以设置

    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //根据上面设置是否对状态栏单独设置颜色
            if (useThemestatusBarColor) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.action_bar_background_color));
            } else {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && useStatusBarColor) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
