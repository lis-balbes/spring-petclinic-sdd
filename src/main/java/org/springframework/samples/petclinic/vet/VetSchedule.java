/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.vet;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Simple JavaBean domain object representing a vet's weekly schedule entry.
 */
@Entity
@Table(name = "vet_schedules")
public class VetSchedule extends BaseEntity {

	@ManyToOne
	@JoinColumn(name = "vet_id")
	@NotNull
	private Vet vet;

	@Column(name = "day_of_week")
	@Min(1)
	@Max(7)
	private int dayOfWeek;

	@NotNull
	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime startTime;

	@NotNull
	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime endTime;

	public Vet getVet() {
		return vet;
	}

	public void setVet(Vet vet) {
		this.vet = vet;
	}

	public int getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public DayOfWeek getDayOfWeekEnum() {
		return DayOfWeek.of(this.dayOfWeek);
	}

	public String getDayOfWeekName() {
		return DayOfWeek.of(this.dayOfWeek).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

}
