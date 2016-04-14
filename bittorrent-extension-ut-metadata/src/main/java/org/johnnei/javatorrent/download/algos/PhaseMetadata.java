package org.johnnei.javatorrent.download.algos;

import java.io.File;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.johnnei.javatorrent.TorrentClient;
import org.johnnei.javatorrent.bittorrent.protocol.messages.IMessage;
import org.johnnei.javatorrent.protocol.UTMetadata;
import org.johnnei.javatorrent.protocol.extension.PeerExtensions;
import org.johnnei.javatorrent.protocol.messages.extension.MessageExtension;
import org.johnnei.javatorrent.protocol.messages.ut_metadata.MessageRequest;
import org.johnnei.javatorrent.torrent.MetadataFileSet;
import org.johnnei.javatorrent.torrent.Torrent;
import org.johnnei.javatorrent.torrent.TorrentFileSet;
import org.johnnei.javatorrent.torrent.files.Block;
import org.johnnei.javatorrent.torrent.files.BlockStatus;
import org.johnnei.javatorrent.torrent.files.Piece;
import org.johnnei.javatorrent.torrent.peer.Peer;
import org.johnnei.javatorrent.torrent.peer.PeerDirection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhaseMetadata extends AMetadataPhase {

	private static final Logger LOGGER = LoggerFactory.getLogger(PhaseMetadata.class);

	private final File downloadFolder;

	public PhaseMetadata(TorrentClient torrentClient, Torrent torrent, File metadataFile, File downloadFolder) {
		super(torrentClient, torrent, metadataFile);
		this.downloadFolder = downloadFolder;
	}

	@Override
	public boolean isDone() {
		if (foundMatchingFile) {
			return true;
		}

		Optional<MetadataFileSet> metadataFileSet = torrent.getMetadata();
		if (!metadataFileSet.isPresent()) {
			return false;
		}
		return metadataFileSet.get().isDone();
	}

	@Override
	public void process() {
		for (Peer peer : getRelevantPeers(torrent.getPeers())) {
			Optional<Piece> pieceOptional = torrent.getPieceSelector().getPieceForPeer(peer);
			if (!pieceOptional.isPresent()) {
				continue;
			}

			Piece piece = pieceOptional.get();
			while (piece.hasBlockWithStatus(BlockStatus.Needed) && peer.getFreeWorkTime() > 0) {
				Optional<Block> blockOptional = piece.getRequestBlock();
				if (!blockOptional.isPresent()) {
					break;
				}

				// The getRelevantPeers call only returns peers which support the extensions and has ut_metadata.
				PeerExtensions peerExtensions = peer.getModuleInfo(PeerExtensions.class).get();
				Block block = blockOptional.get();
				IMessage message = new MessageExtension(peerExtensions.getExtensionId(UTMetadata.NAME), new MessageRequest(block.getIndex()));
				peer.addBlockRequest(piece.getIndex(), block.getIndex(), block.getSize(), PeerDirection.Download);
				peer.getBitTorrentSocket().enqueueMessage(message);
			}
		}
	}

	@Override
	public void onPhaseEnter() {
		super.onPhaseEnter();
		torrent.setPieceSelector(new MetadataSelect(torrent));
	}

	@Override
	public void onPhaseExit() {
		torrent.setFileSet(new TorrentFileSet(metadataFile, downloadFolder));
		LOGGER.info("Metadata download completed");
	}

	private Collection<Peer> getRelevantPeers(Collection<Peer> peers) {
		return peers.stream()
				.filter(this::hasUtMetadataExtension)
				.collect(Collectors.toList());
	}

	private boolean hasUtMetadataExtension(Peer peer) {
		Optional<PeerExtensions> peerExtensions = peer.getModuleInfo(PeerExtensions.class);
		if (!peerExtensions.isPresent()) {
			return false;
		}
		return peerExtensions.get().hasExtension(UTMetadata.NAME);
	}
}
