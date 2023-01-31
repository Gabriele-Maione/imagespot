package com.imagespot.DAO;

import com.imagespot.model.Subject;

import java.util.ArrayList;

public interface SubjectDAO {

    void addSubject(Subject subject);
    ArrayList<Subject> getSubjects(int idimage);
}
