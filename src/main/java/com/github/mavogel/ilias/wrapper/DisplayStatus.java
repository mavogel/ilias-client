package com.github.mavogel.ilias.wrapper;/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2017 Manuel Vogel
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 *  https://opensource.org/licenses/MIT
 */

/**
 * Created by mavogel on 6/25/17.
 *
 * The DisplayStatus has four values:
 * <ul>
 * <li>MEMBER = 1</li>
 * <li>TUTOR  = 2</li>
 * <li>ADMIN  = 4</li>
 * <li>OWNER  = 8</li>
 * </ul>
 * and determines which courses should be returned.
 * <p>
 * It can be used for example in
 * {@link com.github.mavogel.client.ILIASSoapWebservicePortType#getCoursesForUser(String, String)}
 */
public enum DisplayStatus {

    MEMBER(1),
    TUTOR(2),
    ADMIN(4),
    OWNER(8);

    private final int statusNumber;

    DisplayStatus(final int statusNumber) {
        this.statusNumber = statusNumber;
    }

    /**
     * @return the number of the statusNumber
     */
    public int asNumber() {
        return statusNumber;
    }
}
