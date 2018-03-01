package comma.khutimetablehelper;


import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;


public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

<<<<<<< HEAD
//        loadFile();
=======
        loadFile();
        // AppContext.booleanArray = readBoolean();
>>>>>>> ee27fa1f2258be056236eab3c9ba22ed7278e998

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
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SaveList";
        File dirFile = new File(path);
        dirFile.mkdirs();
        String[] fileList = dirFile.list();
        for(int i = 0; i< fileList.length; i++) {
            AppContext.timeTableNameList.add(fileList[i].substring(0,fileList[i].length()-4));
        }
    }

    //boolean값 저장 파일을 읽어와서 boolean값 배열로 받아오기
    public boolean[] readBoolean() {
        BufferedReader br = null;
        String cvsSplitBy = ",";
        String line = "";
        String[] field = new String[6];
        String path = getFilesDir().getAbsolutePath();
        String fileName = path + "/saveboolean.csv";
        try {
            File csv = new File(fileName);
            br = new BufferedReader(new InputStreamReader(new FileInputStream(csv), "euc-kr"));

            while ((line = br.readLine()) != null) {
                field = line.split(cvsSplitBy, -1);
                break;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        boolean[] file = new boolean[6];
        for (int i = 0; i < 6; i++) {
            file[i] = Boolean.valueOf(field[i]).booleanValue();
        }
        return file;
    }

}



