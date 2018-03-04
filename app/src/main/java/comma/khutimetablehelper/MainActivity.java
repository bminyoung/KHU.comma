package comma.khutimetablehelper;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 뒤로가기 핸들러
        backPressCloseHandler = new BackPressCloseHandler(this);

        ImageButton info_imgbtn = (ImageButton)findViewById(R.id.main_imgbtn_info);
        info_imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("주의사항");
                dialog.setMessage("* 선택한 시간표를 통한 수강신청 결과의 책임은 본인에게 있습니다.\n* 시간표 설정 진행 중 세부적인 설명은 각 화면에서 볼 수 있습니다.");
                dialog.setNeutralButton("닫기",null);
                dialog.show();
            }
        });
    }

    @Override
    protected void onDestroy(){
        saveBoolean(AppContext.first); //저장할 불린 배열 인수로 넣어줘라
        super.onDestroy();
    }

    public void saveBoolean(boolean[] bool) {
        String path = getFilesDir().getAbsolutePath();
        String fileName = path + "/saveboolean.csv";
        try {
            BufferedWriter fw = new BufferedWriter(new FileWriter(fileName));
            for (int i = 0; i < 6; i++) {
                fw.write("" + bool[i]);
                if (i != 6) {
                    fw.write(",");
                }
            }
            fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    public void GoSetting(View view) {

        Intent intent = new Intent(this, NeedActivity.class);
        startActivity(intent);
    }

    public void GoList(View view) {
        Intent intent = new Intent(MainActivity.this, SaveListActivity.class);
        startActivity(intent);
    }
}

class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            toast.cancel();
            activity.finish();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity, "한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }

}