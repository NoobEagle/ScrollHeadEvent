package github.eagleweb.xyz.scrollscaledemo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.ValueAnimator;

/**
 * TODO: document your custom view class.
 */
public class TopView extends RelativeLayout {

    private final String TAG = "TopView";
    private BottomView    mBootomView;
    private float         mDownY;
    private float         mMoveY;
    private float         mDownX;
    private float         mMoveX;
    private long          mDownTime;
    private int           mMeasuredHeight;
    private float         mUpY;
    private float         sourceHeight;
    private ValueAnimator mHideAnimator;
    private ValueAnimator mShowAnimator;
    private boolean isOpen    = true; // 是否是展示状态
    private int     mDuration = 100;

    public TopView(Context context) {
        this(context, null);
    }

    public TopView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        sourceHeight = getResources().getDimension(R.dimen.top_height);
    }

    public void setBootomView(BottomView bootomView) {
        mBootomView = bootomView;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mMeasuredHeight = getMeasuredHeight();
                mDownY = event.getRawY();
                mDownX = event.getRawX();
                mDownTime = System.currentTimeMillis();
                Log.d(TAG, "down " + mDownY);

                break;
            case MotionEvent.ACTION_MOVE:
                mMoveY = event.getRawY();
                mMoveX = event.getRawX();
                float translationY = mMoveY - mDownY;
                float v = mMeasuredHeight - Math.abs(translationY);
                Log.d(TAG, "move  mDownY:" + mDownY + "   mMoveY:" + mMoveY + "  measuredHeight:" + mMeasuredHeight + "   translationY:" + translationY + "  差值:" + v);
                if (translationY < 0) {
                    // 向上滑动
                    //                    setTranslationY(translationY);
                    setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) v));


                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                mUpY = event.getRawY();
                float translationY2 = mUpY - mDownY;
                Log.d(TAG, "up/cancel " + mMeasuredHeight);
                long l = System.currentTimeMillis() - mDownTime;
                if (translationY2 > 0) {
                    // 下滑
                    if (isOpen()) {
                        return false;
                    }
                } else {
                    // 上滑
                    if (!isOpen()) {
                        return false;
                    }
                }
                // 如果Y差值大于100，并且时间小于800，则视为快速滑动，直接给他收起
                if ((Math.abs(translationY2) > 10 && (l < 800)) || Math.abs(translationY2) > (sourceHeight / 1.3)) {
                    // 执行滑动动画收起
                    isOpen = false;
                    mHideAnimator = ValueAnimator.ofInt(getHeight(), 0);
                    mHideAnimator.setDuration(mDuration);
                    mHideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) valueAnimator.getAnimatedValue()));
                        }
                    });
                    mHideAnimator.start();
                } else {
                    // 执行滑动动画展开
                    isOpen = true;
                    mShowAnimator = ValueAnimator.ofInt(getHeight(), (int) sourceHeight);
                    mShowAnimator.setDuration(mDuration);
                    mShowAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) valueAnimator.getAnimatedValue()));
                        }
                    });
                    mShowAnimator.start();
                }

                break;
            default:
                break;
        }
        return true;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
}
