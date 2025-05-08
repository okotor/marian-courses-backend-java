package com.tehacko.backend_java.dto;

import javax.validation.constraints.NotBlank;

public class CourseRequest {

    @NotBlank(message = "Title is required.")
    private String title;

    @NotBlank(message = "Summary is required.")
    private String summary;

    @NotBlank(message = "Course description is required.")
    private String courseDescription;

    @NotBlank(message = "Lecturer name is required.")
    private String lecturer;

    @NotBlank(message = "Lecturer email is required.")
    private String lecturerEmail;

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String getLecturerEmail() {
        return lecturerEmail;
    }

    public void setLecturerEmail(String lecturerEmail) {
        this.lecturerEmail = lecturerEmail;
    }
}