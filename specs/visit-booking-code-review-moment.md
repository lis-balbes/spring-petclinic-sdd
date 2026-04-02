# Code Review Moment: VisitController.java

## The code

```java
private final int startHour;                          // line 61
private final int endHour;                            // line 63

// ... constructor stores them ...

@ModelAttribute("timeSlots")                          // line 121
public List<LocalTime> populateTimeSlots() {
    List<LocalTime> slots = new ArrayList<>();
    for (int hour = startHour; hour <= endHour; hour++) {
        slots.add(LocalTime.of(hour, 0));
    }
    return slots;
}
```

The time slots are derived from config properties that are fixed at startup. This method rebuilds the same identical list on every request.

## Review comment

These time slots never change at runtime — can we build the list once in the constructor instead of on every request?

## The fix

```java
private final List<LocalTime> timeSlots;

public VisitController(...,
        @Value("${petclinic.visit.start-hour:9}") int startHour,
        @Value("${petclinic.visit.end-hour:16}") int endHour) {
    // ...
    List<LocalTime> slots = new ArrayList<>();
    for (int hour = startHour; hour <= endHour; hour++) {
        slots.add(LocalTime.of(hour, 0));
    }
    this.timeSlots = List.copyOf(slots);
}

@ModelAttribute("timeSlots")
public List<LocalTime> populateTimeSlots() {
    return this.timeSlots;
}
```

Replaces two `int` fields with one immutable `List<LocalTime>`. The `@ModelAttribute` method becomes a one-liner.
