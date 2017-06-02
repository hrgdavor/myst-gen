package hr.hrg.myst.gen;


import static hr.hrg.javapoet.PoetUtil.*;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import hr.hrg.myst.data.EnumGetterUpdateDelta;

public class GenDelta {

	public TypeSpec.Builder gen2(EntityDef def) {

		TypeSpec.Builder cp = classBuilder(PUBLIC(), def.typeDelta);
		
		cp.superclass(parametrized(EnumGetterUpdateDelta.class, def.typeImmutable, def.typeEnum));
		
		MethodSpec.Builder constr = constructorBuilder(PUBLIC());
		addParameter(constr, long.class, "changeSet");
		addParameter(constr, def.typeImmutable, "obj");
		
		constr.addCode("super(changeSet, obj, $T.COLUMN_ARRAY);", def.typeMeta);
		cp.addMethod(constr.build());
		
		return cp;
	}	
	
}
