package com.leokom.chess.engine;


import java.util.stream.IntStream;

/**
 * Create position in fluent-interface style
 *
 *
 * Author: Leonid
 * Date-time: 28.02.15 22:05
 */
public class PositionBuilder {
	private final Position position;

	public PositionBuilder() {
		//TODO: not the best decision to hard-code side here
		//better delay position construction till build() method call
		position = new Position( Side.WHITE );
	}

	public PositionBuilder add( Side side, String square, PieceType pieceType ) {
		position.add( side, square, pieceType );
		return this;
	}

	public PositionBuilder setSideOf( String square ) {
		return setSide( position.getSide( square ) );
	}

	public PositionBuilder setSide( Side side ) {
		position.setSideToMove( side );
		return this;
	}

	public Position build() {
		return position;
	}

	public PositionBuilder addPawn( Side side, String square ) {
		position.addPawn( side, square );
		return this;
	}

	PositionBuilder addQueen( Side side, String square ) {
		position.addQueen( side, square );
		return this;
	}

	public Position move( String from, String to ) {
		setSideOf( from );
		return position.move( from, to );
	}

	PositionBuilder setEnPassantFile( Character enPassantFile ) {
		position.setEnPassantFile( enPassantFile );
		return this;
	}

	public PositionBuilder rules( Rules rules ) {
		position.setRules( rules );
		return this;
	}

	public PositionBuilder pliesCount( int pliesCount ) {
		//trick to avoid creation of position.setMovesCount
		position.restartObligatoryDrawCounter();
		IntStream.rangeClosed( 1, pliesCount ).forEach( counter -> position.incPliesCount() );
		return this;
	}

	public PositionBuilder winningSide( Side side ) {
		position.setTerminal( side );
		return this;
	}

	public PositionBuilder draw() {
		this.position.setTerminal( null );
		//see the reason of this in PositionBuilder
		this.position.setSideToMove( null );
		return this;
	}
}
