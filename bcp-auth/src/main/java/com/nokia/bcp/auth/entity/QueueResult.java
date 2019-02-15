package com.nokia.bcp.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QueueResult {

	private String key;

	private long curValue;

}
