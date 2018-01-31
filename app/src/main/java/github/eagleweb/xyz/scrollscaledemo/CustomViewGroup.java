package github.eagleweb.xyz.scrollscaledemo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * @创建者 帅子
 * @创建时间 18/1/31.
 * @描述
 */

public class CustomViewGroup extends RelativeLayout {
    private final GestureDetector mGestureDetector;
    private final String TAG = "CustomViewGroup";

    public CustomViewGroup(Context context) {
        this(context, null);
    }

    public CustomViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CustomViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            /*
               *每按一下屏幕立即触发
               * */
            @Override
            public boolean onDown(MotionEvent e) {
                Log.d(TAG, "onDown");
                getChildAt(0).onTouchEvent(e);
                return false;
            }

            /*
            *用户按下屏幕并且没有移动或松开。主要是提供给用户一个可视化的反馈，告诉用户他们的按下操作已经
            * 被捕捉到了。如果按下的速度很快只会调用onDown(),按下的速度稍慢一点会先调用onDown()再调用onShowPress().
            * */
            @Override
            public void onShowPress(MotionEvent e) {
                Log.d(TAG, "onShowPress");
            }

            /*
                *一次单纯的轻击抬手动作时触发
                * */
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "onSingleTapUp");
                return false;
            }


            /*
              *屏幕拖动事件，如果按下的时间过长，调用了onLongPress，再拖动屏幕不会触发onScroll。拖动屏幕会多次触发
              * @param e1 开始拖动的第一次按下down操作,也就是第一个ACTION_DOWN
              * @parem e2 触发当前onScroll方法的ACTION_MOVE
              * @param distanceX 当前的x坐标与最后一次触发scroll方法的x坐标的差值。
              * @param diastancY 当前的y坐标与最后一次触发scroll方法的y坐标的差值。
              * */
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.d(TAG, "onScroll  distanceX:" + distanceX + "   distanceY:" + distanceY);
                getChildAt(0).onTouchEvent(e2);
                return false;
            }

            /*
               * 长按。在down操作之后，过一个特定的时间触发
               * */
            @Override
            public void onLongPress(MotionEvent e) {
                Log.d(TAG, "onLongPress");
            }

            /*
               * 按下屏幕，在屏幕上快速滑动后松开，由一个down,多个move,一个up触发
               * @param e1 开始快速滑动的第一次按下down操作,也就是第一个ACTION_DOWN
               * @parem e2 触发当前onFling方法的move操作,也就是最后一个ACTION_MOVE
               * @param velocityX：X轴上的移动速度，像素/秒
               * @parram velocityY：Y轴上的移动速度，像素/秒
               * */
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d(TAG, "onFling  velocityX:" + velocityX + "    velocityY:" + velocityY);
                getChildAt(0).onTouchEvent(e2);
                return false;
            }
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //        return mGestureDetector.onTouchEvent(ev);

        return super.dispatchTouchEvent(ev);
    }
}
