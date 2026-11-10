package com.corporate.training.controller;

import com.corporate.training.config.ModuleCatalog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@Controller
public class WebController {

 private final ModuleCatalog catalog;
 private final JdbcTemplate jdbc;

 public WebController(ModuleCatalog c, JdbcTemplate j) {
  catalog = c;
  jdbc = j;
 }

 @GetMapping("/login")
 public String login() {
  return "login";
 }

 @GetMapping("/")
 public String dashboard(Model m, Authentication authentication) {

  m.addAttribute("courses", count("courses"));
  m.addAttribute("batches", count("batches"));
  m.addAttribute("trainers", count("trainers"));
  m.addAttribute("classrooms", count("classrooms"));
  m.addAttribute("labs", count("computer_labs"));

  m.addAttribute("conflicts",
          countWhere("conflict_records", "status='UNRESOLVED'"));

  m.addAttribute("upcoming",
          jdbc.queryForList(
                  "SELECT b.batch_name, c.course_name, b.start_date, b.student_count " +
                          "FROM batches b " +
                          "JOIN courses c ON c.id=b.course_id " +
                          "WHERE b.start_date>=CURDATE() " +
                          "ORDER BY b.start_date LIMIT 6"
          ));

  m.addAttribute("modules", catalog.all());
  m.addAttribute("username", authentication.getName());

  return "dashboard";
 }

 @GetMapping("/modules/{key}")
 public String module(
         @PathVariable String key,
         Model m,
         Authentication authentication) {

  m.addAttribute("module", catalog.get(key));
  m.addAttribute("modules", catalog.all());
  m.addAttribute("username", authentication.getName());

  return "module";
 }

 @GetMapping("/allocation")
 public String allocation(Model m) {

  m.addAttribute("modules", catalog.all());

  return "allocation";
 }

 @GetMapping("/maintenance")
 public String maintenance(Model m) {

  m.addAttribute("module", catalog.get("maintenance"));
  m.addAttribute("modules", catalog.all());

  return "module";
 }

 private int count(String t) {
  return jdbc.queryForObject(
          "SELECT COUNT(*) FROM " + t,
          Integer.class
  );
 }

 private int countWhere(String t, String w) {
  return jdbc.queryForObject(
          "SELECT COUNT(*) FROM " + t + " WHERE " + w,
          Integer.class
  );
 }
}