package com.corporate.training.service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class AllocationService {
 private final JdbcTemplate jdbc;
 public AllocationService(JdbcTemplate jdbc){this.jdbc=jdbc;}
 public List<Map<String,Object>> all(){return jdbc.queryForList("SELECT a.*,b.batch_name FROM allocations a JOIN batches b ON b.id=a.batch_id ORDER BY a.id DESC");}
 public Map<String,Object> create(Map<String,Object> p,String actor){
  long batchId=Long.parseLong(p.get("batch_id").toString());
  String type=p.get("resource_type").toString().toUpperCase();
  if(!List.of("CLASSROOM","LAB").contains(type))throw new IllegalArgumentException("Resource type must be CLASSROOM or LAB");
  long resourceId=Long.parseLong(p.get("resource_id").toString());
  String date=p.get("session_date").toString(), start=normalizeTime(p.get("start_time").toString()), end=normalizeTime(p.get("end_time").toString());
  if(start.compareTo(end)>=0)throw new IllegalArgumentException("End time must be later than start time");
  var batch=jdbc.queryForMap("SELECT * FROM batches WHERE id=?",batchId);
  List<String> conflicts=new ArrayList<>();
  if(date.compareTo(String.valueOf(batch.get("start_date")))<0 || date.compareTo(String.valueOf(batch.get("end_date")))>0)
   conflicts.add("Session date must fall within the batch start and end dates.");
  if(start.compareTo(String.valueOf(batch.get("start_time")))<0 || end.compareTo(String.valueOf(batch.get("end_time")))>0)
   conflicts.add("Session time must fall within the batch's scheduled time window.");
  String table=type.equals("LAB")?"computer_labs":"classrooms";
  var resource=jdbc.queryForMap("SELECT * FROM "+table+" WHERE id=?",resourceId);
  if(!"AVAILABLE".equalsIgnoreCase(String.valueOf(resource.get("status"))))conflicts.add("Selected "+type.toLowerCase()+" is not available.");
  Integer overlap=jdbc.queryForObject("SELECT COUNT(*) FROM allocations WHERE resource_type=? AND resource_id=? AND session_date=? AND status='CONFIRMED' AND start_time < ? AND end_time > ?",Integer.class,type,resourceId,date,end,start);
  if(overlap!=null&&overlap>0)conflicts.add("This resource already has an overlapping confirmed allocation.");
  int students=((Number)batch.get("student_count")).intValue();
  int capacity=((Number)resource.get("capacity")).intValue();
  List<Integer> minCapacityRows=jdbc.query("SELECT minimum_capacity FROM batch_requirements WHERE batch_id=? ORDER BY id DESC LIMIT 1",(rs,n)->rs.getInt(1),batchId);
  int minimumCapacity=minCapacityRows.isEmpty()?students:minCapacityRows.get(0);
  int requiredCapacity=Math.max(students,minimumCapacity);
  if(capacity<requiredCapacity)conflicts.add("Capacity shortage: required capacity is "+requiredCapacity+" but resource capacity is "+capacity+".");
  Integer wrongType=jdbc.queryForObject("SELECT COUNT(*) FROM batch_requirements WHERE batch_id=? AND resource_type<>?",Integer.class,batchId,type);
  if(wrongType!=null&&wrongType>0)conflicts.add("Selected resource type does not match the batch resource requirement.");
  if(type.equals("LAB")){
   int systems=((Number)resource.get("system_count")).intValue();
   List<Integer> neededRows=jdbc.query("SELECT COALESCE(systems_needed,0) FROM batch_requirements WHERE batch_id=? ORDER BY id DESC LIMIT 1",(rs,n)->rs.getInt(1),batchId);
   Integer needed=neededRows.isEmpty()?0:neededRows.get(0);
   if(systems<needed)conflicts.add("Insufficient computer systems: required "+needed+", available "+systems+".");
   Integer missing=jdbc.queryForObject("SELECT COUNT(*) FROM course_software cs WHERE cs.course_id=? AND NOT EXISTS (SELECT 1 FROM lab_software ls WHERE ls.lab_id=? AND ls.software_id=cs.software_id)",Integer.class,batch.get("course_id"),resourceId);
   if(missing!=null&&missing>0)conflicts.add("The selected lab does not contain all software required by the course.");
  }
  Boolean projector=resource.get("has_projector") instanceof Number ? ((Number)resource.get("has_projector")).intValue()!=0 : Boolean.valueOf(String.valueOf(resource.get("has_projector")));
  Boolean internet=resource.get("has_internet") instanceof Number ? ((Number)resource.get("has_internet")).intValue()!=0 : Boolean.valueOf(String.valueOf(resource.get("has_internet")));
  Integer reqCount=jdbc.queryForObject("SELECT COUNT(*) FROM batch_requirements WHERE batch_id=? AND ((projector_required=TRUE AND ?=FALSE) OR (internet_required=TRUE AND ?=FALSE))",Integer.class,batchId,projector,internet);
  if(reqCount!=null&&reqCount>0)conflicts.add("Selected resource does not satisfy projector or internet requirements.");
  Integer maintenance=jdbc.queryForObject("SELECT COUNT(*) FROM maintenance WHERE resource_type=? AND resource_id=? AND status='SCHEDULED' AND ? BETWEEN start_date AND end_date",Integer.class,type,resourceId,date);
  if(maintenance!=null&&maintenance>0)conflicts.add("Resource is scheduled for maintenance on this date.");
  if(p.get("trainer_id")!=null && !p.get("trainer_id").toString().isBlank()){
   long trainerId=Long.parseLong(p.get("trainer_id").toString());
   Integer trainerOverlap=jdbc.queryForObject("SELECT COUNT(*) FROM trainer_assignments WHERE trainer_id=? AND start_date=? AND start_time < ? AND end_time > ?",Integer.class,trainerId,date,end,start);
   if(trainerOverlap!=null&&trainerOverlap>0)conflicts.add("Trainer already has an overlapping assignment.");
   Integer trainerUnavailable=jdbc.queryForObject("SELECT COUNT(*) FROM trainers WHERE id=? AND availability_status<>'AVAILABLE'",Integer.class,trainerId);
   if(trainerUnavailable!=null&&trainerUnavailable>0)conflicts.add("Selected trainer is marked unavailable.");
  }
  if(!conflicts.isEmpty()){
   for(String reason:conflicts)jdbc.update("INSERT INTO conflict_records(batch_id,resource_type,resource_id,reason,status) VALUES(?,?,?,?, 'UNRESOLVED')",batchId,type,resourceId,reason);
   throw new ConflictException(conflicts);
  }
  jdbc.update("INSERT INTO allocations(batch_id,resource_type,resource_id,session_date,start_time,end_time,status,created_by) VALUES(?,?,?,?,?,?, 'CONFIRMED',?)",batchId,type,resourceId,date,start,end,actor);
  if(p.get("trainer_id")!=null && !p.get("trainer_id").toString().isBlank())
   jdbc.update("INSERT INTO trainer_assignments(batch_id,trainer_id,start_date,start_time,end_time) VALUES(?,?,?,?,?)",batchId,Long.parseLong(p.get("trainer_id").toString()),date,start,end);
  Long id=jdbc.queryForObject("SELECT LAST_INSERT_ID()",Long.class);
  jdbc.update("INSERT INTO audit_history(entity_type,entity_id,action,details,performed_by) VALUES('ALLOCATION',?,'CREATE',?,?)",id,"Confirmed "+type+" allocation for batch "+batchId,actor);
  return jdbc.queryForMap("SELECT * FROM allocations WHERE id=?",id);
 }
 private String normalizeTime(String value){ return value.length()==5 ? value+":00" : value; }
 public static class ConflictException extends RuntimeException{
  private final List<String> conflicts; public ConflictException(List<String> c){super(String.join(" ",c));conflicts=c;} public List<String> getConflicts(){return conflicts;}
 }
}
