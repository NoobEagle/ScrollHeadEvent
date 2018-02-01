package github.eagleweb.xyz.scrollscaledemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initViews();
        ivTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Avatar", Toast.LENGTH_SHORT).show();
            }
        });
        LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(layout);
        list.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(getLayoutInflater().inflate(R.layout.item_simple, parent, false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                ViewHolder vh = (ViewHolder) holder;
//                vh.mItemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(MainActivity.this, "点击了：" + position, Toast.LENGTH_SHORT).show();
//                    }
//                });
                vh.mItemView.setOnTouchListener(new View.OnTouchListener() {
                    public boolean isUp;
                    public float dX;
                    public float dY;
                    public long dTime;

                    @Override
                    public boolean onTouch(View v, final MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                isUp = false;
                                dTime = System.currentTimeMillis();
                                dX = event.getRawX();
                                dY = event.getRawY();
                                // 可以做个延时任务，1秒之后还未抬起，并且位置变化不超过5，就视为长按
                                v.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isUp) {
                                            float rawX = event.getRawX();
                                            float rawY = event.getRawY();
                                            //                                            long l = System.currentTimeMillis();
                                            if (Math.abs(rawX - dX) < 5 && Math.abs(rawY - dY) < 5) {
                                                Toast.makeText(MainActivity.this, "长按了：" + position, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }, 1000);
                                break;
                            case MotionEvent.ACTION_UP:
                            case MotionEvent.ACTION_CANCEL:
                                isUp = true;
                                float rawX = event.getRawX();
                                float rawY = event.getRawY();
                                long l = System.currentTimeMillis();
                                if (Math.abs(rawX - dX) < 5 && Math.abs(rawY - dY) < 5 && (l - dTime) < 300 && (l - dTime) > 10) {
                                    // 点击事件
                                    Toast.makeText(MainActivity.this, "点击了：" + position, Toast.LENGTH_SHORT).show();
                                } else if (Math.abs(rawX - dX) < 5 && Math.abs(rawY - dY) < 5 && (l - dTime) < 1000) {
                                    //                                    Toast.makeText(MainActivity.this, "长按了：" + position, Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                break;
                        }
                        float rawX = event.getRawX();
                        return false;
                    }
                });
                vh.text.setText("Fake Item " + (position + 1));
                vh.text2.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
            }

            @Override
            public int getItemCount() {
                return 40;
            }

            class ViewHolder extends RecyclerView.ViewHolder {

                TextView text;
                TextView text2;
                View     mItemView;

                public ViewHolder(View itemView) {
                    super(itemView);

                    text = (TextView) itemView.findViewById(R.id.text);
                    text2 = (TextView) itemView.findViewById(R.id.text2);
                    mItemView = itemView;
                }

            }
        });
        rlTop.setBootomView(rlBottom);
        rlBottom.setTopView(rlTop);
        rlBottom.setListManager(layout);
        rlBottom.setListView(list);

    }

    private TopView      rlTop;
    private ImageView    ivTop;
    private BottomView   rlBottom;
    private RecyclerView list;

    public void initViews() {
        rlTop = (TopView) findViewById(R.id.rl_top);
        ivTop = (ImageView) findViewById(R.id.iv_top);
        rlBottom = (BottomView) findViewById(R.id.rl_bottom);
        list = (RecyclerView) findViewById(R.id.list);
    }

}
