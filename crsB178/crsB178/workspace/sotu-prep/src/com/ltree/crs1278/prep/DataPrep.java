package com.ltree.crs1278.prep;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class DataPrep {
	public static void main(String[] args) {
		File dir = new File("/home/user/crs1278/data/sotu");
		File sotu_speeches = new File("/home/user/crs1278/exercises/ch2/sotu_speeches");
//		File sotu_speeches = new File("sotu_speeches");

		File[] speecheFiles = dir.listFiles();
		try (PrintWriter writer = new PrintWriter(new FileWriter(sotu_speeches))) {
			for (File speechfile : speecheFiles) {
				try (BufferedReader reader = new BufferedReader(new FileReader(speechfile))) {
					String line = "";
					while ((line = reader.readLine()) != null) {
						writer.println(line);
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
