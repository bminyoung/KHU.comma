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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //과목정보들 불러오기
        setSubjectList();
        setSubjectOnlyList();

        // 뒤로가기 핸들러
        backPressCloseHandler = new BackPressCloseHandler(this);

        ImageButton info_imgbtn = (ImageButton)findViewById(R.id.main_imgbtn_info);
        info_imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("주의사항");
                dialog.setMessage("* 선택한 시간표를 통한 수강신청의 성공 여부는 본인에게 있습니다.\n* 시간표 설정 진행 중 세부적인 설명은 각 화면에서 볼 수 있습니다.");
                dialog.setNeutralButton("닫기",null);
                dialog.show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
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