package github.eagleweb.xyz.scrollscaledemo;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.ValueAnimator;

/**
 * TODO: document your custom view class.
 */
public class BottomView extends RelativeLayout {

    private final String TAG = "BottomView";
    private final int                 mTouchSlop;
    private       float               mDownY;
    private       float               mMoveY;
    private       float               mDownX;
    private       float               mMoveX;
    private       long                mDownTime;
    private       int                 mMeasuredHeight;
    private       float               mUpY;
    private       float               sourceHeight;
    private       ValueAnimator       mHideAnimator;
    private       ValueAnimator       mShowAnimator;
    private       TopView             mTopView;
    private       int                 mTopViewHeight;
    private       LinearLayoutManager mLinearLayoutManager;
    private int mDuration = 100;
    private RecyclerView mListView;

    public BottomView(Context context) {
        this(context, null);
    }

    public BottomView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public BottomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void setTopView(TopView topView) {

        mTopView = topView;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mLinearLayoutManager.findFirstVisibleItemPosition() != 0) {
            return true;
        }
        sourceHeight = mTopView.getResources().getDimension(R.dimen.top_height);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mMeasuredHeight = getMeasuredHeight();
                mDownY = event.getRawY();
                mDownX = event.getRawX();
                mDownTime = System.currentTimeMillis();
                Log.d(TAG, "down " + mDownY);
                mListView.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveY = event.getRawY();
                mMoveX = event.getRawX();
                float translationY = mMoveY - mDownY;
                if (mTopViewHeight <= 0) {
                    mTopViewHeight = mTopView.getMeasuredHeight();
                }

                float v = mTopViewHeight - Math.abs(translationY);
                Log.d(TAG, "move  mDownY:" + mDownY + "   mMoveY:" + mMoveY + "  measuredHeight:" + mMeasuredHeight + "   translationY:" + translationY + "  差值:" + v);
                if (translationY < 0) {
                    // 向上滑动
                    if (!mTopView.isOpen()) {
                        // 是关闭状态
                        return mListView.onTouchEvent(event);
                    }
                    //                    setTranslationY(translationY);
                    mTopView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) v));
                } else {
                    // 向下滑动
                    // 第一条item可见：拦截，滑动 否则，不管
                    if (!mTopView.isOpen()) {
                        // 是关闭状态
                        if (translationY > sourceHeight) {
                            return mListView.onTouchEvent(event);
                        }
                        if (mLinearLayoutManager.findFirstVisibleItemPosition() == 0) {
                            mTopView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) translationY));
                        }
                    } else {
                        return mListView.onTouchEvent(event);
                    }
                }

                // 如果滑动距离不超过50，时间不超过800ms。则视为点击
                if (Math.abs(translationY) < 3 && Math.abs(mMoveX - mDownX) < 3 && (System.currentTimeMillis() - mDownTime) < 800) {
                    return mListView.onTouchEvent(event);
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //                if (mTopView.isOpen()) {
                // 如果滑动距离不超过50，时间不超过800ms。则视为点击
                // 菜单打开状态

                mUpY = event.getRawY();
                float translationY2 = mUpY - mDownY;
                if (Math.abs(translationY2) < 3 && Math.abs(mMoveX - mDownX) < 3 && (System.currentTimeMillis() - mDownTime) < 500) {
                    return mListView.onTouchEvent(event);
                }
                Log.d(TAG, "up/cancel " + mMeasuredHeight + "  mUpY：" + mUpY);
                long l = System.currentTimeMillis() - mDownTime;
                // 如果Y差值大于100，并且时间小于800，则视为快速滑动，直接给他收起
                if (translationY2 > 0) {
                    // 下滑
                    if (mTopView.isOpen()) {
                        return mListView.onTouchEvent(event);
                    }
                } else {
                    // 上滑
                    if (!mTopView.isOpen()) {
                        return mListView.onTouchEvent(event);
                    }
                }
                if ((Math.abs(translationY2) > 10 && (l < 800)) || Math.abs(translationY2) > (sourceHeight / 3)) {
                    if (mTopView.isOpen()) {
                        // 是打开状态时
                        // 执行滑动动画收起
                        mTopView.setOpen(false);
                        mHideAnimator = ValueAnimator.ofInt(mTopView.getHeight(), 0);
                        mHideAnimator.setDuration(mDuration);
                        mHideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                mTopView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) valueAnimator.getAnimatedValue()));
                            }
                        });
                        mHideAnimator.start();
                    } else {
                        // 执行滑动动画展开
                        mTopView.setOpen(true);
                        mShowAnimator = ValueAnimator.ofInt(mTopView.getHeight(), (int) sourceHeight);
                        mShowAnimator.setDuration(mDuration);
                        mShowAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                mTopView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) valueAnimator.getAnimatedValue()));
                            }
                        });
                        mShowAnimator.start();
                    }
                } else {
                    if (mTopView.isOpen() || Math.abs(translationY2) > sourceHeight / 3) {
                        // 执行滑动动画展开
                        mTopView.setOpen(true);
                        mShowAnimator = ValueAnimator.ofInt(mTopView.getHeight(), (int) sourceHeight);
                        mShowAnimator.setDuration(mDuration);
                        mShowAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                mTopView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) valueAnimator.getAnimatedValue()));
                            }
                        });
                        mShowAnimator.start();
                    }
                }
                //                } else {
                //                    // 菜单关闭状态
                //
                //                }

                break;
            default:
                break;
        }
        return true;
    }

    private float dX;
    private float dY;
    private float mY;
    private long  dTime;

    //    @Override
    //    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
    //        mListView.dispatchNestedFling(velocityX, velocityY, consumed);
    //        return super.dispatchNestedFling(velocityX, velocityY, consumed);
    //    }
    //
    //    @Override
    //    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
    //        mListView.dispatchNestedPreFling(velocityX, velocityY);
    //        return super.dispatchNestedPreFling(velocityX, velocityY);
    //    }
    //
    //    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    //    @Override
    //    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
    //        mListView.onNestedFling(target, velocityX, velocityY, consumed);
    //        return super.onNestedFling(target, velocityX, velocityY, consumed);
    //    }
    //
    //    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    //    @Override
    //    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
    //        mListView.onNestedPreFling(target, velocityX, velocityY);
    //        return super.onNestedPreFling(target, velocityX, velocityY);
    //    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mLinearLayoutManager.findFirstVisibleItemPosition() != 0) {
            return mListView.onInterceptTouchEvent(ev);
        } else {
//            if (true) {
//                return true;
//            }
        }
        mListView.onInterceptTouchEvent(ev);
        // 判断如果是点击 也返回false
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dX = ev.getX();
                dY = ev.getY();
                dTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                mY = ev.getY();
                if (mY - dY > 0) {
                    // 下滑
                    if (mTopView.isOpen()) {
                        //                        return mListView.onInterceptTouchEvent(ev);
                    }
                } else {
                    // 上滑
                    if (!mTopView.isOpen()) {
                        //                        return mListView.onInterceptTouchEvent(ev);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                float x = getX();
                float y = getY();
                if (Math.abs(y - dY) < 10 && Math.abs(x - dX) < 10 && (System.currentTimeMillis() - dTime) < 300) {
                    // 判断是点击，不拦截
                    //                    return mListView.onInterceptTouchEvent(ev);
                }
                break;
            default:
                break;
        }
        //        VelocityTracker obtain = VelocityTracker.obtain();
        //        obtain.addMovement(ev);
        //        float xVelocity = obtain.getXVelocity();
        //        float yVelocity = obtain.getYVelocity();
        //        mListView.fling((int) xVelocity, (int) yVelocity);
        return true;
    }

    //
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //        if (mLinearLayoutManager.findFirstVisibleItemPosition() != 0) {
        //            return super.dispatchTouchEvent(ev);
        //        }
        //        // 判断如果是点击 也返回false
        //        switch (ev.getAction()) {
        //            case MotionEvent.ACTION_DOWN:
        //                dX = ev.getX();
        //                dY = ev.getY();
        //                dTime = System.currentTimeMillis();
        //                break;
        //            case MotionEvent.ACTION_MOVE:
        //                break;
        //            case MotionEvent.ACTION_UP:
        //            case MotionEvent.ACTION_CANCEL:
        //                float x = getX();
        //                float y = getY();
        //                if (Math.abs(y - dY) < 10 && Math.abs(x - dX) < 10 && (System.currentTimeMillis() - dTime) < 300) {
        //                    // 判断是点击，不拦截
        //                    return true;
        //                }
        //                break;
        //            default:
        //                break;
        //        }
        mListView.dispatchTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    public void setListManager(LinearLayoutManager linearLayoutManager) {
        mLinearLayoutManager = linearLayoutManager;
    }

    public void setListView(RecyclerView listView) {
        mListView = listView;
    }
}
