# Test Cases

Run these tests after configuring MySQL and starting the application.

| ID | Scenario | Test data/action | Expected result |
|---|---|---|---|
| TC-01 | Login success | admin / Admin@123 | Dashboard opens |
| TC-02 | Login failure | invalid password | Login page shows error |
| TC-03 | Create course | Add course name, duration and requirements | Record appears in Course Management |
| TC-04 | Create batch | Use an existing course ID and valid dates | Batch is saved |
| TC-05 | Create trainer | Add unique email | Trainer appears in list |
| TC-06 | Room capacity conflict | Batch student count exceeds room capacity | Allocation rejected with capacity reason |
| TC-07 | Lab system shortage | Required systems exceed lab system count | Allocation rejected |
| TC-08 | Resource overlap | Create two same-resource allocations with overlapping time/date | Second allocation rejected |
| TC-09 | Software mismatch | Course has software mapping absent from selected lab | Lab allocation rejected |
| TC-10 | Maintenance conflict | Schedule maintenance for resource/date, then allocate it | Allocation rejected |
| TC-11 | Trainer overlap | Assign same trainer to overlapping time/date | Second assignment rejected |
| TC-12 | Successful allocation | Use available resource meeting requirements | Allocation confirmed and audit row created |
| TC-13 | Audit | Create/update/delete a module record | Audit history contains an event |
| TC-14 | Role access | Sign in as trainer and attempt write API | Write operation denied |

## API sample: allocation
POST `/api/allocations`
```json
{
  "batch_id": 1,
  "resource_type": "LAB",
  "resource_id": 1,
  "session_date": "2030-11-12",
  "start_time": "09:00",
  "end_time": "12:00",
  "trainer_id": 1
}
```
Use a date consistent with your test batch and seeded data. The sample payload illustrates the fields, not a guaranteed conflict-free date.
