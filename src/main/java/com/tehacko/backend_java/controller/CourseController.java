package com.tehacko.backend_java.controller;

import com.tehacko.backend_java.model.Course;
import com.tehacko.backend_java.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/courses")
//@CrossOrigin(origins = {"http://localhost:3000", "https://marian-courses-next-js-frontend.vercel.app"}, allowCredentials = "true") // Allow requests from React
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<?> getCourses() {
        logger.info("Fetching all courses");
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<Course> getCourseBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(courseService.findBySlug(slug));
    }

    @GetMapping("/years")
    public ResponseEntity<List<Integer>> getAvailableCourseYears() {
        return ResponseEntity.ok(courseService.getAvailableCourseYears());
    }

    @GetMapping("/latest")
    public ResponseEntity<List<Course>> getLatestCourses() {
        return ResponseEntity.ok(courseService.getLatestCourses());
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<List<Course>> getCoursesForYear(@PathVariable int year) {
        return ResponseEntity.ok(courseService.getCoursesForYear(year));
    }

    @PostMapping("/save")
    public ResponseEntity<Course> saveCourse(
            @RequestParam("title") String title,
            @RequestParam("summary") String summary,
            @RequestParam("courseDescription") String courseDescription,
            @RequestParam("lecturer") String lecturer,
            @RequestParam("lecturerEmail") String lecturerEmail,
            @RequestParam("image") MultipartFile image) {

        logger.info("Received parameters: title={}, summary={}, courseDescription={}, lecturer={}, lecturerEmail={}",
                title, summary, courseDescription, lecturer, lecturerEmail);

        Course course = courseService.saveCourse(title, summary, courseDescription, lecturer, lecturerEmail, image);
        return ResponseEntity.ok(course);
    }

    @PostMapping("/update/{slug}")
    public ResponseEntity<Course> updateCourse(
            @PathVariable String slug,
            @RequestParam("title") String title,
            @RequestParam("summary") String summary,
            @RequestParam("courseDescription") String courseDescription,
            @RequestParam("lecturer") String lecturer,
            @RequestParam("lecturerEmail") String lecturerEmail,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        logger.info("Received parameters: title={}, summary={}, courseDescription={}, lecturer={}, lecturerEmail={}",
                title, summary, courseDescription, lecturer, lecturerEmail);
        if (image != null) {
            System.out.println("Image filename: " + image.getOriginalFilename());
        } else {
            System.out.println("No image uploaded.");
        }

        Course updatedCourse = courseService.updateCourse(slug, title, summary, courseDescription, lecturer, lecturerEmail, image);
        return ResponseEntity.ok(updatedCourse);
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<Void> deleteCourseBySlug(@PathVariable String slug) {
        courseService.deleteBySlug(slug);
        return ResponseEntity.noContent().build();
    }

    //Search by Keyword
    @GetMapping("/keyword/{keyword}")
    public ResponseEntity<List<Course>> searchByKeyword(@PathVariable("keyword") String keyword) {
        return ResponseEntity.ok(courseService.search(keyword));
    }
}
