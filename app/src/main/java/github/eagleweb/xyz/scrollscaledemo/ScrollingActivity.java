package github.eagleweb.xyz.scrollscaledemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class ScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
//        RecyclerView rv = findViewById(R.id.rv);
//
//        rv.setAdapter(new RecyclerView.Adapter() {
//            @Override
//            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                return new ViewHolder(getLayoutInflater().inflate(R.layout.item_simple, parent, false));
//            }
//
//            @Override
//            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//                ViewHolder vh = (ViewHolder) holder;
//                vh.text.setText("Fake Item " + (position + 1));
//                vh.text2.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
//            }
//
//            @Override
//            public int getItemCount() {
//                return 20;
//            }
//
//            class ViewHolder extends RecyclerView.ViewHolder {
//
//                TextView text;
//                TextView text2;
//
//                public ViewHolder(View itemView) {
//                    super(itemView);
//
//                    text = (TextView) itemView.findViewById(R.id.text);
//                    text2 = (TextView) itemView.findViewById(R.id.text2);
//                }
//
//            }
//        });


        //        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //        setSupportActionBar(toolbar);

        //        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //        fab.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //                        .setAction("Action", null).show();
        //            }
        //        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
