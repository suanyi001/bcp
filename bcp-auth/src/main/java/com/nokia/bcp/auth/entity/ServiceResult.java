package com.nokia.bcp.auth.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResult<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean success = false;

	private String message;

	private T data;

}
