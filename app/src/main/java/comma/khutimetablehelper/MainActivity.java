package comma.khutimetablehelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //static ArrayList<Subject> subjectList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    void setContext(){
        FileInputStream fis;
        XSSFWorkbook workbook = null;
        try {
            //is = (InputStream) getBaseContext().getResources().getAssets().open("이과대학 개설과목.xls");
            fis = new FileInputStream("C:\\Users\\MinYoung\\AndroidStudioProjects\\KHUTimeTableHelper\\app\\src\\main\\이과대학 개설과목.xlsx");
            //workbook = Workbook.getWorkbook(is);
            workbook = new XSSFWorkbook(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int rowindex = 0;
        int columnindex = 0;
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
        Log.d("minyoung", "worksheet"+(workbook == null));
        XSSFSheet sheet = workbook.getSheetAt(0);
        int rows = sheet.getPhysicalNumberOfRows();

        System.out.println("rows : " + rows);
        System.out.println();
        // for test 키위야 일해라
        boolean isDataCorrect = true;
        for (rowindex = 1; rowindex < rows; rowindex++) {
            XSSFRow row = sheet.getRow(rowindex);
            if (!isDataCorrect)
                break;

//         System.out.println("rowIndex : " + rowindex);

            if (row != null) {
                int cells = row.getPhysicalNumberOfCells();
                for (columnindex = 1; columnindex < 10; columnindex++) {

                    if(!isDataCorrect)
                        break;

                    XSSFCell cell = row.getCell(columnindex);
                    String value = "";
                    if (cell == null) {
                        continue;
                    } else {
                        switch (cell.getCellType()) {
                            case XSSFCell.CELL_TYPE_FORMULA:
                                value = cell.getCellFormula();
                                break;
                            case XSSFCell.CELL_TYPE_NUMERIC:
                                value = cell.getNumericCellValue() + "";
                                break;
                            case XSSFCell.CELL_TYPE_STRING:
                                value = cell.getStringCellValue() + "";
                                break;
                            case XSSFCell.CELL_TYPE_BLANK:
                                value = cell.getBooleanCellValue() + "";
                                break;
                            case XSSFCell.CELL_TYPE_ERROR:
                                value = cell.getErrorCellValue() + "";
                                break;
                        }
                    }
                    ;

                    switch (columnindex) {
                        case 1:
                            Num = value;
                            if (value.equals("false")) {
                                isDataCorrect = false;
                            }
                            break;
                        case 2:
                            Name = value;
                            break;
                        case 3:
                            Prof = value;
                            break;
                        case 4:
                            temp = Double.parseDouble(value);
                            Grade = (int) temp;
                            break;
                        case 5:
                            temp = Double.parseDouble(value);
                            Credit = (int) temp;
                            break;
                        case 6:
                            temp = Double.parseDouble(value);
                            Sort = (int) temp;
                            break;
                        case 7:
                            temp = Double.parseDouble(value);
                            Day = (int) temp;
                            break;
                        case 8:
                            Start = Double.parseDouble(value);
                            break;
                        case 9:
                            End = Double.parseDouble(value);
                            break;
                    }
                }

                if(!isDataCorrect)
                    continue;

                //subjectList.add(new Subject(Num, Name, Prof, Grade, Credit, Sort, Day, Start, End));
//            t[rowindex-1].print(); // 실행확인

            }

        }
    }


    public void GoSetting(View view) {

        //Toast.makeText(MainActivity.this, "첫번째 과목이름은 : "+subjectList.get(0).cName, Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    public void GoList(View view) {
        Intent intent = new Intent(MainActivity.this, SaveListActivity.class);
        startActivity(intent);
    }
}