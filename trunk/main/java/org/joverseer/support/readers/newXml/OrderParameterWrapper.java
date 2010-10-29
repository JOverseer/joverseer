package org.joverseer.support.readers.newXml;

public class OrderParameterWrapper {
	int seqNo;
	String parameter;
	String type;

	public OrderParameterWrapper() {
		super();
		type = "Additional";
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

}
