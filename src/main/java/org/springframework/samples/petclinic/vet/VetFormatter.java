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

import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Collection;
import java.util.Locale;

/**
 * Instructs Spring MVC on how to parse and print elements of type 'Vet'. Follows the same
 * pattern as {@link org.springframework.samples.petclinic.owner.PetTypeFormatter}.
 */
@Component
public class VetFormatter implements Formatter<Vet> {

	private final VetRepository vets;

	public VetFormatter(VetRepository vets) {
		this.vets = vets;
	}

	@Override
	public String print(Vet vet, Locale locale) {
		return vet.getFirstName() + " " + vet.getLastName();
	}

	@Override
	public Vet parse(String text, Locale locale) throws ParseException {
		Collection<Vet> allVets = this.vets.findAll();
		for (Vet vet : allVets) {
			String fullName = vet.getFirstName() + " " + vet.getLastName();
			if (fullName.equals(text)) {
				return vet;
			}
		}
		throw new ParseException("vet not found: " + text, 0);
	}

}
