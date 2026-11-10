# Requirement Analysis

## Problem statement
Organizations running technical training programs need classrooms, computer labs, trainers, software installations, systems, projectors, network facilities and related resources. Simultaneous batches make manual allocation error-prone. The system must centrally maintain training and infrastructure information, define batch requirements in advance, match requirements to available resources, detect conflicts and provide monitoring to training managers.

## Functional requirements (from assigned statement)
1. Course Management: maintain courses, duration, technology requirements and training specifications.
2. Batch Management: maintain schedules, student count, course, location and session dates.
3. Trainer Management: maintain profiles, skills, availability and assigned batches.
4. Classroom Management: maintain capacity, location, equipment and availability.
5. Computer Lab Management: maintain system counts, specifications and availability.
6. Software Requirement Management: maintain software/framework versions and configurations and verify lab environments.
7. Resource Requirement Management: define systems, projector, internet, software and classroom capacity for each batch.
8. Resource Allocation Management: allocate classrooms, labs, trainers and supporting resources; record dates, resource and batch.
9. Conflict Detection Engine: check overlapping schedules, room/lab allocations, trainer assignments, capacity and software requirements; explain conflicts before confirmation.
10. Infrastructure Dashboard: display utilization, upcoming requirements, allocation conflicts and resource availability.
11. Maintenance & Unavailability Management: mark resources unavailable and flag affected allocations.
12. Audit History: record allocations, changes, conflicts and maintenance activities.

## User roles in this implementation
- Administrator: full operational access.
- Training Manager: manages courses, batches, resources, requirements and allocations.
- Trainer: read access to module records in this starter build.

## Non-functional requirements
- Security: authenticated access and role-based authorization.
- Usability: responsive dashboard, navigation, forms and understandable conflict feedback.
- Reliability: validation before confirming allocation.
- Maintainability: separate controllers, services, configuration and database schema.
- Data integrity: primary keys, foreign keys, unique constraints and required fields.

## Core allocation rules
- No overlapping confirmed allocation for the same resource and date.
- Room/lab capacity must satisfy batch size.
- Lab system count must satisfy the batch requirement.
- Course-required software must be mapped to the selected lab.
- Projector and internet facilities must meet requirements.
- A resource under scheduled maintenance is not allocatable.
- Optional trainer assignment must not overlap another assignment and trainer must be available.
