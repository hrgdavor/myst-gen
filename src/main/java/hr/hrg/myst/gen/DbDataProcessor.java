package hr.hrg.myst.gen;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.persistence.Id;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

import hr.hrg.myst.data.MystEntity;

@SupportedAnnotationTypes("hr.hrg.myst.data.MystEntity")
@SupportedOptions({"myst_allEntitiesEnumPackage","myst_jackson","myst_genBuilder"})
public class DbDataProcessor extends AbstractProcessor{

    @Override
    public SourceVersion getSupportedSourceVersion() {
        // We may claim to support the latest version, since we are not using
        // any version-specific extensions.
        return SourceVersion.latest();
    }
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(MystEntity.class);
		List<EntityDef> defs = new ArrayList<EntityDef>();
		processingEnv.getMessager().printMessage(Kind.NOTE, "process classes "+elements);
		for (Element element : elements) {
			if		(element.getKind() == ElementKind.INTERFACE) {
				defs.add(generateClass((TypeElement) element, processingEnv));
			}else{
				processingEnv.getMessager().printMessage(Kind.NOTE, "skip because not interface "+element);				
			}
		}

		String allEntitiesEnumPackage = processingEnv.getOptions().get("myst_allEntitiesEnumPackage");
		processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE, "allEntitiesEnumPackage "+allEntitiesEnumPackage);

//		if(allEntitiesEnumPackage != null && defs.size() >0){
//    		ClassPrinter cp = new GenAllEntitiesEnum().gen(allEntitiesEnumPackage,defs).prepare();
//    		cp.printAll(processingEnv);        		
//    	}
		
		return false;
	}

	private EntityDef generateClass(TypeElement clazz, ProcessingEnvironment processingEnv) {
        String interfaceClassName = clazz.getQualifiedName().toString();
   		boolean jackson = "true".equalsIgnoreCase(processingEnv.getOptions().get("myst_jackson"));
        boolean genBuilder = "true".equalsIgnoreCase(processingEnv.getOptions().get("myst_genBuilder"));
        

        processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE,
                "annotated class: " + interfaceClassName);
        
        
        EntityDef def = new EntityDef(clazz);
        
        for (Element element : clazz.getEnclosedElements()) {
        	if(element.getKind() == ElementKind.METHOD) {
        		ExecutableElement method = (ExecutableElement) element;
        		String name = element.getSimpleName().toString();
        		TypeName typeName = TypeName.get(method.getReturnType());

        		String typeNameStr = typeName.toString();
        		if(!name.startsWith("get") && (!name.startsWith("is") && (typeNameStr == "boolean" || typeNameStr == "java.lang.Boolean")) ) continue;

        		Property prop = def.addProp(name, typeName,method.getReturnType(), method);
        		
        		prop.readOnly = method.getAnnotation(Id.class) != null;
        		if(prop.readOnly) {
        			if(def.getPrimaryProp() != null) {
        				processingEnv.getMessager().printMessage(Kind.ERROR, "Second id field found, frist one was at "+def.getPrimaryProp().getterName+"()", method);        				
        			}else {
        				def.setPrimaryProp(prop);        				
        			}
        		}
        	}
		}
        
        try {
        	Builder builder = new GenEnum().gen2(def);
        	write(def.typeEnum.packageName(), builder.build(), processingEnv);

        	builder = new GenImmutable(jackson).gen2(def);
        	write(def.typeImmutable, builder.build(), processingEnv);

        	if(genBuilder){
        		builder = new GenBuilder(jackson).gen2(def);
        		write(def.typeBuilder, builder.build(), processingEnv);
        	}

        	if(def.genUpdate){
				builder = new GenUpdate(jackson, genBuilder).gen2(def);
				write(def.typeUpdate, builder.build(), processingEnv);
        	}
    		if(def.genMeta){
    			
    			builder = new GenMeta().gen(def);
    			write(def.typeDelta, builder.build(), processingEnv);    			
    			
//    			builder = new GenDelta().gen2(def);
//    			write(def.typeDelta, builder.build(), processingEnv);
    		}
        	

        	        	
		} catch (Throwable e) {
			processingEnv.getMessager().printMessage(Kind.ERROR, e.getMessage()+"\n"+getTrace(e), clazz);
		}  
        return def;
	}
	
	public void write(ClassName type, TypeSpec spec, ProcessingEnvironment processingEnv) {
		write(type.packageName(), spec, processingEnv);
	}
	
	public void write(String packageName, TypeSpec spec, ProcessingEnvironment processingEnv) {
		try {
			JavaFileObject jfo = processingEnv.getFiler().createSourceFile(packageName+"."+spec.name);

			try (	OutputStream out = jfo.openOutputStream();
					PrintWriter pw = new PrintWriter(out);
					){
				JavaFile javaFile = JavaFile.builder(packageName, spec).build();
				javaFile.writeTo(pw);
				pw.flush();
			}		
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}	

	String getTrace(Throwable e){
		try(StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);) {			
			e.printStackTrace(pw);
			return sw.toString();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}
}
