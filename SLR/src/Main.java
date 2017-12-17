import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

public class Main {
	private static class Regla {
		public String str;

		public String righthand() {
			String s = "";
			Scanner sc = new Scanner(str);
			sc.useDelimiter("");
			while (sc.hasNext() && !sc.next().equals(">"))
				;
			while (sc.hasNext()) {
				s += sc.next();
			}
			sc.close();
			return s;
		}

		public String lefthand() {
			String s = "";
			String aux = "";
			Scanner sc = new Scanner(str);
			sc.useDelimiter("");
			while (sc.hasNext() && !aux.equals(">")) {
				aux = sc.next();
				if (!aux.equals(">")) {
					s += aux;
				}
			}
			sc.close();
			return s;
		}

		public Regla(String s) {
			str = s;
		}

		public boolean equals(Object o) {
			if (o instanceof Regla) {
				Regla r = (Regla) o;
				return str.equals(r.str);
			} else {
				return false;
			}
		}
	}

	static Set<String> cab;
	static List<Regla> reglas;
	static Set<String> nonT; 
	static Set<String> T;
	static Map<String, String> table; 
	static String question;
										
	/*
	 Para usarlo con otra gramatica solo hay que cambiar el metodo ini
	 */
	public static void ini(Set<String> set) {
		question="1+1$";
		cab = new HashSet<String>(); //esto no hay que tocarlo
		table = new HashMap<String, String>();
		/**/
		reglas = new ArrayList<Regla>(); //aqui van las reglas de la gramatica
		reglas.add(new Regla("S>E$"));
		reglas.add(new Regla("E>E*B"));
		reglas.add(new Regla("E>E+B"));	
		reglas.add(new Regla("E>B"));
		reglas.add(new Regla("B>0"));
		reglas.add(new Regla("B>1"));
		/**/
		nonT = new HashSet<String>(); //lista de no terminales
		nonT.add("E");
		nonT.add("S");
		nonT.add("B");
		/**/
		T = new HashSet<String>(); //lista de terminales
		T.add("1");
		T.add("0");
		T.add("+");
		T.add("*");
		/**/		
		set.add("S>·E$"); //item inicial
	}

	public static void main(String[] args) {
		Set<String> set = new HashSet<String>();
		ini(set);	
		List<Set<String>> superset = new ArrayList<Set<String>>();
		List<Set<String>> oldSuperset;
		superset.add(cierre(set));
		int j = 0;
		do {
			int i = 0;
			oldSuperset = new ArrayList<Set<String>>(superset);
			for (Set<String> s : oldSuperset) {
				for (String x : nonT) {
					Set<String> aux = delta(s, x);
					if (!superset.contains(aux) && !aux.isEmpty()) {
						j++;
						superset.add(aux);
						table.put(i + x, j + "");
					}
				}
				for (String x : T) {
					Set<String> aux = delta(s, x);
					if (!superset.contains(aux) && !aux.isEmpty()) {
						j++;
						superset.add(aux);
						table.put(i + x, "d" + j);
					} else if (!aux.isEmpty()) {
						table.put(i + x, "d" + superset.indexOf(aux));
					}
				}
				i++;
			}
		} while (!superset.equals(oldSuperset));

		System.out.println("SETS:");
		for (Set<String> s : superset) {
			for (String str : s) {
				Scanner sc = new Scanner(str);
				sc.useDelimiter("·");
				sc.next();
				sc.useDelimiter("");
				sc.skip("·");
				if (!sc.hasNext()) {
					sc.close();
					sc = new Scanner(str);
					sc.useDelimiter("·");
					int i = reglas.indexOf(new Regla(sc.next()));
					for (String t : T) {
						if (sig(reglas.get(i).lefthand()).contains(t)) {
							table.put(superset.indexOf(s) + t, "r" + i);
						}
					}
					if (sig(reglas.get(i).lefthand()).contains("$")) {
						table.put(superset.indexOf(s) + "$", "r" + i);
					}
				} else {
					if (sc.next().equals("$")) {
						table.put(superset.indexOf(s) + "$", "ace");
					}
				}
				sc.close();
			}
			System.out.print(superset.indexOf(s) + " ");
			System.out.println(s);
		}
		System.out.println("TABLA:");
		for (String k : table.keySet()) {
			System.out.println(k + " " + table.get(k));
		}
		
		
		/*RESOLUCION*/
		System.out.println("RESOLUTION:");
		boolean done=false;
		Stack<String> st= new Stack<String>();
		st.push("0");
		Scanner sc = new Scanner (question);
		sc.useDelimiter("");
		String aux=sc.next();
		while(!done) {
			System.out.println(st);
			System.out.println(aux);
			String action=table.get(st.peek()+aux);
			switch(action.substring(0, 1)) {
				case("d"):
					st.push(action.substring(1,2));
					aux=sc.next();
					break;
				case("r"):
					Regla r=reglas.get(Integer.parseInt(action.substring(1,2)));
					for(int i=0;i<r.righthand().length();i++) {
						st.pop();
					}
					st.push(table.get(st.peek()+r.lefthand()));
					break;
				case("a"):
					System.out.println("STRING ACCEPTED");
					done=true;
					break;
				default:
					st.push(action);
					break;
			}
		}
		sc.close();
	}

	public static Set<String> cierre(Set<String> set) {
		String aux;
		Set<String> s = new HashSet<String>(set);
		Set<String> s2;
		do {
			s2 = new HashSet<String>(s);
			for (String str : s2) {
				Scanner sc = new Scanner(str);
				sc.useDelimiter("·");
				sc.next();
				sc.useDelimiter("");
				sc.skip("·");
				if (sc.hasNext()) {
					aux = sc.next();
					for (Regla r : reglas) {
						if (r.lefthand().equals(aux)) {
							s.add(r.lefthand() + ">·" + r.righthand());
						}
					}
				}
				sc.close();
			}
		} while (!s.equals(s2));

		return s;
	}

	public static Set<String> delta(Set<String> set, String x) {
		String str;
		String aux = "";
		Set<String> s = new HashSet<String>();
		for (String stri : set) {
			str = "";
			Scanner sc = new Scanner(stri);
			sc.useDelimiter("·");
			str += sc.next();
			sc.useDelimiter("");
			sc.skip("·");
			if (sc.hasNext()) {
				aux = sc.next();
				if (aux.equals(x)) {
					str += aux;
					str += "·";
					while (sc.hasNext()) {
						str += sc.next();
					}
					s.add(str);
				}
			}
			sc.close();

		}
		return cierre(s);
	}

	public static Set<String> sig(String s) {
		Set<String> set = new HashSet<String>();
		String str = "";
		String aux;
		for (Regla r : reglas) {
			aux = "";
			str = "";
			Scanner sc = new Scanner(r.righthand());
			sc.useDelimiter("");
			while (sc.hasNext() && !aux.equals(s)) {
				aux = sc.next();
			}
			if (aux.equals(s)) {
				sc.useDelimiter(s);
				if (sc.hasNext()) {
					str = sc.next();
				}
				set.addAll(cab(str));
				if (cab(str).contains("")) {
					set.remove("");
					if (!r.lefthand().equals(s)) {
						set.addAll(sig(r.lefthand()));
					}
				}
			}
			sc.close();
		}
		return set;
	}

	public static Set<String> cab(String s) {
		cab.add(s);
		Set<String> set = new HashSet<String>();
		String aux;
		if (T.contains(s) || s.equals("$") || s.equals("")) {
			set.add(s);
		} else if (s.length() == 1) {
			for (Regla r : reglas) {
				set.addAll(cab(r.righthand()));
			}
		} else {
			Scanner sc = new Scanner(s);
			sc.useDelimiter("");
			do {
				set.remove("");
				aux = sc.next();
				if (!cab.contains(aux)) {
					set.addAll(cab(aux));
				}
			} while (set.contains(""));
			if (!sc.hasNext()) {
				set.add("");
			}
			sc.close();
		}
		cab.remove(s);
		return set;
	}
}
