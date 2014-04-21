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

import android.content.Context;

import com.linkage.gas_station.model.Flow_Group_Model;
import com.linkage.gas_station.model.Flow_Type_Group_Model;
import com.linkage.gas_station.model.OutputInfoModel;

public class OutputInfoParse extends DefaultHandler {
	
	Context context=null;
	OutputInfoModel info=null;
	ArrayList<Flow_Group_Model> flow_group_list=null;
	Flow_Group_Model fg_model=null;
	ArrayList<Flow_Type_Group_Model> flow_type_group_list=null;
	Flow_Type_Group_Model ftg_model=null;
	//解析标志位
	String parseFlag="";
	
	public OutputInfoModel getOutputInfoModel(String value, Context context) {
		this.context=context;
		SAXParserFactory factory=SAXParserFactory.newInstance();
		try {
			SAXParser parser=factory.newSAXParser();
			ByteArrayInputStream bis=new ByteArrayInputStream(value.getBytes());
			parser.parse(bis, this);
			return info;
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
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		String temp=new String(ch, start, length);
		if(parseFlag.equals("data_time")) {
			info.setData_time(temp);
		}
		if(parseFlag.equals("flow_type_id")) {
			fg_model.setFlow_type_id(Integer.parseInt(temp));
		}
		if(parseFlag.equals("flow_type_name")) {
			fg_model.setFlow_type_name(temp);
		}
		if(parseFlag.equals("flow_type_amount")) {
			fg_model.setFlow_type_amount(temp);
		}
		if(parseFlag.equals("flow_type_used")) {
			fg_model.setFlow_type_used(temp);
		}
		if(parseFlag.equals("flow_type_unused")) {
			fg_model.setFlow_type_unused(temp);
		}
		if(parseFlag.equals("flow_type_self")) {
			Util.setSelf(context, temp);
		}
		if(parseFlag.equals("product_id")) {
			ftg_model.setProduct_id(temp);
		}
		if(parseFlag.equals("product_name")) {
			ftg_model.setProduct_name(temp);
		}
		if(parseFlag.equals("product_amount")) {
			ftg_model.setProduct_amount(temp);
		}
		if(parseFlag.equals("product_used")) {
			ftg_model.setProduct_used(temp);
		}
		if(parseFlag.equals("product_unused")) {
			ftg_model.setProduct_unused(temp);
		}
		parseFlag="";
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
		if(qName.equals("flow_type_group")) {
			if(flow_type_group_list==null) {
				flow_type_group_list=new ArrayList<Flow_Type_Group_Model>();
			}
			flow_type_group_list.add(ftg_model);
		}
		if(qName.equals("flow_group")) {
			fg_model.setModel_list(flow_type_group_list);
			if(flow_group_list==null) {
				flow_group_list=new ArrayList<Flow_Group_Model>();
			}
			flow_group_list.add(fg_model);
		}
		if(qName.equals("OutputInfo")) {
			info.setModel_list(flow_group_list);
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		super.startElement(uri, localName, qName, attributes);
		parseFlag=qName;
		if(qName.equals("OutputInfo")) {
			info=new OutputInfoModel();		
			flow_group_list=new ArrayList<Flow_Group_Model>();
		}
		if(qName.equals("flow_group")) {
			fg_model=new Flow_Group_Model();
			flow_type_group_list=new ArrayList<Flow_Type_Group_Model>(); 
		}
		if(qName.equals("flow_type_group")) {
			ftg_model=new Flow_Type_Group_Model(); 
		}
	}
	
}
