package wallit_app.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.wallit_app.R;

public class FundInfoActivity extends ToolBarActivity {

    // Using this library for the graph view: https://github.com/jjoe64/GraphView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fundinfo_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
