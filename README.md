

Add field example

```java
// intended declaration:
private String type;

// normal JavaPoet
builder.addField(FieldSpec.builder( String.class, "type", Modifier.PRIVATE).build());

// versus with utility
addField(builder, PRIVATE(), String.class, "type");
```

Add field with initializer

```java
// intended declaration:
public static final PRIMARY = null;

// normal JavaPoet
builder.addField(
		FieldSpec.builder( String.class,"PRIMARY",Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
		.initializer("null").build() );

// versus with utility
addField(builder, PUBLIC().STATIC().FINAL(), String.class, "PRIMARY", field-> {
	field.initializer("null");
});
```

Add parameter to a method

```java
// intended declaration for one of the parameters:
method(boolean primitive)

// normal JavaPoet
method.addParameter(ParameterSpec.builder(boolean.class, "primitive").build());

// versus with utility
addParameter(method, boolean.class, "primitive");
```

Add field with getter and setter (bean style)
```java
// intended declaration for one of the parameters:
private boolean primitive;
public booelan isPrimitive(){ 
	return primitive; 
}
public booelan setPrimitive(boolean primitive){ 
	this.primitive = primitive; 
}


// normal JavaPoet
builder.addField(FieldSpec.builder( boolean.class, "primitive", Modifier.PRIVATE).build());

builder.addMethod( MethodSpec.methodBuilder("isPrimitive")
	.addModifiers(Modifier.PUBLIC)
	.addCode("return primitive;").build());

builder.addMethod( MethodSpec.methodBuilder("setPrimitive")
		.addModifiers(Modifier.PUBLIC)
		.addParameter(boolean.class,"primitive")
		.addCode("this.primitive = primitive;\n").build());


// versus with utility
addBeanField(enumbuilder, boolean.class, "primitive");
```


```xml
<factorypath>
    <factorypathentry kind="VARJAR" id="M2_REPO/javax/persistence/persistence-api/1.0/persistence-api-1.0.jar" enabled="true" runInBatchMode="false"/>
    <factorypathentry kind="VARJAR" id="M2_REPO/hr/hrg/myst/myst-data/1.0-SNAPSHOT/myst-data-1.0-SNAPSHOT.jar" enabled="true" runInBatchMode="false"/>
    <factorypathentry kind="VARJAR" id="M2_REPO/hr/hrg/myst/myst-db-gen/1.0-SNAPSHOT/myst-gen-1.0-SNAPSHOT.jar" enabled="true" runInBatchMode="false"/>
    <factorypathentry kind="VARJAR" id="M2_REPO/com/squareup/javapoet/1.7.0/javapoet-1.7.0.jar" enabled="true" runInBatchMode="false"/>
</factorypath>
```