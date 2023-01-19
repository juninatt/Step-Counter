package se.sigma.boostapp.boost_app_java.util;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *  * Utility class that contains methods for comparing and matching strings.
 */
public class StringComparator {

    /**
     * Finds the matching strings from the two lists.
     *
     * @param stringsA the first list of strings
     * @param stringsB the second list of strings
     * @return the intersection(the strings with a match collected) of the two lists, or an empty set if one or both lists are empty
     */
    public static Set<String> getMatching(List<String> stringsA, List<String> stringsB) {
        if (stringsA == null || stringsB == null || stringsA.isEmpty() || stringsB.isEmpty()) {
            return Collections.emptySet();
        }
        return ImmutableSet.copyOf(Sets.intersection(new HashSet<>(stringsA), new HashSet<>(stringsB)));
    }
}