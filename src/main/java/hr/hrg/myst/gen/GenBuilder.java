package hr.hrg.myst.gen;

import static com.squareup.javapoet.TypeSpec.classBuilder;
import static hr.hrg.javapoet.PoetUtil.*;
import static hr.hrg.myst.gen.MystUtil.*;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import hr.hrg.myst.data.EntityValues;
import hr.hrg.myst.data.EnumGetter;
import hr.hrg.myst.data.EnumSetter;

public class GenBuilder {

	private boolean jackson;

	public GenBuilder(boolean jackson) {
		this.jackson = jackson;
	}


	public TypeSpec.Builder gen2(EntityDef def) {
		TypeSpec.Builder builder = classBuilder(def.typeBuilder);
		PUBLIC().to(builder);
        
		if(def.isInterface){			
			builder.addSuperinterface(def.type);
		}else{
			builder.superclass(def.type);
		}

		addInterfaces(def, builder, jackson);
        
        CodeBlock.Builder code = CodeBlock.builder().add("return new $T(", def.typeImmutable);

        int count = def.getProps().size();
        for(int i=0; i<count; i++) {
        	Property prop = def.getProps().get(i);
			FieldSpec fieldSpec = addField(builder, PROTECTED(), prop.type, prop.name);
        	
			MethodSpec.Builder g = methodBuilder(PUBLIC(), prop.type, prop.getterName).addAnnotation(Override.class);
			g.addCode("return "+prop.fieldName+";\n");
			builder.addMethod(g.build());

			MethodSpec.Builder bm = methodBuilder(PUBLIC(), prop.type, prop.name).addAnnotation(Override.class);
			addSetterParameter(bm, fieldSpec,null);
			bm.addCode("return this;");
			
			code.add("\t\t"+prop.fieldName+(i == count-1 ? "":","));
        }
        
        gen_getEntityValues(def, builder, jackson);
        genConstructors(def, builder, jackson);
        
        code.add("\t);");
		
        GenImmutable.addEnumGetter(def, builder);
        builder.addMethod(genEnumSetter(def, builder).build());
        if(jackson) GenImmutable.addDirectSerializer(def,builder);
        
        return builder;
	}

	public static void addInterfaces(EntityDef def, TypeSpec.Builder builder, boolean jackson) {
		builder.addSuperinterface(EntityValues.class);
		builder.addSuperinterface(parametrized(EnumSetter.class, def.typeEnum));
		builder.addSuperinterface(parametrized(EnumGetter.class, def.typeEnum));
	}	
	
	public static void gen_getEntityValues(EntityDef def, TypeSpec.Builder cp, boolean jackson){
		MethodSpec.Builder getEntityValues = methodBuilder(PUBLIC(), ArrayTypeName.of(Object.class), "getEntityValues" );
		getEntityValues.addAnnotation(Override.class);
        getEntityValues.addCode("return new Object[]{\n");

        int count = def.getProps().size();
        for(int i=0; i<count; i++) {
        	Property property = def.getProps().get(i);
			getEntityValues.addCode("\t\t"+property.fieldName+(i == count-1 ? "":",\n"));
        }        
        getEntityValues.addCode("\t};\n");
        cp.addMethod(getEntityValues.build());
	}
	
	public static void genConstructors(EntityDef def, TypeSpec.Builder builder, boolean jackson){
        // empty default constructor
		addconstructor(builder, null);

        GenImmutable.genConstructor(def,builder,jackson);
	}

	
	public static MethodSpec.Builder genEnumSetter(EntityDef def, TypeSpec.Builder cp){

        MethodSpec.Builder setValue = methodBuilder(PUBLIC(), void.class, "setValue");
        setValue.addAnnotation(Override.class);
        setValue.addParameter(def.typeEnum, "column");
        setValue.addParameter(Object.class, "value");
        setValue.addCode("this.setValue(column.ordinal(), value);\n");
		cp.addMethod(setValue.build());
		
        setValue = methodBuilder(PUBLIC(), void.class, "setValue");
        setValue.addAnnotation(Override.class);
        setValue.addParameter(int.class, "ordinal");
        setValue.addParameter(Object.class, "value");
        setValue.addCode("switch (ordinal) {\n");

        int count = def.getProps().size();
        for(int i=0; i<count; i++) {
        	Property prop = def.getProps().get(i);
        	setValue.addCode("case "+i+": this."+prop.fieldName+" = ($T) value;break;\n",prop.type.box());
        }
        setValue.addCode("default: throw new ArrayIndexOutOfBoundsException(ordinal);\n");
        setValue.addCode("}\n");
		
        return setValue;
	}	
	
}
