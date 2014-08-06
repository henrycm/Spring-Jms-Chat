package com.chat.dto;

import java.io.Serializable;

public class EmailDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String de;
	private String para;
	private String msg;
	private String asunto;

	public String getDe() {
		return de;
	}

	public void setDe(String de) {
		this.de = de;
	}

	public String getPara() {
		return para;
	}

	public void setPara(String para) {
		this.para = para;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getAsunto() {
		return asunto;
	}

	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}

}
