//package com.tehacko.backend_java.config;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//@Component
//public class CorsDebugFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, java.io.IOException {
//        filterChain.doFilter(request, response);
//        System.out.println("CORS Headers:");
//        System.out.println("Access-Control-Allow-Origin: " + response.getHeader("Access-Control-Allow-Origin"));
//        System.out.println("Access-Control-Allow-Methods: " + response.getHeader("Access-Control-Allow-Methods"));
//        System.out.println("Access-Control-Allow-Headers: " + response.getHeader("Access-Control-Allow-Headers"));
//    }
//}