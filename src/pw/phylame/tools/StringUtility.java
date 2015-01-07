/*
 * Copyright 2014 Peng Wan <minexiac@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pw.phylame.tools;

/**
 * Utilities for string.
 */
public abstract class StringUtility {

    /**
     * Join string array to string.
     * @param seq array of {@code String}
     * @param separator separator between string and string
     * @return joined string
     */
    public static String join(String[] seq, String separator) {
        return join(java.util.Arrays.asList(seq), separator);
    }

    /**
     * Join string collection to string.
     * @param seq collection of {@code String}
     * @param separator separator between string and string
     * @return joined string
     */
    public static String join(java.util.Collection<String> seq, String separator) {
        int i = 1;
        StringBuilder sb = new StringBuilder();
        for (String s: seq) {
            sb.append(s);
            /* not last item */
            if (i++ != seq.size()) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    /** Return a copy of {@code s} and convert first char to upper case. */
    public static String toCapital(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        StringBuilder sb = new StringBuilder(s);
        sb.setCharAt(0, Character.toUpperCase(s.charAt(0)));
        return sb.toString();
    }

    /** Return a copy of {@code s} and convert each word to capital. */
    public static String toTitle(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        StringBuilder sb = new StringBuilder(s);
        boolean isFirst = true;
        int length = sb.length();
        for (int i=0; i < length; ++i) {
            char ch = sb.charAt(i);
            if (! Character.isLetter(ch)) {
                isFirst = true;
            } else if (isFirst) {
                sb.setCharAt(i, Character.toUpperCase(ch));
                isFirst = false;
            }
        }
        return sb.toString();
    }

    /**
     * Return {@code true} if all chars in {@code s} is lower case.
     */
    public static boolean isLowerCase(String s) {
        for (int i=0; i<s.length(); ++i) {
            /* found upper case */
            if (Character.isUpperCase(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return {@code true} if all chars in {@code s} is upper case.
     */
    public static boolean isUpperCase(String s) {
        for (int i=0; i<s.length(); ++i) {
            /* found lower case */
            if (Character.isLowerCase(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
