package comma.khutimetablehelper;

import android.app.Application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Created by MinYoung on 2018-02-04.
 */

// 과목정보
public class AppContext extends Application {




}

class Subject {
    // 멤버변수
    String cNum;// 학수번호
    String cName;// 강좌명
    String cProf;// 교수명
    int cGrade;// 대상학년
    int cCredit;// 학점
    int cSort;// 이수구분 : 11 전공기초, 04 전공필수, 05 전공선택, 06 교직과, 14 중핵교과, 15 배분이수교과, 16 기초교과, 17
    // 자유이수, 20 교직전선, 08 자유선택교과
    int cDay;// 강의요일 요일구분 : 0 월, 1 화, 2 수, 3 목, 4 금
    double cStart;// 시작시간
    double cEnd;// 종료시간
    // 메소드
    Subject() { }; // 기본 생성자
    Subject(String Num, String Name, String Prof, int Grade, int Credit, int Sort, int Day, double Start, double End) {
        cNum = Num;
        cName = Name;
        cProf = Prof;
        cGrade = Grade;
        cCredit = Credit;
        cSort = Sort;
        cDay = Day;
        cStart = Start;
        cEnd = End;
    }; // 생성자
    public void print() {
        System.out.println(cNum + " " + cName + " " + cProf + " " + cGrade + " " + cCredit + " " + cSort + " " + cDay
                + " " + cStart + " " + cEnd);
    };

    String getName() {
        return cName
                ;
    }
}
