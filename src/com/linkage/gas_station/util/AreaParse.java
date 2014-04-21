package com.linkage.gas_station.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.linkage.gas_station.model.AreaModel;

public class AreaParse extends DefaultHandler {
	
	//解析标志位
	String parseFlag="";
	AreaModel model=null;
	ArrayList<AreaModel> list_model=null;
	String cityName="";
	
	public ArrayList<AreaModel> getAreaParse(String value) {
		SAXParserFactory factory=SAXParserFactory.newInstance();
		try {
			SAXParser parser=factory.newSAXParser();
			ByteArrayInputStream bis=new ByteArrayInputStream(value.getBytes());
			parser.parse(bis, this);
			return list_model;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		super.startElement(uri, localName, qName, attributes);
		parseFlag=qName;
		if(qName.equals("outputinfo")) {
			list_model=new ArrayList<AreaModel>();
		}
		if(qName.equals("type")) {
			model=new AreaModel();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		String temp=new String(ch, start, length);
		if(parseFlag.equals("type")) {
			model.setType(Integer.parseInt(temp));
		}
		if(parseFlag.equals("name")) {
			model.setName(temp);
			if(temp.indexOf("市")!=-1) {
				cityName=temp;
			}
			else {
				model.setParentName(cityName);
			}
		}
		if(parseFlag.equals("id")) {
			model.setId(Integer.parseInt(temp));
			list_model.add(model);
		}
		parseFlag="";
	}

}
