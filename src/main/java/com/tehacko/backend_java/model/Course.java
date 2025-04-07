package com.tehacko.backend_java.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;
    private String slug;
    private String title;
    private String image;
    private String summary;
    @Column(columnDefinition = "TEXT")
    private String courseDescription;
    private String lecturer;
    private String lecturerEmail;

    public int getcId() {
        return cId;
    }

    public void setcId(int cId) {
        this.cId = cId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    @Override
    public String toString() {
        return "Course{" +
                "cId=" + cId +
                ", date=" + date +
                ", slug='" + slug + '\'' +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", summary='" + summary + '\'' +
                ", courseDescription='" + courseDescription + '\'' +
                ", lecturer='" + lecturer + '\'' +
                ", lecturerEmail='" + lecturerEmail + '\'' +
                '}';
    }


}
