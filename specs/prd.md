# Visit Scheduling
## Background
Right now visits in Pet Clinic are just a date + description attached to a pet. There's no concept of appointment time, no vet assignment, and no way to manage visit lifecycle. Owners walk in, the receptionist manually checks who's free, and visits get logged after the fact.
We want to turn this into a proper scheduling system — owners book a specific vet at a specific time, the system prevents conflicts, and everyone can see what's coming up.

## Requirements

### Booking
When creating a visit, the owner selects a veterinarian and a time slot (date + hour).
The system shows which vets are available for the chosen date. A vet is available if they have open slots in their schedule and no existing booking at that time.
Each visit has a type: checkup, vaccination, surgery, or emergency. Default is checkup.
Visits are 1 hour long. No variable duration for now.

### Vet availability
Each vet has a weekly schedule — which days they work and their working hours (e.g. Mon–Fri, 9:00–17:00).
Schedules are configured by clinic staff.
Vets without a configured schedule are not shown as available for booking.

### Double-booking prevention
A vet cannot have two visits at the same time slot.
If a conflict is detected at booking time, the owner sees an error and can pick a different slot.

### Visit lifecycle
Visit statuses: scheduled → completed / cancelled.
Owners can cancel upcoming visits (status = scheduled, date in the future).
Clinic staff can cancel any visit.
Completed visits can have clinical notes added by the treating vet.

### Notifications
Owner receives an email when a visit is confirmed (booked) or cancelled.
Nice to have, not a blocker for V1.

### Staff view
Clinic staff can see all upcoming visits across all vets, grouped by date.
They can filter by vet, date range, and status.

## Out of scope for V1
Online payments
Recurring visits
Multi-vet visits
SMS notifications
Waitlist
