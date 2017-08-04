package hr.hrg.myst.gen;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import hr.hrg.myst.data.MystEntity;

public class EntityDef {

	public final String packageName;
	public final String simpleName;
	public final DeclaredType declaredType;

    List<Property> props = new ArrayList<>();
	private Property primaryProp;
	public final String tableName;
	public final boolean isInterface;
	public final ClassName typeEnum;
	public final ClassName typeImmutable;
	public final ClassName typeBuilder;
	public final ClassName typeUpdate;
	public final ClassName typeMeta;
	public final ClassName typeDelta;
	public final ClassName type;

	public final boolean genMeta;
	public final boolean genUpdate;
	
	public EntityDef(TypeElement clazz){
		String qName = clazz.getQualifiedName().toString();

		isInterface = clazz.getKind().isInterface();
		this.declaredType = (DeclaredType) clazz.asType();
		
		int idx = qName.lastIndexOf('.');
		packageName = qName.substring(0, idx);
		simpleName = qName.substring(idx+1);

		MystEntity mystEntity = clazz.getAnnotation(MystEntity.class);
		if(mystEntity != null && mystEntity.table() != null) 
			this.tableName = mystEntity.table();
		else 
			this.tableName = simpleName.toLowerCase();
		genMeta = mystEntity.genMeta();
		genUpdate = mystEntity.genUpdate();
		this.type = ClassName.get(clazz);

		this.typeEnum      = ClassName.get(packageName, simpleName+"Enum");
		this.typeImmutable = ClassName.get(packageName, simpleName+"Immutable");
		this.typeBuilder   = ClassName.get(packageName, simpleName+"Builder");
		this.typeUpdate    = ClassName.get(packageName, simpleName+"Update");
		this.typeMeta      = ClassName.get(packageName, simpleName+"Meta");
		this.typeDelta     = ClassName.get(packageName, simpleName+"Delta");
		
	}
		
	public Property addProp(String name, TypeName typeName, TypeMirror typeMirror, ExecutableElement method){
		Property property = new Property(name, typeName, method);
		props.add(property);
		return property;
	}
	
	public List<Property> getProps() {
		return props;
	}

	public void setPrimaryProp(Property prop) {
		this.primaryProp = prop;
	}
	
	public Property getPrimaryProp() {
		return primaryProp;
	}
}
