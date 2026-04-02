# SDD Demo Content — Spring PetClinic

Demo scenario: **`specs/jvm-scenario.md`**

## Files

**Shared:** `specs/prd.md` — the PRD shown in YouTrack.

**Task 1 (visit-booking):**
- `specs/visit-booking-initial-prompt.md` → prompt typed by developer
- `specs/visit-booking-v0.md` → generated spec (Beat 1)
- `specs/visit-booking-inspections.md` → 4 IDE findings with explanations (Beat 2)
- `specs/visit-booking-v1.md` → spec after inspections + regeneration (Beat 2 → 3)
- `specs/visit-booking-beat-3-execution.md` → execution log, PAUSE on AC #1 (Beat 3)
- `specs/visit-booking-v2.md` → final spec
- `specs/visit-booking-code-review-moment.md` → what developer reviews in Beat 4

**Task 2 (vet-schedules):**
- `specs/vet-schedules.md` → spec, paused at step 3a controller (Beat 5)

**Smaller details**
- Are in the `specs/jvm-scenario.md`

## Chain
Beats 1, 2, 3, 4, 6:

```
v0 → inspections → v1 → execution → v2 → code review → merge
```

Beat 5 is a separate task running in parallel (different entity, zero file conflicts).
