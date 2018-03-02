package comma.khutimetablehelper;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        loadFile();
        AppContext.first = readBoolean();

        //과목정보들 불러오기
        setSubjectList();
        setSubjectOnlyList();

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


    public void setSubjectList() {
        String line = "";
        BufferedReader br = null;
        int totalRow = 0;

        int Row = 0;
        String Num = "";
        String Name = "";
        String Prof = "";
        int Grade = 0;
        int Credit = 0;
        int Sort = 0;
        int Day = 0;
        double Start = 0.0;
        double End = 0.0;
        int College = 0;
        int Depart = 0;
        double temp = 0.0;


        try {
            InputStream csv = getResources().openRawResource(R.raw.subjectlist);
            InputStreamReader in = new InputStreamReader(csv, "euc-kr");
            br = new BufferedReader(in);
            //br = new BufferedReader(new InputStreamReader(new FileInputStream(csv), "euc-kr"));
//            Charset.forName("UTF-8");

            while ((line = br.readLine()) != null) {
                totalRow++;
            }
            br.close();
            in.close();
            csv.close();
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

        AppContext.subjectList = new Subject[totalRow];

        try {
            InputStream csv = getResources().openRawResource(R.raw.subjectlist);
            InputStreamReader in = new InputStreamReader(csv, "euc-kr");
            //Charset.forName("UTF-8");
            int row = 0;
            br = new BufferedReader(in);

            while ((line = br.readLine()) != null) {

                String[] array = line.split(",", -1);
                for (int i = 0; i < 12; i++) {
                    switch (i) {//고유번호 학수번호 과목명 대상학년 교수명 이수구분 학점 요일 시작 끝 단과대 학과
                        case 0:
                            temp = Double.parseDouble(array[i]);
                            Row = (int) temp;
                        case 1:
                            Num = array[i];
                            break;
                        case 2:
                            Name = array[i];
                            break;
                        case 3:
                            temp = Double.parseDouble(array[i]);
                            Grade = (int) temp;
                            break;
                        case 4:
                            Prof = array[i];
                            break;
                        case 5:
                            temp = Double.parseDouble(array[i]);
                            Sort = (int) temp;
                            break;
                        case 6:
                            temp = Double.parseDouble(array[i]);
                            Credit = (int) temp;
                            break;
                        case 7:
                            temp = Double.parseDouble(array[i]);
                            Day = (int) temp;
                            break;
                        case 8:
                            Start = Double.parseDouble(array[i]);
                            break;
                        case 9:
                            End = Double.parseDouble(array[i]);
                            break;
                        case 10:
                            temp = Double.parseDouble(array[i]);
                            College = (int) temp;
                            break;
                        case 11:
                            temp = Double.parseDouble(array[i]);
                            Depart = (int) temp;
                            break;
                    }
                }
                AppContext.subjectList[row] = new Subject(Row, Num, Name, Prof, Grade, Credit, Sort, Day, Start, End, College, Depart);

                row++;
            }
            br.close();
            in.close();
            csv.close();
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
    }

    public void setSubjectOnlyList() {
        AppContext.onlySubjectList.add(AppContext.subjectList[0]);
        for (int i = 0; i < AppContext.subjectList.length - 1; i++) {
            if (!(AppContext.subjectList[i + 1].cNum.equals(AppContext.subjectList[i].cNum))) {
                AppContext.onlySubjectList.add(AppContext.subjectList[i + 1]);
            }
        }
    }

    public void loadFile() {
        String path = Environment.getExternalStorageDirectory().getPath() + "";
        File dirFile = new File(path, "/SaveList");
        if(!dirFile.exists()) {
            dirFile.mkdirs();
        }

        Log.d("tag", "minyoung" + dirFile.exists() + "/" + (dirFile == null) + "/" + dirFile.getAbsolutePath());

        if(dirFile.exists()){
            String[] fileList = dirFile.list();
            for (int i = 0; i < fileList.length; i++) {
                AppContext.timeTableNameList.add(fileList[i].substring(0, fileList[i].length() - 4));
            }
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



