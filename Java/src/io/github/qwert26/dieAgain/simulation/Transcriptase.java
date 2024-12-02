package io.github.qwert26.dieAgain.simulation;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import io.github.qwert26.dieAgain.util.randoms.*;

public class Transcriptase {
	/**
	 * T=0, C=1, A=2, G=3
	 */
	private static final String CODON_TABLE_FLATTENED = "FFLLSSSSYY**CC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG";
	/**
	 * Contains the counts of the short form amino acids.
	 */
	private static final Map<Character, Long> CODON_COUNTS = CODON_TABLE_FLATTENED.codePoints()
			.mapToObj((i) -> (char) i).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

	/**
	 * A start sequence is either TTG, ATG or GTG, case-insensitive.
	 * 
	 * @param b1 the first dna-base. Must <b>NOT</b> be '{@code c}' or '{@code C}'
	 *           for the function to return <code>true</code>.
	 * @param b2 the second dna-base. Must be '{@code t}' or '{@code T}' for the
	 *           function to return <code>true</code>.
	 * @param b3 the third dna-base. Must be '{@code g}' or '{@code G}' for the
	 *           function to return <code>true</code>.
	 * @return
	 */
	public static final boolean isStart(char b1, char b2, char b3) {
		if (b3 != 'g' && b3 != 'G') {
			return false;
		}
		if (b2 != 't' && b2 != 'T') {
			return false;
		}
		return !(b1 == 'c' || b1 == 'C');
	}

	/**
	 * 
	 * @param v
	 * @return
	 */
	public static final boolean isStart(int v) {
		if (v % 4 != 3) {
			return false;
		}
		v /= 4;
		if (v % 4 != 0) {
			return false;
		}
		v /= 4;
		return v % 4 != 1;
	}

	public static final int decode(char... cs) {
		int ret = 0;
		for (char c : cs) {
			ret *= 4;
			switch (c) {
			case 't', 'T' -> ret += 0;
			case 'c', 'C' -> ret += 1;
			case 'a', 'A' -> ret += 2;
			case 'g', 'G' -> ret += 3;
			default -> ret /= 4;
			}
		}
		return ret;
	}

	public static void main(String[] args) {
		Random source = new MiddleSquareSolo();
		List<String> proteins = createStrandAndTranscribe(10000, source, false);
		int totalAminoAcids = 0;
		System.out.println("Individual Proteins by length:");
		proteins.sort((s1, s2) -> s1.length() - s2.length());
		for (String result : proteins) {
			System.out.println(result.length() + "\t" + result);
			totalAminoAcids += result.length();
		}
		System.out.println("Total amino acids: " + totalAminoAcids);
		TreeMap<Character, Long> countByAminoAcid = new TreeMap<Character, Long>();
		TreeMap<Integer, Long> countByLength = new TreeMap<Integer, Long>();
		for (String protein : proteins) {
			countByLength.compute(protein.length(), (k, v) -> 1L + (v == null ? 0 : v));
			for (char shortForm : protein.toCharArray()) {
				countByAminoAcid.compute(shortForm, (k, v) -> 1L + (v == null ? 0 : v));
			}
		}
		System.out.println("Count for each amino acid:");
		System.out.println(countByAminoAcid);
		System.out.println("Count for each protein-length:");
		System.out.println(countByLength);
		System.out.println("Relative Occurences:");
		double areaMismatch = 0;
		for (Map.Entry<Character, Long> aa2c : countByAminoAcid.entrySet()) {
			double relative = aa2c.getValue().doubleValue() / totalAminoAcids;
			relative *= 61; // 4*4*4-3
			relative /= CODON_COUNTS.get(aa2c.getKey()).doubleValue();
			System.out.println(aa2c.getKey() + " = " + relative);
			areaMismatch += Math.abs(relative - 1);
		}
		System.out.println("Absolute Area Mismatch: " + areaMismatch);
	}

	/**
	 * 
	 * @param count The number of proteins to create
	 * @param r
	 * @return A list of generated proteins.
	 */
	private static List<String> createProteins(long count, Random r) {
		List<String> proteins = new LinkedList<String>();
		StringBuilder sb = null;
		char last = '*';
		for (; count > 0; count--) {
			sb = new StringBuilder();
			do {
				last = CODON_TABLE_FLATTENED.charAt(r.nextInt(64));
			} while (last == '*');
			while (last != '*') {
				sb.append(last);
				last = CODON_TABLE_FLATTENED.charAt(r.nextInt(64));
			}
			proteins.add(sb.toString());
		}
		return proteins;
	}

	/**
	 * 
	 * @param length            the length of the DNA-Strand to create
	 * @param r
	 * @param includeIncomplete If any incomplete proteins should be included in the
	 *                          output: These proteins do not have a stop-codon on
	 *                          the generated strand.
	 * @return a list of transcribed proteins from a generated dna-strand.
	 */
	private static List<String> createStrandAndTranscribe(int length, Random r, boolean includeIncomplete) {
		StringBuffer strand = new StringBuffer(length);
		for (; length > 0; length--) {
			switch (r.nextInt(4)) {
			case 0 -> strand.append('T');
			case 1 -> strand.append('C');
			case 2 -> strand.append('A');
			case 3 -> strand.append('G');
			}
		}
		length = strand.length();
		List<String> proteins = new LinkedList<String>();
		for (int i = 2; i < length; i++) {
			char first = strand.charAt(i - 2);
			char second = strand.charAt(i - 1);
			char third = strand.charAt(i);
			if (isStart(first, second, third)) {
				StringBuffer nextProtein = new StringBuffer(length / 3);
				for (int current = i + 3; current < length; current += 3) {
					char c1 = strand.charAt(current - 2);
					char c2 = strand.charAt(current - 1);
					char c3 = strand.charAt(current);
					char decoded = CODON_TABLE_FLATTENED.charAt(decode(c1, c2, c3));
					if (decoded == '*') {
						if (!nextProtein.isEmpty()) {
							proteins.add(nextProtein.toString());
						}
						nextProtein = null;
						break;
					} else {
						nextProtein.append(decoded);
					}
				}
				if (nextProtein != null && includeIncomplete) {
					if (!nextProtein.isEmpty()) {
						proteins.add(nextProtein.toString());
					}
				}
			}
		}
		return proteins;
	}
}