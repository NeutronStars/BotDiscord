package fr.neutronstars.botdiscord.command;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import fr.neutronstars.botdiscord.BotDiscord;
import fr.neutronstars.botdiscord.command.Command.ExecutorType;
import fr.neutronstars.botdiscord.command.defaut.CommandDefault;
import fr.neutronstars.botdiscord.command.defaut.HelpCommand;
import fr.neutronstars.botdiscord.music.MusicCommand;
import fr.neutronstars.botdiscord.util.*;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public final class CommandMap {

	private final BotDiscord botDiscord;
	
	private final Map<Long, Integer> powers = new HashMap<>();
	
	private final Map<String, SimpleCommand> commands = new HashMap<>();
	private final String tag = "=";
	
	public CommandMap(BotDiscord botDiscord) {
		this.botDiscord = botDiscord;
		
		registerCommands(new CommandDefault(botDiscord, this), new HelpCommand(this), new MusicCommand());
		
		load();
	}
	
	private void load()
	{
		File file = new File("userspower.json");
		if(!file.exists()) return;
		
		try{
			JSONReader reader = new JSONReader(file);
			JSONArray array = reader.toJSONArray();
			
			for(int i = 0; i < array.length(); i++)
			{
				JSONObject object = array.getJSONObject(i);
				powers.put(object.getLong("id"), object.getInt("power"));
			}
			
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	public void save()
	{
		JSONArray array = new JSONArray();
		
		for(Entry<Long, Integer> power : powers.entrySet())
		{
			JSONObject object = new JSONObject();
			object.accumulate("id", power.getKey());
			object.accumulate("power", power.getValue());
			array.put(object);
		}
		
		try(JSONWriter writter = new JSONWriter("userspower.json")){
			
			writter.write(array);
			writter.flush();
			
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	public void addUserPower(User user, int power)
	{
		if(power == 0) removeUserPower(user);
		else powers.put(user.getIdLong(), power);
	}
	
	public void removeUserPower(User user)
	{
		powers.remove(user.getIdLong());
	}
	
	public int getPowerUser(Guild guild, User user)
	{
		if(guild.getMember(user).hasPermission(Permission.ADMINISTRATOR)) return 150;
		return powers.containsKey(user.getIdLong()) ? powers.get(user.getIdLong()) : 0;
	}
	
	public String getTag() {
		return tag;
	}
	
	public Collection<SimpleCommand> getCommands(){
		return commands.values();
	}
	
	public void registerCommands(Object...objects){
		for(Object object : objects) registerCommand(object);
	}
	
	public void registerCommand(Object object){
		for(Method method : object.getClass().getDeclaredMethods()){
			if(method.isAnnotationPresent(Command.class)){
				Command command = method.getAnnotation(Command.class);
				method.setAccessible(true);
				SimpleCommand simpleCommand = new SimpleCommand(command.name(), command.description(), command.type(), object, method, command.power());
				commands.put(command.name(), simpleCommand);
			}
		}
	}
	
	public void commandConsole(String command){
		Object[] object = getCommand(command);
		if(object[0] == null || ((SimpleCommand)object[0]).getExecutorType() == ExecutorType.USER){
			System.out.println("Commande inconnue.");
			return;
		}
		try{
			execute(((SimpleCommand)object[0]), command, (String[])object[1], null);
		}catch(Exception exception){
			System.out.println("La methode "+((SimpleCommand)object[0]).getMethod().getName()+" n'est pas correctement initialisé.");
		}
	}
	
	public boolean commandUser(User user, String command, Message message){
		Object[] object = getCommand(command);
		if(object[0] == null || ((SimpleCommand)object[0]).getExecutorType() == ExecutorType.CONSOLE) return false;
		
		if(message.getGuild() != null && ((SimpleCommand)object[0]).getPower() > getPowerUser(message.getGuild(), message.getAuthor())) return false;
		
		try{
			execute(((SimpleCommand)object[0]), command,(String[])object[1], message);
		}catch(Exception exception){
			System.out.println("La methode "+((SimpleCommand)object[0]).getMethod().getName()+" n'est pas correctement initialisé.");
		}
		return true;
	}
	
	private Object[] getCommand(String command){
		String[] commandSplit = command.split(" ");
		String[] args = new String[commandSplit.length-1];
		for(int i = 1; i < commandSplit.length; i++) args[i-1] = commandSplit[i];
		SimpleCommand simpleCommand = commands.get(commandSplit[0]);
		return new Object[]{simpleCommand, args};
	}
	
	private void execute(SimpleCommand simpleCommand, String command, String[] args, Message message) throws Exception{
		Parameter[] parameters = simpleCommand.getMethod().getParameters();
		Object[] objects = new Object[parameters.length];
		for(int i = 0; i < parameters.length; i++){
			if(parameters[i].getType() == String[].class) objects[i] = args;
			else if(parameters[i].getType() == User.class) objects[i] = message == null ? null : message.getAuthor();
			else if(parameters[i].getType() == TextChannel.class) objects[i] = message == null ? null : message.getTextChannel();
			else if(parameters[i].getType() == PrivateChannel.class) objects[i] = message == null ? null : message.getPrivateChannel();
			else if(parameters[i].getType() == Guild.class) objects[i] = message == null ? null : message.getGuild();
			else if(parameters[i].getType() == String.class) objects[i] = command;
			else if(parameters[i].getType() == Message.class) objects[i] = message;
			else if(parameters[i].getType() == JDA.class) objects[i] = botDiscord.getJda();
			else if(parameters[i].getType() == MessageChannel.class) objects[i] = message == null ? null : message.getChannel();
		}
		simpleCommand.getMethod().invoke(simpleCommand.getObject(), objects);
	}
}
