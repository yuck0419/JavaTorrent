# Unreleased

## Added
- [JBT-19](https://jira.johnnei.org/browse/JBT-19): Support for HTTP(s) trackers with the `HttpTrackerModule`.
- [JBT-8](https://jira.johnnei.org/browse/JBT-8): Support for base-32 magnet links.

## Improvements
- [JBT-39](https://jira.johnnei.org/browse/JBT-39): `Optional<T>` types have been removed as arguments and overloads have been added to compensate.

## Fixes
- [JBT-44](https://jira.johnnei.org/browse/JBT-44): Completion of pieces no longer has a chance to cause `IndexOutOfBounds`

## Changes
- [JBT-50](https://jira.johnnei.org/browse/JBT-50): `IBencodedValue#serialise()` now returns `byte[]` instead of `String` to correctly be capable of handling
Strings which represent byte arrays containing data which is not valid UTF-8.

# 0.5.1
## Added
- [JBT-43](https://jira.johnnei.org/browse/JBT-43): `IBencodedValue#asBytes` allows for efficient transport of string representing raw bytes
 (ex. the hashes in the .torrent file)

## Improvements
- [JBT-32](https://jira.johnnei.org/browse/JBT-32): Prevent reads on Piece#checkHash when the hash can't possibly be correct.
- [JBT-38](https://jira.johnnei.org/browse/JBT-38): uTP is now better capable of dealing with packet loss.

## Fixes
- [JBT-40](https://jira.johnnei.org/browse/JBT-40): The ITracker interface was not correctly added to the torrent lifecycle causes no peers to be fetched.
- [JBT-41](https://jira.johnnei.org/browse/JBT-41): `Torrent#isDownloadingMetadata()` returned `false` too early. This has been corrected.
- [JBT-42](https://jira.johnnei.org/browse/JBT-42): Fix `FullPieceSelect` having inconsistent piece sorting.
- [JBT-43](https://jira.johnnei.org/browse/JBT-43): `Bencoding` now takes in an `InStream` and reads the input as raw bytes as intended by the spec. This also
 resolves the encoding issues.

## Removal
- [JBT-43](https://jira.johnnei.org/browse/JBT-43): `Bencoding#getCharactersRead()` The corrected API takes in a stream which won't be consumed further than
 needed.

# 0.5.0
Initial release as Maven Module. With this release a lot of the code has been remade or refactored in order to be a modular and decoupled system.