package io.brutus.experiments.dynamicproxy;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * A small proof-of-concept plugin to test the feasibility of dynamically 
 * adding/removing servers from BungeeCord.
 * <p>
 * Date Created: May 27, 2014
 * 
 * @author Brutus
 *
 */
public class DynamicProxyPlugin extends Plugin {

	//the info of the test server that will be added dynamically
	private ServerInfo info;

	@Override
	public void onEnable() 
	{
		init();

		getLogger().info("------------[DynamicProxy]------------");

		logServers();

		//adds the test server to the list of active servers.
		// No waiting is implemented in this example and it is removed right
		// away, but I have tested it and you can actually connect to the
		// server added here.
		addTestServer();

		logServers();

		//removes the test server from the list of active servers.
		// The method and steps are inspired by the plugin DynamicBungee:
		// http://www.spigotmc.org/resources/dynamicbungee.451/
		removeTestServer();
		
		logServers();

		getLogger().info("------------[DynamicProxy]------------");
	}
	

	
	private void init() 
	{
		info = getProxy().constructServerInfo(
				"testserver",
				new InetSocketAddress("localhost", 25587),
				"This is the motd for the test server",
				false);
	}
	

	/**
	 * Adds the test server to BungeeCord.
	 */
	private void addTestServer() {

		getLogger().info("Adding the test server...");
		
		getProxy().getServers().put(info.getName(), info);

	}


	/**
	 * Removes the test server from BungeeCord.
	 */
	private void removeTestServer() {
		
		getLogger().info("Removing the test server...");
		
		TextComponent reason = new TextComponent("You were kicked because "
				+ "the test server was removed.");
		
		for (ProxiedPlayer player : info.getPlayers()) {
            player.disconnect(reason);
        }
		
		getProxy().getServers().remove(info.getName());
	}

	/**
	 * Logs the current servers in BungeeCord to the console.
	 */
	private void logServers() 
	{
		
		getLogger().info("Currently connected servers:");
		
		Map<String, ServerInfo> servers = getProxy().getServers();
		if(servers == null) 
		{
			return;
		}

		for(Map.Entry<String, ServerInfo> ent : servers.entrySet()) 
		{
			logServer(ent);
		}
	}


	private void logServer(Entry<String, ServerInfo> ent) 
	{
		ServerInfo si = ent.getValue();
		getLogger().info("(" + ent.getKey() + ") name: " + si.getName() 
				+ ", address: " + si.getAddress()
				+ ", motd: " + si.getMotd() 
				+ ", players: " + stringFromPlayers(si.getPlayers())
				);
	}

	private String stringFromPlayers(Collection<ProxiedPlayer> players) 
	{
		String ret = "";

		if(players != null) for(ProxiedPlayer pp : players) 
		{
			ret += pp.getName() + ", ";
		}

		if(ret.length() > 0) 
		{
			ret.substring(0, ret.length() - 2);
		}

		return ret;
	}

}
