package github.eagleweb.xyz.scrollscaledemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
                vh.mItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "点击了：" + position, Toast.LENGTH_SHORT).show();
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
