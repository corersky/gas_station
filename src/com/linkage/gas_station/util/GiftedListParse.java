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

import com.linkage.gas_station.model.DetailGroupModel;

public class GiftedListParse extends DefaultHandler {
	
	ArrayList<DetailGroupModel> model_list=new ArrayList<DetailGroupModel>();
	DetailGroupModel model=null;
	//解析标志位
	String parseFlag="";
	
	public ArrayList<DetailGroupModel> getDetailGroupModelList(String value) {		
		SAXParserFactory factory=SAXParserFactory.newInstance();
		try {
			SAXParser parser=factory.newSAXParser();
			ByteArrayInputStream bis=new ByteArrayInputStream(value.getBytes());
			parser.parse(bis, this);
			return model_list;
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
		if("detail_group".equals(qName)) {
			model=new DetailGroupModel();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
		model_list.add(model);
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		String temp=new String(ch, start, length);
		if(parseFlag.equals("phone_num")) {
			model.setPhone_num(temp);
		}
		if(parseFlag.equals("gifted_phone_num")) {
			model.setGifted_phone_num(temp);
		}
		if(parseFlag.equals("gift_amount")) {
			model.setGift_amount(temp);
		}
		if(parseFlag.equals("gift_time")) {
			model.setGift_time(temp);
		}
		if(parseFlag.equals("accu_level_name")) {
			model.setAccu_level_name(temp);
		}
		if(parseFlag.equals("oper_type")) {
			model.setOper_type(temp);
		}
		parseFlag="";
	}
	
}
