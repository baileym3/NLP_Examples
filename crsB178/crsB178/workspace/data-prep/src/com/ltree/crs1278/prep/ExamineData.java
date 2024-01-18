package com.ltree.crs1278.prep;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ExamineData {
public static void main(String[] args) {
	new ExamineData().execute();

}

private void execute() {
	File file1 =  new File("classification/naiveBayesData.txt");
	head(file1);
	
}

private void head(File file) {
	try(
	BufferedReader reader = new BufferedReader(new FileReader(file));
			){
		System.out.println(file.getName());
		for(int i=0; i<10; i++) {
			System.out.println(reader.readLine());
		}
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
}
}
