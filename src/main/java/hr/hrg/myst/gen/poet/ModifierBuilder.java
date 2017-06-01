package hr.hrg.myst.gen.poet;

import java.util.EnumSet;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

public class ModifierBuilder{
	EnumSet<Modifier> mods = EnumSet.noneOf(Modifier.class);

	public ModifierBuilder() {
	}
	
	public ModifierBuilder(Modifier modifier){
		mods.add(modifier);
	}

	private final ModifierBuilder mod(Modifier m){ mods.add(m); return this; }
	public ModifierBuilder ABSTRACT()    { return mod(Modifier.ABSTRACT);}
	public ModifierBuilder DEFAULT()     { return mod(Modifier.DEFAULT);}
	public ModifierBuilder FINAL()       { return mod(Modifier.FINAL);}
	public ModifierBuilder NATIVE()      { return mod(Modifier.NATIVE);}
	public ModifierBuilder PRIVATE()     { return mod(Modifier.PRIVATE);}
	public ModifierBuilder PROTECTED()   { return mod(Modifier.PROTECTED);}
	public ModifierBuilder PUBLIC()      { return mod(Modifier.PUBLIC);}
	public ModifierBuilder STATIC()      { return mod(Modifier.STATIC);}
	public ModifierBuilder STRICTFP()    { return mod(Modifier.STRICTFP);}
	public ModifierBuilder SYNCHRONIZED(){ return mod(Modifier.SYNCHRONIZED);}
	public ModifierBuilder TRANSIENT()   { return mod(Modifier.TRANSIENT);}
	public ModifierBuilder VOLATILE()    { return mod(Modifier.VOLATILE);}

	public ModifierBuilder add(Modifier ...modifiers){ 
		for(Modifier m:modifiers) mods.add(m);
		return this;
	}

	public Modifier[] toArray() {
		return mods.toArray(new Modifier[mods.size()]);
	}

	public MethodSpec.Builder to(MethodSpec.Builder b)      {b.addModifiers(toArray()); return b;}
	public FieldSpec.Builder to(FieldSpec.Builder b)        {b.addModifiers(toArray()); return b;}
	public TypeSpec.Builder to(TypeSpec.Builder b)          {b.addModifiers(toArray()); return b;}
	public ParameterSpec.Builder to(ParameterSpec.Builder b){b.addModifiers(toArray()); return b;}
	
	public boolean isEmpty(){
		return mods.isEmpty();
	}

}