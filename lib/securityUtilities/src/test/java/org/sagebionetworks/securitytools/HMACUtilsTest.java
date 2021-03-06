package org.sagebionetworks.securitytools;

import org.joda.time.DateTime;
import org.junit.Test;


/**
 * Unit test for HMACUtilsTest
 */
public class HMACUtilsTest {
	
	@Test
	public void testHMACUtils() throws Exception {
		DateTime timeStamp = new DateTime();
		System.out.println(timeStamp);

//		String timeStampString = "";
//		String userId="demouser@sagebase.org";
//		String uri="/repo/v1/dataset";
//		String uri="/services-repository-0.7-SNAPSHOT/repo/v1/dataset";
//		String base64EncodedSecretKey = "nUielh3l3rHuZis4JQ/4sr05N8ounV8OnQsZqmjmHnD2r1ITmJQSkr4WmM37e5Fi81lQ+WdZ794G6KEDMx/NKw==";
//		String base64EncodedSecretKey = "pDfk2KtmuvwFNKJzOn16ZfIY5qbSDebNFpTPHd6DuGemivMLWCV3tBFny6qGQ3luwXW7Q13IL3SUYC29mXeKdg==";

		String timeStampString = timeStamp.toString(); //"2011-09-28T13:31:16.90-0700";
		
		
//		String userId="matt.furia@sagebase.org";
//		String uri="/repo/v1/entity/17428/type";
//		String timeStampString = "2011-10-07T00:09:40.44-0700";
		
		String userId="serviceaccount@google.com";//"devUser1@sagebase.org";
		//String uri="/auth/v1/resourceSession/qUVzPsx6QgoNqZucWD00wHvFflKJ4g7+dyPTVL7+3kVkxJpgWh6ShKMRC6lDTcIN";//"/repo/v1/query";
		//String uri="/auth/v1/resourceSession/0HQgst54p7FkDPzG6yNZ8+PB68eKRQfsdyPTVL7+3kVk-JpgWh6ShKMRC6lDTcIN";
		String uri="/auth/v1/resourceSession/foo";
		//		String timeStampString = "2011-12-14T14:20:26.554-08:00";
		String base64EncodedSecretKey = //"XL4CIyGR8ooxwBwy+cDelpiU42TG6DQdA0LsuxHeZ0HIgPV4zevQ5WrH8TuI6I9yRDzKLOel2+E73EMOVZS+2A==";
					"0AD0sHdeXfMwjZ7p7eXwbUxD2TSSirCquoaEOwmNvdCV9uAZ0/KbenvWOYqp5AjLpiy8f/Ubck/PjGqoTUUijQ==";
		
		
		//username: serviceaccount@google.com
		uri="/auth/v1/resourceSession/qUVzPsx6QgoNqZucWD00wHvFflKJ4g7+dyPTVL7+3kVk/JpgWh6ShKMRC6lDTcIN";
		timeStampString="2012-02-27T15:17:18-0800";
		base64EncodedSecretKey="0AD0sHdeXfMwjZ7p7eXwbUxD2TSSirCquoaEOwmNvdCV9uAZ0/KbenvWOYqp5AjLpiy8f/Ubck/PjGqoTUUijQ==";
		//sig: MDc2ZjI0Y2QzZWNlODNhZWJlMDE2MTU1ZGM0YzcwOWRlZTE2ZDIyMg==
		
		String encoded = HMACUtils.generateHMACSHA1Signature(
				userId,
	    		uri,
	    		timeStampString,
	    		base64EncodedSecretKey);
		
//		encoded = new String(HMACUtils.generateHMACSHA1SignatureFromBase64EncodedKey("matt.furia@sagebase.org/repo/v1/entity/17428/type2011-10-07T00:09:40.44-0700", base64EncodedSecretKey));
		if (false) {
			System.out.println("Secret key: "+base64EncodedSecretKey+"\nData: "+userId+uri+timeStampString+"\nHash for data: "+encoded);
			System.out.println(
					"curl -i -k -H Accept:application/json -H userId:"+userId+" -H signatureTimestamp:"+
					timeStampString+" -H signature:"+encoded+" -X GET https://auth-dev.sagebase.org"+uri);
		}
	}
	
}
