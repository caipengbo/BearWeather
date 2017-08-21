package cn.bearweather;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.bearweather.activity.SelectAreaActivity;
import cn.bearweather.fragment.SelectAreaFragment;

public class MainActivity extends AppCompatActivity {

    private Button mSelectButton;
    private TextView mShowText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mShowText = (TextView) findViewById(R.id.show_text);
        mSelectButton = (Button) findViewById(R.id.select_button);
        mSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectAreaActivity.class);
                startActivityForResult(intent,100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            String s = data.getStringExtra("info");
            mShowText.setText(s);
        }

    }
}
