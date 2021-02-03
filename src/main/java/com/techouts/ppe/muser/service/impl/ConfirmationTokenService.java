package com.techouts.ppe.muser.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techouts.ppe.muser.model.ConfirmationToken;
import com.techouts.ppe.muser.repositories.ConfirmationTokenRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
//@AllArgsConstructor
public class ConfirmationTokenService {

	@Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

	public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    public int setConfirmedAt(String token) {
        return confirmationTokenRepository.updateConfirmedAt(
                token, LocalDateTime.now());
    }
}
