package com.leokom.chess.player.legal;

import com.google.common.collect.Maps;

import java.util.EnumMap;
import java.util.Map;

/**
 * Author: Leonid
 * Date-time: 27.08.16 13:28
 */
class NormalizedEvaluatorFactory implements EvaluatorFactory {
	private static final Map< EvaluatorType, Evaluator > evaluators;
	static {
		//we keep references to instances of evaluators here
		//so they're practically singletons
		//any valid Evaluator must be stateless and thread-safe

		Map< EvaluatorType, Evaluator > evaluatorsMutable = new EnumMap<>( EvaluatorType.class );
		evaluatorsMutable.put( EvaluatorType.ATTACK, new AttackEvaluator() );
		evaluatorsMutable.put( EvaluatorType.CASTLING_SAFETY, new CastlingSafetyEvaluator() );
		evaluatorsMutable.put( EvaluatorType.CENTER_CONTROL, new CenterControlEvaluator() );
		evaluatorsMutable.put( EvaluatorType.CHECKMATE, new CheckmateEvaluator() );
		evaluatorsMutable.put( EvaluatorType.MATERIAL, new MaterialEvaluator() );
		evaluatorsMutable.put( EvaluatorType.MOBILITY, new MobilityEvaluator() );
		evaluatorsMutable.put( EvaluatorType.PROTECTION, new ProtectionEvaluator() );
		evaluatorsMutable.put( EvaluatorType.SPECIAL_MOVE, new SpecialMoveEvaluator() );

		evaluators = Maps.immutableEnumMap( evaluatorsMutable );
	}

	@Override
	public Evaluator get( EvaluatorType type ) {
		return evaluators.get( type );
	}
}
