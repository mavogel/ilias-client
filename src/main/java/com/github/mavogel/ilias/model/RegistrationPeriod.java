package com.github.mavogel.ilias.model;

import java.time.LocalDateTime;

/**
 * Created by mavogel on 9/9/16.
 */
public class RegistrationPeriod {

    private LocalDateTime registrationStart;
    private LocalDateTime registrationEnd;

    public RegistrationPeriod(final LocalDateTime registrationStart, final LocalDateTime registrationEnd) {
        this.registrationStart = registrationStart;
        this.registrationEnd = registrationEnd;
    }

    public LocalDateTime getRegistrationStart() {
        return registrationStart;
    }

    public LocalDateTime getRegistrationEnd() {
        return registrationEnd;
    }
}
