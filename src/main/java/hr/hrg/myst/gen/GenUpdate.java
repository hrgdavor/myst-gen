package hr.hrg.myst.gen;

import static com.squareup.javapoet.TypeSpec.classBuilder;
import static hr.hrg.javapoet.PoetUtil.*;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import hr.hrg.myst.data.Update;
import hr.hrg.myst.data.UpdateDelta;

public class GenUpdate {

	private boolean jackson;
	private boolean genBuilder;

	public GenUpdate(boolean jackson, boolean genBuilder) {
		this.jackson = jackson;
		this.genBuilder = genBuilder;
	}

	public Builder gen2(EntityDef def) {

		TypeSpec.Builder cp = classBuilder(def.typeUpdate);
		PUBLIC().to(cp);
        
		if(genBuilder){
			cp.superclass(def.typeBuilder);			
		}else{
			GenBuilder.addInterfaces(def, cp, jackson);
		}
		
		
		cp.addSuperinterface(parametrized(Update.class,def.type,def.typeEnum));

    	addField(cp,PROTECTED(), long.class, "_changeSet");

        MethodSpec.Builder getDelta = methodBuilder(PUBLIC(), parametrized(UpdateDelta.class,def.typeEnum), "getDelta" );
        getDelta.addAnnotation(Override.class);
        getDelta.addCode("return $T.delta(_changeSet,getEntityValues());\n", def.typeMeta);
        cp.addMethod(getDelta.build());
        
        int count = def.getProps().size();
        for(int i=0; i<count; i++) {
        	Property prop = def.getProps().get(i);
        	
        	if(!genBuilder)	addField(cp,PROTECTED(), prop.type, prop.name);

			MethodSpec.Builder g = methodBuilder(PUBLIC(), prop.type, prop.getterName);
			g.addCode("return "+prop.fieldName+";\n");
			if(genBuilder) g.addAnnotation(Override.class);
			cp.addMethod(g.build());

			MethodSpec.Builder bm = methodBuilder(PUBLIC(), def.typeUpdate, prop.name);
			bm.addCode("this._changeSet |= "+(1L<<i)+";\n");
			addSetterParameter(bm, prop.type, prop.name, null);
			bm.addCode("return this;\n");
			cp.addMethod(bm.build());
        }
        
        MethodSpec.Builder setValue = null;
        if(genBuilder){        
			setValue = methodBuilder(PUBLIC(), void.class, "setValue" );
	        setValue.addAnnotation(Override.class);
	        addParameter(setValue,int.class, "ordinal");
	        addParameter(setValue,Object.class, "value");
	        setValue.addCode("super.setValue(ordinal, value);\n");
	        genConstrucotrsExt(def, cp, jackson);
        }else{
        	setValue = GenBuilder.genEnumSetter(def, cp);
        	GenBuilder.genConstructors(def, cp, jackson);
        	GenBuilder.gen_getEntityValues(def, cp, jackson);
            GenImmutable.addEnumGetter(def, cp);
            if(jackson) GenImmutable.addDirectSerializer(def,cp);
        }
        setValue.addCode("this._changeSet |= (1L<<ordinal);\n");
        cp.addMethod(setValue.build());

        
        return cp;
	}
	
	
	public static void genConstrucotrsExt(EntityDef def, TypeSpec.Builder cp, boolean jackson){
        MethodSpec.Builder constr = constructorBuilder(PUBLIC());
        constr.addCode("super();\n");
        cp.addMethod(constr.build());

        constr = constructorBuilder(PUBLIC());
        addParameter(constr, def.type, "v");
        constr.addCode("super(v);");
        cp.addMethod(constr.build());

        constr = constructorBuilder(PUBLIC());

        constr.addCode("super(");
        
        int count = def.getProps().size();
        for(int i=0; i<count; i++) {
        	Property property = def.getProps().get(i);
        	addParameter(constr, property.type, property.name);        	

            constr.addCode(""+property.name+(i == count-1 ? "":","));
        }
        
        constr.addCode(");\n");
        cp.addMethod(constr.build());
	}
	
}
