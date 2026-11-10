package com.corporate.training.service;
import com.corporate.training.config.ModuleCatalog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class ModuleService {
 private final JdbcTemplate jdbc; private final ModuleCatalog catalog;
 public ModuleService(JdbcTemplate jdbc,ModuleCatalog catalog){this.jdbc=jdbc;this.catalog=catalog;}
 public List<Map<String,Object>> list(String key){return jdbc.queryForList("SELECT * FROM "+catalog.get(key).table()+" ORDER BY id DESC");}
 public Map<String,Object> get(String key,long id){return jdbc.queryForMap("SELECT * FROM "+catalog.get(key).table()+" WHERE id=?",id);}
 public Map<String,Object> save(String key,Map<String,Object> data){
  var m=catalog.get(key); Map<String,Object> clean=new LinkedHashMap<>();
  for(var f:m.fields()) if(data.containsKey(f.name())) clean.put(f.name(),convert(data.get(f.name()),f.type()));
  if(clean.isEmpty()) throw new IllegalArgumentException("No supported fields supplied");
  if(data.get("id")!=null && !data.get("id").toString().isBlank()){
   long id=Long.parseLong(data.get("id").toString());
   String sets=String.join(",",clean.keySet().stream().map(k->k+"=?").toList());
   List<Object> args=new ArrayList<>(clean.values());args.add(id);
   jdbc.update("UPDATE "+m.table()+" SET "+sets+" WHERE id=?",args.toArray());
   jdbc.update("INSERT INTO audit_history(entity_type,entity_id,action,details,performed_by) VALUES(?,?,'UPDATE',?,'system')",key.toUpperCase(),id,"Updated "+m.title());
   return get(key,id);
  } else {
   String cols=String.join(",",clean.keySet());String marks=String.join(",",Collections.nCopies(clean.size(),"?"));
   List<Object> args=new ArrayList<>(clean.values());
   jdbc.update("INSERT INTO "+m.table()+" ("+cols+") VALUES ("+marks+")",args.toArray());
   Long id=jdbc.queryForObject("SELECT LAST_INSERT_ID()",Long.class);
   jdbc.update("INSERT INTO audit_history(entity_type,entity_id,action,details,performed_by) VALUES(?,?,'CREATE',?,'system')",key.toUpperCase(),id,"Created "+m.title());
   return get(key,id);
  }
 }
 private Object convert(Object v,String type){
  if(v==null)return null;String s=v.toString().trim();if(s.isEmpty())return null;
  if(type.equals("number"))return Integer.valueOf(s);
  if(type.equals("checkbox"))return Boolean.valueOf(s);
  return s;
 }
 public void delete(String key,long id){jdbc.update("DELETE FROM "+catalog.get(key).table()+" WHERE id=?",id);
  jdbc.update("INSERT INTO audit_history(entity_type,entity_id,action,details,performed_by) VALUES(?,?,'DELETE',?,'system')",key.toUpperCase(),id,"Deleted record from "+catalog.get(key).title());}
}
