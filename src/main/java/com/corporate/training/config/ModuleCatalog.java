package com.corporate.training.config;
import java.util.*;
import org.springframework.stereotype.Component;
@Component
public class ModuleCatalog {
 public record Field(String name,String label,String type,boolean required){}
 public record Module(String key,String title,String table,List<Field> fields){}
 private final Map<String,Module> modules=new LinkedHashMap<>();
 public ModuleCatalog(){
  add("courses","Course Management","courses", f("course_name","Course name","text",true),f("duration_weeks","Duration (weeks)","number",true),f("technology_requirements","Technology requirements","textarea",false),f("description","Description","textarea",false));
  add("batches","Batch Management","batches",f("batch_name","Batch name","text",true),f("course_id","Course ID","number",true),f("start_date","Start date","date",true),f("end_date","End date","date",true),f("start_time","Start time","time",true),f("end_time","End time","time",true),f("student_count","Student count","number",true),f("location","Location","text",false),f("status","Status (PLANNED/ACTIVE/COMPLETED)","text",true));
  add("trainers","Trainer Management","trainers",f("trainer_name","Trainer name","text",true),f("email","Email","email",true),f("skills","Skills","textarea",false),f("availability_status","Availability (AVAILABLE/UNAVAILABLE)","text",true));
  add("classrooms","Classroom Management","classrooms",f("classroom_name","Classroom name","text",true),f("capacity","Seating capacity","number",true),f("location","Location","text",false),f("has_projector","Projector (true/false)","checkbox",false),f("has_internet","Internet (true/false)","checkbox",false),f("status","Status (AVAILABLE/UNAVAILABLE)","text",true));
  add("labs","Computer Lab Management","computer_labs",f("lab_name","Lab name","text",true),f("system_count","Number of systems","number",true),f("capacity","Seating capacity","number",true),f("specifications","Hardware specifications","textarea",false),f("location","Location","text",false),f("has_projector","Projector (true/false)","checkbox",false),f("has_internet","Internet (true/false)","checkbox",false),f("status","Status (AVAILABLE/UNAVAILABLE)","text",true));
  add("software","Software Management","software",f("software_name","Software name","text",true),f("version","Version","text",true),f("configuration","Configuration / notes","textarea",false));
  add("requirements","Resource Requirement Management","batch_requirements",f("batch_id","Batch ID","number",true),f("resource_type","Required resource (CLASSROOM/LAB)","text",true),f("systems_needed","Systems needed","number",false),f("minimum_capacity","Minimum capacity","number",true),f("projector_required","Projector required (true/false)","checkbox",false),f("internet_required","Internet required (true/false)","checkbox",false),f("software_ids","Required software IDs (comma separated)","text",false));
  add("trainer-assignments","Trainer Assignments","trainer_assignments",f("batch_id","Batch ID","number",true),f("trainer_id","Trainer ID","number",true),f("start_date","Start date","date",true),f("start_time","Start time","time",true),f("end_time","End time","time",true));
  add("course-software","Course Software Requirements","course_software",f("course_id","Course ID","number",true),f("software_id","Software ID","number",true));
  add("lab-software","Lab Software Installations","lab_software",f("lab_id","Lab ID","number",true),f("software_id","Software ID","number",true),f("installed_version","Installed version","text",false));
  add("conflicts","Conflict History","conflict_records",f("batch_id","Batch ID","number",false),f("resource_type","Resource type","text",false),f("resource_id","Resource ID","number",false),f("reason","Conflict reason","textarea",true),f("status","Status (UNRESOLVED/RESOLVED)","text",true));
  add("audit","Audit History","audit_history",f("entity_type","Entity type","text",true),f("entity_id","Entity ID","number",false),f("action","Action","text",true),f("details","Details","textarea",false),f("performed_by","Performed by","text",false));
  add("maintenance","Maintenance & Unavailability","maintenance",f("resource_type","Resource type (CLASSROOM/LAB)","text",true),f("resource_id","Resource ID","number",true),f("start_date","Start date","date",true),f("end_date","End date","date",true),f("reason","Reason","textarea",true),f("status","Status (SCHEDULED/COMPLETED)","text",true));
 }
 private Field f(String n,String l,String t,boolean r){return new Field(n,l,t,r);}
 private void add(String k,String t,String table,Field...fields){modules.put(k,new Module(k,t,table,List.of(fields)));}
 public Module get(String key){Module m=modules.get(key);if(m==null)throw new IllegalArgumentException("Unknown module: "+key);return m;}
 public Collection<Module> all(){return modules.values();}
}
