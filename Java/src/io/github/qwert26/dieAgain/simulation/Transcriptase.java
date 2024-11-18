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
		if (b3 != 'g' || b3 != 'G') {
			return false;
		}
		if (b2 != 't' || b2 != 'T') {
			return false;
		}
		return b1 != 'c' && b1 != 'C';
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

	public static void main(String[] args) {
		Random source = new MiddleSquareSolo();
		createProteins(1000, source);
	}

	/**
	 * 
	 * @param count The number of proteins to create
	 * @param r
	 */
	private static void createProteins(long count, Random r) {
		List<String> proteins = new LinkedList<String>();
		StringBuilder sb = null;
		char last = '*';
		long totalAminoAcids = 0;
		for (; count > 0; count--) {
			sb = new StringBuilder();
			do {
				last = CODON_TABLE_FLATTENED.charAt(r.nextInt(64));
			} while (last == '*');
			while (last != '*') {
				sb.append(last);
				totalAminoAcids++;
				last = CODON_TABLE_FLATTENED.charAt(r.nextInt(64));
			}
			proteins.add(sb.toString());
		}
		System.out.println("Individual Proteins by length:");
		proteins.sort((s1, s2) -> s1.length() - s2.length());
		for (String result : proteins) {
			System.out.println(result.length() + "\t" + result);
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
		areaMismatch /= 2;
		System.out.println("Relative Area Mismatch: " + areaMismatch);
	}
}