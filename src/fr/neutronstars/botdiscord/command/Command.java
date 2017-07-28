package fr.neutronstars.botdiscord.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value=ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

	public String name();
	public String description() default "Sans description.";
	public ExecutorType type() default ExecutorType.ALL;
	
	public enum ExecutorType{
		ALL, USER, CONSOLE;
	}
}
