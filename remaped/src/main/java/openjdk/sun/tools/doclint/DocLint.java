/*
 * Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package openjdk.sun.tools.doclint;

import java.util.ServiceLoader;

import openjdk.sun.source.util.JavacTask;
import openjdk.sun.source.util.Plugin;
import java.util.function.Supplier;
    
/**
 * The base class for the DocLint service used by javac.
 *
 * <p><b>This is NOT part of any supported API.
 * If you write code that depends on this, you do so at your own risk.
 * This code and its internal interfaces are subject to change or
 * deletion without notice.</b>
 */
public abstract class DocLint implements Plugin {
    public static final String XMSGS_OPTION = "-Xmsgs";
    public static final String XMSGS_CUSTOM_PREFIX = "-Xmsgs:";
    public static final String XCHECK_PACKAGE = "-XcheckPackage:";

    private static Provider<DocLint> docLintProvider;

    public abstract boolean isValidOption(String opt);

    public static synchronized DocLint newDocLint() {
        if (docLintProvider == null) {           
            for (DocLint docLint : ServiceLoader.load(DocLint.class, ClassLoader.getSystemClassLoader())) {
                if (docLint.getName().equals("doclint")) {
                    return docLint;                   
               }
            }
            
            if (docLintProvider == null) {
                docLintProvider = new Provider<>() {
                    @Override
                    public Class<? extends DocLint> type() {
                        return NoDocLint.class;
                    }

                    @Override
                    public DocLint get() {
                        return new NoDocLint();
                    }
                };
            }
        }
        return docLintProvider.get();
    }

    private static class NoDocLint extends DocLint {
        @Override
        public String getName() {
            return "doclint-not-available";
        }

        @Override
        public void init(JavacTask task, String... args) {
            throw new IllegalStateException("doclint not available");
        }

        @Override
        public boolean isValidOption(String s) {
            // passively accept all "plausible" options
            return s.equals(XMSGS_OPTION)
                    || s.startsWith(XMSGS_CUSTOM_PREFIX)
                    || s.startsWith(XCHECK_PACKAGE);
        }
    }

    /**
     * Represents a service provider located by {@code ServiceLoader}.
     *
     * <p> When using a loader's {@link ServiceLoader#stream() stream()} method
     * then the elements are of type {@code Provider}. This allows processing
     * to select or filter on the provider class without instantiating the
     * provider. </p>
     *
     * @param  <S> The service type
     * @since 9
     */
    public static interface Provider<S> extends Supplier<S> {
        /**
         * Returns the provider type. There is no guarantee that this type is
         * accessible or that it has a public no-args constructor. The {@link
         * #get() get()} method should be used to obtain the provider instance.
         *
         * <p> When a module declares that the provider class is created by a
         * provider factory then this method returns the return type of its
         * public static "{@code provider()}" method.
         *
         * @return The provider type
         */
        Class<? extends S> type();

        /**
         * Returns an instance of the provider.
         *
         * @return An instance of the provider.
         *
         * @throws ServiceConfigurationError
         *         If the service provider cannot be instantiated, or in the
         *         case of a provider factory, the public static
         *         "{@code provider()}" method returns {@code null} or throws
         *         an error or exception. The {@code ServiceConfigurationError}
         *         will carry an appropriate cause where possible.
         */
        @Override S get();
    }

}
