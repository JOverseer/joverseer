package org.litesoft.p2pchat;

import java.io.*;
import java.net.*;


// Copyright Status:
//
// All Software available from LiteSoft.org (including this file) is
// hereby released into the public domain.
//
// It is free!  As in, you may use it freely in both commercial and
// non-commercial applications, bundle it with your software
// distribution, include it on a CD-ROM, list the source code in a book,
// mirror the documentation at your own web site, or use it in any other
// way you see fit.
//
// NO Warranty!
//
// All software is provided "as is".
//
// There is ABSOLUTELY NO WARRANTY OF ANY KIND: not for the design, fitness
// (for a particular purpose), level of errors (or lack thereof), or
// applicability of this software.  The entire risk as to the quality
// and performance of this software is with you.  Should this software
// prove defective, you assume the cost of all necessary servicing, repair
// or correction.
//
// In no event unless required by applicable law or agreed to in writing
// will any party who created or may modify and/or redistribute this
// software, be liable to you for damages, including any general,
// special, incidental or consequential damages arising out of the use or
// inability to use this software (including but not limited to loss of
// data or data being rendered inaccurate or losses sustained by you or
// third parties or a failure of this software to operate with any
// other programs), even if such holder or other party has been advised
// of the possibility of such damages.
//
// NOTE: Should you discover a bug, have a recogmendation for a change, wish
// to submit modifications, or wish to add new classes/functionality,
// please email them to:
//
//        changes@litesoft.org
//

/**
 * @author  Devin Smith and George Smith
 * @version 0.3 02/02/02 Added IllegalArgument.ifNull for all public params that may not be null
 * @version 0.2 01/28/02 Refactored and Added Licence
 * @version 0.1 12/27/01 Initial Version
 */
public class PendingPeerManager extends Thread implements ActivePeer.NewPeersSupport
{
    public interface ActivePeersSupport
    {
        boolean isAlreadyConnected( PeerInfo pPeerInfo );

        void addActivePeer( PeerInfo pPeerInfo , InputStream pInputStream , OutputStream pOutputStream );
    }

    private UserDialog zUserDialog;
    private PendingPeerLinkedList zPendingPeers = new PendingPeerLinkedList();
    private ActivePeersSupport zActivePeersSupport = null;

    public PendingPeerManager( UserDialog pUserDialog )
    {
        IllegalArgument.ifNull( "UserDialog" , zUserDialog = pUserDialog );
        zUserDialog.setPendingPeerManager( this );
    }

    public void start( ActivePeersSupport pActivePeersSupport )
    {
        IllegalArgument.ifNull( "ActivePeersSupport" , zActivePeersSupport = pActivePeersSupport );
        start();
    }

    public void addNewPeer( PeerInfo pInfo )
    {
        IllegalArgument.ifNull( "Info" , pInfo );
        zPendingPeers.add( null , pInfo );
    }

    public void addNewPeer( Socket pSocket )
    {
        IllegalArgument.ifNull( "Socket" , pSocket );
        InetAddress theirAddress = pSocket.getInetAddress();
        String theirName = theirAddress.getHostName();
        String theirIP = theirAddress.getHostAddress();
        String name = theirIP.equals( theirName ) ? null : "(Host: " + theirName + ")";
        zPendingPeers.add( pSocket , new PeerInfo( name , theirIP ) );
    }

    public void run()
    {
        while ( true )
        {
            handleNewPeerClient( zPendingPeers.next() );
        }
    }

    private void handleNewPeerClient( PendingPeerNode pPendingPeerNode )
    {
        PeerInfo peerInfo = pPendingPeerNode.getPeerInfo();
        if ( zActivePeersSupport.isAlreadyConnected( peerInfo ) )
            return;

        Socket socket = pPendingPeerNode.getSocket();
        if ( socket == null )
            if ( null == (socket = getPeerClientSocketFromAddresses( peerInfo )) )
            {
                zUserDialog.showConnectFailed( peerInfo );
                return;
            }
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try
        {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        }
        catch ( IOException e )
        {
            zUserDialog.showStreamsFailed( peerInfo );
            try
            {
                socket.close();
            }
            catch ( IOException ignore )
            {
            }
            return;
        }
        zActivePeersSupport.addActivePeer( peerInfo , inputStream , outputStream );
        zUserDialog.showConnect( peerInfo );
    }

    private Socket getPeerClientSocketFromAddresses( PeerInfo pPeerInfo )
    {
        String pPeerAddresses = pPeerInfo.getAddresses();
        int peerPort = pPeerInfo.getPort();
        for ( int i ; -1 != (i = pPeerAddresses.indexOf( ',' )) ; pPeerAddresses = pPeerAddresses.substring( i + 1 ) )
        {
            Socket s = getPeerClientSocket( pPeerAddresses.substring( 0 , i ) , peerPort );
            if ( s != null )
                return s;
        }
        return getPeerClientSocket( pPeerAddresses , peerPort );
    }

    private Socket getPeerClientSocket( String pPeerAddress , int pPeerPort )
    {
        try
        {
            return new Socket( pPeerAddress , pPeerPort );
        }
        catch ( IOException e )
        {
        }
        return null;
    }
}