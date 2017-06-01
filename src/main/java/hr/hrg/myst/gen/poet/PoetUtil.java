package hr.hrg.myst.gen.poet;

import java.lang.reflect.Type;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

public class PoetUtil {

	public static final ClassName TN_CLASS  = ClassName.get(Class.class);
	public static final TypeName TN_WILDCARD = WildcardTypeName.subtypeOf(Object.class);
	public static final TypeName TN_CLASS_Q = ParameterizedTypeName.get(ClassName.get(Class.class), TN_WILDCARD);
	public static final ArrayTypeName TN_OBJECT_ARRAY = ArrayTypeName.of(Object.class);
	
	public static ParameterizedTypeName parametrized(Class<?> clazz, TypeName ...arguments){
		return ParameterizedTypeName.get(ClassName.get(clazz), arguments);
	}

	public static ParameterizedTypeName parametrized(Class<?> clazz, Type ...arguments){
		return ParameterizedTypeName.get(clazz, arguments);
	}

	public static ParameterizedTypeName parametrized(ClassName clazz, TypeName ...arguments){
		return ParameterizedTypeName.get(clazz, arguments);
	}
	
	public static ParameterizedTypeName parametrized(Class<?> clazz, Class<?> ...arr){
		TypeName[] arguments = new TypeName[arr.length];
		for(int i=0; i<arr.length; i++){
			arguments[i] = ClassName.get(arr[i]);
		}
		return ParameterizedTypeName.get(ClassName.get(clazz), arguments);
	}
	
	
	/** Method that converts Object to TypeName used in other utility methods.<br>
	 * Supported types: {@link TypeName}, {@link Type}, {@link TypeMirror}, {@link TypeElement}
	 * 
	 * @param type - any of the supported types (TypeName, Type, TypeMirror, TypeElement)
	 * @return TypeName
	 */
	public static final TypeName toTypeName(Object type){
		if(type == null) throw new NullPointerException("type can not be null");
		
		if(type instanceof TypeName) 
			return (TypeName) type;
		
		else if(type instanceof Type) 
			return TypeName.get((Type)type);
		
		else if(type instanceof TypeMirror) 
			return TypeName.get((TypeMirror)type);

		else if(type instanceof TypeElement) 
			return ClassName.get((TypeElement)type);
				
		throw new UnsupportedOperationException("Type "+type+" of "+type.getClass().getName()+" not supported for conversion to TypeName");
	}

	/** Method that converts Object to ClassName used in other utility methods.<br>
	 * Supported types: {@link TypeName}, {@link Type}, {@link TypeMirror}, {@link TypeElement}
	 * 
	 * @param type - any of the supported types (TypeName, Type, TypeMirror, TypeElement)
	 * @return TypeName
	 */
	public static final ClassName toClassName(Object type){
		if(type == null) throw new NullPointerException("type can not be null");
		
		if(type instanceof ClassName) 
			return (ClassName) type;
		
		else if(type instanceof Class) 
			return ClassName.get((Class<?>)type);

		else if(type instanceof TypeElement) 
			return ClassName.get((TypeElement)type);
				
		throw new UnsupportedOperationException("Type "+type+" of "+type.getClass().getName()+" not supported for conversion to ClassName");
	}
	
	// *********************** annotation ********************************************* 
	
	public static final AnnotationSpec annotationSpec(Object type, String member, String typeArgumentsFormat, Object ...codeParams){
		return AnnotationSpec.builder(toClassName(type)).addMember(member, typeArgumentsFormat, codeParams).build();
	}
	
	public static final TypeSpec.Builder anonymousClassBuilder(ModifierBuilder modifiers, String typeArgumentsFormat, Object... args){
		TypeSpec.Builder anonymousClassBuilder = TypeSpec.anonymousClassBuilder(typeArgumentsFormat, args);
		if(modifiers != null) modifiers.to(anonymousClassBuilder);		
		return anonymousClassBuilder;
	}	
	
	public static final TypeSpec.Builder classBuilder(ModifierBuilder modifiers, String name){
		TypeSpec.Builder classBuilder = TypeSpec.classBuilder(name);
		if(modifiers != null) modifiers.to(classBuilder); 		
		return classBuilder;
	}
	
	public static final TypeSpec.Builder classBuilder(ModifierBuilder modifiers, ClassName clazz){
		return classBuilder(modifiers, clazz.simpleName());
	}
	
	public static final TypeSpec.Builder enumBuilder(ModifierBuilder modifiers, String name){
		TypeSpec.Builder enumBuilder = TypeSpec.enumBuilder(name);
		if(modifiers != null) modifiers.to(enumBuilder);		
		return enumBuilder;
	}

	public static final TypeSpec.Builder enumBuilder(ModifierBuilder modifiers, ClassName name){
		return enumBuilder(modifiers, name.simpleName());
	}
	
	/** Shortcut for {@link MethodSpec#constructorBuilder()} with modifiers. 
	 *  
	 * @param modifiers modifiers
	 */
	public static final MethodSpec.Builder constructorBuilder(ModifierBuilder modifiers){
		return modifiers.to(MethodSpec.constructorBuilder());
	}
	
	/** Shortcut for {@link #methodBuilder(ModifierBuilder, Object, String)} with returnsType=void. 
	 *  
	 * @param modifiers modifiers
	 * @param name - method name
	 * @return {@link MethodSpec.Builder}
	 * @see #methodBuilder(ModifierBuilder, Object, String)
	 */
	public static final MethodSpec.Builder methodBuilder(ModifierBuilder modifiers, String name){
		return methodBuilder(modifiers, TypeName.VOID, name);
	}

	/**
	 * 
	 * @param modifiers modifiers
	 * @param returnsType - return type of the method (any definition supported by {@link #toTypeName(Object)}
	 * @param name - method name
	 * @return {@link MethodSpec.Builder}
	 */
	public static final MethodSpec.Builder methodBuilder(ModifierBuilder modifiers, Object returnsType, String name){
		MethodSpec.Builder b = MethodSpec.methodBuilder(name).returns(toTypeName(returnsType));
		if(modifiers != null) b.addModifiers(modifiers.toArray());
		return b;
	}
	
	// *********************** addConstructor ********************************************* 
	public static final void addConstructor(TypeSpec.Builder builder,MethodCustomizer customizer){
		MethodSpec.Builder b = MethodSpec.constructorBuilder();
		customizer.customize(b);
		builder.addMethod(b.build());
	}

	// *********************** addMethod ********************************************* 

	public static final void addMethod(TypeSpec.Builder builder, Object type, String name, MethodCustomizer customizer) {
		customizeAndAddMethod(builder, null, MethodSpec.methodBuilder(name).returns(toTypeName(type)), customizer);
	}
	public static final void addMethod(TypeSpec.Builder builder, ModifierBuilder modifiers, Object type, String name, MethodCustomizer customizer) {
		customizeAndAddMethod(builder, modifiers,   MethodSpec.methodBuilder(name).returns(toTypeName(type)), customizer);
	}

	public static final void addconstructor(TypeSpec.Builder builder, ModifierBuilder modifiers, MethodCustomizer customizer) {
		customizeAndAddMethod(builder, modifiers,  MethodSpec.constructorBuilder(), customizer);
	}
	public static final void addconstructor(TypeSpec.Builder builder, MethodCustomizer customizer) {
		customizeAndAddMethod(builder, null,MethodSpec.constructorBuilder(), customizer);
	}
	
	private static void customizeAndAddMethod(TypeSpec.Builder builder, ModifierBuilder modifiers, MethodSpec.Builder method, MethodCustomizer customizer) {
		if(customizer != null) {
			customizer.customize(method);
		}
		if(modifiers != null) modifiers.to(method);
		builder.addMethod(method.build());
	}

	// *********************** addParam ********************************************* 

	public static final void addParameter(MethodSpec.Builder builder, Object type, String name) {
		addParameter(builder, null, ParameterSpec.builder(toTypeName(type), name));
	}
	public static final void addParameter(MethodSpec.Builder builder, ModifierBuilder modifiers, Object type, String name) {
		addParameter(builder, modifiers, ParameterSpec.builder(toTypeName(type), name));
	}
	public static final void addParameter(MethodSpec.Builder builder, Object type, String name, ParameterCustomizer customizer) {
		customizeAndAddParam(builder, null, ParameterSpec.builder(toTypeName(type), name), customizer);
	}
	public static final void addParameter(MethodSpec.Builder builder, ModifierBuilder modifiers, Object type, String name, ParameterCustomizer customizer) {
		customizeAndAddParam(builder, modifiers, ParameterSpec.builder(toTypeName(type), name), customizer);
	}

	private static final void addParameter(MethodSpec.Builder builder, ModifierBuilder modifiers, ParameterSpec.Builder fb) {
		if(modifiers!=null && !modifiers.isEmpty()) modifiers.to(fb);
		builder.addParameter(fb.build());
	}

	private static void customizeAndAddParam(MethodSpec.Builder builder, ModifierBuilder modifiers,ParameterSpec.Builder param, ParameterCustomizer customizer) {
		if(customizer != null) {
			customizer.customize(param);
		}
		if(modifiers != null) modifiers.to(param);
		builder.addParameter(param.build());
	}	

	// *********************** addBean ********************************************* 


	public static final void addBeanfieldReadonly(TypeSpec.Builder cb, Object type, String name, BeanCustomizer customizer) {
		addBeanField(cb, toTypeName(type), name, true, false, customizer);
	}
	public static final void addBeanfieldReadonly(TypeSpec.Builder cb, Object type, String name) {
		addBeanField(cb, toTypeName(type), name, true, false, null);
	}
	public static final void addBeanField(TypeSpec.Builder cb, Object type, String name, BeanCustomizer customizer) {
		addBeanField(cb, toTypeName(type), name, true, true, customizer);
	}
	public static final void addBeanField(TypeSpec.Builder cb, Object type, String name) {
		addBeanField(cb, toTypeName(type), name, true, true, null);
	}
	
	public static final void addBeanField(TypeSpec.Builder cb, TypeName type, String name, boolean withGetter, boolean withSetter, BeanCustomizer customizer) {
		FieldSpec.Builder field = FieldSpec.builder(type, name).addModifiers(Modifier.PRIVATE);
		String methondName = null;
		String upperName = Character.toUpperCase(name.charAt(0))+name.substring(1);

		MethodSpec.Builder getter = null;
		if(withGetter){
			methondName = ("boolean".equalsIgnoreCase(type.toString()) ? "is":"get")+upperName;
			getter = MethodSpec.methodBuilder(methondName).addModifiers(Modifier.PUBLIC).returns(type);	
			getter.addCode("return "+name+";\n");
		}

		MethodSpec.Builder setter = null;
		if(withSetter){
			methondName = "set"+upperName;
			setter = MethodSpec.methodBuilder(methondName).addModifiers(Modifier.PUBLIC);
			addParameter(setter, type, name);
			setter.addCode("this."+name+" = "+name+";\n");
		}

		if(customizer != null) customizer.customize(field, getter, setter);
		
		cb.addField(field.build());

		if(withGetter) cb.addMethod(getter.build());
		if(withSetter) cb.addMethod(setter.build());
	}
	
	// *********************** addField ********************************************* 
	// type,name
	public static final FieldSpec addField(TypeSpec.Builder cb, Object type, String name) {
		return customizeAndAdd(cb, null, FieldSpec.builder(toTypeName(type), name), null);
	}
	// type,name,initializerFormat,formatParams
	public static final FieldSpec addField(TypeSpec.Builder cb, Object type, String name, String initializerFormat, Object ...formatParams) {
		return customizeAndAdd(cb, null, FieldSpec.builder(toTypeName(type), name).initializer(initializerFormat,formatParams), null);
	}
	// modifiers,type,name
	public static final FieldSpec addField(TypeSpec.Builder cb,ModifierBuilder modifiers, Object type, String name) {
		return customizeAndAdd(cb, modifiers, FieldSpec.builder(toTypeName(type), name), null);
	}
	// modifiers,type,name,initializerFormat,formatParams
	public static final FieldSpec addField(TypeSpec.Builder cb,ModifierBuilder modifiers, Object type, String name, String initializerFormat, Object ...formatParams) {
		return customizeAndAdd(cb, modifiers, FieldSpec.builder(toTypeName(type), name).initializer(initializerFormat,formatParams), null);
	}
	public static final FieldSpec addField(TypeSpec.Builder cb, Object type, String name, FieldCustomizer customizer) {
		return customizeAndAdd(cb, null, FieldSpec.builder(toTypeName(type), name), customizer);
	}
	public static final FieldSpec addField(TypeSpec.Builder cb, ModifierBuilder modifiers, Object type, String name, FieldCustomizer customizer) {
		return customizeAndAdd(cb, modifiers, FieldSpec.builder(toTypeName(type), name), customizer);
	}

	private static FieldSpec customizeAndAdd(TypeSpec.Builder cb, ModifierBuilder modifiers, FieldSpec.Builder field, FieldCustomizer customizer) {
		if(customizer != null) {
			customizer.customize(field);
		}
		if(modifiers != null) modifiers.to(field);
		FieldSpec fieldSpec = field.build();
		cb.addField(fieldSpec);
		return fieldSpec;
	}
	
	// ********************************************** addSetterParameter ***************************************
	
	public static void addSetterParameter(TypeSpec.Builder builder, MethodSpec.Builder method, String ...fieldNames) {
		addSetterParameter(builder.build(), method, fieldNames);
	}
	
	public static void addSetterParameter(TypeSpec spec, MethodSpec.Builder method, String ...fieldNames) {
		for(String fieldName:fieldNames){
			addSetterParameter(spec, method, fieldName,null);
		}
	}
	public static void addSetterParameter(TypeSpec.Builder builder,MethodSpec.Builder method, List<String> fieldNames) {
		addSetterParameter(builder.build(), method, fieldNames);
	}
	public static void addSetterParameter(TypeSpec spec,MethodSpec.Builder method, List<String> fieldNames) {
		for(String fieldName:fieldNames){
			addSetterParameter(spec, method, fieldName,null);
		}
	}

	public static void addSetterParameter(TypeSpec.Builder builder, MethodSpec.Builder method, String fieldName, ParameterCustomizer customizer) {
		addSetterParameter(builder.build(), method, fieldName, customizer);		
	}
	public static void addSetterParameter(TypeSpec spec, MethodSpec.Builder method, String fieldName, ParameterCustomizer customizer) {
		FieldSpec field = null;
		for(FieldSpec tmp:spec.fieldSpecs){
			if(tmp.name.equals(fieldName)) field = tmp;
		}
		if(field == null){
			throw new RuntimeException("Field "+fieldName+" not found in "+spec.name+" while adding aprameter for "+method.build().name);
		}else{
			addSetterParameter(method, field, customizer);
		}
	}

	public static void addSetterParameter(MethodSpec.Builder method, FieldSpec field, ParameterCustomizer customizer) {
		addSetterParameter(method, field.type, field.name, customizer);
	}
	
	public static void addSetterParameter(MethodSpec.Builder method, Object type, String name, ParameterCustomizer customizer) {
		addParameter(method, type, name, customizer);
		method.addCode("this."+name+" = "+name+";\n");
	}
	
	public static ModifierBuilder NO_MODIFIER(){ return new ModifierBuilder();}
	public static ModifierBuilder ABSTRACT(){ return new ModifierBuilder(Modifier.ABSTRACT);}
	public static ModifierBuilder DEFAULT(){ return new ModifierBuilder(Modifier.DEFAULT);}
	public static ModifierBuilder FINAL(){ return new ModifierBuilder(Modifier.FINAL);}
	public static ModifierBuilder NATIVE(){ return new ModifierBuilder(Modifier.NATIVE);}
	public static ModifierBuilder PRIVATE(){ return new ModifierBuilder(Modifier.PRIVATE);}
	public static ModifierBuilder PROTECTED(){ return new ModifierBuilder(Modifier.PROTECTED);}
	public static ModifierBuilder PUBLIC(){ return new ModifierBuilder(Modifier.PUBLIC);}
	public static ModifierBuilder STATIC(){ return new ModifierBuilder(Modifier.STATIC);}
	public static ModifierBuilder STRICTFP(){ return new ModifierBuilder(Modifier.STRICTFP);}
	public static ModifierBuilder SYNCHRONIZED(){ return new ModifierBuilder(Modifier.SYNCHRONIZED);}
	public static ModifierBuilder TRANSIENT(){ return new ModifierBuilder(Modifier.TRANSIENT);}
	public static ModifierBuilder VOLATILE(){ return new ModifierBuilder(Modifier.VOLATILE);}

	
}

