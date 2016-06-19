package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.avaje.ebean.Model;

public class DataContainer implements Serializable{

	private static final long serialVersionUID = 1L;
	private List<Data> dataList = new ArrayList<Data>();

	public DataContainer(List<Data> dataList) {
		super();
		this.dataList = dataList;
	}

	public List<Data> getDataList() {
		return dataList;
	}

	public void setDataList(List<Data> dataList) {
		this.dataList = dataList;
	}
	
}
