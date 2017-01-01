package bgu.spl.a2.sim;

import java.io.*;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import bgu.spl.a2.sim.Product;

public class SerReader {
	static PrintWriter out;
	public static void main(String[] args) throws Exception {
		out = new PrintWriter(new OutputStreamWriter(
				new BufferedOutputStream(new FileOutputStream("out.txt")), "UTF-8"));
		AtomicBoolean flag = new AtomicBoolean(false);

		out.println(String.format("%-20s", "----Deserializer----"));
		out.println("SPL171 deserializer for assignment 2");
		out.println();
		out.println();

		SerReader obj = new SerReader();

		ConcurrentLinkedQueue<Product> res = obj.deserialzeObject("result.ser");

		if (res == null) {
			out.println("error -> null object");
			out.println("ser file is corrupted!");
			out.println("final grade is zero  (╯°□°)╯︵ ┻━┻");
		} else {
			res.iterator().forEachRemaining((var) -> {
				flag.set(true);
				PrintPro(var);
				out.println();
			});
		}

		if (flag.get()) {
			out.println("*************************************************************************");
			out.println("*\t\t\tIt seems that you did it right!\t\t\t*");
			out.println("*\tbut we don't take any responsibility on any result! \\_(ʘ_ʘ)_/ \t*");
			out.println("*\t\t\t\tcompare your output!\t\t\t*");
			out.println("*\tCredits:\t\t\t\t\t\t\t*");
			out.println("*\t\t\t\t***M.Zidane***\t\t\t\t*");
			out.println("*\t\t\t\t***M.Sleiman***\t\t\t\t*");
			out.println("*************************************************************************");
		}
		out.close();
	}

	public ConcurrentLinkedQueue<Product> deserialzeObject(String filename) {

		ConcurrentLinkedQueue<Product> res = null;

		FileInputStream fin = null;
		ObjectInputStream ois = null;

		try {

			fin = new FileInputStream(filename);
			ois = new ObjectInputStream(fin);
			ConcurrentLinkedQueue<Product> concurrentLinkedQueue = (ConcurrentLinkedQueue<Product>) (ois.readObject());
			res = concurrentLinkedQueue;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		return res;

	}

	private static void PrintPro(Product product) {
		out.println("ProductName: " + product.getName() + "  Product Id = " + product.getFinalId());

		out.println("PartsList {");
		if (product.getParts().size() > 0) {
			for (int i = 0; i < product.getParts().size(); i++) {
				PrintPro(product.getParts().get(i));
			}
		}
		out.println("}");

	}
}
