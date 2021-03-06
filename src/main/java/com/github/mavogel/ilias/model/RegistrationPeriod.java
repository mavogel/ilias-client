/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2016 Manuel Vogel
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 *  and associated documentation files (the "Software"), to deal in the Software without restriction,
 *  including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 *  subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies
 *  or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 *  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *  https://opensource.org/licenses/MIT
 */
package com.github.mavogel.ilias.model;

import org.apache.commons.lang3.Validate;

import java.time.LocalDateTime;

/**
 * Model for the registration period.
 *
 * Created by mavogel on 9/9/16.
 */
public class RegistrationPeriod {

    private LocalDateTime registrationStart;
    private LocalDateTime registrationEnd;

    public RegistrationPeriod(final LocalDateTime registrationStart, final LocalDateTime registrationEnd) {
        Validate.notNull(registrationStart, "empty registration start is not allowed");
        Validate.notNull(registrationEnd, "empty registration end is not allowed");
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
