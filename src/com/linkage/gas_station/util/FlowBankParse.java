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

import com.linkage.gas_station.model.GifTableListModel;
import com.linkage.gas_station.model.ProductGroupModel;

public class FlowBankParse extends DefaultHandler {
	
	GifTableListModel gif_model=null;
	ArrayList<ProductGroupModel> productGroupModel_list=null;
	ProductGroupModel model=null;
	//解析标志位
	String parseFlag="";
	 
	public GifTableListModel getGifTableListModel(String value) {
		SAXParserFactory factory=SAXParserFactory.newInstance();
		try {
			SAXParser parser=factory.newSAXParser();
			ByteArrayInputStream bis=new ByteArrayInputStream(value.getBytes());
			parser.parse(bis, this);
			return gif_model;
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
		if(qName.equals("OutputInfo")) {
			gif_model=new GifTableListModel();
		}
		if(qName.equals("comments")) {
			productGroupModel_list=new ArrayList<ProductGroupModel>();
		}
		if(qName.equals("product_group")) {
			model=new ProductGroupModel();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
		if(qName.equals("product_group")) {
			productGroupModel_list.add(model);
			gif_model.setProductGroupModel_list(productGroupModel_list);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		String temp=new String(ch, start, length);
		if(parseFlag.equals("product_id")) {
			model.setProduct_id(temp);
		}
		if(parseFlag.equals("product_name")) {
			model.setProduct_name(temp);
		}
		if(parseFlag.equals("accu_type_id")) {
			model.setAccu_type_id(temp);
		}
		if(parseFlag.equals("unused_amount")) {
			model.setUnused_amount(Double.parseDouble(temp.substring(0, temp.length()-2)));
		}
		if(parseFlag.equals("giftable_num")) {
			gif_model.setGiftable_num(Integer.parseInt(temp));
		}
		parseFlag="";
	}
	
}
