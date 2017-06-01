package hr.hrg.myst.gen;

import static com.squareup.javapoet.TypeSpec.anonymousClassBuilder;
import static com.squareup.javapoet.TypeSpec.enumBuilder;
import static hr.hrg.myst.gen.poet.PoetUtil.*;

import java.io.IOException;

import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import hr.hrg.myst.data.ColumnMeta;
import hr.hrg.myst.gen.poet.BeanCustomizer;

public class GenEnum {

	public TypeSpec.Builder gen2(EntityDef def) throws IOException {
		
		Builder enumbuilder = enumBuilder(def.typeEnum).addSuperinterface(ColumnMeta.class);
		PUBLIC().to(enumbuilder);
		
		String initCode = def.getPrimaryProp() == null ? "null":def.getPrimaryProp().fieldName;
		addField(enumbuilder, PUBLIC().STATIC().FINAL(), def.typeEnum, "PRIMARY", initCode);
		
		BeanCustomizer addOverride = (field, getter, setter) -> {
			getter.addAnnotation(Override.class);
		};
		
		addBeanfieldReadonly(enumbuilder, TN_CLASS_Q, "type", addOverride);
		addBeanfieldReadonly(enumbuilder, boolean.class, "primitive", addOverride);
		addBeanfieldReadonly(enumbuilder, String.class, "columnName", addOverride);
		
		for(Property prop: def.getProps()){
			TypeSpec spec = anonymousClassBuilder(
					"$S,$T.class,$L", 
					prop.columnName, 
					prop.type,
					prop.isPrimitive()
					).build();
			
			enumbuilder.addEnumConstant(prop.fieldName, spec);
		}
				
		addconstructor(enumbuilder, (method) -> {
			PRIVATE().to(method);
			addSetterParameter(enumbuilder,method,"columnName","type","primitive");
		});
		
		return enumbuilder;
	}

	
}
