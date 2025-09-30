package com.syos.application.service;

import com.syos.application.dto.CustomerRegisterRequestDTO;
import com.syos.domain.model.User;

public abstract class RegistrationService<T extends User> {

	public final T register(CustomerRegisterRequestDTO customerRegisterRequestDTO) {
		validate(customerRegisterRequestDTO);
		checkUnique(customerRegisterRequestDTO);
		@SuppressWarnings("unchecked")
		T user = (T) createUser(customerRegisterRequestDTO);
		save(user);
		postRegister(user);
		return user;
	}

	protected abstract void validate(CustomerRegisterRequestDTO customerRegisterRequestDTO);

	protected abstract void checkUnique(CustomerRegisterRequestDTO customerRegisterRequestDTO);

	protected abstract User createUser(CustomerRegisterRequestDTO customerRegisterRequestDTO);

	protected abstract void save(T user);

	protected void postRegister(T user) {
	}

}
