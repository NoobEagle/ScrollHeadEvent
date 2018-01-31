package github.eagleweb.xyz.scrollscaledemo;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.lang.ref.WeakReference;

/**
 * @创建者 帅子
 * @创建时间 18/1/30.
 * @描述
 */

public class CustomBehavior extends CoordinatorLayout.Behavior<RelativeLayout> {
    private WeakReference<View> dependentView;

    public CustomBehavior() {
    }

    public CustomBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, RelativeLayout child, View dependency) {
        // 每当UI有变化时，就会调用这个函数，如果是dependency 要返回true，就可以控制child的行为
        if (dependency != null && dependency.getId() == R.id.iv_target) {
            dependentView = new WeakReference<>(dependency);
            return true;
        }
        return dependency instanceof ImageView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, RelativeLayout child, View dependency) {
        // layoutDependsOn方法返回true之后，就开始调用该方法
        Resources resources = getDependentView().getResources();
        final float progress = 1.f -
                Math.abs(dependency.getTranslationY() / (dependency.getHeight() - resources.getDimension(R.dimen.collapsed_header_height)));

        child.setTranslationY(dependency.getHeight() + dependency.getTranslationY());

        float scale = 1 + 0.4f * (1.f - progress);
        dependency.setScaleX(scale);
        dependency.setScaleY(scale);

        dependency.setAlpha(progress);

        return true;
    }


    private float getDependentViewCollapsedHeight() {
        return getDependentView().getResources().getDimension(R.dimen.collapsed_header_height);
    }

    private View getDependentView() {
        return dependentView.get();
    }
}
