package org.johnnei.javatorrent.bittorrent.encoding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class Bencode {
	private String bencoded;

	public Bencode(String bencoded) {
		this.bencoded = bencoded;
	}

	private char peekNextToken() {
		return bencoded.charAt(0);
	}

	private char getNextToken() {
		char c = bencoded.charAt(0);
		bencoded = bencoded.substring(1);
		return c;
	}

	public Map<String, Object> decodeDictionary() {
		if (getNextToken() != 'd') {
			throw new IllegalStateException("The next item in the bencoded string is not a Dictionary.");
		}
		HashMap<String, Object> dictionary = new HashMap<>();

		while (peekNextToken() != 'e') {
			String key = decodeString();
			char token = peekNextToken();
			Object value = null;
			if (token == 'i') {
				value = decodeInteger();
			} else if (token == 'l') {
				value = decodeList();
			} else if (token == 'd') {
				value = decodeDictionary();
			} else {
				value = decodeString();
			}
			dictionary.put(key, value);
		}
		getNextToken();
		return dictionary;
	}

	List<Object> decodeList() {
		if (getNextToken() != 'l') {
			throw new IllegalStateException("The next item in the bencoded string is not a List.");
		}
		ArrayList<Object> list = new ArrayList<Object>();
		while (peekNextToken() != 'e') {
			char token = peekNextToken();
			if (token == 'i') {
				list.add(decodeInteger());
			} else if (token == 'd') {
				list.add(decodeDictionary());
			} else if (token == 'l') {
				list.add(decodeList());
			} else {
				list.add(decodeString());
			}
		}
		getNextToken(); // Remove the end of this list from the bencoded string
		return list;
	}

	Object decodeInteger() {
		if (getNextToken() != 'i') {
			throw new IllegalStateException("The next item in the bencoded string is not an Integer.");
		}
		String[] data = bencoded.split("e");
		Object o = null;
		if (isInt(data[0])) {
			o = Integer.parseInt(data[0]);
		} else {
			o = Long.parseLong(data[0]);
		}
		bencoded = bencoded.substring(data[0].length() + 1);
		return o;
	}

	String decodeString() {
		String data[] = bencoded.split(":", 2);
		int headerLength = data[0].length() + 1; // Integer in base-10 string and 1 char for colon
		if (isInt(data[0])) {
			// Read string
			int stringLength = Integer.parseInt(data[0]);
			String string = data[1].substring(0, stringLength);

			// Consume information from bencoded string
			bencoded = bencoded.substring(headerLength + stringLength);
			return string;
		} else {
			throw new IllegalStateException("The next item in the bencoded string is not a String. " + bencoded);
		}
	}

	private boolean isInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public int remainingChars() {
		return bencoded.length();
	}

}
