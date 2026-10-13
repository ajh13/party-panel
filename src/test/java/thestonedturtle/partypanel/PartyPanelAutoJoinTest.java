package thestonedturtle.partypanel;

import java.lang.reflect.Field;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.party.PartyMember;
import net.runelite.client.party.PartyService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PartyPanelAutoJoinTest
{
	private PartyPanelPlugin plugin;
	private PartyPanelConfig config;
	private PartyService partyService;

	@Before
	public void setUp() throws Exception
	{
		plugin = new PartyPanelPlugin();
		config = mock(PartyPanelConfig.class);
		partyService = mock(PartyService.class);
		setField("config", config);
		setField("partyService", partyService);
		setField("panel", mock(PartyPanel.class));
		setField("client", mock(Client.class));
		setField("clientThread", mock(ClientThread.class));
	}

	@Test
	public void emptyPassphraseDisablesAutoJoin() throws Exception
	{
		assertEquals("", new PartyPanelConfig()
		{
			@Override
			public void setPreviousPartyId(String id)
			{
			}
		}.autoJoinPartyPassphrase());
		when(config.autoJoinPartyPassphrase()).thenReturn("");
		login();
		verify(partyService, never()).changeParty(org.mockito.ArgumentMatchers.anyString());
	}

	@Test
	public void joinsConfiguredPartyOnceOnLogin() throws Exception
	{
		when(config.autoJoinPartyPassphrase()).thenReturn("  My Friends  ");
		login();
		login();
		verify(partyService).changeParty("My-Friends");
	}

	@Test
	public void skipsEmptyAndInvalidPassphrases() throws Exception
	{
		when(config.autoJoinPartyPassphrase()).thenReturn("  ", "bad!party");
		login();
		loginScreen();
		login();
		verify(partyService, never()).changeParty(org.mockito.ArgumentMatchers.anyString());
	}

	@Test
	public void doesNotReplaceExistingParty() throws Exception
	{
		when(config.autoJoinPartyPassphrase()).thenReturn("friends");
		when(partyService.isInParty()).thenReturn(true);
		when(partyService.getLocalMember()).thenReturn(mock(PartyMember.class));
		login();
		verify(partyService, never()).changeParty(org.mockito.ArgumentMatchers.anyString());
	}

	@Test
	public void manualLeaveDoesNotRejoinUntilNextLogin() throws Exception
	{
		when(config.autoJoinPartyPassphrase()).thenReturn("friends");
		login();
		plugin.leaveParty();
		login();
		verify(partyService).changeParty("friends");
		loginScreen();
		login();
		verify(partyService, org.mockito.Mockito.times(2)).changeParty("friends");
	}

	private void login()
	{
		gameState(GameState.LOGGED_IN);
	}

	private void loginScreen()
	{
		gameState(GameState.LOGIN_SCREEN);
	}

	private void gameState(GameState state)
	{
		GameStateChanged event = new GameStateChanged();
		event.setGameState(state);
		plugin.onGameStateChanged(event);
	}

	private void setField(String name, Object value) throws Exception
	{
		Field field = PartyPanelPlugin.class.getDeclaredField(name);
		field.setAccessible(true);
		field.set(plugin, value);
	}
}
