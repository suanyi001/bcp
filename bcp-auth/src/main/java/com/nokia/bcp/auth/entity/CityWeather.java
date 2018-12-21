package com.nokia.bcp.auth.entity;

import java.io.Serializable;

import org.springframework.stereotype.Component;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Component
@ApiModel("天气信息")
public class CityWeather implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "省份编码", required = true, dataType = "String", example = "liaoning")
	private String provinceCode;
	@ApiModelProperty(value = "省份名称", required = true, dataType = "String", example = "辽宁")
	private String provinceName;
	@ApiModelProperty(value = "城市名称", required = true, dataType = "String", example = "沈阳")
	private String cityName;
	@ApiModelProperty(value = "城市编码", required = true, dataType = "String", example = "101070101")
	private String cityCode;
	@ApiModelProperty(value = "气温（摄氏度）", required = true, dataType = "integer", example = "-15")
	private int temperature;

	@Override
	public String toString() {
		return "provinceCode: " + provinceCode + ", provinceName: " + provinceName + ", cityName: " + cityName
				+ ", cityCode: " + cityCode + ", temperature: " + temperature;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

}
