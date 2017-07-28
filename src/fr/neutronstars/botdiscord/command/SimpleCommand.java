package fr.neutronstars.botdiscord.command;

import java.lang.reflect.Method;

import fr.neutronstars.botdiscord.command.Command.ExecutorType;

public final class SimpleCommand {

	private final String name, description;
	private final ExecutorType executorType;
	private final Object object;
	private final Method method;
	
	public SimpleCommand(String name, String description, ExecutorType executorType, Object object, Method method) {
		super();
		this.name = name;
		this.description = description;
		this.executorType = executorType;
		this.object = object;
		this.method = method;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public ExecutorType getExecutorType() {
		return executorType;
	}

	public Object getObject() {
		return object;
	}

	public Method getMethod() {
		return method;
	}
	
	
}
