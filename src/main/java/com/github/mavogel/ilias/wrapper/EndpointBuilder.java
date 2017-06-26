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

import com.github.mavogel.ilias.model.LoginConfiguration;
import com.github.mavogel.ilias.wrapper.soap.SoapEndpoint;
import org.apache.commons.lang.NotImplementedException;

/**
 * Created by mavogel on 6/26/17.
 */
public class EndpointBuilder {

    /**
     * The possible endpoint types
     */
    public enum Type {
        SOAP,
        REST
    }

    private EndpointBuilder() {
        throw new AssertionError("do not instantiate");
    }

    /**
     * Creates a new endpoint instance and logs in with the given credentials.
     *
     * @param type the type of endpoint @see {@link Type}
     * @param loginConfiguration the login configuration data
     * @return the new {@link IliasEndpoint}
     * @throws Exception in case of failure on endpoint creation or wrong credentials
     */
    public static AbstractIliasEndpoint build(final Type type, final LoginConfiguration loginConfiguration) throws Exception {
        switch (type) {
            case SOAP:
                return new SoapEndpoint(loginConfiguration);
            case REST:
                throw new NotImplementedException("REST endpoint has not yet been implemented");
            default:
                throw new IllegalArgumentException("case '" + type.name() + "' has not yet been implemented");
        }
    }

}
