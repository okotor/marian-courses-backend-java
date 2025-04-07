package com.tehacko.backend_java.service;
import com.github.slugify.Slugify;
import com.tehacko.backend_java.model.Course;
import com.tehacko.backend_java.repo.CourseRepo;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Random;

@Service
public class CourseService {

    private final CourseRepo courseRepo;

    @Autowired
    public CourseService(CourseRepo courseRepo) {
        this.courseRepo = courseRepo;
    }

    public Course findBySlug(String slug) {
        return courseRepo.findBySlug(slug);
    }

    public List<Integer> getAvailableCourseYears() {
        return courseRepo.findDistinctCourseYears();
    }

    public List<Course> getLatestCourses() {
        return courseRepo.findLatestCourses();
    }

    public List<Course> getCoursesForYear(int year) {
        return courseRepo.findCoursesByYear(year);
    }

    public Course saveCourse(Course course, MultipartFile imageFile) throws IOException {
        // Generate a slug
        String[] words = course.getTitle().split(" ");
        String twoSlugWords = String.join(" ", words.length > 1 ? new String[]{words[0], words[1]} : words);
        String randomSlugAddition = String.valueOf(new Random().nextInt(100) + 1);
        String modifiedTitle = twoSlugWords + " " + randomSlugAddition;
        // Create a Slugify instance using the builder
        Slugify slugify = Slugify.builder().build();
        // Generate the slug
        String slug = slugify.slugify(modifiedTitle);
        course.setSlug(slug);
        // Sanitize course description
        String sanitizedDescription = Jsoup.clean(course.getCourseDescription(), Safelist.basic());
        course.setCourseDescription(sanitizedDescription);
        // Handle image processing
        String extension = imageFile.getOriginalFilename().substring(imageFile.getOriginalFilename().lastIndexOf("."));
        String fileName = slug + extension;
        Path filePath = Paths.get("../public/" + fileName);

        try {
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            // Handle the exception, possibly rethrow it or log it
            throw new IOException("Failed to save the image file", e);
        }
        // Set image name for DB storage
        course.setImage(fileName);

        return courseRepo.save(course);
    }

    public List<Course> search(String keyword) {
        return courseRepo.findByTitleContainingOrCourseDescriptionContainingOrSummaryContainingOrLecturerContaining(keyword, keyword, keyword, keyword);
    }

}
