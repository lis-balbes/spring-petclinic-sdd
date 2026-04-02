# Execution Log: Vet Selection & Time Slot Booking

## Loop 1 — Implementation

### Step 1: Schema changes
Updated all three schema files (`db/h2/schema.sql`, `db/mysql/schema.sql`, `db/postgres/schema.sql`):
- Added `vet_id` (FK → vets) and `visit_time` (TIME) columns to the `visits` table.
- Added `UNIQUE(vet_id, visit_date, visit_time)` constraint.
- Used DB-specific syntax: H2 uses `ALTER TABLE ADD CONSTRAINT`, MySQL uses inline `UNIQUE`, PostgreSQL uses inline `UNIQUE`.

Updated all three `data.sql` files to include `vet_id` and `visit_time` for the 4 existing seed visits (assigned vets 1–4 with various times).

No surprises. Straightforward.

### Step 2: Visit entity
Added to `Visit.java`:
- `@ManyToOne @JoinColumn(name = "vet_id") @NotNull Vet vet`
- `@Column(name = "visit_time") @DateTimeFormat(pattern = "HH:mm") @NotNull LocalTime time`
- Getters and setters for both fields.

Required cross-package import of `org.springframework.samples.petclinic.vet.Vet` — expected per the spec's implementation notes.

### Step 3: VetFormatter
Created `vet/VetFormatter.java` following the `PetTypeFormatter` pattern:
- `print()`: returns `firstName + " " + lastName`.
- `parse()`: iterates `vetRepository.findAll()`, matches by full name string.

**Discovery: Vet.toString() needed.** The `selectField.html` Thymeleaf fragment renders `${item}` as option text, which calls `toString()`. The `Vet` class had no `toString()` override (unlike `PetType` which extends `NamedEntity` with a `name` field). Added `toString()` to `Vet.java` returning `firstName + " " + lastName`. This was not in the plan.

### Step 4: VisitRepository
Created `owner/VisitRepository.java` extending `Repository<Visit, Integer>`:
- `boolean existsByVetIdAndDateAndTime(Integer vetId, LocalDate date, LocalTime time)` — for double-booking check.
- `List<Visit> findByDateAndTime(LocalDate date, LocalTime time)` — for pre-filtering booked vets.

Spring Data derives both queries from the method names. Field navigation: `vet.id` for `VetId`, `date` and `time` map directly to Visit entity fields.

### Step 5: VisitController updates
Rewrote `VisitController.java`:
- Injected `VetRepository`, `VisitRepository`, `@Value startHour`, `@Value endHour`.
- `@ModelAttribute("vets")` with `@RequestParam(required = false) LocalDate date` and `@RequestParam(required = false) LocalTime time`:
  - When both non-null (POST re-render): query `findByDateAndTime`, collect booked vet IDs, filter them out.
  - When null (GET): return `vetRepository.findAll()`.
- `@ModelAttribute("timeSlots")`: generates hourly `LocalTime` values from `startHour` to `endHour` inclusive.
- Double-booking check in `processNewVisitForm`: calls `existsByVetIdAndDateAndTime`, rejects with field error on `vet` if conflict.
- Wrapped `this.owners.save(owner)` in a try/catch for `DataIntegrityViolationException` — per the plan's instruction to handle the concurrent race condition gracefully. On catch, re-renders the form with the same user-friendly error on the `vet` field.

### Step 6: Configuration
Added to `application.properties`:
```properties
petclinic.visit.start-hour=9
petclinic.visit.end-hour=16
```

### Step 7: Form template
Updated `createOrUpdateVisitForm.html`:
- Added vet dropdown: `~{fragments/selectField :: select ('Vet', 'vet', ${vets})}`.
- Added time dropdown: `~{fragments/selectField :: select ('Time', 'time', ${timeSlots})}`.

**Discovery: i18n keys needed.** The `I18nPropertiesSyncTest` validates that all locale `.properties` files have identical keys. Added `vet=Vet` and `time=Time` to `messages.properties` and all 8 locale variants (`messages_de`, `messages_en`, `messages_es`, `messages_fa`, `messages_ko`, `messages_pt`, `messages_ru`, `messages_tr`). This was not in the plan.

### Step 8: Owner details display
Updated `ownerDetails.html` visit history table:
- Added Time and Vet columns in header.
- Display with null-safe rendering: `visit.time != null ? format : ''` and `visit.vet != null ? name : ''`.

### Step 9: Tests
Updated `VisitControllerTests.java`:
- Added `@MockitoBean` for `VetRepository` and `VisitRepository`.
- Set up `testVet` in `@BeforeEach` with mock `findAll()` returning it.
- `initNewVisitForm`: added assertions for `model().attributeExists("vets")` and `"timeSlots"`.
- `processNewVisitFormSuccess`: added `vet` and `time` params.
- `processNewVisitFormDoubleBookingRejected`: new test — mocks `existsByVetIdAndDateAndTime` returning true, asserts field error on `vet`.

**Discovery: @WebMvcTest doesn't auto-detect VetFormatter.** First test run failed because `@WebMvcTest(VisitController.class)` doesn't include the `VetFormatter` component from the `vet` package. Fixed by adding `includeFilters = @ComponentScan.Filter(value = VetFormatter.class, type = FilterType.ASSIGNABLE_TYPE)` — matching the pattern used by `PetControllerTests` for `PetTypeFormatter`. This was not in the plan.

**Discovery: ClinicServiceTests.shouldAddNewVisitForPet broken.** This integration test creates a `Visit` with only `description` set, hitting `@NotNull` validation on `vet` and `time`. Fixed by adding `visit.setVet(vet)` and `visit.setTime(LocalTime.of(9, 0))` to the test. This was not in the plan.

### AC Verification (Loop 1)

**AC 1 — "Visit form shows a dropdown of available vets for the selected date/time."** **[PAUSE]**
- Read `VisitController.populateVets()`: when `date` and `time` params are non-null, queries `findByDateAndTime` and excludes booked vets. When null (initial GET), returns `vetRepository.findAll()`.
- Read `createOrUpdateVisitForm.html`: confirms `${vets}` rendered via `selectField` fragment.
- **Finding:** On initial page load, no date/time is selected — params are null — so all vets are shown, not "available vets for the selected date/time." AC overpromises. Pausing for spec update.

**AC 2 — "Visit form includes a time slot picker with hourly slots from 09:00 to 16:00. Slot range is configurable."** **[DONE]**
- Read `VisitController.populateTimeSlots()`: generates `LocalTime` values from `startHour` (9) to `endHour` (16) inclusive — 8 slots.
- Read `application.properties`: confirmed `petclinic.visit.start-hour=9` and `petclinic.visit.end-hour=16`.
- Read `createOrUpdateVisitForm.html`: confirms `${timeSlots}` rendered via `selectField` fragment.
- Changed `application.properties` to `start-hour=10`, ran app, verified dropdown starts at 10:00. Reverted.

**AC 3 — "A vet cannot be booked for the same date+time twice (server-side validation + database unique constraint)."** **[DONE]**
- Read `VisitController.processNewVisitForm()`: calls `existsByVetIdAndDateAndTime` before save, rejects with field error on `vet`. Catch block for `DataIntegrityViolationException` as safety net.
- Verified `UNIQUE(vet_id, visit_date, visit_time)` constraint present in all 3 schema files.
- Read `VisitControllerTests.processNewVisitFormDoubleBookingRejected`: mocks `existsByVetIdAndDateAndTime` returning true, asserts form re-renders with error.
- Ran tests: `processNewVisitFormDoubleBookingRejected` passes.

**AC 4 — "Vet and time are persisted with the visit."** **[DONE]**
- Read `Visit.java`: `@ManyToOne @JoinColumn(name = "vet_id") @NotNull Vet vet` and `@Column(name = "visit_time") @NotNull LocalTime time` present.
- Read `db/h2/schema.sql`: `visits` table has `vet_id INTEGER` and `visit_time TIME` columns with FK to vets.
- Verified all 3 `data.sql` files include `vet_id` and `visit_time` values for seed visits.
- Ran `ClinicServiceTests.shouldAddNewVisitForPet`: creates a Visit with vet and time, saves, reloads — both fields persisted. Passes.

**AC 5 — "Existing visit display shows the assigned vet and time."** **[DONE]**
- Read `ownerDetails.html`: visit history table has `<th>Time</th>` and `<th>Vet</th>` columns.
- Rendering uses null-safe checks: `visit.time != null` and `visit.vet != null`.
- Ran app, navigated to owner 6 (Jean Coleman) → pet Samantha → confirmed visit history table shows vet name and formatted time for seed visits.

**AC 6 — "All three DB schemas and seed data are updated."** **[DONE]**
- Diffed all 3 `schema.sql` files: each has `vet_id`, `visit_time`, and `UNIQUE` constraint with dialect-appropriate syntax.
- Diffed all 3 `data.sql` files: each seed visit now includes `vet_id` (1–4) and `visit_time` values.
- Ran `./mvnw test`: all 58 tests pass (includes H2 integration tests). MySQL and PostgreSQL integration tests skipped (no Docker).

### PAUSE: AC #1 Overpromises

**What was found:** The `@ModelAttribute("vets")` method correctly pre-filters on POST re-renders (when `date` and `time` request params are present). But on initial GET, no date/time is selected — both params are null — so all vets are returned. The AC text "available vets for the selected date/time" implies filtering always works, which is not what happens on first page load.

**Root cause:** Full real-time filtering when the user selects a date would require AJAX (JavaScript calling a REST endpoint to fetch available vets), which is out of scope for this server-rendered Thymeleaf application.

**Suggested reword:** "Visit form shows a dropdown of vets. On initial page load, all vets are shown. When the form is re-rendered after submission (e.g., validation error), only vets available for the selected date/time are shown. Double-booking check on submit is the safety net."

**Developer's decision:** Accepted the reword. Spec updated to v2.

---

## Between Loops

Updated spec to v2 with:
- AC #1 reworded to match actual behavior.
- New decision: "Vet dropdown shows all vets on initial page load..."
- Plan step 5 updated with `@RequestParam(required = false)` mechanism.
- Plan steps 10–12 added for Vet.toString(), i18n keys, and test fixes.

---

## Loop 2 — Re-verification

Re-read the updated spec. AC #1 was reworded — re-verified all ACs against the existing implementation. No code changes needed.

**AC 1 (reworded) — "Visit form shows a dropdown of vets. On initial page load, all vets are shown. When the form is re-rendered after submission, only vets available for the selected date/time are shown."** **[DONE]**
- Re-read `VisitController.populateVets()`: GET (null params) → `findAll()`. POST re-render (params present) → filters out booked vets. Matches reworded AC exactly.

**AC 2–6** — **[DONE]** — No changes since Loop 1. Re-confirmed tests pass (58 run, 0 failures, 2 skipped Docker tests).

All ACs **[DONE]**.

---

## Deviations from Plan

1. **Vet.toString() not in plan.** The `selectField.html` fragment renders options via `${item}` which calls `toString()`. Without it, the dropdown showed the default Object.toString() output. Added `toString()` to `Vet.java`.

2. **i18n keys not in plan.** The project has an `I18nPropertiesSyncTest` that validates all locale `.properties` files have identical keys. Adding `vet` and `time` to `messages.properties` without adding them to the 8 locale variants caused a test failure. Added keys to all locale files.

3. **@WebMvcTest filter for VetFormatter not in plan.** `@WebMvcTest` doesn't auto-detect `@Component` formatters from other packages. Had to add `includeFilters` for `VetFormatter` — same pattern as `PetControllerTests` uses for `PetTypeFormatter`.

4. **ClinicServiceTests fix not in plan.** The existing `shouldAddNewVisitForPet` integration test creates a Visit without vet/time, which now fails `@NotNull` validation. Had to set both fields in the test.

5. **AC #1 reworded.** The original AC overpromised — "available vets for the selected date/time" implies always-filtered, but on GET no date/time exists to filter by. Reworded to accurately describe behavior.

---

## Decisions Discovered During Execution

1. **Vet.toString() returns full name.** The selectField fragment uses `toString()` for option text. `Vet` extends `Person` which has no `toString()`. Chose `firstName + " " + lastName` — consistent with `VetFormatter.print()` and the `VetFormatter.parse()` lookup.

2. **VetFormatter placed in `vet` package.** Following the plan and the `PetTypeFormatter` pattern (formatter lives next to its repository), placed in `org.springframework.samples.petclinic.vet`.

3. **Seed data assigns vets 1–4 to the 4 existing visits.** Chose distinct vets and spread across different times to avoid hitting the UNIQUE constraint. Vet IDs 1 (James Carter), 2 (Helen Leary), 3 (Linda Douglas), 4 (Rafael Ortega).

4. **ClinicServiceTests uses vet ID 1 and time 09:00.** Picked the first vet and first time slot — simple, deterministic, and avoids conflicts with seed data (seed visits are on different dates).

5. **Null-safe rendering in ownerDetails.html.** Used conditional Thymeleaf expressions (`visit.vet != null ? ... : ''`) because existing visits in the DB might have been created before the feature, though in practice the seed data was updated. Defensive approach.
