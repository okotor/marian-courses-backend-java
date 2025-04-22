package com.tehacko.backend_java.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.github.slugify.Slugify;
import com.tehacko.backend_java.model.Course;
import com.tehacko.backend_java.repo.CourseRepo;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
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

        // Handle image upload to S3
        if (imageFile != null && !imageFile.isEmpty()) {
            // Extract the file extension from the original file name
            String extension = imageFile.getOriginalFilename().substring(imageFile.getOriginalFilename().lastIndexOf("."));
            // Generate the new file name based on the slug and extension
            String fileName = slug + extension;
            String s3Key = "public/" + fileName; // Full path in the S3 bucket

            try {
                // Initialize the S3 client
                AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                        .withRegion("us-east-1") // Replace with your region
                        .withCredentials(new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(
                                        System.getenv("AWS_ACCESS_KEY_ID"), // Access Key from environment variable
                                        System.getenv("AWS_SECRET_ACCESS_KEY")  // Secret Key from environment variable
                                )
                        ))
                        .build();

                // Set metadata for the file
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType(imageFile.getContentType());
                metadata.setContentLength(imageFile.getSize());

                // Upload the file to S3
                s3Client.putObject("marian-courses-bucket", s3Key, imageFile.getInputStream(), metadata);

                // Set only the image name in the course object (not the full S3 path)
                course.setImage(fileName);
            } catch (IOException e) {
                throw new IOException("Failed to upload the image to S3", e);
            }
        }
//        // Handle image processing
//        String extension = imageFile.getOriginalFilename().substring(imageFile.getOriginalFilename().lastIndexOf("."));
//        String fileName = slug + extension;
//        Path filePath = Paths.get("../public/" + fileName);
//        try {
//            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//        } catch (IOException e) {
//            // Handle the exception, possibly rethrow it or log it
//            throw new IOException("Failed to save the image file", e);
//        }
//        // Set image name for DB storage
//        course.setImage(fileName);
        return courseRepo.save(course);
    }

    public List<Course> search(String keyword) {
        return courseRepo.findByTitleContainingOrCourseDescriptionContainingOrSummaryContainingOrLecturerContaining(keyword, keyword, keyword, keyword);
    }

}
