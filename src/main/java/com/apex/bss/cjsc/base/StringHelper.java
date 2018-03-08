package com.apex.bss.cjsc.base;

/**
 * Created by Jinshi on 2016/2/19.
 */

import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringHelper {
    private static Logger logger;
    public static final String EMPTY_STRING = "";
    public static final char DOT = '.';
    public static final char UNDERSCORE = '_';
    public static final String COMMA_SPACE = ", ";
    public static final String COMMA = ",";
    public static final String OPEN_PAREN = "(";
    public static final String CLOSE_PAREN = ")";
    public static final char SINGLE_QUOTE = '\'';
    public static final String CRLF = "\r\n";

    static {

    }

    private StringHelper() {
    }

    public static boolean checkstr(String username) {
        String indexStr = username.substring(0, 1);
        String numberStr = "abcdefghijklmnopqrstuvwxyz0123456789_";
        String emailStr = "@.!~#$%^&*()|";
        if(username.length() < 6) {
            return true;
        } else {
            for(int i = 0; i < username.length(); ++i) {
                if(i == username.length()) {
                    return true;
                }

                if(emailStr.indexOf(username.substring(i, i + 1)) != -1) {
                    return true;
                }
            }

            if(numberStr.indexOf(indexStr) == -1) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static String join(String seperator, String[] strings) {
        int length = strings.length;
        if(length == 0) {
            return "";
        } else {
            StringBuffer buf = (new StringBuffer(length * strings[0].length())).append(strings[0]);

            for(int i = 1; i < length; ++i) {
                buf.append(seperator).append(strings[i]);
            }

            return buf.toString();
        }
    }

    public static String join(String seperator, Iterator objects) {
        StringBuffer buf = new StringBuffer();
        if(objects.hasNext()) {
            buf.append(objects.next());
        }

        while(objects.hasNext()) {
            buf.append(seperator).append(objects.next());
        }

        return buf.toString();
    }

    public static String[] add(String[] x, String seperator, String[] y) {
        String[] result = new String[x.length];

        for(int i = 0; i < x.length; ++i) {
            result[i] = x[i] + seperator + y[i];
        }

        return result;
    }

    public static String repeat(String string, int times) {
        StringBuffer buf = new StringBuffer(string.length() * times);

        for(int i = 0; i < times; ++i) {
            buf.append(string);
        }

        return buf.toString();
    }

    public static String replace(String source, String old, String replace) {
        StringBuffer output = new StringBuffer();
        int sourceLen = source.length();
        int oldLen = old.length();

        int posStart;
        int pos;
        for(posStart = 0; (pos = source.indexOf(old, posStart)) >= 0; posStart = pos + oldLen) {
            output.append(source.substring(posStart, pos));
            output.append(replace);
        }

        if(posStart < sourceLen) {
            output.append(source.substring(posStart));
        }

        return output.toString();
    }

    public static String replace(String template, String placeholder, String replacement, boolean wholeWords) {
        int loc = template.indexOf(placeholder);
        if(loc < 0) {
            return template;
        } else {
            boolean actuallyReplace = wholeWords || loc + placeholder.length() == template.length() || !Character.isJavaIdentifierPart(template.charAt(loc + placeholder.length()));
            String actualReplacement = actuallyReplace?replacement:placeholder;
            return template.substring(0, loc) + actualReplacement + replace(template.substring(loc + placeholder.length()), placeholder, replacement, wholeWords);
        }
    }

    public static String replaceOnce(String template, String placeholder, String replacement) {
        int loc = template.indexOf(placeholder);
        return loc < 0?template:template.substring(0, loc) + replacement + template.substring(loc + placeholder.length());
    }

    public static String[] split(String list, String seperators) {
        return split(list, seperators, false);
    }

    public static String[] split(String list, String seperators, boolean include) {
        StringTokenizer tokens = new StringTokenizer(list, seperators, include);
        String[] result = new String[tokens.countTokens()];

        for(int i = 0; tokens.hasMoreTokens(); result[i++] = tokens.nextToken()) {
            ;
        }

        return result;
    }

    public static String unqualify(String qualifiedName) {
        return unqualify(qualifiedName, ".");
    }

    public static String unqualify(String qualifiedName, String seperator) {
        return qualifiedName.substring(qualifiedName.lastIndexOf(seperator) + 1);
    }

    public static String qualifier(String qualifiedName) {
        int loc = qualifiedName.lastIndexOf(".");
        return loc < 0?"":qualifiedName.substring(0, loc);
    }

    public static String[] suffix(String[] columns, String suffix) {
        if(suffix == null) {
            return columns;
        } else {
            String[] qualified = new String[columns.length];

            for(int i = 0; i < columns.length; ++i) {
                qualified[i] = suffix((String)columns[i], suffix);
            }

            return qualified;
        }
    }

    public static String suffix(String name, String suffix) {
        return suffix == null?name:name + suffix;
    }

    public static String[] prefix(String[] columns, String prefix) {
        if(prefix == null) {
            return columns;
        } else {
            String[] qualified = new String[columns.length];

            for(int i = 0; i < columns.length; ++i) {
                qualified[i] = prefix + columns[i];
            }

            return qualified;
        }
    }

    public static String prefix(String name, String prefix) {
        return prefix == null?name:prefix + name;
    }

    public static boolean booleanValue(String tfString) {
        String trimmed = tfString.trim().toLowerCase();
        return trimmed.equals("true") || trimmed.equals("t");
    }

    public static String toString(Object[] array) {
        int len = array.length;
        if(len == 0) {
            return "";
        } else {
            StringBuffer buf = new StringBuffer(len * 12);

            for(int i = 0; i < len - 1; ++i) {
                buf.append(array[i]).append(", ");
            }

            return buf.append(array[len - 1]).toString();
        }
    }

    public static String[] multiply(String string, Iterator placeholders, Iterator replacements) {
        String[] result;
        for(result = new String[]{string}; placeholders.hasNext(); result = multiply((String[])result, (String)((String)placeholders.next()), (String[])((String[])replacements.next()))) {
            ;
        }

        return result;
    }

    private static String[] multiply(String[] strings, String placeholder, String[] replacements) {
        String[] results = new String[replacements.length * strings.length];
        int n = 0;

        for(int i = 0; i < replacements.length; ++i) {
            for(int j = 0; j < strings.length; ++j) {
                results[n++] = replaceOnce(strings[j], placeholder, replacements[i]);
            }
        }

        return results;
    }

    public static int count(String string, char character) {
        int n = 0;

        for(int i = 0; i < string.length(); ++i) {
            if(string.charAt(i) == character) {
                ++n;
            }
        }

        return n;
    }

    public static int countUnquoted(String string, char character) {
        if(39 == character) {
            throw new IllegalArgumentException("Unquoted count of quotes is invalid");
        } else {
            int count = 0;
            int stringLength = string == null?0:string.length();
            boolean inQuote = false;

            for(int indx = 0; indx < stringLength; ++indx) {
                if(inQuote) {
                    if(39 == string.charAt(indx)) {
                        inQuote = false;
                    }
                } else if(39 == string.charAt(indx)) {
                    inQuote = true;
                } else if(string.charAt(indx) == character) {
                    ++count;
                }
            }

            return count;
        }
    }

    public static boolean isBlank(String str) {
        int strLen;
        if(str != null && (strLen = str.length()) != 0) {
            for(int i = 0; i < strLen; ++i) {
                if(!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public static boolean isNotBlank(String str) {
        int strLen;
        if(str != null && (strLen = str.length()) != 0) {
            for(int i = 0; i < strLen; ++i) {
                if(!Character.isWhitespace(str.charAt(i))) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public static boolean isNotEmpty(String string) {
        return string != null && string.length() > 0;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static String qualify(String name, String prefix) {
        return name.startsWith("\'")?name:(new StringBuffer(prefix.length() + name.length() + 1)).append(prefix).append('.').append(name).toString();
    }

    public static String[] qualify(String[] names, String prefix) {
        if(prefix == null) {
            return names;
        } else {
            int len = names.length;
            String[] qualified = new String[len];

            for(int i = 0; i < len; ++i) {
                qualified[i] = qualify((String)prefix, names[i]);
            }

            return qualified;
        }
    }

    public static int firstIndexOfChar(String sqlString, String string, int startindex) {
        int matchAt = -1;

        for(int i = 0; i < string.length(); ++i) {
            int curMatch = sqlString.indexOf(string.charAt(i), startindex);
            if(curMatch >= 0) {
                if(matchAt == -1) {
                    matchAt = curMatch;
                } else {
                    matchAt = Math.min(matchAt, curMatch);
                }
            }
        }

        return matchAt;
    }

    public static String truncate(String string, int length, boolean appendSuspensionPoints) {
        if(!isEmpty(string) && length >= 0) {
            if(length == 0) {
                return "";
            } else {
                int strLength = string.length();
                int byteLength = byteLength(string);
                length *= 2;
                boolean needSus = false;
                if(appendSuspensionPoints && byteLength >= length) {
                    needSus = true;
                    length -= 2;
                }

                StringBuffer result = new StringBuffer();
                int count = 0;

                for(int i = 0; i < strLength && count < length; ++i) {
                    char c = string.charAt(i);
                    if(isLetter(c)) {
                        result.append(c);
                        ++count;
                    } else if(count == length - 1) {
                        result.append(" ");
                        ++count;
                    } else {
                        result.append(c);
                        count += 2;
                    }
                }

                if(needSus) {
                    result.append("...");
                }

                return result.toString();
            }
        } else {
            return string;
        }
    }

    public static boolean isLetter(char c) {
        short k = 128;
        return c / k == 0;
    }

    public static int byteLength(String s) {
        char[] c = s.toCharArray();
        int len = 0;

        for(int i = 0; i < c.length; ++i) {
            if(isLetter(c[i])) {
                ++len;
            } else {
                len += 2;
            }
        }

        return len;
    }

    public static String truncate(String string, int length) {
        return isEmpty(string)?string:(string.length() <= length?string:string.substring(0, length));
    }

    public static String leftTrim(String value) {
        String result = value;
        if(value == null) {
            return value;
        } else {
            char[] ch = value.toCharArray();
            int index = -1;

            for(int i = 0; i < ch.length && Character.isWhitespace(ch[i]); index = i++) {
                ;
            }

            if(index != -1) {
                result = value.substring(index + 1);
            }

            return result;
        }
    }

    public static String rightTrim(String value) {
        String result = value;
        if(value == null) {
            return value;
        } else {
            char[] ch = value.toCharArray();
            int endIndex = -1;

            for(int i = ch.length - 1; i > -1 && Character.isWhitespace(ch[i]); endIndex = i--) {
                ;
            }

            if(endIndex != -1) {
                result = value.substring(0, endIndex);
            }

            return result;
        }
    }

    public static String N2S(String source) {
        return source != null?source:"";
    }

    public static String N2S(String source, String defaultStr) {
        return source != null?source:defaultStr;
    }

    public static String A2C(String source) {
        char[] strChar = source.toCharArray();
        byte[] abyte0 = new byte[strChar.length];

        for(int exception = 0; exception < strChar.length; ++exception) {
            abyte0[exception] = (byte)(strChar[exception] & 255);
        }
        try {
            return new String(abyte0,"gb2312");
        } catch (Exception var4) {
            logger.debug(var4);
            return source;
        }
    }

    public static String C2A(String source) {
        try {

            byte[] abyte0 = source.getBytes();
            char[] ac = new char[abyte0.length];

            for(int i = 0; i < abyte0.length; ++i) {
                ac[i] = (char)(abyte0[i] & 255);
            }

            return new String(ac);
        } catch (Exception var5) {
            logger.debug(var5);
            return source;
        }
    }

    public static final String toGb2312(String source) {
        String temp = null;
        if(source != null && !source.equals("")) {
            try {
                temp = new String(source.getBytes("8859_1"), "GB2312");
            } catch (Exception var3) {
                logger.error("转换字符串为gb2312编码出错:" + var3.getMessage());
            }

            return temp;
        } else {
            return source;
        }
    }

    public static final String toGBK(String source) {
        String temp = null;
        if(source != null && !source.equals("")) {
            try {
                temp = new String(source.getBytes("8859_1"), "GBK");
            } catch (Exception var3) {
                logger.error("Convert code Error:" + var3.getMessage());
            }

            return temp;
        } else {
            return source;
        }
    }

    public static final String to8859(String source) {
        String temp = null;
        if(source != null && !source.equals("")) {
            try {
                temp = new String(source.getBytes("GBK"), "8859_1");
            } catch (Exception var3) {
                logger.error("Convert code Error:" + var3.getMessage());
            }

            return temp;
        } else {
            return source;
        }
    }

    public static String chineseToUnicode(String source) {
        if(isEmpty(source)) {
            return source;
        } else {
            String unicode = null;
            String temp = null;

            for(int i = 0; i < source.length(); ++i) {
                temp = "\\u" + Integer.toHexString(source.charAt(i));
                unicode = unicode == null?temp:unicode + temp;
            }

            return unicode;
        }
    }

    public static String toScript(String str) {
        if(str == null) {
            return null;
        } else {
            String html = new String(str);
            html = replace(html, "\"", "\\\"");
            html = replace(html, "\r\n", "\n");
            html = replace(html, "\n", "\\n");
            html = replace(html, "\t", "    ");
            html = replace(html, "\'", "\\\'");
            html = replace(html, "  ", " &nbsp;");
            html = replace(html, "</script>", "<\\/script>");
            html = replace(html, "</SCRIPT>", "<\\/SCRIPT>");
            return html;
        }
    }

    public static String trim(String s) {
        return s == null?s:s.trim();
    }

    public static int strTrim(String source, int defaultValue) {
        if(isEmpty(source)) {
            return defaultValue;
        } else {
            try {
                source = source.trim();
                int ex = (new Integer(source)).intValue();
                return ex;
            } catch (Exception var3) {
                return defaultValue;
            }
        }
    }

    public static String strTrim(String source, String defaultValue) {
        if(isEmpty(source)) {
            return defaultValue;
        } else {
            try {
                source = source.trim();
                return source;
            } catch (Exception var3) {
                return defaultValue;
            }
        }
    }

    public static String encodeHtml(String source) {
        if(source == null) {
            return null;
        } else {
            String html = new String(source);
            html = replace(html, "&", "&amp;");
            html = replace(html, "<", "&lt;");
            html = replace(html, ">", "&gt;");
            html = replace(html, "\"", "&quot;");
            return html;
        }
    }

    public static String decodeHtml(String source) {
        if(source == null) {
            return null;
        } else {
            String html = new String(source);
            html = replace(html, "&amp;", "&");
            html = replace(html, "&lt;", "<");
            html = replace(html, "&gt;", ">");
            html = replace(html, "&quot;", "\"");
            html = replace(html, "\r\n", "\n");
            html = replace(html, "\n", "<br>\n");
            html = replace(html, "\t", "    ");
            html = replace(html, "  ", " &nbsp;");
            return html;
        }
    }

    public static boolean isBoolean(String source) {
        return source.equalsIgnoreCase("true") || source.equalsIgnoreCase("false");
    }

    public static String lastCharTrim(String str, String strMove) {
        if(isEmpty(str)) {
            return "";
        } else {
            String newStr = "";
            if(str.lastIndexOf(strMove) != -1 && str.lastIndexOf(strMove) == str.length() - 1) {
                newStr = str.substring(0, str.lastIndexOf(strMove));
            }

            return newStr;
        }
    }

    public static String clearHtml(String html) {
        if(isEmpty(html)) {
            return "";
        } else {
            String patternStr = "(<[^>]*>)";
            Pattern pattern = Pattern.compile(patternStr, 2);
            Matcher matcher = null;
            StringBuffer bf = new StringBuffer();

            try {
                matcher = pattern.matcher(html);
                boolean ex = true;
                boolean start = false;

                int end;
                for(end = 0; matcher.find(); end = matcher.end(1)) {
                    int start1 = matcher.start(1);
                    if(ex) {
                        bf.append(html.substring(0, start1));
                        ex = false;
                    } else {
                        bf.append(html.substring(end, start1));
                    }
                }

                if(end < html.length()) {
                    bf.append(html.substring(end));
                }

                html = bf.toString();
                String var10 = html;
                return var10;
            } catch (Exception var13) {
                logger.debug(var13);
            } finally {
                pattern = null;
                matcher = null;
            }

            return html;
        }
    }

    public static boolean chenckEmail(String str) {
        boolean result = false;
        String chen = null;
        String temp = "abcdefghijklmnopqrstuvwxyz0123456789@.-_";
        if(str != null && str.length() > 0) {
            chen = str.toLowerCase();

            int len;
            for(len = 0; len < str.length(); ++len) {
                chen.charAt(len);
                if(temp.indexOf(chen.charAt(len)) == -1) {
                    System.out.println("输入的email地址含有无效字符:" + chen);
                    return false;
                }
            }

            len = str.length();
            int temps = chen.indexOf("@");
            int tempd = chen.indexOf(".", temps);
            if(temps <= 1 || tempd - temps <= 2) {
                return false;
            }

            if(chen.indexOf("@.") > 1) {
                return false;
            }

            if(tempd == -1 || len - tempd < 2) {
                return false;
            }
        }

        return true;
    }

    public static String textFmtToHtmlFmt(String content) {
        content = replace(content, " ", "&nbsp;");
        content = replace(content, "\n", "<br>");
        return content;
    }

    public static String getFilterStr(String str) {
        String filterStr = str;
        if(str != null && str.length() > 0) {
            filterStr = replace(str, "<", "&lt;");
            filterStr = replace(filterStr, ">", "&gt;");
            filterStr = replace(filterStr, "\"", "&quot");
            filterStr = replace(filterStr, "\'", "&quot");
        }

        return filterStr;
    }

    public static boolean isCharacter(String str) {
        char[] chars = str.toCharArray();
        boolean isGB2312 = false;

        for(int i = 0; i < chars.length; ++i) {
            byte[] bytes = ("" + chars[i]).getBytes();
            if(bytes.length == 2) {
                int[] ints = new int[]{bytes[0] & 255, bytes[1] & 255};
                if(ints[0] >= 129 && ints[0] <= 254 && ints[1] >= 64 && ints[1] <= 254) {
                    isGB2312 = true;
                    break;
                }
            }
        }

        return isGB2312;
    }
}