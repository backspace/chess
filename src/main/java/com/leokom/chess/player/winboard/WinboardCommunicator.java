package com.leokom.chess.player.winboard;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * Low-level communication engine with some Winboard 'server'
 * By Winboard server I mean the GUI implementing Winboard protocol
 *
 * This low-level communicator simply sends and receives commands
 * But doesn't do any sophisticated processing.
 * Author: Leonid
 * Date-time: 20.08.12 16:13
 */
class WinboardCommunicator implements Communicator {
	/**
	 * I don't set up UTF-8 since specification says we'll get only latin characters and digits
	 * US-ASCII is 7-bit latin charset
	 */
	private static final String INPUT_ENCODING = "US-ASCII";
	private final BufferedReader reader;
    private final PrintStream outputStream;
	private Logger logger = LogManager.getLogger( this.getClass() );

    /**
     * Create the winboard-commander with default dependencies
	 * We don't need extra flexibility of injecting in/out streams
	 * till really proved by tests
	 */
    public WinboardCommunicator() {
		//TODO: if in any application place we'll use System.out.println or System.in.read
		//this may damage Winboard behaviour. The easiest way to fix it is to redirect System.out, System.in calls
		//to anything else (Logger?) and use the 'standard' in/out only inside WinboardPlayer

		this.reader = getReaderFromStream( System.in );
		this.outputStream = System.out;
    }

	private static BufferedReader getReaderFromStream( InputStream inputStream ) {
		final InputStreamReader streamReader;
		try {
			streamReader = new InputStreamReader( inputStream, INPUT_ENCODING );
		} catch ( UnsupportedEncodingException e ) {
			final InstantiationError instantiationError = new InstantiationError(
				"Java installation seems incorrect. It doesn't support standard encoding = " + INPUT_ENCODING );
			instantiationError.initCause( e );
			throw instantiationError;
		}
		//TODO: think about buffers, they're not recommended to use
		return new BufferedReader( streamReader );
	}

	@Override
    public void send( String command ) {
		logger.info( "Sent: " + command );
		//From Spec : All your output to xboard must be in complete lines; any form of prompt or partial line will cause problems.
        outputStream.println( command );
    }

    @Override
    public String receive() {
        try {
			//TODO: PERFORMANCE: profiler shows this readLine is the bottleneck
			//from CPU POV
			//http://stackoverflow.com/questions/7281385/bufferedreader-read-eating-100-of-cpu
			final String line = reader.readLine();
			logger.info( "Received: " + line );
			return line;
        } catch ( IOException e ) {
			//avoid propagating internal exception to the method signature
            throw new RuntimeException( e );
        }
    }
}
