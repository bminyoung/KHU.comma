package comma.khutimetablehelper;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    private BackPressCloseHandler backPressCloseHandler;

    //static ArrayList<Subject> subjectList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //과목정보들 불러오기
        setSubjectList();
        setSubjectOnlyList();

//        //저장된 시간표 불러오기
//        while(false) {
//            String fileName = "";
//            int[] tableContent = loadTimeTableList(fileName); //객체 번호 저장 배열
//            for (int i = 0; i < tableContent.length; i++) {
//                Log.d("tag", ""+tableContent[i]);
//            } // 테스트용 출력문
//        }

        // 뒤로가기 핸들러
        backPressCloseHandler = new BackPressCloseHandler(this);
    }

    public int[] loadTimeTableList(String fileName){
        BufferedReader br = null;
        String cvsSplitBy = ",";
        String line = "";
        String[] field = new String[42];

        int arSize = 0;
        try {
            File csv = new File(getApplicationContext().getFilesDir().getAbsolutePath() + fileName + ".csv");
            br = new BufferedReader(new InputStreamReader(new FileInputStream(csv), "euc-kr"));
            // Charset.forName("UTF-8");

            while ((line = br.readLine()) != null) {
                field = line.split(cvsSplitBy, -1);
                arSize = field.length;
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
        int[] file = new int[arSize];
        for (int i = 0; i < arSize; i++) {
            double temp = Double.parseDouble(field[i]);
            file[i] = (int) temp;
        }
        return file;

    };

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
        double temp = 0.0;

        try {
            InputStream csv = getResources().openRawResource(R.raw.list_of_science);
            //InputStream csv = openFileInput("이과대학 개설과목00.csv");
            InputStreamReader in = new InputStreamReader(csv, "euc-kr");
            //File csv = new File("C:\\Users\\MinYoung\\Desktop\\이과대학 개설과목00.csv");
            br = new BufferedReader(in);
            //br = new BufferedReader(new InputStreamReader(new FileInputStream(csv), "euc-kr"));
            Charset.forName("UTF-8");

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
            InputStream csv = getResources().openRawResource(R.raw.list_of_science);
            InputStreamReader in = new InputStreamReader(csv, "euc-kr");
            //File csv = new File("C:\\Users\\MinYoung\\Desktop\\이과대학 개설과목00.csv");

            br = new BufferedReader(in);
            //br = new BufferedReader(new InputStreamReader(new FileInputStream(csv), "euc-kr"));
            Charset.forName("UTF-8");
            int row = 0, i;

            while ((line = br.readLine()) != null) {
                String[] array = line.split(",", -1);
                for (i = 0; i < 10; i++) {
                    switch (i) {
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
                            Prof = array[i];
                            break;
                        case 4:
                            temp = Double.parseDouble(array[i]);
                            Grade = (int) temp;
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
                    }
                }
                AppContext.subjectList[row] = new Subject(Row, Num, Name, Prof, Grade, Credit, Sort, Day, Start, End);
//                sub[row].print();
//                System.out.println("");

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