package fr.neutronstars.botdiscord.event;

import fr.neutronstars.botdiscord.command.CommandMap;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class BotListener implements EventListener {

	private final CommandMap commandMap;
	
	public BotListener(CommandMap commandMap){
		this.commandMap = commandMap;
	}
	
	@Override
	public void onEvent(Event event) {
		System.out.println(event.getClass().getSimpleName());
		if(event instanceof MessageReceivedEvent) onMessage((MessageReceivedEvent)event); 
	}
	
	private void onMessage(MessageReceivedEvent event){
		if(event.getAuthor().equals(event.getJDA().getSelfUser())) return;
		
		String message = event.getMessage().getContent();
		if(message.startsWith(commandMap.getTag())){
			message = message.replaceFirst(commandMap.getTag(), "");
			if(commandMap.commandUser(event.getAuthor(), message, event.getMessage())){
				if(event.getTextChannel() != null && event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)){
					event.getMessage().delete().queue();
				}
			}
		}
	}
}
