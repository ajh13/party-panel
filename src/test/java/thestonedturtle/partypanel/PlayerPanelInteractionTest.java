package thestonedturtle.partypanel;

import javax.swing.SwingUtilities;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.party.PartyMember;
import org.junit.Test;
import thestonedturtle.partypanel.data.PartyPlayer;
import thestonedturtle.partypanel.ui.PlayerPanel;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class PlayerPanelInteractionTest
{
	@Test
	public void toggleExpandedReversesCardState() throws Exception
	{
		SwingUtilities.invokeAndWait(() ->
		{
			PartyMember member = new PartyMember(1L);
			member.setDisplayName("Alice");
			PlayerPanel card = new PlayerPanel(new PartyPlayer(member), mock(PartyPanelConfig.class),
				mock(SpriteManager.class), mock(ItemManager.class));
			assertFalse(card.isShowInfo());
			card.toggleExpanded();
			assertTrue(card.isShowInfo());
			card.toggleExpanded();
			assertFalse(card.isShowInfo());
		});
	}
}
