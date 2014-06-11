/*
 * Copyright 2011, University of Southern California. All Rights Reserved.
 * 
 * This software is experimental in nature and is provided on an AS-IS basis only. 
 * The University SPECIFICALLY DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT 
 * LIMITATION ANY WARRANTY AS TO MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * This software may be reproduced and used for non-commercial purposes only, 
 * so long as this copyright notice is reproduced with each such copy made.
 */
package edu.usc.pgroup.floe.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

public class EucalyptusAccountInfo {
    String endPoint;
    String awsAccessKey;
    String secretKey;
    String signatureMethod;
    String signatureVersion;
    String version;
    String extension;

    public EucalyptusAccountInfo(InputStream eucaProperties) {
        Properties props = new Properties();
        try {
            props.load(eucaProperties);
            this.endPoint = props.getProperty("EndPoint");
            this.awsAccessKey = props.getProperty("AWSAccessKeyId");
            this.secretKey = props.getProperty("SecretKey");
            this.extension = props.getProperty("Extension");
            this.signatureMethod = "HmacSHA256";
            this.signatureVersion = "2";
            this.version = "2009-04-04";
            eucaProperties.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public EucalyptusAccountInfo(String eucalyptusconfigfile) throws FileNotFoundException {
		this(new FileInputStream(eucalyptusconfigfile));
	}

	public String generateSignature(String value) {
        String prefixStringReq = "GET\n" + this.endPoint + "\n" + this.extension + "\n" + value;
        try {
            byte[] keyBytes = this.secretKey.getBytes();
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, this.signatureMethod);
            Mac mac = Mac.getInstance(this.signatureMethod);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(prefixStringReq.getBytes());
            return new String(Base64.encodeBase64(rawHmac));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

   public String generateQueryString(TreeMap<String, String> inpParams) {
        TreeMap<String, String> queryParams = inpParams;
        queryParams.put("AWSAccessKeyId", this.awsAccessKey);
        queryParams.put("SignatureMethod", this.signatureMethod);
        queryParams.put("SignatureVersion", this.signatureVersion);
        queryParams.put("Version", this.version);
        Set<Map.Entry<String, String>> entrySet = queryParams.entrySet();
        Iterator<Map.Entry<String, String>> entryIter = entrySet.iterator();
        String retQueryStr = "";
        while (entryIter.hasNext()) {
            Map.Entry<String, String> tempEntry = entryIter.next();
            retQueryStr += tempEntry.getKey() + "=" + tempEntry.getValue() + "&";
        }
        retQueryStr = retQueryStr.substring(0, retQueryStr.length() - 1);
        String queryString = "";
        try {
            queryString = "http://" + this.endPoint + this.extension + "?" + retQueryStr + "&Signature=" + URLEncoder.encode(generateSignature(retQueryStr), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return queryString;
    }
}
