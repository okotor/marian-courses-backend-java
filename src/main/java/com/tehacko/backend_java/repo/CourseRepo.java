package com.tehacko.backend_java.repo;

import com.tehacko.backend_java.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Repository
@CrossOrigin(origins = "http://localhost:3000") // Allow React
public interface CourseRepo extends JpaRepository<Course, Integer> {

    Course findBySlug(String slug);

    @Query("SELECT DISTINCT YEAR(c.date) FROM Course c ORDER BY YEAR(c.date) DESC")
    List<Integer> findDistinctCourseYears();

    @Query("SELECT c FROM Course c ORDER BY c.date DESC LIMIT 3")
    List<Course> findLatestCourses();

    @Query("SELECT c FROM Course c WHERE YEAR(c.date) = :year ORDER BY c.date DESC")
    List<Course> findCoursesByYear(@Param("year") int year);

    List<Course> findByTitleContainingOrCourseDescriptionContainingOrSummaryContainingOrLecturerContaining(
            String titleKeyword, String courseDescriptionKeyword, String summaryKeyword, String lecturerKeyword);
}
