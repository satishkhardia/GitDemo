package apiTest;



import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import junit.framework.Assert;

public class E2E_Test {

	
	public static void main(String[] args) {
		
		String userID= "2f16aafd-a759-4037-b366-730f4497fac1";
		String userName= "Satish1";
		String password= "Test@123";
		
		RestAssured.baseURI="http://bookstore.toolsqa.com";
		RequestSpecification request=RestAssured.given();
		
		//Step 1 to generate token
		
		request.header("Content-Type", "application/json");
		Response response=request.body("{ \"userName\":\"" + userName + "\", \"password\":\"" + password + "\"}")
							.post("/Account/v1/GenerateToken");
		
		Assert.assertEquals(response.getStatusCode(), 200);
		
		JsonPath jsonPathEvaluator= response.jsonPath();
		String Token= jsonPathEvaluator.get("token");
		
		System.out.println("Output string is "+response.getBody().asString());
		System.out.println("Token is "+Token);
		
		
		//Step 2 to get book- no authentication required, everyone can get it
		
		response = request.get("/BookStore/v1/Books");
		Assert.assertEquals(response.getStatusCode(), 200);
		
		String jsonString = response.asString();
	
		List<Map<String, String>> books=  JsonPath.from(jsonString).get("books");
		Assert.assertTrue(books.size()>0);
		System.out.println("test is pass " +books.size());
		
		String bookid=books.get(1).get("isbn");
		System.out.println("book id is "+bookid);
		
		
		//Step 3 add a book authentication
		
		request.header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json");
		 
		request.body("{ \"userId\": \"" + userID + "\", " + "\"collectionOfIsbns\": [ { \"isbn\": \"" + bookid + "\" } ]}");
		response= request.post("/BookStore/v1/Books");
		
		System.out.println("Get status code: "+response.getStatusCode());
		
		//Step 4 delete that book
		
		request.header("Authorization", "Bearer " + Token)
               .header("Content-Type", "application/json");
		
		request.body("{ \"isbn\": \"" + bookid + "\", \"userId\": \"" + userID + "\"}");
		
		response= request.delete("/BookStore/v1/Book");
		
		System.out.println("Deleted successfuuly and status code is  "+response.getStatusCode());
		
		
		// Step 5 Get user
		
		request.header("Authorization", "Bearer " + Token)
				.header("Content-Type", "application/json");
		
		response = request.get("/Account/v1/User/" + userID);
		
		System.out.println("Status code is " +response.getStatusCode());
		
	}

}
