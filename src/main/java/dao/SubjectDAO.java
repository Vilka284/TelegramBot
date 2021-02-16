package dao;

import entity.Subject;

import java.util.List;

public class SubjectDAO {

    private static SubjectDAO instance;

    public static SubjectDAO getInstance() {
        return instance != null ? instance : new SubjectDAO();
    }

    public Subject getSubjectById(long id) {
        // TODO
        return null;
    }

    public List<Subject> getAllSubjects() {
        // TODO
        return null;
    }


}
