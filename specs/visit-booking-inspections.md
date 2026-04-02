# Spec Inspection Results: `specs/spec-v0.md`

## Finding 1: Missing `VetFormatter` — form binding will fail at runtime

**Location:** Section 5 (Form template update), line 45

**Full explanation:**
The project uses a `Formatter<T>` pattern to convert between string form values and JPA entities on select fields. The existing `PetType` select works because `PetTypeFormatter` (`src/main/java/.../owner/PetTypeFormatter.java`) is a `@Component` implementing `Formatter<PetType>` — it looks up the `PetType` by name from the database on form submission.

The spec proposes a `<select>` for `Vet` but never mentions creating an equivalent `VetFormatter` (or any other converter). Without one, when the form POSTs, Spring MVC receives a string value for the `vet` field and has no way to convert it into a `Vet` entity. This will throw a type conversion error at runtime.

The spec also doesn't mention where this formatter would live. `PetTypeFormatter` is in the `owner` package alongside `PetTypeRepository`. A `VetFormatter` would need `VetRepository` (from the `vet` package), raising a question about package placement.

**What IDE shows:**

Warning: Incomplete plan. This project uses `Formatter<T>` to bind form selects to entities (see `PetTypeFormatter`). Plan adds a `<select>` for Vet but no `VetFormatter`. Form POST will fail.

Quick fix: Show "Add VetFormatter step", should work like full-line completion

---

## Finding 2: No database unique constraint for double-booking — race condition possible

**Location:** Plan Section 3, line 34

**Full explanation:**
The spec relies solely on an application-level check-then-act: query if a conflicting visit exists, then save if not. Without a `UNIQUE(vet_id, visit_date, visit_time)` constraint in the schema, two concurrent requests can both pass the check and both insert, creating a double-booking (classic TOCTOU race condition).

The schema change section (Section 1) only mentions adding columns and a FK, not a unique constraint. All three schema files (`h2/schema.sql`, `mysql/schema.sql`, `postgres/schema.sql`) would need it.

**What IDE shows:**

Warning:
Possible race condition. App-level check-then-act without a DB constraint.

Quick-fix: Show three options and a text input
1. Add UNIQUE constraint to schema
2. App-level check is sufficient
3. Decide yourself
---

## Finding 3: AC #1 says "available vets" but the plan loads all vets

**Location:** Acceptance Criteria #1 (line 9)

**Full explanation:**
"Available vets for the selected date/time" implies filtering out vets who already have a booking at that time. The spec actually has the building blocks to do this: `VisitRepository.existsByVetIdAndDateAndTime` (line 34) can check whether a vet is booked at a given date+time. On a POST request, the date and time are known from the submitted form, so the `@ModelAttribute("vets")` method could use `VisitRepository` to filter out already-booked vets before populating the dropdown.

But the spec doesn't wire it that way. It uses `findAll()` for the dropdown (line 39) and only uses the double-booking query for validation-on-submit in `processNewVisitForm` (line 40). These are two different UX patterns:

1. Pre-filter: dropdown only shows unbooked vets (wire `VisitRepository` into the `@ModelAttribute` method)
2. Validate on submit: dropdown shows all vets, reject conflicts on POST

The spec implements pattern 2 but the AC reads like pattern 1. Either the AC should be reworded to match the validate-on-submit approach, or the plan should wire the existing `VisitRepository` query into the `@ModelAttribute("vets")` method for pre-filtering (at least on POST re-renders, where date+time are known).
The developer should consciously choose, and the AC should match the chosen approach.

**What IDE shows:**

Warning:
AC/Plan mismatch: AC says "available vets" but plan loads all vets via findAll()

Quick-fix: selection
1. Filter the dropdown — exclude booked vets
2. Show all, validate on submit — catch conflicts at booking time
3. [Text field]
---

## Finding 4: AC #2 uses vague wording — time slot granularity is undefined

**Location:** Acceptance Criteria #2, line 10

**Full explanation:**
The "e.g." makes this acceptance criterion untestable. It leaves open multiple questions that an implementer must silently decide:

- Is the granularity hourly, half-hourly, or free-form `<input type="time">`?
- Are the bounds 09:00–16:00 firm requirements or just an example?
- Does 16:00 mean the last bookable slot is 16:00, or is it the end-of-day boundary (last slot at 15:00)?
- Should the slots be hardcoded or configurable?

The plan itself (Section 5, line 46) mirrors the ambiguity: "Add a `<select>` or `<input type="time">`" — the spec author wasn't sure either. These are two very different UX choices: a constrained list of slots vs. free-form time entry. The double-booking validation logic, the `visit_time` column type, and the seed data values all depend on which choice is made.

**What IDE shows:**

Warning:
Ambiguous AC

Quick-fix: Show text input field
