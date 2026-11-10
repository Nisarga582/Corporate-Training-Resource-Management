CREATE TABLE IF NOT EXISTS courses (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, course_name VARCHAR(150) NOT NULL, duration_weeks INT NOT NULL,
 technology_requirements TEXT, description TEXT, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS batches (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, batch_name VARCHAR(150) NOT NULL, course_id BIGINT NOT NULL,
 start_date DATE NOT NULL, end_date DATE NOT NULL, start_time TIME NOT NULL, end_time TIME NOT NULL,
 student_count INT NOT NULL, location VARCHAR(150), status VARCHAR(30) NOT NULL DEFAULT 'PLANNED',
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 CONSTRAINT fk_batch_course FOREIGN KEY(course_id) REFERENCES courses(id)
);
CREATE TABLE IF NOT EXISTS trainers (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, trainer_name VARCHAR(150) NOT NULL, email VARCHAR(180) NOT NULL UNIQUE,
 skills TEXT, availability_status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS classrooms (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, classroom_name VARCHAR(120) NOT NULL, capacity INT NOT NULL,
 location VARCHAR(150), has_projector BOOLEAN DEFAULT FALSE, has_internet BOOLEAN DEFAULT FALSE,
 status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE', created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS computer_labs (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, lab_name VARCHAR(120) NOT NULL, system_count INT NOT NULL,
 capacity INT NOT NULL, specifications TEXT, location VARCHAR(150), has_projector BOOLEAN DEFAULT FALSE,
 has_internet BOOLEAN DEFAULT FALSE, status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS software (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, software_name VARCHAR(150) NOT NULL, version VARCHAR(80) NOT NULL,
 configuration TEXT, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS course_software (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, course_id BIGINT NOT NULL, software_id BIGINT NOT NULL, UNIQUE KEY uq_course_software(course_id,software_id),
 FOREIGN KEY(course_id) REFERENCES courses(id) ON DELETE CASCADE, FOREIGN KEY(software_id) REFERENCES software(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS lab_software (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, lab_id BIGINT NOT NULL, software_id BIGINT NOT NULL, installed_version VARCHAR(80),
 UNIQUE KEY uq_lab_software(lab_id,software_id), FOREIGN KEY(lab_id) REFERENCES computer_labs(id) ON DELETE CASCADE,
 FOREIGN KEY(software_id) REFERENCES software(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS batch_requirements (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, batch_id BIGINT NOT NULL, resource_type VARCHAR(20) NOT NULL,
 systems_needed INT DEFAULT 0, minimum_capacity INT NOT NULL, projector_required BOOLEAN DEFAULT FALSE,
 internet_required BOOLEAN DEFAULT FALSE, software_ids VARCHAR(500),
 FOREIGN KEY(batch_id) REFERENCES batches(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS trainer_assignments (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, batch_id BIGINT NOT NULL, trainer_id BIGINT NOT NULL,
 start_date DATE NOT NULL, start_time TIME NOT NULL, end_time TIME NOT NULL,
 FOREIGN KEY(batch_id) REFERENCES batches(id) ON DELETE CASCADE, FOREIGN KEY(trainer_id) REFERENCES trainers(id)
);
CREATE TABLE IF NOT EXISTS allocations (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, batch_id BIGINT NOT NULL, resource_type VARCHAR(20) NOT NULL,
 resource_id BIGINT NOT NULL, session_date DATE NOT NULL, start_time TIME NOT NULL, end_time TIME NOT NULL,
 status VARCHAR(30) NOT NULL DEFAULT 'CONFIRMED', created_by VARCHAR(80),
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 FOREIGN KEY(batch_id) REFERENCES batches(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS maintenance (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, resource_type VARCHAR(20) NOT NULL, resource_id BIGINT NOT NULL,
 start_date DATE NOT NULL, end_date DATE NOT NULL, reason TEXT NOT NULL, status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS conflict_records (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, batch_id BIGINT, resource_type VARCHAR(20), resource_id BIGINT,
 reason TEXT NOT NULL, status VARCHAR(30) NOT NULL DEFAULT 'UNRESOLVED', detected_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY(batch_id) REFERENCES batches(id) ON DELETE SET NULL
);
CREATE TABLE IF NOT EXISTS audit_history (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, entity_type VARCHAR(50) NOT NULL, entity_id BIGINT,
 action VARCHAR(40) NOT NULL, details TEXT, performed_by VARCHAR(80), performed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
INSERT IGNORE INTO courses(id,course_name,duration_weeks,technology_requirements,description) VALUES
(1,'Java Full Stack Development',12,'Java, Spring Boot, MySQL, HTML, CSS, JavaScript','Sample technical training course'),
(2,'Data Analytics',8,'Python, SQL, visualization tools','Sample analytics course');
INSERT IGNORE INTO batches(id,batch_name,course_id,start_date,end_date,start_time,end_time,student_count,location,status) VALUES
(1,'JFS-NOV-01',1,DATE_ADD(CURDATE(),INTERVAL 3 DAY),DATE_ADD(CURDATE(),INTERVAL 30 DAY),'09:00:00','13:00:00',25,'Block A','PLANNED'),
(2,'DA-NOV-02',2,DATE_ADD(CURDATE(),INTERVAL 5 DAY),DATE_ADD(CURDATE(),INTERVAL 25 DAY),'14:00:00','17:00:00',18,'Block B','PLANNED');
INSERT IGNORE INTO trainers(id,trainer_name,email,skills,availability_status) VALUES
(1,'Asha Rao','asha@example.com','Java, Spring Boot, SQL','AVAILABLE'),
(2,'Rahul Nair','rahul@example.com','Data Analytics, SQL','AVAILABLE');
INSERT IGNORE INTO classrooms(id,classroom_name,capacity,location,has_projector,has_internet,status) VALUES
(1,'CR-101',40,'Block A',TRUE,TRUE,'AVAILABLE'),(2,'CR-102',20,'Block B',TRUE,TRUE,'AVAILABLE');
INSERT IGNORE INTO computer_labs(id,lab_name,system_count,capacity,specifications,location,has_projector,has_internet,status) VALUES
(1,'LAB-JAVA-01',30,30,'Intel i5, 16GB RAM, SSD','Block A',TRUE,TRUE,'AVAILABLE'),
(2,'LAB-DATA-01',20,20,'Intel i5, 8GB RAM, SSD','Block B',TRUE,TRUE,'AVAILABLE');
INSERT IGNORE INTO software(id,software_name,version,configuration) VALUES
(1,'JDK','21','Temurin/OpenJDK'),(2,'MySQL','8.0','Community Server'),(3,'VS Code','Latest','Java extensions');
INSERT IGNORE INTO course_software(course_id,software_id) VALUES(1,1),(1,2),(1,3);
INSERT IGNORE INTO lab_software(lab_id,software_id,installed_version) VALUES(1,1,'21'),(1,2,'8.0'),(1,3,'Latest');
INSERT IGNORE INTO batch_requirements(id,batch_id,resource_type,systems_needed,minimum_capacity,projector_required,internet_required,software_ids) VALUES
(1,1,'LAB',25,25,TRUE,TRUE,'1,2,3'),(2,2,'LAB',18,18,TRUE,TRUE,'');
