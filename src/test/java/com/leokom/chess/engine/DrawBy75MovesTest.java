package com.leokom.chess.engine;

import org.jooq.lambda.Seq;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/**
 * Author: Leonid
 * Date-time: 19.03.16 21:56
 */
public class DrawBy75MovesTest {
	//a multiplier that is perceived as ('we'll definitely overcome limit
	//of 75 moves (150 semi-moves?). It will be multiplied by 4 semi-moves
	private static final int BIG_AMOUNT_OF_TIMES = 100;

	/* Before 1 July 2014, consecutive 75 moves from both players
			 * didn't cause automatic draw. No limits if neither player claims draw */

	@Test
	public void rulesBeforeJuly2014() {
		final Position initialPosition = Position.getInitialPosition( Rules.BEFORE_JULY_2014 );

		//knights moving forth and back
		//first usage of cool library jOOλ
		//knights moving forth and back
		final Seq<Move> moves = Seq.of( new Move( "g1", "f3" ), new Move( "g8", "f6" ),
				new Move( "f3", "g1" ), new Move( "f6", "g8" ) )
				.cycle( BIG_AMOUNT_OF_TIMES );


		new PositionMover( initialPosition, moves )
			.afterMove( assertNonTerminal() )
			.run();
	}

	@Test
	public void rulesAfterJuly2014() {
		final Position initialPosition = Position.getInitialPosition( Rules.DEFAULT );

		//knights moving forth and back
		//75 * 2 = 150 / 4 = 37.5
		final int iterationsCloseToEnd = 37;
		final Seq<Move> moves = Seq.of( new Move( "g1", "f3" ), new Move( "g8", "f6" ),
				new Move( "f3", "g1" ), new Move( "f6", "g8" ) )
				.cycle( iterationsCloseToEnd )
				.append( new Move( "g1", "f3" ), new Move( "g8", "f6" ) );

		final Position result = new PositionMover( initialPosition, moves ).run();

		Assert.assertTrue( result.isTerminal() );
	}

	@Test
	public void enPassantResetsCounter() {
		Rules rules = getRules( 1 );

		Position initialPosition = Position.getInitialPosition( rules );
		Seq< Move > moves = Seq.of(
				new Move( "e2", "e4" ),
				new Move( "a7", "a5" ),
				new Move( "e4", "e5" ),
				new Move( "f7", "f5" ),
				new Move( "e5", "f6" ) );

		final Position result = new PositionMover( initialPosition, moves )
				.afterMove( assertNonTerminal() )
				.run();

		Assert.assertTrue( result.move( "g8", "h6" ).move( "g1", "f3" ).isTerminal() );
	}

	private Consumer<Position> assertNonTerminal() {
		return position -> assertFalse( position.isTerminal() );
	}

	@Test
	public void newRulesInjection() {
		Rules rules = getRules( 1 );

		Position position =
			Position.getInitialPosition( rules )
			.move( new Move( "g1", "f3" ) )
			.move( new Move( "g8", "f6" ) );

		Assert.assertTrue( position.isTerminal() );
	}

	@Test
	public void positionStateCorrect() {
		Rules rules = getRules( 1 );
		Position position =
				Position.getInitialPosition( rules )
						.move( new Move( "g1", "f3" ) )
						.move( new Move( "g8", "f6" ) );

		assertNull( position.getWinningSide() );
		assertEquals( new Piece( PieceType.KNIGHT, Side.BLACK ), position.getPiece( "f6" ) );
	}
	@Test
	public void offerDrawIsNotCounted() {
		Rules rules = getRules( 1 );
		Position position =
				Position.getInitialPosition( rules )
						.move( new Move( "g1", "f3" ) )
						.move( Move.OFFER_DRAW );

		assertFalse( position.isTerminal() );
	}

	@Test
	public void resignIsStillResign() {
		Rules rules = getRules( 1 );
		Position position =
				Position.getInitialPosition( rules )
						.move( new Move( "g1", "f3" ) )
						.move( Move.RESIGN );

		Assert.assertTrue( position.isTerminal() );
		assertEquals( Side.WHITE, position.getWinningSide() );
	}

	@Test
	public void pawnMovementResetsCounter() {
		Rules rules = getRules( 1 );
		Position position =
				Position.getInitialPosition( rules )
						.move( new Move( "e2", "e4" ) )
						.move( new Move( "g8", "f6" ) );

		Assert.assertFalse( position.isTerminal() );
	}

	//pawn doMove is not the end, it just starts new counter
	@Test
	public void counterStillWorksAfterPawnMove() {
		Rules rules = getRules( 1 );
		Position position =
				Position.getInitialPosition( rules )
						.move( new Move( "e2", "e4" ) )
						.move( new Move( "g8", "f6" ) )
						.move( new Move( "g1", "f3" ) );

		Assert.assertTrue( position.isTerminal() );
	}

	@Test
	public void countOfMovesRespected() {
		Rules rules = getRules( 2 );
		Position position =
				Position.getInitialPosition( rules )
						.move( new Move( "g1", "f3" ) )
						.move( new Move( "g8", "f6" ) );

		assertFalse( position.isTerminal() );
	}

	@Test
	public void countOfMovesRespectedFully() {
		Rules rules = getRules( 2 );
		Position position =
				Position.getInitialPosition( rules )
						.move( new Move( "g1", "f3" ) )
						.move( new Move( "g8", "f6" ) )
						.move( new Move( "f3", "g1" ) )
						.move( new Move( "f6", "g8" ) );

		Assert.assertTrue( position.isTerminal() );
	}

	@Test
	public void countOfMovesForBlackRespected() {
		final Position position = new PositionBuilder()
				.rules( getRules( 1 ) )
				.add( Side.WHITE, "g1", PieceType.KNIGHT )
				.add( Side.BLACK, "g8", PieceType.KNIGHT )
				.setSide( Side.BLACK )
				.build();

		final Position result = position
				.move( new Move( "g8", "f6" ) )
				.move( new Move( "g1", "f3" ) );

		Assert.assertTrue( result.isTerminal() );
	}

	@Test
	public void captureResetsCount() {
		Rules rules = getRules( 1 );
		Position position =
				Position.getInitialPosition( rules )
						.move( new Move( "e2", "e4" ) ) //pawn
						.move( new Move( "g8", "f6" ) )
						.move( new Move( "d2", "d4" ) ) //pawn
						.move( new Move( "f6", "e4" ) ) //capture
						.move( new Move( "g1", "f3" ) );

		Assert.assertFalse( position.isTerminal() );
	}

	//If the last move resulted in checkmate, that shall take precedence.
	@Test
	public void checkmateAtLastMomentIsCheckmate() {
		Position position = new PositionBuilder()
				.add( Side.WHITE, "a1", PieceType.ROOK )
				.add( Side.WHITE, "b7", PieceType.ROOK )
				.add( Side.BLACK, "h7", PieceType.KING )
				.setSide( Side.BLACK )
				.rules( new RulesBuilder().movesTillDraw( 1 ).build() )
				.build();

		final Position result = position.move( "h7", "h8" ).move( "a1", "a8" );
		assertEquals( Side.WHITE, result.getWinningSide() );
	}

	@Test
	public void captureWithPromotionResetsCount() {
		final Position position = new PositionBuilder()
				.rules( getRules( 1 ) )
				.add( Side.WHITE, "f7", PieceType.PAWN )
				.add( Side.BLACK, "g8", PieceType.KNIGHT )
				.add( Side.BLACK, "a1", PieceType.KING )
				.add( Side.WHITE, "a8", PieceType.KING )
				.setSide( Side.WHITE )
				.build();

		final Position result = position
				.move( new Move( "f7", "g8R" ) )
				.move( new Move( "a1", "a2" ) );

		Assert.assertFalse( result.isTerminal() );
	}

	private Rules getRules( int smallestPossibleCount ) {
		return new RulesBuilder().movesTillDraw( smallestPossibleCount ).build();
	}

	/*
	 * + Ability to keep old behaviour (unlimited rules < 07.2014)
	 * + Ability to specify 75 by not hard-coding it (inject it)
	 * + Make 75 moves rule - default
	 *
	 * + Special moves are definitely not counted (specifically OFFER_DRAW)
	 * + other special moves cause creation of terminal position,
	 * anyway RESIGN on the 75'th doMove is still resign !
	 *
	 * + position should be terminal
	 * + reason : DRAW /when winningSide == null it's enough
	 * ? detailed reason? (draw by 75 moves). This info is not needed so far
	 *
	 * + a capture resets count
	 * + en passant capture resets count (done through pawn movement anyway, but position.isCapture also improved)
	 * + pawn movement resets count
	 * + non-capture & non-pawn : increases count
	 * + take into account semi-moves! BLACK start?
	 *
	 * + Send to Winboard
	 * + Receive from Winboard
	 * + Send to LegalPlayer
	 * + Receive from LegalPlayer
	 *
	 * + If the last move resulted in checkmate, that shall take precedence.
	 */
}
