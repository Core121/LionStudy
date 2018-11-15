package lionstudy.Classes;

import java.util.List;

public class Teacher {
    private List<Course> courseList;

    //Getters
    public List<Course> getCourseList() {
        return courseList;
    }
    //Setters
    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }
}
