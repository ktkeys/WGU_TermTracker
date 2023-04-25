package com.wgu.termtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

public class TermDatabase extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "study.db";

    private static TermDatabase mStudyDb;

    public enum TermSortOrder{ALPHABETIC, UPDATE_DESC, UPDATE_ASC};

    public static TermDatabase getInstance(Context context){
        if (mStudyDb == null){
            mStudyDb = new TermDatabase(context);
        }
        return mStudyDb;
    }

    private TermDatabase(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    private static final class TermTable{
        private static final String TABLE = "terms";
        private static final String COL_TITLE = "title";
        private static final String COL_START_TIME = "startTime";
        private static final String COL_END_TIME = "endTime";

    }

    private static final class CourseTable{
        private static final String TABLE = "courses";
        private static final String COL_ID = "_id";
        private static final String COL_TITLE = "title";
        private static  final String COL_START_TIME = "startTime";
        private static final String COL_END_TIME = "endTime";
        private static final String COL_STATUS = "status";

    }

    private static final class AssessmentTable{
        private static final String TABLE = "assessments";
        private static final String COL_ID = "_id";
        private static final String COL_ASSESSMENT = "assessments";
        private static  final String COL_START_TIME = "startTime";
        private static final String COL_END_TIME = "endTime";
        private static final String COL_PERF_ASSESSMENT = "perfAssessment";
        private static final String COL_OBJ_ASSESSMENT = "objAssessment";
    }

    private static final class TermCoursesTable{
        private static final String TABLE = "termCourses";
        private static final String COL_TERM = "termCourse";
        private static  final String COL_COURSE_ID = "courseID";

    }

    private static final class CourseAssessmentTable{
        private static final String TABLE = "courseAssessments";
        private static final String COL_ID = "_id";
        private static final String COL_ASSESSMENT_ID = "assessmentID";
        private static  final String COL_COURSE_ID = "courseID";

    }

    private static final class InstuctorTable{
        private static final String TABLE = "instructor";
        private static final String COL_NAME = "name";
        private static final String COL_EMAIL = "email";
        private static  final String COL_PHONENUMBER = "phonenumber";

    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        //creates term table
        db.execSQL("create table " + TermTable.TABLE + "(" +
                TermTable.COL_TITLE + " primary key, " +
                TermTable.COL_START_TIME + " date , " +
                TermTable.COL_END_TIME + " date) ");

        //creates course table with foreign key that cascade deletes
        db.execSQL("CREATE TABLE " + CourseTable.TABLE + " (" +
                CourseTable.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CourseTable.COL_TITLE + ", " +
                CourseTable.COL_START_TIME + " date, " +
                CourseTable.COL_END_TIME + " date, " +
                CourseTable.COL_STATUS + ")");

        //creates course table with foreign key that cascade deletes
        db.execSQL("CREATE TABLE " + InstuctorTable.TABLE + " (" +
                InstuctorTable.COL_EMAIL + " PRIMARY KEY, " +
                InstuctorTable.COL_NAME + ", " +
                InstuctorTable.COL_PHONENUMBER
                );

        //creates assessment table with foreign key that cascade deletes
        db.execSQL("CREATE TABLE " + AssessmentTable.TABLE + " (" +
                AssessmentTable.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AssessmentTable.COL_ASSESSMENT + ", " +
                AssessmentTable.COL_START_TIME + " date, " +
                AssessmentTable.COL_END_TIME + " date, " +
                AssessmentTable.COL_OBJ_ASSESSMENT + ", " +
                AssessmentTable.COL_PERF_ASSESSMENT + ")");

        db.execSQL("CREATE TABLE " + TermCoursesTable.TABLE + " (" +
                TermCoursesTable.COL_COURSE_ID + " REFERENCES " + CourseTable.COL_ID + ", " +
                TermCoursesTable.COL_TERM + TermTable.COL_TITLE + " REFERENCES " +
                TermTable.COL_TITLE + ", " +
                "PRIMARY KEY (" + TermCoursesTable.COL_COURSE_ID + ", " + TermCoursesTable.COL_TERM + ")"
                );

        db.execSQL("CREATE TABLE " + CourseAssessmentTable.TABLE + " (" +
                CourseAssessmentTable.COL_ASSESSMENT_ID + " REFERENCES " + AssessmentTable.COL_ID + ", " +
                CourseAssessmentTable.COL_COURSE_ID  + " REFERENCES " + CourseTable.COL_ID + ", " +
                "PRIMARY KEY (" + CourseAssessmentTable.COL_COURSE_ID + ", " + CourseAssessmentTable.COL_ASSESSMENT_ID + ")"
        );

        // add terms
        String[] terms = {"History", "Math", "Computing"};
        for (String sub: terms) {
            Term term = new Term(sub);
            ContentValues values = new ContentValues();
            values.put(TermTable.COL_TITLE, term.getText());
            values.put(TermTable.COL_START_TIME, term.getUpdateTime());
            db.insert(TermTable.TABLE, null, values);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TermTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CourseTable.TABLE);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db){
        super.onOpen(db);
        if(!db.isReadOnly()){
            //enable foreign key restraints
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
                db.execSQL("PRAGMA FOREIGN_KEYS = ON;");
            } else {
                db.setForeignKeyConstraintsEnabled(true);
            }
        }
    }


    public List<Term> getTerms(TermSortOrder order){
        List<Term> terms = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String orderBy;

        switch(order){
            case ALPHABETIC:
                orderBy = TermTable.COL_TITLE + " COLLATE NOCASE";
                break;
            case UPDATE_DESC:
                orderBy = TermTable.COL_START_TIME + " DESC";
                break;
            default:
                orderBy = TermTable.COL_START_TIME + " ASC";
                break;
        }


        String sql = "SELECT * FROM " + TermTable.TABLE + " ORDER BY " + orderBy;
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do{
                Term term = new Term();
                term.setText(cursor.getString(0));
                term.setUpdateTime(cursor.getLong(1));
                terms.add(term);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return terms;
    }

    public boolean addTerm(Term term){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TermTable.COL_TITLE, term.getText());
        values.put(TermTable.COL_START_TIME, term.getUpdateTime());
        int id = (int) db.insert(TermTable.TABLE, null, values);
        return id != -1;
    }

    public void updateTerm(Term term){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TermTable.COL_TITLE, term.getText());
        values.put(TermTable.COL_START_TIME, term.getUpdateTime());
        db.update(TermTable.TABLE, values, TermTable.COL_TITLE + " = ?", new String[] {term.getText()});
    }

    public void deleteTerm(Term term){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TermTable.TABLE, TermTable.COL_TITLE + " = ?", new String[] {term.getText()});
    }

    public List<Course> getCourses(String term){
        List<Course> courses = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + CourseTable.TABLE +
                " WHERE " + CourseTable.COL_TITLE + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { term });
        if (cursor.moveToFirst()){
            do {
                Course course = new Course();
                course.setID(cursor.getInt(0));
                course.setTitle(cursor.getString(1));
                 //TODO clean this up to match courses.
                //course.setStartDate(cursor.getString(2));
                //course.setTerm(cursor.getString(3));
                courses.add(course);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return courses;

    }

    public Course getCourse(int courseId){
        Course course = null;

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + CourseTable.TABLE +
                " WHERE " + CourseTable.COL_ID + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {Float.toString(courseId)});

        if (cursor.moveToFirst()){
            course = new Course();
            course.setID(cursor.getInt(0));
            /* TODO clean this up to match courses course.setText(cursor.getString(1));
            course.setAnswer(cursor.getString(2));
            course.setTerm(cursor.getString(3)); */
        }
        return course;
    }

    public void addCourse (Course course){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        /* TODO Clean this up to mach terms/courses, whatever it si
        values.put(CourseTable.COL_TITLE, course.getText());

        int courseID = db.insert(CourseTable.TABLE, null, values);
        course.setID(courseID);

        //change update time in terms table
        updateTerm(new Term(course.getTerm())); */
    }

    public void updateCourse(Course course) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CourseTable.COL_ID, course.getID());
        //values.put(CourseTable.COL_TITLE, course.getText());
        /* values.put(CourseTable.COL_ANSWER, course.getAnswer());
        values.put(CourseTable.COL_SUBJECT, course.getTerm()); */
        db.update(CourseTable.TABLE, values, CourseTable.COL_ID + " = " + course.getID(), null);

    // change update time in terms table
        //updateTerm(new Term(course.getTerm()));

    }

    public void deleteCourse (int courseID){
      SQLiteDatabase db = getWritableDatabase();
      db.delete(CourseTable.TABLE, CourseTable.COL_ID + " = " + courseID, null);
    }

}
