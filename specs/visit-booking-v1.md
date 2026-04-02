# Spec: Vet Selection & Time Slot Booking for Visits

## Goal

Add vet assignment and time slot selection to the visit creation flow. When booking a visit, users pick a vet and a time slot for the chosen date. The system prevents double-booking (same vet, same date+time).

## Acceptance Criteria

1. Visit form shows a dropdown of available vets for the selected date/time.
2. Visit form includes a time slot picker with hourly slots from 09:00 to 16:00 (last bookable slot). Slot range is configurable via `application.properties`.
3. A vet cannot be booked for the same date+time twice (server-side validation + database unique constraint).
4. Vet and time are persisted with the visit.
5. Existing visit display (owner details page, visit history table) shows the assigned vet and time.
6. All three DB schemas (H2, MySQL, PostgreSQL) and seed data are updated.

## Plan

### 1. Schema changes
Add `vet_id` (FK → vets) and `visit_time` (TIME) columns to the `visits` table in:
- `src/main/resources/db/h2/schema.sql`
- `src/main/resources/db/mysql/schema.sql`
- `src/main/resources/db/postgres/schema.sql`

Add a `UNIQUE(vet_id, visit_date, visit_time)` constraint to the `visits` table in all three schema files to enforce double-booking prevention at the database level.

Update seed data in the corresponding `data.sql` files to include vet and time for existing visits.

### 2. Visit entity
**`src/main/java/org/springframework/samples/petclinic/owner/Visit.java`**
- Add `@ManyToOne` field `vet` (references `Vet` entity) with `@JoinColumn(name = "vet_id")`.
- Add `LocalTime time` field with `@Column(name = "visit_time")` and `@DateTimeFormat(pattern = "HH:mm")`.
- Add `@NotNull` validation on both new fields.

### 3. VetFormatter
Create **`src/main/java/org/springframework/samples/petclinic/vet/VetFormatter.java`** — a `@Component` implementing `Formatter<Vet>`, following the `PetTypeFormatter` pattern.
- `print`: return vet's full name (`firstName + " " + lastName`).
- `parse`: look up the vet by name from `VetRepository.findAll()`.

This is required for Spring MVC to convert form select values into `Vet` entity instances on POST.

### 4. VisitRepository
Create `src/main/java/org/springframework/samples/petclinic/owner/VisitRepository.java` (Spring Data interface extending `Repository<Visit, Integer>`).
- Add method: `boolean existsByVetIdAndDateAndTime(Integer vetId, LocalDate date, LocalTime time)` — used for double-booking check.
- Add method: `List<Visit> findByDateAndTime(LocalDate date, LocalTime time)` — used to find booked vets for a given slot, so the dropdown can filter them out.

### 5. VisitController updates
**`src/main/java/org/springframework/samples/petclinic/owner/VisitController.java`**
- Inject `VetRepository` and new `VisitRepository`.
- Add `@ModelAttribute("vets")` method that populates available vets. On GET (no date/time selected yet), return all vets via `vetRepository.findAll()`. On POST (date and time submitted with the form), use `VisitRepository.findByDateAndTime` to identify already-booked vets and exclude them from the list.
- Add `@ModelAttribute("timeSlots")` method that exposes the list of bookable time slots. Read slot boundaries from configuration (`petclinic.visit.start-hour` and `petclinic.visit.end-hour` in `application.properties`, defaulting to 9 and 16). Generate hourly `LocalTime` values from start to end inclusive.
- In `processNewVisitForm`: before saving, call the double-booking check. If conflict, add a `BindingResult` error on the `vet` field and return the form.

### 6. Configuration
Add to `src/main/resources/application.properties`:
```properties
petclinic.visit.start-hour=9
petclinic.visit.end-hour=16
```

Use `@Value` injection in `VisitController` to read these values.

### 7. Form template update
**`src/main/resources/templates/pets/createOrUpdateVisitForm.html`**
- Add a `<select>` dropdown for vet (bound to `visit.vet`, populated from `${vets}`), displaying vet name.
- Add a `<select>` dropdown for time slot (bound to `visit.time`, populated from `${timeSlots}`), displaying formatted hour values.
- Show validation errors for the new fields.

### 8. Owner details / visit history display
**`src/main/resources/templates/owners/ownerDetails.html`**
- Add Vet and Time columns to the visits table that shows per-pet visit history.

### 9. Tests
- Update or add controller tests in `src/test/java/org/springframework/samples/petclinic/owner/VisitControllerTests.java` to cover:
    - Vet list populated in model.
    - Successful booking with vet+time.
    - Double-booking rejected.

## Implementation Notes

**Current Visit entity** (`Visit.java`): Only has `date` (LocalDate) and `description` (String). Extends `BaseEntity`. No relationship to Vet.

**Current visits table**: Columns are `id`, `pet_id`, `visit_date`, `description`. No vet or time columns.

**Vet entity** (`vet/Vet.java`): Extends `Person` (firstName, lastName). Has `@ManyToMany` specialties. Lives in `org.springframework.samples.petclinic.vet` package — the Visit entity in `owner` package will need a cross-package import.

**VetRepository** (`vet/VetRepository.java`): Has `findAll()` (cached). Returns `Collection<Vet>` or `Page<Vet>`.

**No existing VisitRepository**: Visits are currently persisted entirely through cascade (`Owner` → `Pet` → `Visit` via `CascadeType.ALL`). A new repository is needed for the double-booking query and availability filtering.

**VisitController** (`owner/VisitController.java`): Uses `@ModelAttribute("visit")` in `loadPetWithVisit` to create a new Visit and attach it to the Pet. `processNewVisitForm` saves via `ownerRepository.save(owner)` (cascade). Only injects `OwnerRepository` today.

**Form template** (`pets/createOrUpdateVisitForm.html`): Simple Thymeleaf form with date + description fields and a previous-visits table.

**PetTypeFormatter pattern** (`owner/PetTypeFormatter.java`): The project uses `Formatter<T>` components to convert between form string values and JPA entities. `VetFormatter` follows this same pattern, placed in the `vet` package next to `VetRepository`.

## Decisions

### Double-booking uses both app-level check and DB constraint
The app-level `existsByVetIdAndDateAndTime` check provides a user-friendly validation error. The `UNIQUE` constraint is a safety net for concurrent requests. A race condition that passes the app check but hits the DB constraint will throw `DataIntegrityViolationException` — the controller should handle this gracefully rather than surfacing a 500.

## Other

### Out of scope

- **Vet specialties matching**: No filtering of vets by pet type or specialty. All vets shown regardless.
- **Dynamic availability (AJAX)**: Vet dropdown is static on page load; no dynamic refresh when date changes. Could be a follow-up.
- **Multi-slot / duration-based booking**: Visits occupy exactly one time slot. No concept of appointment duration.
- **Vet calendar view**: No dedicated UI for vets to see their schedule.
- **VetRepository cache invalidation**: `findAll()` is `@Cacheable("vets")`. Adding a vet won't auto-refresh the dropdown — existing behavior, not introduced by this change.
