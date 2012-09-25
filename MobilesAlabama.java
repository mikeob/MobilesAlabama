import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Scanner;

public class MobilesAlabama {

	final static String MATCH_BEFORE_OR_AFTER = "((?<=%1$s)|(?=%1$s))";
	// Lookaround splitting delimiter borrowed from Godmar Back's example code
	// match whitespace or right before or after ( )
	// consumes whitespace - but does not consume ( or )
	final static String delim = "\\p{javaWhitespace}|"
			+ String.format(MATCH_BEFORE_OR_AFTER, "[\\(\\)]");

	Deque<String> tokens = new ArrayDeque<String>();
	ArrayList<String> ans = new ArrayList<String>();
	Scanner in = new Scanner(System.in).useDelimiter(delim);

	// Grammar:
	// 1. ( D w )
	// D indiciates DecObject
	// w is weight

	// 2. ( B # L m1 m2 )
	// B indicates Bar
	// # is identifier
	// L is length
	// m1 is expression 1
	// m2 is expression 2
	public void solve() {

		while (inputExpression() > 2) {

			
			poll(); // Removes first (
			poll(); // Removes first B

			Bar topBar = (Bar) parseBar();
			ans.addAll(topBar.balance());

			Collections.sort(ans, new AnswerComparator());

			// Output answers
			for (String s : ans) {
				System.out.println(s);
			}

			ans.clear();
		}

		in.close();
	}

	char peek() {

		if (tokens.isEmpty()) {
			return '0';
		}

		return tokens.peek().charAt(0);
	}

	char poll() {
		char ans = peek();
		tokens.poll();
		return ans;
	}

	// Assumes given the input tokens " # L (...) (...) )"
	Node parseBar() {

		int barNum = Integer.parseInt(tokens.poll());
		double length = Double.parseDouble(tokens.poll());
		poll(); // Remove initial (
		Node m1;
		Node m2;

		char next = poll();

		if (next == 'D') {
			m1 = parseD();
		} else {
			m1 = parseBar();
		}

		poll(); // Remove ( prefix
		next = poll();

		if (next == 'D') {
			m2 = parseD();
		} else {
			m2 = parseBar();
		}

		poll(); // Remove trailing )

		return new Bar(barNum, length, m1, m2);

	}

	// Assumes given "w )"
	Node parseD() {
		Node ans = new DecObject(Double.parseDouble(tokens.poll()));
		poll(); // Remove trailing )

		return ans;
	}

	int inputExpression() {
		tokens.clear();

		int numOpen = 1;

		String next = in.next();

		while (next.equals("")) {
			next = in.next();
		}

		tokens.add(next);
		while (numOpen != 0) {

			next = in.next();

			if (next.equals("")) {
				continue;
			} else if (next.equals("(")) {
				numOpen++;
			} else if (next.equals(")")) {
				numOpen--;
			}

			tokens.add(next);
		}

		return tokens.size();

	}

	static interface Node {
		double weight();
	}

	static class DecObject implements Node {
		double weight;

		public DecObject(double weight) {
			this.weight = weight;
		}

		public double weight() {
			return weight;
		}
	}

	static class Bar implements Node {

		int barNum;
		double length;
		Node m1;
		Node m2;

		public Bar(int num, double l, Node m1, Node m2) {
			barNum = num;
			length = l;
			this.m1 = m1;
			this.m2 = m2;
		}

		public double weight() {
			return m1.weight() + m2.weight();
		}

		public ArrayList<String> balance() {

			ArrayList<String> ans = new ArrayList<String>();

			if (m1 instanceof Bar) {
				ans.addAll(((Bar) m1).balance());
			}
			if (m2 instanceof Bar) {
				ans.addAll(((Bar) m2).balance());
			}

			double totalWeight = weight();

			double L1 = (length * m1.weight()) / totalWeight;
			double L2 = (length * m2.weight()) / totalWeight;

			double best = Math.min(L1, L2);

			ans.add(String.format("Bar %d must be tied %.1f from one end.",
					barNum, best));

			return ans;

		}
	}

	public static void main(String[] args) {
		new MobilesAlabama().solve();
	}

	class AnswerComparator implements Comparator<String> {

		@Override
		public int compare(String a1, String a2) {
			
			String[] ans1 = a1.split(" ");
			String[] ans2 = a2.split(" ");
			
			int first = Integer.parseInt(ans1[1]);
			int second = Integer.parseInt(ans2[1]);
			
			
			return first - second;
		}
		
	}
	
}
