/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2018-2019 by European Spallation Source ERIC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.europeanspallationsource.xaos.tools.lang;


import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;


/**
 * Utilities to navigate Java language model (useful in annotation processing).
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class ModelUtils {

	private static final Map<String, String> PRIMITIVE_WRAPPER = new HashMap<String, String>(8);

	/*
	 * Static initialiser.
	 */
	static {
		PRIMITIVE_WRAPPER.put(Boolean.class.getName(), boolean.class.getName());
		PRIMITIVE_WRAPPER.put(Byte.class.getName(), byte.class.getName());
		PRIMITIVE_WRAPPER.put(Short.class.getName(), short.class.getName());
		PRIMITIVE_WRAPPER.put(Integer.class.getName(), int.class.getName());
		PRIMITIVE_WRAPPER.put(Long.class.getName(), long.class.getName());
		PRIMITIVE_WRAPPER.put(Character.class.getName(), char.class.getName());
		PRIMITIVE_WRAPPER.put(Float.class.getName(), float.class.getName());
		PRIMITIVE_WRAPPER.put(Double.class.getName(), double.class.getName());
	}

	/**
	 * Returns the annotation mirror of the given {@code annotation} for the
	 * given {@link Element}.
	 *
	 * @param <T>        The type of the annotation {@link Class}.
	 * @param element    The {@link Element} for which the annotation mirror
	 *                   must be returned.
	 * @param annotation The annotation {@link Cladd}.
	 * @return The annotation mirror of the given {@code annotation} for the
	 *         given {@link Element}, or {@code null}.
	 */
	public static <T> AnnotationMirror getAnnotationMirror( Element element, Class<T> annotation ) {
		return element.getAnnotationMirrors().stream()
			.filter(mirror -> matches(mirror, annotation))
			.findFirst()
			.orElse(null);
	}

	/**
	 * Returns the first {@link Modifier} in the given {@code modifiers} array
	 * that is contained into the given {@link Set}.
	 *
	 * @param set       A {@link Set} of {@link Modifier}s. The method will
	 *                  return {@code null} if none of the given {@code modifiers}
	 *                  is contained in this set.
	 * @param modifiers The array of {@link Modifier}s. The first of them found
	 *                  in the given {@code set} will be returned.
	 * @return The first of the {@code modifiers} found in the given {@code set}
	 *         or {@code null}.
	 */
	public static Modifier getModifier( Set<Modifier> set, Modifier... modifiers ) {

		for ( Modifier modifier : modifiers ) {
			if ( set.contains(modifier) ) {
				return modifier;
			}
		}

		return null;

	}

	/**
	 * Returns the package's qualified name of the given {@code element}.
	 *
	 * @param element The [@link Element} whose package name must be returned.
	 * @return The package's qualified name of the given {@code element}.
	 */
	@SuppressWarnings( "AssignmentToMethodParameter" )
	public static String getPackage( Element element ) {

		while ( !PackageElement.class.isInstance(element) ) {
			element = element.getEnclosingElement();
		}

		return ( (QualifiedNameable) element ).getQualifiedName().toString();

	}

	/**
	 * Returns the element representing the requested parameter of the
	 * {@code method} element.
	 *
	 * @param method        The {@link ExecutableElement} representing the method
	 *                      whose specified parameter must be returned.
	 * @param parameterName The name of the parameter whose element descriptor must
	 *                      be returned.
	 * @return The element representing the requested parameter of the
	 *         {@code method} element.
	 */
	public static VariableElement getParameter( ExecutableElement method, String parameterName ) {
		return method.getParameters().stream()
			.filter(parameter -> parameter.getSimpleName().contentEquals(parameterName))
			.findFirst()
			.orElse(null);
	}

	public static AnnotationValue getRawAnnotationValue( ProcessingEnvironment environment, Element pos, AnnotationMirror mirror, String method ) {
		for ( Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror.getElementValues().entrySet() ) {
			if ( entry.getKey().getSimpleName().contentEquals(method) ) {
				return entry.getValue();
			}
		}
		for ( Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : environment.getElementUtils().getElementValuesWithDefaults(mirror).entrySet() ) {
			if ( entry.getKey().getSimpleName().contentEquals(method) ) {
				return entry.getValue();
			}
		}
		throw new AnnotationError(pos, mirror, "annotation " + ( (TypeElement) mirror.getAnnotationType().asElement() ).getQualifiedName() + " is missing " + method);
	}

	/**
	 * Return the {@link TypeElement} representing the super-class or
	 * super-interface of the element represented by the given parameter.
	 *
	 * @param clazz The {@link TypeElement} of the element whose super-class
	 *              must be returned.
	 * @return The existing super-class {@link TypeElement} or {@code null}.
	 */
	public static TypeElement getSuper( TypeElement clazz ) {

		TypeMirror superMirror = clazz.getSuperclass();

		if ( DeclaredType.class.isInstance(superMirror) ) {
			return (TypeElement) ( (DeclaredType) superMirror ).asElement();
		} else {
			return null;
		}

	}

	/**
	 * Returns {@code true} if the given {@link Element} is accessible. An
	 * element is considered accessible if it is <i>public</i>, if it is
	 * <i>protected</i> and {@code subClass} is {@code true}, or if it has a
	 * <i>package</i> visibility and {@code samePackage} is {@code true}.
	 *
	 * @param element     The {@link Element} to be checked.
	 * @param samePackage {@code true} if <i>package</i> visibility is allowed.
	 * @param subClass    {@code true} if <i>protected</i> visibility is allowed.
	 * @return {@code true} if the given {@link Element} is accessible.
	 */
	public static boolean isAccessible( Element element, boolean samePackage, boolean subClass ) {

		Modifier modifier = getModifier(element.getModifiers(), Modifier.PUBLIC, Modifier.PROTECTED, Modifier.PRIVATE);

		if ( modifier == null ) {
			return samePackage;
		} else {
			switch ( modifier ) {
				case PRIVATE:
					return false;
				case PROTECTED:
					return ( samePackage || subClass );
				default:
					return true;
			}
		}

	}

	/**
	 * Returns {@code true} if the class defined by {@code type} is assignable
	 * to the given {@code clazz}.
	 *
	 * @param <T>         The type of {@code clazz}.
	 * @param environment The {@link ProcessingEnvironment} used to get the
	 *                    {@link TypeMirror} of {@code clazz} and perform the check.
	 * @param type        {@link TypeMirror} of the class to be tested whether is
	 *                    assignable to {@code clazz} or not.
	 * @param clazz       The {@link Class} to test for assignability to.
	 * @return {@code true} if the class defined by {@code type} is assignable
	 *         to the given {@code clazz}
	 */
	public static <T> boolean isAssignable( ProcessingEnvironment environment, TypeMirror type, Class<T> clazz ) {

		TypeMirror superType = environment.getElementUtils().getTypeElement(clazz.getCanonicalName()).asType();

		return environment.getTypeUtils().isAssignable(type, superType);

	}

	/**
	 * Returns {@code true} if the given element represents an inner class or
	 * interface.
	 *
	 * @param elem The {@link TypeElement} to be checked.
	 * @return {@code true} if the given element represents an inner class or
	 *         interface.
	 */
	public static boolean isInnerClass( TypeElement elem ) {
		return !PackageElement.class.isInstance(elem.getEnclosingElement());
	}

	/**
	 * Returns {@code true} it the element represented by the given
	 * {@code mirror} is primitive.
	 *
	 * @param mirror The {@link TypeMirror} to be checked.
	 * @return {@code true} it the element represented by the given
	 *         {@code mirror} is primitive.
	 */
	public static boolean isPrimitive( TypeMirror mirror ) {
		switch ( mirror.getKind() ) {
			case ARRAY:
			case DECLARED:
				return false;
			default:
				return true;
		}
	}

	/**
	 * Returns {@code true} if the given {@code mirror} represents a primitive
	 * wrapper class.
	 *
	 * @param mirror Yje {@link TypeMirror} to be checked.
	 * @return {@code true} if the given {@code mirror} represents a primitive
	 *         wrapper class.
	 */
	public static boolean isPrimitiveWrapper( TypeMirror mirror ) {
		return ( mirror.getKind() == TypeKind.DECLARED )
			&& PRIMITIVE_WRAPPER.containsKey(toString(mirror, false));
	}

	/**
	 * Returns {@code true} if the {@code mirror} matches the {@code annotation}
	 * class, i.e. the mirror's annotation type's name is equal to the
	 * annotation class one.
	 *
	 * @param <T>        The type of the annotation {@link Class}.
	 * @param mirror     The {@link AnnotationMirror} to be checked.
	 * @param annotation The annotation {@link Class} to be matched.
	 * @return {@code true} if the {@code mirror} matches the {@code annotation}
	 *         class.
	 */
	public static <T> boolean matches( AnnotationMirror mirror, Class<T> annotation ) {
		return ( (QualifiedNameable) mirror.getAnnotationType().asElement() ).getQualifiedName().contentEquals(annotation.getCanonicalName());
	}

	/**
	 * Returns the first enclosing parent of the given {@code type}.
	 *
	 * @param <T>     The type of the parent to be found.
	 * @param element The element whose parent must be found.
	 * @param type    The type of the parent to be found.
	 * @return The found parent instance of the given {@code type} or
	 *         {@code null} if no parent can be found.
	 */
	@SuppressWarnings( { "AssignmentToMethodParameter", "unchecked" } )
	public static <T> T parent( Element element, Class<T> type ) {

		do {
			element = element.getEnclosingElement();
		} while ( !type.isInstance(element) );

		return (T) element;

	}

	/**
	 * Return the signature string of the given {@code method} element.
	 *
	 * @param method        The {@link ExecutableElement} representing the
	 *                      method whose signature string must be returned.
	 * @param useParamNames If {@code true} signature string will contain also
	 *                      parameters name.
	 * @return The signature string of the given {@code method} element.
	 */
	public static String signature( ExecutableElement method, boolean useParamNames ) {

		StringBuilder builder = new StringBuilder();
		Set<Modifier> modifiers = method.getModifiers();
		Modifier modifier = getModifier(modifiers, Modifier.PUBLIC, Modifier.PROTECTED, Modifier.PRIVATE);

		if ( modifier != null ) {
			builder.append(modifier).append(' ');
		}

		builder.append(toString(method.getReturnType(), false));
		builder.append(' ');
		builder.append(method.getSimpleName());
		builder.append('(');

		int i = 0;

		for ( VariableElement param : method.getParameters() ) {

			if ( i > 0 ) {
				builder.append(", ");
			}

			builder.append(toString(param.asType(), false));

			if ( useParamNames ) {
				builder.append(' ').append(param.getSimpleName());
			}

			i++;

		}

		builder.append(')');

		List<? extends TypeMirror> throwTypes = method.getThrownTypes();

		if ( throwTypes.size() > 0 ) {

			builder.append(" throws ");

			i = 0;

			for ( TypeMirror throwType : throwTypes ) {

				if ( i > 0 ) {
					builder.append(", ");
				}

				builder.append(throwType);
				i++;

			}

		}

		return builder.toString();

	}

	/**
	 * Returns the string representation of the class represented by the given
	 * {@code mirror}.
	 *
	 * @param mirror               The {@link TypeMirror} of the class whose
	 *                             string representation must be returned.
	 * @param usePrimitiveWrappers If {@code true} the string representation of
	 *                             the primitive wrappers class will be used
	 *                             instead of the primitive one.
	 * @return The string representation of the class represented by the given
	 *         {@code mirror}.
	 */
	public static String toString( TypeMirror mirror, boolean usePrimitiveWrappers ) {

		TypeKind kind = mirror.getKind();

		switch ( kind ) {
			case ARRAY:
				return toString(( (ArrayType) mirror ).getComponentType(), false) + "[]";
			case BOOLEAN:
			case BYTE:
			case DOUBLE:
			case FLOAT:
			case LONG:
			case SHORT: {

				String name = kind.toString().toLowerCase();

				if ( usePrimitiveWrappers ) {
					return "java.lang." + Character.toUpperCase(name.charAt(0)) + name.substring(1);
				} else {
					return name;
				}

			}
			case CHAR:
				return usePrimitiveWrappers ? Character.class.getName() : kind.toString().toLowerCase();
			case INT:
				return usePrimitiveWrappers ? Integer.class.getName() : kind.toString().toLowerCase();
			case VOID:
				return "void";
			case DECLARED: {

				Name paramType = ( (QualifiedNameable) ( (DeclaredType) mirror ).asElement() ).getQualifiedName();
				List<? extends TypeMirror> typeArguments = ( (DeclaredType) mirror ).getTypeArguments();

				if ( typeArguments.isEmpty() ) {
					return paramType.toString();
				} else {

					StringBuilder buff = new StringBuilder(paramType).append('<');

					typeArguments.forEach(typeArgument -> buff.append(toString(typeArgument, false)));

					return buff.append('>').toString();

				}

			}
			default:
				throw new RuntimeException(
					MessageFormat.format("{0} is not implemented for {1}.", kind, mirror.getClass())
				);
		}

	}

}
