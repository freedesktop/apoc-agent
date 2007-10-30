/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either
 * the GNU General Public License Version 2 only ("GPL") or
 * the Common Development and Distribution License("CDDL")
 * (collectively, the "License"). You may not use this file
 * except in compliance with the License. You can obtain a copy
 * of the License at www.sun.com/CDDL or at COPYRIGHT. See the
 * License for the specific language governing permissions and
 * limitations under the License. When distributing the software,
 * include this License Header Notice in each file and include
 * the License file at /legal/license.txt. If applicable, add the
 * following below the License Header, with the fields enclosed
 * by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by
 * only the CDDL or only the GPL Version 2, indicate your
 * decision by adding "[Contributor] elects to include this
 * software in this distribution under the [CDDL or GPL
 * Version 2] license." If you don't indicate a single choice
 * of license, a recipient has the option to distribute your
 * version of this file under either the CDDL, the GPL Version
 * 2 or to extend the choice of license to its licensees as
 * provided above. However, if you add GPL Version 2 code and
 * therefore, elected the GPL Version 2 license, then the
 * option applies only if the new code is made subject to such
 * option by the copyright holder.
 */
package com.sun.apoc.daemon.apocd;

import com.sun.apoc.daemon.messaging.*;
import com.sun.apoc.daemon.misc.*;
import com.sun.apoc.daemon.transaction.*;

import java.io.*;

class ClientEventHandler implements EventHandler
{
	private final Client	    mClient;

	ClientEventHandler( final Client inClient)
	{
		mClient = inClient;
	}

	public void handleEvent( final Messenger inMessenger )
	{
		try
		{
			inMessenger.setClientChannel( mClient.getClientChannel() );
			final Transaction theTransaction =
				TransactionFactory
					.getInstance()
						.createTransaction(
							mClient,
							inMessenger.receiveRequest() );
			inMessenger.sendResponse( theTransaction.execute() );
		}
		catch( APOCException theException )
		{
			final Throwable theThrowable = theException.getCause();
			if ( theThrowable == null || !(theThrowable instanceof IOException))
			{	
				APOCLogger.throwing( "ClientEventHandler",
									 "handleEvent",
									 theException );
				inMessenger.sendResponse(
					MessageFactory.createResponse(
					APOCSymbols.sSymRespInvalidRequest, null, null ) );
			}
		}
	}		
}