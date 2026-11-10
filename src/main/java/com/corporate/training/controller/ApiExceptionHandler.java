package com.corporate.training.controller;
import com.corporate.training.service.AllocationService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler(AllocationService.ConflictException.class)
 public ResponseEntity<?> conflict(AllocationService.ConflictException e){return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","Allocation blocked by conflict detection","conflicts",e.getConflicts()));}
 @ExceptionHandler(Exception.class)
 public ResponseEntity<?> error(Exception e){return ResponseEntity.badRequest().body(Map.of("message",e.getMessage()==null?"Request failed":e.getMessage()));}
}
