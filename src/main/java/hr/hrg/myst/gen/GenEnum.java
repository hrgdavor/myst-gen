package hr.hrg.myst.gen;

import static com.squareup.javapoet.TypeSpec.anonymousClassBuilder;
import static com.squareup.javapoet.TypeSpec.enumBuilder;
import static hr.hrg.javapoet.PoetUtil.*;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import hr.hrg.javapoet.BeanCustomizer;
import hr.hrg.myst.data.ColumnMeta;
import hr.hrg.myst.data.ImmutableList;

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
		addBeanfieldReadonly(enumbuilder, parametrized(ImmutableList.class, TN_CLASS_Q), "typeParams", addOverride);
		
		for(Property prop: def.getProps()){
			com.squareup.javapoet.CodeBlock.Builder codeBlock = CodeBlock.builder().add("$S",prop.columnName);
			if(prop.type instanceof ParameterizedTypeName){				
				ParameterizedTypeName parameterizedTypeName = (ParameterizedTypeName)prop.type;
				codeBlock.add(",$T.class",parameterizedTypeName.rawType);
				codeBlock.add(",$L",prop.isPrimitive());
				for(TypeName ta: parameterizedTypeName.typeArguments){
					codeBlock.add(",$T.class",ta);					
				}
			}else{
				codeBlock.add(",$T.class",prop.type);
				codeBlock.add(",$L",prop.isPrimitive());
			}
			
			TypeSpec spec = anonymousClassBuilder(codeBlock.build()
//					"$S,$T.class,$L", 
//					prop.columnName, 
//					prop.type,
//					prop.isPrimitive()
					).build();
			
			enumbuilder.addEnumConstant(prop.fieldName, spec);
		}
				
		addconstructor(enumbuilder, PRIVATE(), (method) -> {
			addSetterParameter(enumbuilder,method,"columnName","type","primitive");
			method.addParameter(ArrayTypeName.of(TN_CLASS_Q), "typeParams");
			method.varargs();
			method.addCode("this.typeParams = $T.safe(typeParams);\n",ImmutableList.class);
		});

		addColumnsDef(enumbuilder,def);
		
		addMethod(enumbuilder, PUBLIC(), boolean.class, "isGeneric", method->{
			method.addAnnotation(Override.class);
			method.addCode("return typeParams.isEmpty();\n");
		});
				
		return enumbuilder;
	}

	private void addColumnsDef(TypeSpec.Builder cp, EntityDef def) {
		List<String> colNames = new ArrayList<>();
		for(Property p:def.props) {
			colNames.add(p.columnName);
		}

		StringBuffer arr = new StringBuffer("(");
		StringBuffer str = new StringBuffer("\"");

		String delim = "";
		for (String col : colNames) {
			arr.append(delim); str.append(delim);
			arr.append("\"").append(col).append("\"");
			str.append(col);
			delim = ",";
		}

		str.append("\"");
		arr.append(")");
		
		addField(cp,PUBLIC().STATIC().FINAL(), String.class, "COLUMNS_STR", 
				field->field.initializer(str.toString()));
		
		addField(cp,PUBLIC().STATIC().FINAL(), int.class, "COLUMN_COUNT", 
				field->field.initializer(""+def.props.size()));
		
		addField(cp,PUBLIC().STATIC().FINAL(), parametrized(ImmutableList.class, String.class), "COLUMN_NAMES", 
				field->field.initializer("ImmutableList.safe"+arr.toString()));
		
		addField(cp,PUBLIC().STATIC().FINAL(), ArrayTypeName.of(def.typeEnum), "COLUMN_ARRAY", 
				field->field.initializer("$T.values()",def.typeEnum));

		addField(cp,PUBLIC().STATIC().FINAL(), parametrized(ImmutableList.class, def.typeEnum), "COLUMNS", 
				field->field.initializer("ImmutableList.safe(COLUMN_ARRAY);"));
		
	}
	
}
