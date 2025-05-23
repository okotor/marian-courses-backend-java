package com.tehacko.backend_java.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.github.slugify.Slugify;
import com.tehacko.backend_java.exception.CustomException;
import com.tehacko.backend_java.model.Course;
import com.tehacko.backend_java.repo.CourseRepo;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
public class CourseService {

    private final CourseRepo courseRepo;
    private final AmazonS3 s3Client;
    private final String bucketName;

    public CourseService(CourseRepo courseRepo, AmazonS3 s3Client, @Value("${aws.s3.bucket-name}") String bucketName) {
        this.courseRepo = courseRepo;
        this.s3Client = s3Client;
        this.bucketName = bucketName;
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

    public List<Course> search(String keyword) {
        return courseRepo.findByTitleContainingOrCourseDescriptionContainingOrSummaryContainingOrLecturerContaining(keyword, keyword, keyword, keyword);
    }

    public List<Course> getAllCourses() {
        return courseRepo.findAll(); // Fetch all courses from the database
    }

    public Course saveCourse(String title, String summary, String courseDescription, String lecturer,
                             String lecturerEmail, MultipartFile imageFile) {
        validateInputs(title, summary, imageFile);

        Course course = new Course();
        course.setDate(LocalDate.now()); // Set the current date
        course.setTitle(title);
        course.setSummary(summary);
        course.setCourseDescription(sanitizeDescription(courseDescription));
        course.setLecturer(lecturer);
        course.setLecturerEmail(lecturerEmail);

        String slug = generateSlug(title);
        course.setSlug(slug);

        String imageName = uploadImageToS3(imageFile, slug);
        course.setImage(imageName);

        return courseRepo.save(course);
    }

    public void deleteBySlug(String slug) {
        Course course = courseRepo.findBySlug(slug);
        if (course == null) {
            throw new CustomException("Course with slug '" + slug + "' not found", 404);
        }

        // Delete image from S3 if exists
        if (course.getImage() != null && !course.getImage().isEmpty()) {
            deleteImageFromS3(course.getImage());
        }

        courseRepo.delete(course);
    }

    public Course updateCourse(String slug, String title, String summary, String courseDescription,
                               String lecturer, String lecturerEmail, MultipartFile imageFile) {

        System.out.println("🔍 Starting updateCourse");
        System.out.println("Slug: " + slug);
        System.out.println("Title: " + title);
        System.out.println("Summary: " + summary);
        System.out.println("Lecturer: " + lecturer);
        System.out.println("LecturerEmail: " + lecturerEmail);
        System.out.println("ImageFile: " + (imageFile != null ? imageFile.getOriginalFilename() : "null"));

        Course existingCourse = courseRepo.findBySlug(slug);
        if (existingCourse == null) {
            throw new CustomException("Course with slug '" + slug + "' not found", 404);
        }

        System.out.println("✅ Course found");

        validateInputs(title, summary, imageFile);

        existingCourse.setTitle(title);
        existingCourse.setSummary(summary);
        existingCourse.setCourseDescription(sanitizeDescription(courseDescription));
        existingCourse.setLecturer(lecturer);
        existingCourse.setLecturerEmail(lecturerEmail);

        // Update slug if title changed (optional)
        if (!existingCourse.getTitle().equals(title)) {
            String newSlug = generateSlug(title);
            existingCourse.setSlug(newSlug);
            System.out.println("🔁 Title changed, new slug: " + newSlug);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            System.out.println("📷 Updating image...");
            // Delete old image from S3 if present
            if (existingCourse.getImage() != null && !existingCourse.getImage().isEmpty()) {
                deleteImageFromS3(existingCourse.getImage());
            }
            String imageName = uploadImageToS3(imageFile, existingCourse.getSlug());
            existingCourse.setImage(imageName);
            System.out.println("✅ Image updated: " + imageName);
        }

        return courseRepo.save(existingCourse);
    }

    private void deleteImageFromS3(String imageName) {
        String s3Key = "public/" + imageName;
        if (s3Client.doesObjectExist(bucketName, s3Key)) {
            s3Client.deleteObject(bucketName, s3Key);
            System.out.println("Deleted image from S3: " + s3Key);
        }
    }


    private void validateInputs(String title, String summary, MultipartFile imageFile) {
        if (title == null || title.isEmpty()) {
            throw new CustomException("Course title is required.", 400);
        }
        if (summary == null || summary.isEmpty()) {
            throw new CustomException("Course summary is required.", 400);
        }
        if (imageFile != null) {
            if (!imageFile.getContentType().startsWith("image/")) {
                throw new CustomException("Invalid file type. Only images are allowed.", 400);
            }
            if (imageFile.getSize() > 5 * 1024 * 1024) {
                throw new CustomException("File size exceeds the limit of 5MB.", 400);
            }
        }
    }

    private String sanitizeDescription(String description) {
        return Jsoup.clean(description, Safelist.basic());
    }

    private String generateSlug(String title) {
        String[] words = title.split(" ");
        String twoSlugWords = String.join(" ", words.length > 1 ? new String[]{words[0], words[1]} : words);
        String randomSlugAddition = String.valueOf(new Random().nextInt(100) + 1);
        String modifiedTitle = twoSlugWords + " " + randomSlugAddition;

        Slugify slugify = Slugify.builder().build();
        return slugify.slugify(modifiedTitle);
    }

    private String uploadImageToS3(MultipartFile imageFile, String slug) {
        try {
            String extension = imageFile.getOriginalFilename().substring(imageFile.getOriginalFilename().lastIndexOf("."));
            String fileName = slug + extension;
            String s3Key = "public/" + fileName;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(imageFile.getContentType());
            metadata.setContentLength(imageFile.getSize());

            s3Client.putObject(bucketName, s3Key, imageFile.getInputStream(), metadata);
            return fileName;
        } catch (IOException e) {
            throw new CustomException("Failed to upload image to S3: " + e.getMessage(), 500);
        }
    }
}
