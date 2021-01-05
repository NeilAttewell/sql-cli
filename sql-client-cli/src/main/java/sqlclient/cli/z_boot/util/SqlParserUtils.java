package sqlclient.cli.z_boot.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import sqlclient.cli.SpecialCharacterRegistry;
import sqlclient.cli.domain.Query;

/**
 * @author Neil Attewell
 */
public class SqlParserUtils {
	public static List<Query> parse(String input, SpecialCharacterRegistry registry) {
		List<Query> out = new  ArrayList<>();
		String currentPart = "";
		String currentWrapper = null;
		List<String> parts = new ArrayList<>();
		for(int i = 0 ; i < input.length() ; i++) {
			String wrapper = getIfWrapperAtPosition(input, i, currentWrapper, registry);
			if(currentWrapper != null && wrapper != null) {
				currentPart += (char) input.charAt(i);
				currentWrapper=null;
				parts.add(currentPart);
				currentPart = "";
				continue;
			}
			if(wrapper != null) {
				parts.add(currentPart);
				currentPart = "";

				currentWrapper = wrapper;
				currentPart += (char) input.charAt(i);
				continue;
			}
			String split = getIfAtSplit(input, i, registry);
			if(split == null) {
				currentPart += (char) input.charAt(i);
				continue;
			}
			currentPart += (char) input.charAt(i);
			if(currentWrapper != null) {
				continue;
			}
			parts.add(StringUtils.substringBeforeLast(currentPart, split));
			out.add(new Query(parts,split));
			input = StringUtils.substring(input, i+1);
			i=-1;
			currentPart="";
			parts = new ArrayList<>();
		}
		if(StringUtils.isNotBlank(currentPart)) {
			String split = getIfAtSplit(currentPart, currentPart.length()-1, registry);
			parts.add(StringUtils.substringBeforeLast(currentPart, split));
		}
		if(!parts.isEmpty()) {
			out.add(new Query(parts,null));
		}
		return out;
	}
	private static String getIfWrapperAtPosition(String string, int position, String currentWrapper, SpecialCharacterRegistry registry) {
		for(String wrapString : registry.getWrapperStrings()) {
			if(StringUtils.isNotBlank(currentWrapper)) {
				if(!StringUtils.equals(currentWrapper, wrapString)) {
					continue;
				}
			}
			
			int startPosition = (position+1)-wrapString.length();
			if(startPosition < 0) {
				continue;
			}
			int endPosition = position+1;
			String subString = StringUtils.substring(string, startPosition, endPosition);
			if(!StringUtils.equals(subString, wrapString)) {
				continue;
			}
			if(wrapString.length() > 1) {
				return wrapString;
			}
			if(startPosition == 0) {
				return wrapString;
			}
			if(isEscaped(string, position, registry)) {
				continue;
			}
			return wrapString;
		}
		return null;
	}
	private static String getIfAtSplit(String string, int position, SpecialCharacterRegistry registry) {
		final String[] splitStrings = registry.getEnabledDelimiters();
		for(String splitString : splitStrings) {
			int startPosition = (position+1)-splitString.length();
			if(startPosition < 0) {
				continue;
			}
			int endPosition = position+1;
			String subString = StringUtils.substring(string, startPosition, endPosition);
			if(StringUtils.equals(subString, splitString)) {
				return splitString;
			}
		}
		return null;
	}
	private static boolean isEscaped(String string, int position, SpecialCharacterRegistry registry) {
		position--;
		if(position < 0) {
			return false;
		}
		if(string.charAt(position) != registry.getEscapeCharacter()) {
			return false;
		}
		return !isEscaped(string, position, registry);
	}
}