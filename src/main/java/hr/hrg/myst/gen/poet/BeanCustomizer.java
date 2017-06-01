package hr.hrg.myst.gen.poet;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;

public interface BeanCustomizer{
	public void customize(FieldSpec.Builder field, MethodSpec.Builder getter,MethodSpec.Builder setter);
}