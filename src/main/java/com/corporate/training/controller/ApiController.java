package com.corporate.training.controller;
import com.corporate.training.config.ModuleCatalog;
import com.corporate.training.service.ModuleService;
import com.corporate.training.service.AllocationService;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/api")
public class ApiController {
 private final ModuleCatalog catalog; private final ModuleService service; private final AllocationService allocations;
 public ApiController(ModuleCatalog c,ModuleService s,AllocationService a){catalog=c;service=s;allocations=a;}
 @GetMapping("/modules") public Collection<ModuleCatalog.Module> modules(){return catalog.all();}
 @GetMapping("/modules/{key}/meta") public ModuleCatalog.Module meta(@PathVariable String key){return catalog.get(key);}
 @GetMapping("/modules/{key}") public List<Map<String,Object>> list(@PathVariable String key){return service.list(key);}
 @GetMapping("/modules/{key}/{id}") public Map<String,Object> get(@PathVariable String key,@PathVariable long id){return service.get(key,id);}
 @PostMapping("/modules/{key}") public Map<String,Object> save(@PathVariable String key,@RequestBody Map<String,Object> body){return service.save(key,body);}
 @DeleteMapping("/modules/{key}/{id}") public Map<String,Object> delete(@PathVariable String key,@PathVariable long id){service.delete(key,id);return Map.of("message","Record deleted");}
 @GetMapping("/allocations") public List<Map<String,Object>> allocations(){return allocations.all();}
 @PostMapping("/allocations") public Map<String,Object> allocate(@RequestBody Map<String,Object> body,java.security.Principal principal){return allocations.create(body,principal.getName());}
}
