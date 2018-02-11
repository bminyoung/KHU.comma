package comma.khutimetablehelper;

import android.app.Application;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by MinYoung on 2018-02-04.
 */

// 과목정보
public class AppContext extends Application {

    //이번 학기 과목정보
    static Subject[] subjectList;
    static ArrayList<Subject> onlySubjectList = new ArrayList<Subject>();
    //static ArrayList<Subject> subjectList = new ArrayList<Subject>();

    static ArrayList<Subject> timeTable = new ArrayList<Subject>(); //임시 시간표
    static ArrayList<ArrayList<Subject>> tempTimeTableList = new ArrayList<ArrayList<Subject>>(); // MadeResult에서 보여줄 시간표결과 목록
    static ArrayList<ArrayList<Subject>> timeTableList = new ArrayList<ArrayList<Subject>>(); // LoadResult에서 보여줄 저장된 시간표 목록
    static ArrayList<String> timeTableNameList = new ArrayList<String>(); // 저장된 시간표 이름 목록

}


//과목클래스
class Subject implements Serializable {

    // 멤버변수
    int cRow; // 고유번호
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
    public Subject() {
    }; // 기본 생성자

    public Subject(int Row, String Num, String Name, String Prof, int Grade, int Credit, int Sort, int Day,
                   double Start, double End) {
        cRow = Row;
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

    public String getName(){
        return cName;
    }
}

class TimeTable{
    ArrayList<Subject> subject;
}




