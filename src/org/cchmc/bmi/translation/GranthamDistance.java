package org.cchmc.bmi.translation;

import java.util.HashMap;

/**
 * Lookup table for the Grantham distance between two amino acids
 * Source: http://www.genome.jp/dbget-bin/www_bget?aax2:GRAR740104
 * @author dexzb9
 *
 */
public class GranthamDistance {

	public static int get(AminoAcid a, AminoAcid b) {
		HashMap<AminoAcid, Integer> x = distance.get(a);
		if (x == null)
			return -1;
		Integer result = x.get(b);
		return (result == null) ? -1 : result;
	}
	
	private static HashMap<AminoAcid, HashMap<AminoAcid, Integer>> distance;
	private static void populate(AminoAcid a, AminoAcid b, int val) {
		distance.get(a).put(b, val);
		if (a != b)
			distance.get(b).put(a, val);
	}
	static {
		distance = new HashMap<AminoAcid, HashMap<AminoAcid, Integer>>();
		for (AminoAcid i : AminoAcid.values()) {
			distance.put(i, new HashMap<AminoAcid, Integer>());
		}
		populate(AminoAcid.ALA, AminoAcid.ALA, 0);
		populate(AminoAcid.ALA, AminoAcid.ARG, 112);
		populate(AminoAcid.ALA, AminoAcid.ASN, 111);
		populate(AminoAcid.ALA, AminoAcid.ASP, 126);
		populate(AminoAcid.ALA, AminoAcid.CYS, 195);
		populate(AminoAcid.ALA, AminoAcid.GLN, 91);
		populate(AminoAcid.ALA, AminoAcid.GLU, 107);
		populate(AminoAcid.ALA, AminoAcid.GLY, 60);
		populate(AminoAcid.ALA, AminoAcid.HIS, 86);
		populate(AminoAcid.ALA, AminoAcid.ILE, 94);
		populate(AminoAcid.ALA, AminoAcid.LEU, 96);
		populate(AminoAcid.ALA, AminoAcid.LYS, 106);
		populate(AminoAcid.ALA, AminoAcid.MET, 84);
		populate(AminoAcid.ALA, AminoAcid.PHE, 113);
		populate(AminoAcid.ALA, AminoAcid.PRO, 27);
		populate(AminoAcid.ALA, AminoAcid.SER, 99);
		populate(AminoAcid.ALA, AminoAcid.THR, 58);
		populate(AminoAcid.ALA, AminoAcid.TRP, 148);
		populate(AminoAcid.ALA, AminoAcid.TYR, 112);
		populate(AminoAcid.ALA, AminoAcid.VAL, 64);

		populate(AminoAcid.ARG, AminoAcid.ARG, 0);
		populate(AminoAcid.ARG, AminoAcid.ASN, 86);
		populate(AminoAcid.ARG, AminoAcid.ASP, 96);
		populate(AminoAcid.ARG, AminoAcid.CYS, 180);
		populate(AminoAcid.ARG, AminoAcid.GLN, 43);
		populate(AminoAcid.ARG, AminoAcid.GLU, 54);
		populate(AminoAcid.ARG, AminoAcid.GLY, 125);
		populate(AminoAcid.ARG, AminoAcid.HIS, 29);
		populate(AminoAcid.ARG, AminoAcid.ILE, 97);
		populate(AminoAcid.ARG, AminoAcid.LEU, 102);
		populate(AminoAcid.ARG, AminoAcid.LYS, 26);
		populate(AminoAcid.ARG, AminoAcid.MET, 91);
		populate(AminoAcid.ARG, AminoAcid.PHE, 97);
		populate(AminoAcid.ARG, AminoAcid.PRO, 103);
		populate(AminoAcid.ARG, AminoAcid.SER, 110);
		populate(AminoAcid.ARG, AminoAcid.THR, 71);
		populate(AminoAcid.ARG, AminoAcid.TRP, 101);
		populate(AminoAcid.ARG, AminoAcid.TYR, 77);
		populate(AminoAcid.ARG, AminoAcid.VAL, 96);

		populate(AminoAcid.ASN, AminoAcid.ASN, 0);
		populate(AminoAcid.ASN, AminoAcid.ASP, 23);
		populate(AminoAcid.ASN, AminoAcid.CYS, 139);
		populate(AminoAcid.ASN, AminoAcid.GLN, 46);
		populate(AminoAcid.ASN, AminoAcid.GLU, 42);
		populate(AminoAcid.ASN, AminoAcid.GLY, 80);
		populate(AminoAcid.ASN, AminoAcid.HIS, 68);
		populate(AminoAcid.ASN, AminoAcid.ILE, 149);
		populate(AminoAcid.ASN, AminoAcid.LEU, 153);
		populate(AminoAcid.ASN, AminoAcid.LYS, 94);
		populate(AminoAcid.ASN, AminoAcid.MET, 142);
		populate(AminoAcid.ASN, AminoAcid.PHE, 158);
		populate(AminoAcid.ASN, AminoAcid.PRO, 91);
		populate(AminoAcid.ASN, AminoAcid.SER, 46);
		populate(AminoAcid.ASN, AminoAcid.THR, 65);
		populate(AminoAcid.ASN, AminoAcid.TRP, 174);
		populate(AminoAcid.ASN, AminoAcid.TYR, 143);
		populate(AminoAcid.ASN, AminoAcid.VAL, 133);

		populate(AminoAcid.ASP, AminoAcid.ASP, 0);
		populate(AminoAcid.ASP, AminoAcid.CYS, 154);
		populate(AminoAcid.ASP, AminoAcid.GLN, 61);
		populate(AminoAcid.ASP, AminoAcid.GLU, 45);
		populate(AminoAcid.ASP, AminoAcid.GLY, 94);
		populate(AminoAcid.ASP, AminoAcid.HIS, 81);
		populate(AminoAcid.ASP, AminoAcid.ILE, 168);
		populate(AminoAcid.ASP, AminoAcid.LEU, 172);
		populate(AminoAcid.ASP, AminoAcid.LYS, 101);
		populate(AminoAcid.ASP, AminoAcid.MET, 160);
		populate(AminoAcid.ASP, AminoAcid.PHE, 177);
		populate(AminoAcid.ASP, AminoAcid.PRO, 108);
		populate(AminoAcid.ASP, AminoAcid.SER, 65);
		populate(AminoAcid.ASP, AminoAcid.THR, 85);
		populate(AminoAcid.ASP, AminoAcid.TRP, 181);
		populate(AminoAcid.ASP, AminoAcid.TYR, 160);
		populate(AminoAcid.ASP, AminoAcid.VAL, 152);

		populate(AminoAcid.CYS, AminoAcid.CYS, 0);
		populate(AminoAcid.CYS, AminoAcid.GLN, 154);
		populate(AminoAcid.CYS, AminoAcid.GLU, 170);
		populate(AminoAcid.CYS, AminoAcid.GLY, 159);
		populate(AminoAcid.CYS, AminoAcid.HIS, 174);
		populate(AminoAcid.CYS, AminoAcid.ILE, 198);
		populate(AminoAcid.CYS, AminoAcid.LEU, 198);
		populate(AminoAcid.CYS, AminoAcid.LYS, 202);
		populate(AminoAcid.CYS, AminoAcid.MET, 196);
		populate(AminoAcid.CYS, AminoAcid.PHE, 205);
		populate(AminoAcid.CYS, AminoAcid.PRO, 169);
		populate(AminoAcid.CYS, AminoAcid.SER, 112);
		populate(AminoAcid.CYS, AminoAcid.THR, 149);
		populate(AminoAcid.CYS, AminoAcid.TRP, 215);
		populate(AminoAcid.CYS, AminoAcid.TYR, 194);
		populate(AminoAcid.CYS, AminoAcid.VAL, 192);

		populate(AminoAcid.GLN, AminoAcid.GLN, 0);
		populate(AminoAcid.GLN, AminoAcid.GLU, 29);
		populate(AminoAcid.GLN, AminoAcid.GLY, 87);
		populate(AminoAcid.GLN, AminoAcid.HIS, 24);
		populate(AminoAcid.GLN, AminoAcid.ILE, 109);
		populate(AminoAcid.GLN, AminoAcid.LEU, 113);
		populate(AminoAcid.GLN, AminoAcid.LYS, 53);
		populate(AminoAcid.GLN, AminoAcid.MET, 101);
		populate(AminoAcid.GLN, AminoAcid.PHE, 116);
		populate(AminoAcid.GLN, AminoAcid.PRO, 76);
		populate(AminoAcid.GLN, AminoAcid.SER, 68);
		populate(AminoAcid.GLN, AminoAcid.THR, 42);
		populate(AminoAcid.GLN, AminoAcid.TRP, 130);
		populate(AminoAcid.GLN, AminoAcid.TYR, 99);
		populate(AminoAcid.GLN, AminoAcid.VAL, 96);

		populate(AminoAcid.GLU, AminoAcid.GLU, 0);
		populate(AminoAcid.GLU, AminoAcid.GLY, 98);
		populate(AminoAcid.GLU, AminoAcid.HIS, 40);
		populate(AminoAcid.GLU, AminoAcid.ILE, 134);
		populate(AminoAcid.GLU, AminoAcid.LEU, 138);
		populate(AminoAcid.GLU, AminoAcid.LYS, 56);
		populate(AminoAcid.GLU, AminoAcid.MET, 126);
		populate(AminoAcid.GLU, AminoAcid.PHE, 140);
		populate(AminoAcid.GLU, AminoAcid.PRO, 93);
		populate(AminoAcid.GLU, AminoAcid.SER, 80);
		populate(AminoAcid.GLU, AminoAcid.THR, 65);
		populate(AminoAcid.GLU, AminoAcid.TRP, 152);
		populate(AminoAcid.GLU, AminoAcid.TYR, 122);
		populate(AminoAcid.GLU, AminoAcid.VAL, 121);

		populate(AminoAcid.GLY, AminoAcid.GLY, 0);
		populate(AminoAcid.GLY, AminoAcid.HIS, 98);
		populate(AminoAcid.GLY, AminoAcid.ILE, 135);
		populate(AminoAcid.GLY, AminoAcid.LEU, 138);
		populate(AminoAcid.GLY, AminoAcid.LYS, 127);
		populate(AminoAcid.GLY, AminoAcid.MET, 127);
		populate(AminoAcid.GLY, AminoAcid.PHE, 153);
		populate(AminoAcid.GLY, AminoAcid.PRO, 42);
		populate(AminoAcid.GLY, AminoAcid.SER, 56);
		populate(AminoAcid.GLY, AminoAcid.THR, 59);
		populate(AminoAcid.GLY, AminoAcid.TRP, 184);
		populate(AminoAcid.GLY, AminoAcid.TYR, 147);
		populate(AminoAcid.GLY, AminoAcid.VAL, 109);

		populate(AminoAcid.HIS, AminoAcid.HIS, 0);
		populate(AminoAcid.HIS, AminoAcid.ILE, 94);
		populate(AminoAcid.HIS, AminoAcid.LEU, 99);
		populate(AminoAcid.HIS, AminoAcid.LYS, 32);
		populate(AminoAcid.HIS, AminoAcid.MET, 87);
		populate(AminoAcid.HIS, AminoAcid.PHE, 100);
		populate(AminoAcid.HIS, AminoAcid.PRO, 77);
		populate(AminoAcid.HIS, AminoAcid.SER, 89);
		populate(AminoAcid.HIS, AminoAcid.THR, 47);
		populate(AminoAcid.HIS, AminoAcid.TRP, 115);
		populate(AminoAcid.HIS, AminoAcid.TYR, 83);
		populate(AminoAcid.HIS, AminoAcid.VAL, 84);

		populate(AminoAcid.ILE, AminoAcid.ILE, 0);
		populate(AminoAcid.ILE, AminoAcid.LEU, 5);
		populate(AminoAcid.ILE, AminoAcid.LYS, 102);
		populate(AminoAcid.ILE, AminoAcid.MET, 10);
		populate(AminoAcid.ILE, AminoAcid.PHE, 21);
		populate(AminoAcid.ILE, AminoAcid.PRO, 95);
		populate(AminoAcid.ILE, AminoAcid.SER, 142);
		populate(AminoAcid.ILE, AminoAcid.THR, 89);
		populate(AminoAcid.ILE, AminoAcid.TRP, 61);
		populate(AminoAcid.ILE, AminoAcid.TYR, 33);
		populate(AminoAcid.ILE, AminoAcid.VAL, 29);

		populate(AminoAcid.LEU, AminoAcid.LEU, 0);
		populate(AminoAcid.LEU, AminoAcid.LYS, 107);
		populate(AminoAcid.LEU, AminoAcid.MET, 15);
		populate(AminoAcid.LEU, AminoAcid.PHE, 22);
		populate(AminoAcid.LEU, AminoAcid.PRO, 98);
		populate(AminoAcid.LEU, AminoAcid.SER, 145);
		populate(AminoAcid.LEU, AminoAcid.THR, 92);
		populate(AminoAcid.LEU, AminoAcid.TRP, 61);
		populate(AminoAcid.LEU, AminoAcid.TYR, 36);
		populate(AminoAcid.LEU, AminoAcid.VAL, 32);

		populate(AminoAcid.LYS, AminoAcid.LYS, 0);
		populate(AminoAcid.LYS, AminoAcid.MET, 95);
		populate(AminoAcid.LYS, AminoAcid.PHE, 102);
		populate(AminoAcid.LYS, AminoAcid.PRO, 103);
		populate(AminoAcid.LYS, AminoAcid.SER, 121);
		populate(AminoAcid.LYS, AminoAcid.THR, 78);
		populate(AminoAcid.LYS, AminoAcid.TRP, 110);
		populate(AminoAcid.LYS, AminoAcid.TYR, 85);
		populate(AminoAcid.LYS, AminoAcid.VAL, 97);

		populate(AminoAcid.MET, AminoAcid.MET, 0);
		populate(AminoAcid.MET, AminoAcid.PHE, 28);
		populate(AminoAcid.MET, AminoAcid.PRO, 87);
		populate(AminoAcid.MET, AminoAcid.SER, 135);
		populate(AminoAcid.MET, AminoAcid.THR, 81);
		populate(AminoAcid.MET, AminoAcid.TRP, 67);
		populate(AminoAcid.MET, AminoAcid.TYR, 36);
		populate(AminoAcid.MET, AminoAcid.VAL, 21);

		populate(AminoAcid.PHE, AminoAcid.PHE, 0);
		populate(AminoAcid.PHE, AminoAcid.PRO, 114);
		populate(AminoAcid.PHE, AminoAcid.SER, 155);
		populate(AminoAcid.PHE, AminoAcid.THR, 103);
		populate(AminoAcid.PHE, AminoAcid.TRP, 40);
		populate(AminoAcid.PHE, AminoAcid.TYR, 22);
		populate(AminoAcid.PHE, AminoAcid.VAL, 50);

		populate(AminoAcid.PRO, AminoAcid.PRO, 0);
		populate(AminoAcid.PRO, AminoAcid.SER, 74);
		populate(AminoAcid.PRO, AminoAcid.THR, 38);
		populate(AminoAcid.PRO, AminoAcid.TRP, 147);
		populate(AminoAcid.PRO, AminoAcid.TYR, 110);
		populate(AminoAcid.PRO, AminoAcid.VAL, 68);

		populate(AminoAcid.SER, AminoAcid.SER, 0);
		populate(AminoAcid.SER, AminoAcid.THR, 58);
		populate(AminoAcid.SER, AminoAcid.TRP, 177);
		populate(AminoAcid.SER, AminoAcid.TYR, 144);
		populate(AminoAcid.SER, AminoAcid.VAL, 124);

		populate(AminoAcid.THR, AminoAcid.THR, 0);
		populate(AminoAcid.THR, AminoAcid.TRP, 128);
		populate(AminoAcid.THR, AminoAcid.TYR, 92);
		populate(AminoAcid.THR, AminoAcid.VAL, 69);

		populate(AminoAcid.TRP, AminoAcid.TRP, 0);
		populate(AminoAcid.TRP, AminoAcid.TYR, 37);
		populate(AminoAcid.TRP, AminoAcid.VAL, 88);

		populate(AminoAcid.TYR, AminoAcid.TYR, 0);
		populate(AminoAcid.TYR, AminoAcid.VAL, 55);

		populate(AminoAcid.VAL, AminoAcid.VAL, 0);
	}
}
