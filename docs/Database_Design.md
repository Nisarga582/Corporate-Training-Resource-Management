# Database Design

The relational design separates training data, infrastructure, requirements and operational history.

| Table | Purpose | Key relationships |
|---|---|---|
| courses | Course catalogue | Parent of batches |
| batches | Training batch and schedule | FK to courses |
| trainers | Trainer profile and availability | Parent of trainer_assignments |
| trainer_assignments | Trainer-to-batch schedule | FK to batches and trainers |
| classrooms | Room capacity and facilities | Referenced by allocations using resource_type/resource_id |
| computer_labs | Lab systems and facilities | Referenced by allocations using resource_type/resource_id |
| software | Software catalogue | Linked to courses and labs |
| course_software | Required software for courses | FK to courses and software |
| lab_software | Installed software in labs | FK to computer_labs and software |
| batch_requirements | Infrastructure requirements per batch | FK to batches |
| allocations | Confirmed resource bookings | FK to batches; resource type/id identifies room or lab |
| maintenance | Unavailability window | resource type/id identifies room or lab |
| conflict_records | Detected conflict reasons | Optional FK to batches |
| audit_history | History of create/update/delete/allocation actions | Stores entity type and entity id |

## Keys and constraints
- Each main table uses an auto-increment BIGINT primary key.
- Batch.course_id references courses.id.
- Trainer assignments reference batches and trainers.
- Course/lab software mapping tables use foreign keys and unique mapping constraints.
- Batch requirements reference batches.
- Allocations and conflict records reference batches.
- Required names, dates and capacities are non-null where appropriate.

## ER diagram
See `ER_Diagram.mmd` for the Mermaid diagram.

## Notes
Classroom/lab allocations and maintenance use a polymorphic pair (`resource_type`, `resource_id`). This is simple for the prototype but cannot have a conventional foreign key to two different resource tables. A stricter production schema could use a unified `resources` supertype table.
