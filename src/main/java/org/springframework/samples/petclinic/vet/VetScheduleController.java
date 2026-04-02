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
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

/**
 * Controller for managing vet weekly schedules.
 */
@Controller
class VetScheduleController {

	private final VetRepository vetRepository;

	private final VetScheduleRepository vetScheduleRepository;

	VetScheduleController(VetRepository vetRepository, VetScheduleRepository vetScheduleRepository) {
		this.vetRepository = vetRepository;
		this.vetScheduleRepository = vetScheduleRepository;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("vet")
	public Vet findVet(@PathVariable(name = "vetId", required = false) Integer vetId) {
		if (vetId == null) {
			return null;
		}
		return this.vetRepository.findById(vetId)
			.orElseThrow(() -> new IllegalArgumentException("Vet not found with id: " + vetId));
	}

	@ModelAttribute("daysOfWeek")
	public DayOfWeek[] populateDaysOfWeek() {
		return DayOfWeek.values();
	}

	@GetMapping("/vets/schedules")
	public String listVetsForSchedules(Model model) {
		Collection<Vet> vets = this.vetRepository.findAll();
		model.addAttribute("vets", vets);
		return "vets/scheduleList";
	}

	@GetMapping("/vets/{vetId}/schedules")
	public String showVetSchedule(@PathVariable("vetId") int vetId, Model model) {
		List<VetSchedule> schedules = this.vetScheduleRepository.findByVetIdOrderByDayOfWeek(vetId);
		model.addAttribute("schedules", schedules);
		model.addAttribute("vetSchedule", new VetSchedule());
		return "vets/vetSchedule";
	}

	@PostMapping("/vets/{vetId}/schedules")
	public String processNewScheduleEntry(@ModelAttribute("vet") Vet vet, @Valid VetSchedule vetSchedule,
			BindingResult result, Model model, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			List<VetSchedule> schedules = this.vetScheduleRepository.findByVetIdOrderByDayOfWeek(vet.getId());
			model.addAttribute("schedules", schedules);
			return "vets/vetSchedule";
		}

		vetSchedule.setVet(vet);
		this.vetScheduleRepository.save(vetSchedule);
		redirectAttributes.addFlashAttribute("message", "Schedule entry added.");
		return "redirect:/vets/" + vet.getId() + "/schedules";
	}

	@GetMapping("/vets/{vetId}/schedules/{scheduleId}/edit")
	public String initEditScheduleEntry(@PathVariable("vetId") int vetId, @PathVariable("scheduleId") int scheduleId,
			Model model) {
		VetSchedule schedule = this.vetScheduleRepository.findById(scheduleId)
			.orElseThrow(() -> new IllegalArgumentException("Schedule entry not found with id: " + scheduleId));
		model.addAttribute("vetSchedule", schedule);
		return "vets/editScheduleEntry";
	}

	@PostMapping("/vets/{vetId}/schedules/{scheduleId}/edit")
	public String processEditScheduleEntry(@ModelAttribute("vet") Vet vet, @PathVariable("scheduleId") int scheduleId,
			@Valid VetSchedule vetSchedule, BindingResult result, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			return "vets/editScheduleEntry";
		}

		vetSchedule.setId(scheduleId);
		vetSchedule.setVet(vet);
		this.vetScheduleRepository.save(vetSchedule);
		redirectAttributes.addFlashAttribute("message", "Schedule entry updated.");
		return "redirect:/vets/" + vet.getId() + "/schedules";
	}

	@PostMapping("/vets/{vetId}/schedules/{scheduleId}/delete")
	public String processDeleteScheduleEntry(@ModelAttribute("vet") Vet vet, @PathVariable("scheduleId") int scheduleId,
			RedirectAttributes redirectAttributes) {
		this.vetScheduleRepository.deleteById(scheduleId);
		redirectAttributes.addFlashAttribute("message", "Schedule entry deleted.");
		return "redirect:/vets/" + vet.getId() + "/schedules";
	}

}
