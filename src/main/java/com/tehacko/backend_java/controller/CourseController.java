package com.tehacko.backend_java.controller;

import com.tehacko.backend_java.model.Course;
import com.tehacko.backend_java.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/courses")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true") // Allow requests from React
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/{slug}")
    public ResponseEntity<Course> getCourseBySlug(@PathVariable String slug) {
        Course course = courseService.findBySlug(slug);
        if (course == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(course);
    }

    @GetMapping("/years")
    public List<Integer> getAvailableCourseYears() {
        return courseService.getAvailableCourseYears();
    }

    @GetMapping("/latest")
    public List<Course> getLatestCourses() {
        return courseService.getLatestCourses();
    }

    @GetMapping("/year/{year}")
    public List<Course> getCoursesForYear(@PathVariable int year) {
        return courseService.getCoursesForYear(year);
    }

    @PostMapping("/save")
    public ResponseEntity<Course> saveCourse(
            @RequestParam("title") String title,
            @RequestParam("summary") String summary,
            @RequestParam("courseDescription") String courseDescription,
            @RequestParam("lecturer") String lecturer,
            @RequestParam("lecturerEmail") String lecturerEmail,
            @RequestParam("image") MultipartFile image) {

        try {
            Course course = new Course();
            course.setDate(LocalDate.now()); // Set the current date
            course.setTitle(title);
            course.setSummary(summary);
            course.setCourseDescription(courseDescription);
            course.setLecturer(lecturer);
            course.setLecturerEmail(lecturerEmail);

            Course savedCourse = courseService.saveCourse(course, image);
            return ResponseEntity.ok(savedCourse);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    //Search by Keyword
    @GetMapping("courses/keyword/{keyword}")
    public List<Course> searchByKeyword(@PathVariable("keyword") String keyword){
        return courseService.search(keyword);
    }

}
