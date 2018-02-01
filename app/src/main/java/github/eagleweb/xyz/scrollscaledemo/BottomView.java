package github.eagleweb.xyz.scrollscaledemo;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
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
    private int mDuration = 130;
    private RecyclerView mListView;
    private boolean      isUp;   // 是否抬起

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
        sourceHeight = mTopView.getResources().getDimension(R.dimen.index_top_height);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isUp = false;
                mMeasuredHeight = getMeasuredHeight();
                mDownY = event.getRawY();
                mDownX = event.getRawX();
                mDownTime = System.currentTimeMillis();
                Log.d(TAG, "down " + mDownY);
                mListView.onTouchEvent(event);
                //                if (mHideAnimator != null) {
                //                    mHideAnimator.cancel();
                //                }
                //                if (mShowAnimator != null) {
                //                    mShowAnimator.cancel();
                //                }
                break;
            case MotionEvent.ACTION_MOVE:
                isUp = false;
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
                    mTopView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) v));
//                    mTopView.setTranslationY(v);
                } else {
                    // 向下滑动
                    if (!mTopView.isOpen()) {
                        // 是关闭状态
                        // 第一条item可见：拦截，滑动 否则，不管
                        if (mLinearLayoutManager.findFirstVisibleItemPosition() == 0) {
                            mTopView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) translationY));
//                            mTopView.setTranslationY(translationY);
                        }
                    } else {
                        //                        return mListView.onTouchEvent(event);
                        mLinearLayoutManager.scrollToPositionWithOffset(0, 0);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //                if (mTopView.isOpen()) {
                // 如果滑动距离不超过50，时间不超过800ms。则视为点击
                // 菜单打开状态
                isUp = true;
                mUpY = event.getRawY();
                float translationY2 = mUpY - mDownY;
                //                if (Math.abs(translationY2) < 3 && Math.abs(mMoveX - mDownX) < 3 && (System.currentTimeMillis() - mDownTime) < 500) {
                //                    return mListView.onTouchEvent(event);
                //                }
                long l = System.currentTimeMillis() - mDownTime;
                Log.d(TAG, "up/cancel " + mMeasuredHeight + "  mUpY：" + mUpY + "  translationY2:" + translationY2 + "  mTopView.isOpen():" + mTopView.isOpen() + "  l:" + l + "  (sourceHeight / 3):" + (sourceHeight / 3));
                // 如果Y差值大于100，并且时间小于800，则视为快速滑动，直接给他收起
                if (translationY2 > 0) {
                    // 下滑
                    if (mTopView.isOpen()) {
                        mLinearLayoutManager.scrollToPositionWithOffset(0, 0);
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
                        hideTopView();
                    } else {
                        // 执行滑动动画展开
                        showTopView();
                    }
                } else {
                    if (mTopView.isOpen() || Math.abs(translationY2) > sourceHeight / 3) {
                        // 执行滑动动画展开
                        mListView.smoothScrollToPosition(0);
                        showTopView();
                    } else {
                        hideTopView();
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

    private void showTopView() {
        if (mHideAnimator != null) {
            mHideAnimator.cancel();
        }
        mTopView.setOpen(true);
        mShowAnimator = ValueAnimator.ofInt(mTopView.getMeasuredHeight(), (int) sourceHeight);
        mShowAnimator.setDuration(mDuration);
        mShowAnimator.setInterpolator(new LinearInterpolator());
        mShowAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mTopView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) valueAnimator.getAnimatedValue()));
            }
        });
        mShowAnimator.start();
    }

    private void hideTopView() {
        if (mShowAnimator != null) {
            mShowAnimator.cancel();
        }
        mTopView.setOpen(false);
        mHideAnimator = ValueAnimator.ofInt(mTopView.getHeight(), 0);
        mHideAnimator.setDuration(mDuration);
        mHideAnimator.setInterpolator(new LinearInterpolator());
        mHideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mTopView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) valueAnimator.getAnimatedValue()));
            }
        });
        mHideAnimator.start();
    }

    private float dX;
    private float dY;
    private float mY;
    private long  dTime;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mLinearLayoutManager.findFirstVisibleItemPosition() != 0 && !mTopView.isOpen()) {
            Log.d(TAG, "onInterceptTouchEvent event to listView");
            return mListView.onInterceptTouchEvent(ev);
        } else {
            //            if (true) {
            //                Log.d(TAG, "onInterceptTouchEvent event self");
            //                return true;
            //            }
        }
        if (!mTopView.isOpen() && mTopView.getMeasuredHeight() <= 0) {
            Log.d(TAG, "onInterceptTouchEvent event to listView");
            mListView.onInterceptTouchEvent(ev);
        } else {
            Log.d(TAG, "onInterceptTouchEvent event self");
        }
        //        // 判断如果是点击 也返回false
        //        switch (ev.getAction()) {
        //            case MotionEvent.ACTION_DOWN:
        //                dX = ev.getX();
        //                dY = ev.getY();
        //                dTime = System.currentTimeMillis();
        //                break;
        //            case MotionEvent.ACTION_MOVE:
        //                mY = ev.getY();
        //                if (mY - dY > 0) {
        //                    // 下滑
        //                    if (mTopView.isOpen()) {
        //                        //                        return mListView.onInterceptTouchEvent(ev);
        //                    }
        //                } else {
        //                    // 上滑
        //                    if (!mTopView.isOpen()) {
        //                        //                        return mListView.onInterceptTouchEvent(ev);
        //                    }
        //                }
        //                break;
        //            case MotionEvent.ACTION_UP:
        //            case MotionEvent.ACTION_CANCEL:
        //                float x = getX();
        //                float y = getY();
        //                if (Math.abs(y - dY) < 10 && Math.abs(x - dX) < 10 && (System.currentTimeMillis() - dTime) < 300) {
        //                    // 判断是点击，不拦截
        //                    //                    return mListView.onInterceptTouchEvent(ev);
        //                }
        //                break;
        //            default:
        //                break;
        //        }
        //        VelocityTracker obtain = VelocityTracker.obtain();
        //        obtain.addMovement(ev);
        //        float xVelocity = obtain.getXVelocity();
        //        float yVelocity = obtain.getYVelocity();
        //        mListView.fling((int) xVelocity, (int) yVelocity);
        return true;
    }


    private float ddX;
    private float ddY;
    private float dmY;
    private long  ddTime;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean isFirst = mLinearLayoutManager.findFirstVisibleItemPosition() == 0;
        //        if (isFirst) {
        //            if (mTopView.isOpen()) {
        //
        //                //            return super.dispatchTouchEvent(ev);
        //            }
        //        }

        // 判断如果是点击 也返回false
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ddX = ev.getRawX();
                ddY = ev.getRawY();
                ddTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                dmY = ev.getRawY();
                // 如果是超过10了
                if (Math.abs(dmY - ddY) > 10) {
                    // 是滑动 需要判断是不是打开状态，打开状态，不允许滑动，关闭可以
                    if (mTopView.isOpen()) {
                        return super.dispatchTouchEvent(ev);
                    } else {
                        // 关闭状态下的滑动，如果是向下滑动，拦截，向上滑动，不拦截  第一条可见
                        if (isFirst) {
                            if (dmY - ddY > 0) {
                                // 向下滑动 ；拦截
                                return super.dispatchTouchEvent(ev);
                            } else {
                                if (mTopView.getMeasuredHeight() > 0) {
                                    return super.dispatchTouchEvent(ev);
                                } else {
                                    // 向上滑动 不拦截
                                    return mListView.dispatchTouchEvent(ev);
                                }
                            }
                        } else {
                            // 第一条不可见 不拦截
                            return mListView.dispatchTouchEvent(ev);
                        }
                    }
                } else {
                    // 可能是点击

                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                float x = ev.getRawX();
                float y = ev.getRawY();
                if (mTopView.isOpen()) {
                    if (Math.abs(y - ddY) < 5 && Math.abs(x - ddX) < 5 && (System.currentTimeMillis() - ddTime) < 300) {
                        // 判断是点击，不拦截
                        Log.d(TAG, "click ");
                        return mListView.dispatchTouchEvent(ev);
                    } else {
                        Log.d(TAG, "open up");
                        mListView.dispatchTouchEvent(ev);
                        return super.dispatchTouchEvent(ev);
                    }
                } else {
                    // 关闭状态下，如果头部布局小于0，可以给 如果大于0 不给
                    if (mTopView.getMeasuredHeight() > 0) {
                        Log.d(TAG, "close up > 0");
                        mListView.dispatchTouchEvent(ev);
                        return onTouchEvent(ev);
                    } else {
                        Log.d(TAG, "close < 0");
                        return mListView.dispatchTouchEvent(ev);
                    }
                }
            default:
                break;
            //                break;
            //                break;
        }
        mListView.dispatchTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    public void setListManager(LinearLayoutManager linearLayoutManager) {
        mLinearLayoutManager = linearLayoutManager;
    }

    public void setListView(RecyclerView listView) {
        mListView = listView;
        mListView.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                Log.d(TAG, "onFling");
                if (mLinearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                    showTopView();
                } else {
                    if (mTopView.isOpen()) {
                        hideTopView();
                    }
                }
                return false;
            }
        });
        mListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d(TAG, "onScrollStateChanged " + newState);
                if (newState == 0) {
                    if (mTopView.isOpen()) {
                        mLinearLayoutManager.scrollToPositionWithOffset(0, 0);
                    } else {
                        // 如果没有收到up/cancel事件，则在这里执行同样的处理
                        if (!isUp) {
                            isUp = true;
                            //                            mLinearLayoutManager.scrollToPositionWithOffset(0, 0);
                            int translationY2 = mTopView.getMeasuredHeight();
                            if ((Math.abs(translationY2) > 10) || Math.abs(translationY2) > (sourceHeight / 3)) {
                                if (mTopView.isOpen()) {
                                    // 是打开状态时
                                    // 执行滑动动画收起
                                    hideTopView();
                                } else {
                                    // 执行滑动动画展开
                                    showTopView();
                                }
                            } else {
                                if (mTopView.isOpen() || Math.abs(translationY2) > sourceHeight / 3) {
                                    // 执行滑动动画展开
                                    mListView.smoothScrollToPosition(0);
                                    showTopView();
                                }
                            }
                        }
                    }

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled " + dy + "   " + mTopView.isOpen() + "   isUp:" + isUp);
                if (mTopView.isOpen()) {
                    //                    if (!isUp) {
                    mLinearLayoutManager.scrollToPositionWithOffset(0, 0);
                    //                    }
                } else {
                    //                    // 关闭状态，刚打开的开关，也需要禁止滑动
                    //                    if (!isUp) {
                    //                        mLinearLayoutManager.scrollToPositionWithOffset(0, 0);
                    //                    }
                }
            }
        });
    }
}
