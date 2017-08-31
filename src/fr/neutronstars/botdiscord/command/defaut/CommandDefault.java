package fr.neutronstars.botdiscord.command.defaut;

import java.awt.Color;

import fr.neutronstars.botdiscord.BotDiscord;
import fr.neutronstars.botdiscord.command.Command;
import fr.neutronstars.botdiscord.command.Command.ExecutorType;
import fr.neutronstars.botdiscord.command.CommandMap;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game.GameType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.impl.GameImpl;

public class CommandDefault {
	
	private final BotDiscord botDiscord;
	private final CommandMap commandMap;
	
	public CommandDefault(BotDiscord botDiscord, CommandMap commandMap){
		this.botDiscord = botDiscord;
		this.commandMap = commandMap;
	}
	
	@Command(name="stop",type=ExecutorType.CONSOLE)
	private void stop(){
		botDiscord.setRunning(false);
	}
	
	@Command(name="power",power=150)
	private void power(User user, MessageChannel channel, Message message, String[] args)
	{
		if(args.length == 0 || message.getMentionedUsers().size() == 0)
		{
			channel.sendMessage("power <Power> <@User>").queue();
			return;
		}
		
		int power = 0;
		try{
			power = Integer.parseInt(args[0]);
		}catch(NumberFormatException nfe){
			channel.sendMessage("Le power doit être un nombre.").queue();
			return;
		}
		
		User target = message.getMentionedUsers().get(0);
		commandMap.addUserPower(target, power);
		channel.sendMessage("Le power de "+target.getAsMention()+" est maintenant de "+power).queue();
	}
	
	@Command(name="info",type=ExecutorType.USER)
	private void info(User user, MessageChannel channel){
		if(channel instanceof TextChannel){
			TextChannel textChannel = (TextChannel)channel;
			if(!textChannel.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_EMBED_LINKS)) return;
		}
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setAuthor(user.getName(), null, user.getAvatarUrl()+"?size=256");
		builder.setTitle("Informations");
		builder.setDescription("[>](1) le message a été envoyé depuis le channel "+channel.getName());
		builder.setColor(Color.green);
		
		channel.sendMessage(builder.build()).queue();
	}
	
	@Command(name="game",power=100)
	private void game(JDA jda, String[] args){
		StringBuilder builder = new StringBuilder();
		for(String str : args){
			if(builder.length() > 0) builder.append(" ");
			builder.append(str);
		}
		
		jda.getPresence().setGame(new GameImpl(builder.toString(), null, GameType.DEFAULT));
	}
}
