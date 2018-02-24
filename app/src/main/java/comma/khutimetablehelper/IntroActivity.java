package comma.khutimetablehelper;


import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;


public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

//        loadFile();

        // 2초동안 띄우고 다음화면
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);
    }

    public void loadFile() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        path += "/SaveList";
        File dirFile = new File(path);
        dirFile.mkdirs();
        String[] fileList = dirFile.list();
        for(int i = 0; i< fileList.length; i++) {
            AppContext.timeTableNameList.add(fileList[i].substring(0,fileList[i].length()-4));
            Log.d("tag", "minyoung/"+fileList[i]);
        }
    }

}



