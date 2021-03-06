package com.leokom.chess;

import com.leokom.chess.engine.Side;
import com.leokom.chess.player.Player;
import com.leokom.chess.player.winboard.WinboardPlayer;
import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PlayerFactoryTest {
	private static String whiteProperty;
	private static String blackProperty;

	@BeforeClass
	public static void preserveSystemProperties() {
		whiteProperty = System.getProperty( "white" );
		blackProperty = System.getProperty( "black" );
	}

	@AfterClass
	public static void restoreSystemProperties() {
		//if any of them is null, @After method already cleared it.
		//setting null value of system property causes NPE
		if ( whiteProperty != null ) {
			System.setProperty( "white", whiteProperty );
		}

		if ( blackProperty != null ) {
			System.setProperty( "black", blackProperty );
		}
	}

	//ensure one test has no influence on another
	@Before
	@After
	public void clearSystemProperties() {
		System.clearProperty( "black" );
		System.clearProperty( "white" );
	}

	@Test
	public void noSystemPropertiesDefaultPlayerBlack() {
		final Player player = PlayerFactory.createPlayer( Side.BLACK );
		assertIsLegal( player );
	}

	@Test
	public void canSelectSimpleEngineForWhite() {
		System.setProperty( "white", "Simple" );

		final Player player = PlayerFactory.createPlayer( Side.WHITE );
		assertIsSimple( player );
	}

	private void assertIsSimple(Player player) {
		assertEquals( "LegalPlayer : SimpleBrain", player.name() );
	}

	@Test
	public void canSelectWinboardForBlack() {
		System.setProperty( "black", "Winboard" );

		final Player player = PlayerFactory.createPlayer( Side.BLACK );
		assertTrue( player instanceof WinboardPlayer );
	}

	@Test
	public void noSystemPropertiesDefaultPlayerWhite() {
		final Player player = PlayerFactory.createPlayer( Side.WHITE );
		assertTrue( player instanceof WinboardPlayer );
	}

	@Test
	public void legalSelected() {
		System.setProperty( "black", "Legal" );

		final Player player = PlayerFactory.createPlayer( Side.BLACK );
		assertIsLegal( player );
	}

	private void assertIsLegal( Player player ) {
		assertEquals( "LegalPlayer : DenormalizedBrain", player.name() );
	}

	@Test
	public void legalSelectedWhite() {
		System.setProperty( "white", "Legal" );

		final Player player = PlayerFactory.createPlayer( Side.WHITE );
		assertIsLegal( player );
	}
}