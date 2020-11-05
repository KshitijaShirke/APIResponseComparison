package apiresponsecomparison;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Comparison {

	public static void getSimilarProductResponse() throws Exception {

		JsonParser parser = new JsonParser();

		// reading input from excel file
		FileInputStream fileInputStream = new FileInputStream(
				new File("/Users/kshitija/Documents/apiresponsecomparison/src/test/java/resources/Data.xlsx"));
		XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
		XSSFSheet sheet = workbook.getSheet("InputData");
		int totalRows = sheet.getLastRowNum();

		for (int a = 1; a <= totalRows; a++) {

			if (sheet.getRow(a).getCell(0) != null && sheet.getRow(a).getCell(1) != null) {
				// get data from input data sheet
				String file1 = sheet.getRow(a).getCell(0).toString();
				String file2 = sheet.getRow(a).getCell(1).toString();

				// for response (file1)
				CloseableHttpClient httpclient1 = HttpClients.createDefault();
				// Creating a HttpGet object
				HttpGet httpget1 = new HttpGet(file1);

				// Executing the Get request
				HttpResponse httpresponse1 = httpclient1.execute(httpget1);
				String response1 = EntityUtils.toString(httpresponse1.getEntity());
				System.out.println(response1);

				// for response (file2)
				CloseableHttpClient httpclient2 = HttpClients.createDefault();
				// Creating a HttpGet object
				HttpGet httpget2 = new HttpGet(file2);

				// Executing the Get request
				HttpResponse httpresponse2 = httpclient2.execute(httpget2);
				String response2 = EntityUtils.toString(httpresponse2.getEntity());
				System.out.println(response2);

				JsonElement jsonElement1 = parser.parse(new StringReader(response1));
				JsonElement jsonElement2 = parser.parse(new StringReader(response2));
				System.out.println(compareJson(jsonElement1, jsonElement2));

				if (compareJson(jsonElement1, jsonElement2)) {
					System.out.println(file1 + " equals " + file2);
				} else {
					System.out.println(file1 + " not equals " + file2);
				}
			}
		}

		// close excel
		workbook.close();
	}

	public static boolean compareJson(JsonElement json1, JsonElement json2) {
		boolean isEqual = true;
		// Check whether both jsonElement are not null
		if (json1 != null && json2 != null) {

			// Check whether both jsonElement are objects
			if (json1.isJsonObject() && json2.isJsonObject()) {
				Set<Entry<String, JsonElement>> ens1 = ((JsonObject) json1).entrySet();
				Set<Entry<String, JsonElement>> ens2 = ((JsonObject) json2).entrySet();
				JsonObject json2obj = (JsonObject) json2;
				if (ens1 != null && ens2 != null && (ens2.size() == ens1.size())) {
					// Iterate JSON Elements with Key values
					for (Entry<String, JsonElement> en : ens1) {
						isEqual = isEqual && compareJson(en.getValue(), json2obj.get(en.getKey()));
					}
				} else {
					return false;
				}
			}

			// Check whether both jsonElement are arrays
			else if (json1.isJsonArray() && json2.isJsonArray()) {
				JsonArray jarr1 = json1.getAsJsonArray();
				JsonArray jarr2 = json2.getAsJsonArray();
				if (jarr1.size() != jarr2.size()) {
					return false;
				} else {
					int i = 0;
					// Iterate JSON Array to JSON Elements
					for (JsonElement je : jarr1) {
						isEqual = isEqual && compareJson(je, jarr2.get(i));
						i++;
					}
				}
			}

			// Check whether both jsonElement are null
			else if (json1.isJsonNull() && json2.isJsonNull()) {
				return true;
			}

			// Check whether both jsonElement are primitives
			else if (json1.isJsonPrimitive() && json2.isJsonPrimitive()) {
				if (json1.equals(json2)) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else if (json1 == null && json2 == null) {
			return true;
		} else {
			return false;
		}
		return isEqual;
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		getSimilarProductResponse();
	}

}
