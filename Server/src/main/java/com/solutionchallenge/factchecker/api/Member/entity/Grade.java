package com.solutionchallenge.factchecker.api.Member.entity;

public enum Grade {
    BEGINNER("beginner"),
    INTERMEDIATE("intermediate"),
    ADVANCED("advanced");

    private final String grade;

    Grade(String grade) {
        this.grade = grade;
    }

    public String getGrade(){return grade; }

    public static Grade getGrade(String grade){
        for (Grade grade1 : Grade.values()){
            if (grade1.grade.equalsIgnoreCase(grade)){
                return grade1;
            }
        }
        return null;
    }

}
