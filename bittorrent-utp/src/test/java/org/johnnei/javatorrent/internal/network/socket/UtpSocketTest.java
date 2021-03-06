package org.johnnei.javatorrent.internal.network.socket;

import java.net.InetSocketAddress;

import org.johnnei.javatorrent.internal.utp.protocol.ConnectionState;
import org.johnnei.javatorrent.internal.utp.protocol.UtpMultiplexer;
import org.johnnei.javatorrent.internal.utp.protocol.UtpOutputStream;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests {@link UtpSocket}
 */
public class UtpSocketTest {

	private UtpSocket cut;

	private UtpSocketImpl.Builder socketFactoryMock;
	private UtpSocketImpl socketMock;
	private UtpMultiplexer multiplexerMock;

	@Before
	public void setUp() {
		socketMock = mock(UtpSocketImpl.class);
		socketFactoryMock = mock(UtpSocketImpl.Builder.class);
		multiplexerMock = mock(UtpMultiplexer.class);

		cut = new UtpSocket(multiplexerMock);
		Whitebox.setInternalState(cut, UtpSocketImpl.class, socketMock);
		Whitebox.setInternalState(cut, UtpSocketImpl.Builder.class, socketFactoryMock);
	}

	@Test
	public void testGetInputStream() throws Exception {
		cut.getInputStream();

		verify(socketMock).getInputStream();
	}

	@Test
	public void testGetOutputStream() throws Exception {
		cut.getOutputStream();

		verify(socketMock).getOutputStream();
	}

	@Test
	public void testClose() throws Exception {
		cut.close();

		verify(socketMock).close();
	}

	@Test
	public void testIsInputShutdown() {
		when(socketMock.isInputShutdown()).thenReturn(true);

		assertTrue("Input stream is closed, must be reported as such", cut.isInputShutdown());
	}

	@Test
	public void testIsInputShutdownWhenFalse() {
		when(socketMock.isInputShutdown()).thenReturn(false);

		assertFalse("Input stream is NOT closed, must be reported as such", cut.isInputShutdown());
	}

	@Test
	public void testIsOutputShutdown() {
		when(socketMock.isOutputShutdown()).thenReturn(true);

		assertTrue("Output stream is closed, must be reported as such", cut.isOutputShutdown());
	}

	@Test
	public void testIsOutputShutdownWhenFalse() {
		when(socketMock.isOutputShutdown()).thenReturn(false);

		assertFalse("Output stream is NOT closed, must be reported as such", cut.isOutputShutdown());
	}

	@Test
	public void testFlush() throws Exception {
		UtpOutputStream outputStreamMock = mock(UtpOutputStream.class);

		when(socketMock.getOutputStream()).thenReturn(outputStreamMock);

		cut.flush();

		verify(outputStreamMock).flush();
	}

	@Test
	public void testConnect() throws Exception {
		when(socketFactoryMock.build()).thenReturn(socketMock);
		when(multiplexerMock.registerSocket(same(socketMock))).thenReturn(false).thenReturn(true);

		InetSocketAddress socketAddress = new InetSocketAddress("localhost", 27960);
		cut.connect(socketAddress);

		verify(socketMock).connect(same(socketAddress));
	}

	@Test
	public void isClosedWhenUnconnected() throws Exception {
		// Remove the mock
		Whitebox.setInternalState(cut, UtpSocketImpl.class, (UtpSocketImpl) null);

		assertTrue(cut.isClosed());
	}

	@Test
	public void testIsClosedWhenConnecting() throws Exception {
		when(socketMock.getConnectionState()).thenReturn(ConnectionState.CONNECTING);
		assertFalse(cut.isClosed());
	}

	@Test
	public void testIsClosedWhenConnected() throws Exception {
		when(socketMock.getConnectionState()).thenReturn(ConnectionState.CONNECTED);
		assertFalse(cut.isClosed());
	}

	@Test
	public void testIsClosedWhenDisconnecting() throws Exception {
		when(socketMock.getConnectionState()).thenReturn(ConnectionState.DISCONNECTING);
		assertFalse(cut.isClosed());
	}

	@Test
	public void testIsClosedWhenDisconnected() throws Exception {
		when(socketMock.getConnectionState()).thenReturn(ConnectionState.CLOSED);
		assertTrue(cut.isClosed());
	}

}