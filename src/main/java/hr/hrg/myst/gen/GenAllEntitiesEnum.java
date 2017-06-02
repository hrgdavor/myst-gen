package hr.hrg.myst.gen;


import static hr.hrg.javapoet.PoetUtil.*;

import java.util.List;

import com.squareup.javapoet.TypeSpec;

public class GenAllEntitiesEnum {


	public TypeSpec.Builder gen(String allEntitiesEnumPackage, List<EntityDef> defs) {
		String myName = allEntitiesEnumPackage+".AllEntities";

		TypeSpec.Builder cp = enumBuilder(PUBLIC(), "AllEntities");
		
//		for(EntityDef def: defs){
//			StringBuffer enumDef = new StringBuffer()
//					.append("\t").append(def.getSimpleName()).append("{\n")
//					.append("\t\t@Override\n")
//					.append("\t\tpublic "+cp.simpleName(def.classMeta())+" getMeta() {\n")
//					.append("\t\t\treturn "+cp.simpleName(def.classMeta())+".INSTANCE;\n")
//					.append("\t\t}\n")
//					.append("\t}");
//			cp.addEnumDef(enumDef.toString());
//		}
//		
//		cp.addMethod("public abstract", MYST_EntityMeta, "getMeta")
//			.addAnnotation("SuppressWarnings", str("rawtypes"));
				
		
		
		return cp;
	}

}
