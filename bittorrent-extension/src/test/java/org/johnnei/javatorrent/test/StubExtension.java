package org.johnnei.javatorrent.test;

import java.util.Map;

import org.johnnei.javatorrent.network.InStream;
import org.johnnei.javatorrent.network.protocol.IMessage;
import org.johnnei.javatorrent.protocol.IExtension;
import org.johnnei.javatorrent.torrent.download.peer.Peer;
import org.johnnei.javatorrent.torrent.encoding.Bencoder;

public class StubExtension implements IExtension {

	private final String name;

	public StubExtension(String name) {
		this.name = name;
	}

	@Override
	public IMessage getMessage(InStream data) {
		return new StubMessage();
	}

	@Override
	public void addHandshakeMetadata(Peer peer, Bencoder bencoder) {
		/* Don't do anything in this stub */
	}

	@Override
	public void processHandshakeMetadata(Peer peer, Map<String, Object> dictionary, Map<?, ?> mEntry) {
		/* Don't do anything in this stub */
	}

	@Override
	public String getExtensionName() {
		return name;
	}

}