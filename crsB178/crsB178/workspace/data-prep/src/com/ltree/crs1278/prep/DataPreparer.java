package com.ltree.crs1278.prep;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DataPreparer {

	Logger logger = Logger.getLogger(getClass().getName());
	private String dataDir = "/home/user/crs1278/data/bills";
	private String[] trainingData = { "110", "111", "112" };
	private Set<String> categories = new HashSet<>();
	private Map<Integer, String> codeToCategoryMap = new HashMap<>();
	private Map<String, Integer> categoryTocodeMap = new HashMap<>();

	public static void main(String[] args) {
		DataPreparer preparer = new DataPreparer();
		preparer.prepareTextFile();
//		preparer.createIdfModel();
	}
/*
	private void createIdfModel() {
		File inputFile = new File("classification/naiveBayesData.txt");
		File outputFile = new File("classification/naiveBayesLibsvmData.txt");
		loadCategories();
		SparkSession spark = SparkSession.builder().appName("TfIdf").master("local[*]").getOrCreate();
		List<Row> data = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));) {
			for (int i = 0; i < 20000; i++) {
				String line = reader.readLine();
				System.out.println(line);
				String[] pieces = line.split("\t");
				String catString = pieces[0].trim();
				System.out.println("Category is "+catString);
				double category = categoryTocodeMap.get(catString);
				data.add(RowFactory.create(category, pieces[2].toLowerCase().trim()));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StructType schema = new StructType(
				new StructField[] { new StructField("label", DataTypes.DoubleType, false, Metadata.empty()),
						new StructField("summary", DataTypes.StringType, false, Metadata.empty()) });
		Dataset<Row> sentenceData = spark.createDataFrame(data, schema);
		Tokenizer tokenizer = new Tokenizer().setInputCol("summary").setOutputCol("allwords");
		Dataset<Row> wordsData = tokenizer.transform(sentenceData);
		StopWordsRemover swr = new StopWordsRemover().setInputCol("allwords").setOutputCol("words");
		Dataset<Row> stoppedData = swr.transform(wordsData);
		int numFeatures = 200000;
		HashingTF hashingTF = new HashingTF().setInputCol("words").setOutputCol("rawFeatures")
				.setNumFeatures(numFeatures);
		
		Dataset<Row> featurizedData = hashingTF.transform(stoppedData);
		IDF idf = new IDF().setInputCol("rawFeatures").setOutputCol("features");
		IDFModel idfModel = idf.fit(featurizedData);
		Dataset<Row> rescaledData = idfModel.transform(featurizedData);
		try {
			idfModel.save("classification/idfModel");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		rescaledData.select("label", "features").show();

		List<Row> rows = rescaledData.collectAsList();
//Write the libsvm file		
		try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
			for (Row row : rows) {
				String category = row.get(0).toString();
				String features = row.get(4).toString();
				String[] tokens = features.split("\\[");
				String line = category;
				String[] codes = tokens[1].split("\\]")[0].split(",");
				String[] values = tokens[2].split("\\]")[0].split(",");
				for (int i = 0; i < codes.length; i++) {
					if(Integer.valueOf(codes[i])!=0) {
					line += " " + codes[i] + ":" + values[i];
					}
				}
				writer.println(line);
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		spark.stop();
	}
*/
	


/*
	
	
	
	public void loadCategories() {
		try (BufferedReader reader = new BufferedReader(new FileReader("classification/categories.txt"));) {
			String line = "";
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split("\t");
				Integer code = Integer.valueOf(tokens[0]);
				String category = tokens[1];
				codeToCategoryMap.put(code, category);
				categoryTocodeMap.put(category, code);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

*/

/*
	public String getCategoryFromCode(int catInt) {
		return codeToCategoryMap.get(catInt);
	}
	
*/	
	/**
	 * Creates a text file with the data from all the documents. Each document gets
	 * one line. The format has to be for each document category <tab> documentId
	 * <tab> documentTextData.
	 */
	private void prepareTextFile() {
		// The training files.
		List<File> trainingFiles = new ArrayList<File>();
//		for (String congressSession : trainingData) {
			String parentDir = dataDir;
			File[] fileList = new File(parentDir).listFiles();
			for (File xmlFile : fileList) {
//				File xmlFile = new File(hrFolder.getAbsolutePath() + "/data.xml");
				trainingFiles.add(xmlFile);
			}
//		}
		logger.info("Found "+trainingFiles.size() + " XML training files");
		new File("classification").mkdirs();
		File dataInput = new File("classification/summaries.txt");
		logger.info("Writing text training files");
		int counter = 0;
		try (PrintWriter writer = new PrintWriter(new FileWriter(dataInput));) {
			for (File xmlFile : trainingFiles) {
				String dataForFile;
				try {
					dataForFile = createLineForBill(xmlFile);
					//Avoid large files as we only have one node.
					if(dataForFile.length()<1
						|| dataForFile.length()>5000) {
						continue;
					}
					writer.println(dataForFile);
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
				counter++;
				if (counter % 500 == 0) {
					logger.info("Have processed "+counter+" files");
				}
			} // End of loop for the all the files
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println(categories.size());
		try (PrintWriter writer = new PrintWriter(new FileWriter("classification/categories.txt"));) {
			counter = 1;
			for (String cat : categories) {
				System.out.println(cat);
				writer.println(counter++ + "\t" + cat);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String createLineForBill(File xmlFile) throws IOException {
		String result = "";
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		Document doc;
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			Node root = doc.getDocumentElement();
			String billId = root.getAttributes().getNamedItem("number").getNodeValue();
			String session = root.getAttributes().getNamedItem("session").getNodeValue();
			String type = root.getAttributes().getNamedItem("type").getNodeValue();
			// Figure out the topic and convert it to an int
			NodeList nList = doc.getElementsByTagName("term");
			if (nList.getLength() > 0) {
				Element termElement = (Element) nList.item(0);
				String category = termElement.getAttribute("name");
				categories.add(category.trim());
				// The title(s)
				NodeList titleNodes = doc.getElementsByTagName("title");
				String titles = "";
				for (int i = 0; i < titleNodes.getLength(); i++) {
					titles += titleNodes.item(i).getTextContent() + " ";
				}
				// The summary
				NodeList summaryNodeList = doc.getElementsByTagName("summary");
				String summary = summaryNodeList.item(0).getTextContent();
				int dotPosition = summary.indexOf('.');
				summary = summary.substring(dotPosition + 1).trim();
				summary = summary.replace("\n", " ");
				summary = summary.replace("\r", " ");
				if (summary.startsWith("(This measure has not been amended ")) {
					int cutoff = summary.indexOf(")");
					summary = summary.substring(cutoff + 1);
				}
				summary = summary.trim();
				// figure out the list of committees
				StringBuilder commiteeBuilder = new StringBuilder();
				Element committeesNode = (Element) (doc.getElementsByTagName("committees").item(0));
				NodeList committeesList = committeesNode.getElementsByTagName("committee");
				for (int i = 0; i < committeesList.getLength(); i++) {
					Element committeeElement = (Element) committeesList.item(i);
					String committeeName = committeeElement.getAttribute("name");
					commiteeBuilder.append(committeeName + " ");
				}
				String committeeNames = commiteeBuilder.toString();
//TO NOTICE: We create a String with the information gleaned from a document on one line
//in the format category <tab> documentId <tab> documentText						
				result = category + "\t" // Target variable
						+ session + "-" + type + "-" + billId + "\t" // Id
						+ titles + " " + summary + " " + committeeNames; // Data
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
