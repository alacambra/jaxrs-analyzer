/*
 * Copyright (C) 2015 Sebastian Daschner, sebastian-daschner.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sebastian_daschner.jaxrs_analyzer.analysis.utils;

import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.SignatureAttribute;

/**
 * Contains Java and Javassist utility functionality.
 *
 * @author Sebastian Daschner
 */
public final class JavaUtils {

    public static final String INITIALIZER_NAME = "<init>";
    public static final String BOOTSTRAP_ATTRIBUTE_NAME = "BootstrapMethods";

    private JavaUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if the given method name is a Java initializer.
     *
     * @param name The method name
     * @return {@code true} if name is an initializer
     */
    public static boolean isInitializerName(final String name) {
        return INITIALIZER_NAME.equals(name);
    }

    /**
     * Returns the parameter classes of a method identifier. These are needed for retrieving a method with Javassist.
     *
     * @param identifier The method identifier
     * @return The parameter classes of the method
     * @throws NotFoundException If some parameter classes could not be found
     */
    public static CtClass[] getParameterClasses(final MethodIdentifier identifier) throws NotFoundException {
        final CtClass[] parameterClasses = new CtClass[identifier.getParameterTypes().length];
        for (int i = 0; i < parameterClasses.length; i++) {
            parameterClasses[i] = ClassPool.getDefault().get(identifier.getParameterTypes()[i]);
        }
        return parameterClasses;
    }

    /**
     * Returns the return type of the method signature.
     *
     * @param signature The method signature
     * @return The return type
     * @throws BadBytecode If the bytecode could not be analyzed
     */
    public static String getMethodReturnType(final SignatureAttribute.MethodSignature signature) throws BadBytecode {
        final String type = getType(signature.getReturnType());

        if ("void".equals(type)) {
            return null;
        }
        return type;
    }

    /**
     * Returns the method parameters of the method signature.
     *
     * @param signature The method signature
     * @return The method parameter types
     * @throws BadBytecode If the bytecode could not be analyzed
     */
    public static String[] getMethodParameters(final SignatureAttribute.MethodSignature signature) throws BadBytecode {
        final SignatureAttribute.Type[] parameterTypes = signature.getParameterTypes();

        final String[] parameters = new String[parameterTypes.length];

        int index = 0;
        for (SignatureAttribute.Type type : parameterTypes) {
            parameters[index++] = getType(type);
        }

        return parameters;
    }

    /**
     * Returns the Java type representation (including {@code $} as inner class separator) of the given {@link SignatureAttribute.Type}.
     *
     * @param type The Javassist type
     * @return The Java type representation
     */
    public static String getType(final SignatureAttribute.Type type) {
        if (type instanceof SignatureAttribute.NestedClassType) {
            final SignatureAttribute.NestedClassType nestedClassType = (SignatureAttribute.NestedClassType) type;
            return nestedClassType.getDeclaringClass().getName() + '$' + nestedClassType.getName();
        }
        return type.toString();
    }

}
