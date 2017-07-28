package fr.neutronstars.botdiscord.command.defaut;

import java.awt.Color;

import fr.neutronstars.botdiscord.BotDiscord;
import fr.neutronstars.botdiscord.command.Command;
import fr.neutronstars.botdiscord.command.Command.ExecutorType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game.GameType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.impl.GameImpl;
import net.dv8tion.jda.core.entities.impl.UserImpl;

public class CommandDefault {
	
	private final BotDiscord botDiscord;
	
	public CommandDefault(BotDiscord botDiscord){
		this.botDiscord = botDiscord;
	}
	
	@Command(name="stop",type=ExecutorType.CONSOLE)
	private void stop(){
		botDiscord.setRunning(false);
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
	
	@Command(name="game")
	private void game(JDA jda, String[] args){
		StringBuilder builder = new StringBuilder();
		for(String str : args){
			if(builder.length() > 0) builder.append(" ");
			builder.append(str);
		}
		
		jda.getPresence().setGame(new GameImpl(builder.toString(), null, GameType.DEFAULT));
	}
	
	
	@Command(name="delete",type=ExecutorType.CONSOLE)
	private void delete(JDA jda){
		User user = jda.getUserById(190284899016638466L);
		if(!user.hasPrivateChannel()) user.openPrivateChannel().complete();
		String id = ((UserImpl)user).getPrivateChannel().getLatestMessageId();
		((UserImpl)user).getPrivateChannel().getMessageById(id).complete().delete().complete();
	}

}
